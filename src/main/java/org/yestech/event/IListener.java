/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import java.io.Serializable;

/**
 *
 * @author A.J. Wright
 * @param <EVENT> An instance of IEvent
 * @param <RESULT> An instance of Serializable
 */
public interface IListener<EVENT extends IEvent, RESULT extends Serializable>
{

    void handle(EVENT event, ResultReference<RESULT> result);

}
