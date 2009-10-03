/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.annotation.ListenedEvents;
import org.yestech.event.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a multicaster implementation that takes the default order that is supplied when registering
 * Listeners.
 *
 * @param <EVENT> An implementation of IEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
public class DefaultOrderEventMulticaster<EVENT extends IEvent, RESULT> extends DefaultEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultOrderEventMulticaster.class);

    @Override
    protected void addListeners(List<IListener> listeners) {
        if (listeners != null) {
            for (IListener listener : listeners) {
                ListenedEvents listenedEvents = listener.getClass().getAnnotation(ListenedEvents.class);

                if (listenedEvents != null) {
                    for (Class<? extends IEvent> eventClass : listenedEvents.value()) {

                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Listener %s Registered against Event %s",
                                    listener.getClass().getSimpleName(), eventClass.getSimpleName()));
                        }
                        getListenerMap().put(eventClass, listener);
                    }
                } else {
                    String msg = String.format("%s must contain an ListenedEvents annotation",
                            listener.getClass().getSimpleName());
                    logger.error(msg);
                    throw new InvalidListenerException(msg);
                }
            }
        }
    }
}
