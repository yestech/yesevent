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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT extends Serializable> implements IEventMulticaster<EVENT, RESULT> {

    private final Multimap<Class, IListener> listenerMap = new ArrayListMultimap<Class, IListener>();
    private ExecutorService pool;
    private int corePoolSize = 1;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60;
    private List<IListener> listeners;

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

    public void setListeners(List<IListener> listeners) {
        this.listeners = listeners;
    }

    public void init() {

        for (IListener listener : listeners) {
            ListenedEvents listenedEvents = listener.getClass().getAnnotation(ListenedEvents.class);
            if (listenedEvents != null) {
                for (Class<? extends IEvent> eventClass : listenedEvents.value()) {
                    listenerMap.put(eventClass, listener);
                }
            }
        }
        
        if (pool == null) {
            pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>());
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
        return ref.getResult();
    }

    private void processAsync(final EVENT event, final ResultReference<RESULT> ref, final IListener listener) {
        pool.execute(new Runnable() {
            public void run() {
                listener.handle(event, ref);
            }
        });
    }


}
