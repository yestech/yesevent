/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event.listener;

import org.yestech.event.multicaster.IEventMulticaster;
import org.yestech.event.event.IEvent;

/**
 * Represents a Listener that is executed by a {@link IEventMulticaster}.  The result
 * from the Execution will be aggregated in a {@link org.yestech.event.AggregateResultReference} for access
 * by the caller.  The contract is still the same as {@link IListener}.
 *
 * @param <EVENT> An instance of IEvent
 * @param <RESULT> The result object
 */
public interface IAggregateListener<EVENT extends IEvent, RESULT> extends IListener<EVENT, RESULT>
{
    /**
     * Return the Token to associate the result.
     *
     * @return the Token
     */
    Enum<?> getToken();

    /**
     * Set the token to use in association.
     *
     * @param token The Token
     */
    void setToken(Enum<?> token);

}
