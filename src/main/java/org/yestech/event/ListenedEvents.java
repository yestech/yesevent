/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Must be added to every instance of {@link org.yestech.event.IListener}. This annotation tells what
 * events the listener must listen for.
 *
 * @author A.J. Wright
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface ListenedEvents
{
    /**
     * Which events the listener is listening for.
     *
     * @return
     */
    Class<? extends IEvent>[] value();
}
