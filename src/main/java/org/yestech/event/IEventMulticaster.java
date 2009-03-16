/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for the event multicaster
 *
 * @author $$Author$$
 * @version $$Revision$$
 * @see org.yestech.event.IEvent
 * @see org.yestech.event.IListener
 */
public interface IEventMulticaster<EVENT extends IEvent, RESULT extends Serializable>
{

    /**
     * Called by the client to process an event.
     *
     * @param event The event that should be processed.
     * @return A result from the multicaster.
     */
    RESULT process(EVENT event);

}