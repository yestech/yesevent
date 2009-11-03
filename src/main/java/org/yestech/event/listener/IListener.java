/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.yestech.event.annotation.ListenedEvents;
import org.yestech.event.annotation.RegisteredEvents;
import org.yestech.event.multicaster.IEventMulticaster;
import org.yestech.event.event.IEvent;
import org.yestech.event.ResultReference;

/**
 * Represents a Listener that is executed by a {@link IEventMulticaster}.  To register
 * a listener with a multicaster atleast one of the following annotations must
 * supplied {@link ListenedEvents}, {@link RegisteredEvents}.  Which annotation
 * depends on the concrete multicaster is being used.
 *
 * @param <EVENT> An instance of IEvent
 * @param <RESULT> The result object
 */
public interface IListener<EVENT extends IEvent, RESULT>
{

    /**
     * Called by the {@link IEventMulticaster} when the Event is fired.
     *
     * @param event Event registered
     * @param result The result to return
     */
    void handle(EVENT event, ResultReference<RESULT> result);

    /**
     * The Multicaster to use when registering the listener.  Should only be
     * called on Construction.
     *
     * @param multicaster The Multicaster
     */
    void setMulticaster(IEventMulticaster<EVENT, RESULT> multicaster);

    /**
     * Result the Multicaster to use when registering the listener.
     *
     * @return The multicaster
     */
    IEventMulticaster<EVENT, RESULT> getMulticaster();

    /**
     * Registers the Listener with the {@link IEventMulticaster} and all
     * associated events the listener can handle.
     */
    @PostConstruct
    public void register();

    /**
     * DeRegisters the Listener with the {@link IEventMulticaster} and all
     * associated events the listener can handle.
     */
    @PreDestroy
    public void deregister();

}
