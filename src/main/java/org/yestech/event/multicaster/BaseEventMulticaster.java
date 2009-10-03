/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

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

    public BaseEventMulticaster() {
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
    }

    @PostConstruct
    public void init() {
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
