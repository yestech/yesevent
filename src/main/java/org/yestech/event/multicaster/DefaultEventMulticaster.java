/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.annotation.AsyncListener;
import org.yestech.event.*;
import com.google.common.collect.ArrayListMultimap;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yestech.event.annotation.RegisterEvent;
import org.yestech.event.annotation.RegisteredEvents;

/**
 * The default event multicaster implementation this implementation expects all {@link IListener} that wish to be
 * executed contain a {@link RegisteredEvents} annotation.
 *
 * @param <EVENT> An implementation of IEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT> extends BaseEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventMulticaster.class);
    private final Multimap<Class, ListenerAdapter> listenerMap = ArrayListMultimap.create();
    private List<IListener> listeners = newArrayList();

    @Override
    public <L extends IListener> void registerListener(L listener) {
        listeners.add(listener);
    }

    /**
     * Sets a list of {@link IListener}s
     *
     * @param listeners
     */
    public void setListeners(List<IListener> listeners) {
        this.listeners = listeners;
    }

    public List<IListener> getListeners() {
        return listeners;
    }
  
    @PostConstruct
    @Override
    public void init() {
        addListeners(listeners);
        initializeThreadPool();
    }

    protected void addListeners(List<IListener> listeners) {
        Map<Class, List<ListenerContainer>> tempListenerMap = Maps.newHashMap();
        if (listeners != null) {
            for (IListener listener : listeners) {
                RegisteredEvents listenedEvents = listener.getClass().getAnnotation(RegisteredEvents.class);

                if (listenedEvents != null) {
                    for (RegisterEvent eventClass : listenedEvents.events()) {
                        Class<? extends IEvent> event = eventClass.event();
                        Integer order = eventClass.order();
                        ListenerContainer container = new ListenerContainer();
                        container.setListener(listener);
                        container.setOrder(order);
                        List<ListenerContainer> tempListenerList = tempListenerMap.get(event);
                        if (tempListenerList == null) {
                            tempListenerList = newArrayList();
                            tempListenerMap.put(event, tempListenerList);
                        }
                        tempListenerList.add(container);
                    }
                } else {
                    String msg = String.format("%s must contain an RegisteredEvents annotation",
                            listener.getClass().getSimpleName());
                    logger.error(msg);
                    throw new InvalidListenerException(msg);
                }
            }
            reorderListeners(tempListenerMap);
        }
    }

    Multimap<Class, ListenerAdapter> getListenerMap() {
        return listenerMap;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public RESULT process(final EVENT event) {
        Collection<ListenerAdapter> list = listenerMap.get(event.getClass());
        ResultReference<RESULT> ref = new ResultReference<RESULT>();

        if (list != null && !list.isEmpty()) {
            for (ListenerAdapter listener : list) {
                if (listener.isAsync()) {
                    processAsync(event, ref, listener);
                } else {
                    listener.handle(event, ref);
                }
            }
        }
        Object result = ref.getResult();

        validate(event, result);

        return (RESULT) result;
    }

    protected void addListener(Class<? extends IEvent> event, IListener listener) {
        listenerMap.put(event, new ListenerAdapter(listener));
    }

    private void reorderListeners(Map<Class, List<ListenerContainer>> tempListenerMap) {
        for (Map.Entry<Class, List<ListenerContainer>> tempEntry : tempListenerMap.entrySet()) {
            Class<? extends IEvent> event = tempEntry.getKey();
            List<ListenerContainer> tempListenersList = tempEntry.getValue();
            Collections.sort(tempListenersList);
            for (ListenerContainer listenerContainer : tempListenersList) {
                addListener(event, listenerContainer.getListener());
            }
        }
    }

    class ListenerAdapter<EVENT extends IEvent, RESULT> extends BaseListener {
        private IListener adaptee;
        private boolean async;

        public ListenerAdapter(IListener adaptee) {
            this.adaptee = adaptee;
            async = adaptee.getClass().isAnnotationPresent(AsyncListener.class);
        }

        public boolean isAsync() {
            return async;
        }

        public IListener getAdaptee() {
            return adaptee;
        }

        @Override
        public void handle(IEvent event, ResultReference result) {
            adaptee.handle(event, result);
        }

        @Override
        public void deregister() {
            getMulticaster().deregisterListener(adaptee);
        }

        @Override
        public void register() {
            getMulticaster().registerListener(adaptee);
        }

    }

    private class ListenerContainer implements Comparable<ListenerContainer> {

        private IListener listener;
        private Integer order;

        public IListener getListener() {
            return listener;
        }

        public void setListener(IListener listener) {
            this.listener = listener;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        @Override
        public int compareTo(ListenerContainer compare) {
            return order.compareTo(compare.getOrder());
        }
    }
}
