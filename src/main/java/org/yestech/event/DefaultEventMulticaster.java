/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default event multicaster implementation
 *
 * @param <EVENT> An implementation of IEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
@Service("eventMulticaster")
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT> implements IEventMulticaster<EVENT, RESULT> {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventMulticaster.class);

    private final Multimap<Class, IListener> listenerMap = new ArrayListMultimap<Class, IListener>();
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

    private void addListeners(List<IListener> listeners) {
        if (listeners != null) {
            for (IListener listener : listeners) {
                ListenedEvents listenedEvents = listener.getClass().getAnnotation(ListenedEvents.class);

                if (listenedEvents != null) {
                    for (Class<? extends IEvent> eventClass : listenedEvents.value()) {
                        
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Listener %s Registered against Event %s",
                                    listener.getClass().getSimpleName(), eventClass.getSimpleName()));
                        }
                        listenerMap.put(eventClass, listener);
                    }
                }
                else {
                    String msg = String.format("%s must contain an IListenedEvents annotation",
                            listener.getClass().getSimpleName());
                    log.error(msg);
                    throw new InvalidListenerException(msg);
                }
            }
        }
    }

    Multimap<Class, IListener> getListenerMap() {
        return listenerMap;
    }


    @SuppressWarnings({"unchecked"})
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

        if (result != null && event.getClass().isAnnotationPresent(EventResultType.class)) {
            EventResultType resultType = event.getClass().getAnnotation(EventResultType.class);

            if (resultType.value() != null) {
                if (!resultType.value().isAssignableFrom(result.getClass())) {
                    String msg = String.format("%s Requires that a type of %s was returned",
                            event.getClass().getSimpleName(),
                            resultType.getClass().getSimpleName());
                    log.error(msg);
                    throw new InvalidResultException(msg);
                }
            }
        }


        return (RESULT) result;
    }

    private void processAsync(final EVENT event, final ResultReference<RESULT> ref, final IListener listener) {
        pool.execute(new Runnable() {
            @SuppressWarnings({"unchecked"})
            public void run() {
                listener.handle(event, ref);
            }
        });
    }


}
