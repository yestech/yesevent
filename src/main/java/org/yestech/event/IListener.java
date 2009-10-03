/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import org.yestech.event.annotation.ListenedEvents;
import org.yestech.event.annotation.RegisteredEvents;
import org.yestech.event.multicaster.IEventMulticaster;

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

    void handle(EVENT event, ResultReference<RESULT> result);

}
