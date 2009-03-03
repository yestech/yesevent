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
 * @author $$Author$$
 * @version $$Revision$$
 */
public interface IEventMulticaster<EVENT extends IEvent, RESULT extends Serializable>
{
    RESULT process(EVENT event);

}