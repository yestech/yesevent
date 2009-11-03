/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.listener.IListener;
import org.yestech.event.event.IEvent;

/**
 * Interface for the event multicaster
 *
 * @see org.yestech.event.event.IEvent
 * @see org.yestech.event.listener.IListener
 */
public interface IEventMulticaster<EVENT extends IEvent, RESULT>
{

    /**
     * Return is the result type should be checked.
     *
     * @return
     */
    public boolean isCheckResultType();

    /**
     * Sets whether the type of result is checked or not.
     *
     * @param resultTypeCheck
     */
    public void setCheckResultType(boolean checkResultType);

    /**
     * Called by the client to process an event.
     *
     * @param event The event that should be processed.
     * @return A result from the multicaster.
     */
    RESULT process(EVENT event);

    /**
     * Registers a listener with an event it can handle.
     *
     * @param listener Listener to register
     */
     <L extends IListener> void registerListener(L listener);

    /**
     * DeRegisters a listener from and event it can handle.
     *
     * @param listener Listener to deregister
     */
    <L extends IListener> void deregisterListener(L listener);

}