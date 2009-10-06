/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.yestech.event.annotation.EventResultType;
import org.yestech.event.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Class for {@link IEventMulticaster}
 *
 * @param <EVENT> An implementation of IEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
public abstract class BaseEventMulticaster<EVENT extends IEvent, RESULT> implements IEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(BaseEventMulticaster.class);
    private boolean checkResultType;
    private ExecutorService pool;
    private int corePoolSize = 1;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60;

    public BaseEventMulticaster() {
    }

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

    @Override
    public boolean isCheckResultType() {
        return checkResultType;
    }

    @Override
    public void setCheckResultType(boolean resultTypeCheck) {
        this.checkResultType = resultTypeCheck;
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            getPool().shutdown();
        }
    }

    @PostConstruct
    public void init() {
    }

    protected void initializeThreadPool() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>());
        }
    }

    protected void processAsync(final EVENT event, final ResultReference<RESULT> ref, final IListener listener) {
        pool.execute(new Runnable() {

            @SuppressWarnings({"unchecked"})
            @Override
            public void run() {
                listener.handle(event, ref);
            }
        });
    }


    protected void validate(EVENT event, Object result) {
        if (checkResultType) {
            if (result != null) {
                EventResultType resultType = EventUtils.getResultType(event);
                if (resultType != null && resultType.value() != null) {
                    if (!resultType.value().isAssignableFrom(result.getClass())) {
                        String msg = String.format("%s Requires that a type of %s was returned",
                                event.getClass().getSimpleName(),
                                resultType.getClass().getSimpleName());
                        logger.error(msg);
                        throw new InvalidResultException(msg);
                    }
                }
            }
        }
    }
}
