/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Must be added to every instance of {@link org.yestech.event.listener.IListener}. This annotation tells what
 * events the listener must listen for.  Used with the {@link DefaultEventMulticaster}.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface RegisteredEvents
{
    /**
     * Which events a listener is listening for with predetermined order.
     *
     * @return
     */
    RegisterEvent[] events() default {};

}
