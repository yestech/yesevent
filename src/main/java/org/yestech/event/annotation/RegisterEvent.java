/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event.annotation;

import org.yestech.event.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the Event to register with and the order of execution
 *
 * @author A.J. Wright
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface RegisterEvent
{
    /**
     * Which events the listener is listening for.
     *
     * @return
     */
    Class<? extends IEvent> event();

    /**
     * Which order is the listener fired.  if this  is used there must be the same number of elements as events
     *
     * @return the order that this listener should be fired
     */
    int order() default 0;
}
