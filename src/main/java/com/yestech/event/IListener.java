package com.yestech.event;

import java.io.Serializable;

/**
 *
 * @author A.J. Wright
 * @param <EVENT> An instance of IEvent
 * @param <RESULT> An instance of Serializable
 */
public interface IListener<EVENT extends IEvent, RESULT extends Serializable>
{

    void handle(EVENT event, RESULT result);

}
