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
import com.google.common.collect.Lists;
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

public class AggregatingEventMulticaster<EVENT extends IEvent, RESULT extends AggregateResultReference> extends BaseEventMulticaster<EVENT, AggregateResultReference> {

    private static final Logger logger = LoggerFactory.getLogger(AggregatingEventMulticaster.class);
    private final Multimap<Class, ListenerAdapter> listenerMap = ArrayListMultimap.create();
    private List<IAggregateListener> listeners;

    /**
     * Sets a list of {@link IListener}s
     *
     * @param listeners
     */
    public void setListeners(List<IAggregateListener> listeners) {
        this.listeners = listeners;
    }

    public List<IAggregateListener> getListeners() {
        return listeners;
    }

    @PostConstruct
    @Override
    public void init() {
        addListeners(listeners);
        initializeThreadPool();
    }


    protected void addListeners(List<IAggregateListener> listeners) {
        Map<Class, List<ListenerContainer>> tempListenerMap = Maps.newHashMap();
        if (listeners != null) {
            for (IAggregateListener listener : listeners) {
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
                            tempListenerList = Lists.newArrayList();
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

    @Override
    public AggregateResultReference process(EVENT event) {
        Collection<ListenerAdapter> list = listenerMap.get(event.getClass());
        AggregateResultReference aggregateReference = new AggregateResultReference();


        if (list != null && !list.isEmpty()) {
            for (ListenerAdapter listener : list) {
                ResultReference ref = new ResultReference();
                if (listener.isAsync()) {
                    processAsync(event, ref, listener);
                } else {
                    listener.handle(event, ref);
                }
                Object result = ref.getResult();
                validate(event, result);
                aggregateReference.addResult(listener.getToken(), ref);
            }
        }

        return aggregateReference;
    }

    protected void addListener(Class<? extends IEvent> event, IAggregateListener listener) {
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

    class ListenerAdapter<EVENT extends IEvent, RESULT> implements IAggregateListener {

        private IAggregateListener adaptee;
        private boolean async;

        @Override
        public Enum getToken() {
            return adaptee.getToken();
        }

        @Override
        public void setToken(Enum token) {
            adaptee.setToken(token);
        }

        public ListenerAdapter(IAggregateListener adaptee) {
            this.adaptee = adaptee;
            async = adaptee.getClass().isAnnotationPresent(AsyncListener.class);
        }

        public boolean isAsync() {
            return async;
        }

        public IAggregateListener getAdaptee() {
            return adaptee;
        }

        @Override
        public void handle(IEvent event, ResultReference result) {
            adaptee.handle(event, result);
        }
    }

    private class ListenerContainer implements Comparable<ListenerContainer> {

        private IAggregateListener listener;
        private Integer order;

        public IAggregateListener getListener() {
            return listener;
        }

        public void setListener(IAggregateListener listener) {
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
