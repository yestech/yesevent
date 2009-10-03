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
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yestech.event.annotation.RegisterEvent;
import org.yestech.event.annotation.RegisteredEvents;

/**
 * The default event multicaster implementation
 *
 * @param <EVENT> An implementation of IEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT> extends BaseEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventMulticaster.class);

    private final Multimap<Class, IListener> listenerMap = ArrayListMultimap.create();
    
    private ExecutorService pool;
    private int corePoolSize = 1;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60;
    private List<IListener> listeners;
    private List<List<IListener>> listenerGroups;

    public ExecutorService getPool() {
        return pool;
    }

    public void setPool(ExecutorService pool) {
        this.pool = pool;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Sets a list of {@link IListener}s
     *
     * @param listeners
     */
    public void setListeners(List<IListener> listeners) {
        this.listeners = listeners;
    }

    public List<List<IListener>> getListenerGroups() {
        return listenerGroups;
    }

    /**
     * Sets a list of {@link IListener}s that allows for easier grouping of listeners
     * and set there execution order.
     *
     * @param listenerGroups
     */
    public void setListenerGroups(List<List<IListener>> listenerGroups) {
        this.listenerGroups = listenerGroups;
    }

    @PostConstruct
    @Override
    public void init() {
        addListeners(listeners);

        if (listenerGroups != null) {
            for (List<IListener> listenerGroup : listenerGroups) {
                addListeners(listenerGroup);
            }
        }
        
        initializeThreadPool();
    }

    @PreDestroy
    @Override
    public void destroy() {
        pool.shutdown();
    }

    private void initializeThreadPool() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>());
        }
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
                            tempListenerList = Lists.newArrayList();
                            tempListenerMap.put(event, tempListenerList);
                        }
                        tempListenerList.add(container);
                    }
                }
                else {
                    String msg = String.format("%s must contain an RegisteredEvents annotation",
                            listener.getClass().getSimpleName());
                    logger.error(msg);
                    throw new InvalidListenerException(msg);
                }
            }
            for (Map.Entry<Class, List<ListenerContainer>> tempEntry : tempListenerMap.entrySet()) {
                Class<? extends IEvent> event = tempEntry.getKey();
                List<ListenerContainer> tempListenersList = tempEntry.getValue();
                Collections.sort(tempListenersList);
                for (ListenerContainer listenerContainer : tempListenersList) {
                    listenerMap.put(event, listenerContainer.getListener());
                }
            }
        }
    }
    
    protected Multimap<Class, IListener> getListenerMap() {
        return listenerMap;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public RESULT process(final EVENT event) {
        Collection<IListener> list = listenerMap.get(event.getClass());
        ResultReference<RESULT> ref = new ResultReference<RESULT>();

        if (list != null && !list.isEmpty()) {
            for (IListener listener : list) {
                if (listener.getClass().isAnnotationPresent(AsyncListener.class)) {
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

    private void processAsync(final EVENT event, final ResultReference<RESULT> ref, final IListener listener) {
        pool.execute(new Runnable() {
            @SuppressWarnings({"unchecked"})
            @Override
            public void run() {
                listener.handle(event, ref);
            }
        });
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
