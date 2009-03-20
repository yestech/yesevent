/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

/**
 *
 * @author A.J. Wright
 * @param <EVENT> An instance of IEvent
 * @param <RESULT> The result object
 */
public interface IListener<EVENT extends IEvent, RESULT>
{

    void handle(EVENT event, ResultReference<RESULT> result);

}
