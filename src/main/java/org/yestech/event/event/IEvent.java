/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event.event;

import java.io.Serializable;

/**
 * Interface all events should implement.  If the event wishes to
 * be explicit on the type of result it requires use the {@link org.yestech.event.annotation.EventResultType} annotation.
 * 
 * @see org.yestech.event.annotation.EventResultType
 * @see org.yestech.event.listener.IListener
 */
public interface IEvent extends Serializable
{
    /**
     * Returns a unique name for the event.
     * 
     * @return the name
     */
    String getEventName();
}
