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
import org.yestech.event.multicaster.DefaultOrderEventMulticaster;

/**
 * Must be added to every instance of {@link org.yestech.event.IListener}. This annotation tells what
 * events the listener must listen for.  Used with the {@link DefaultOrderEventMulticaster}.
 *
 * @author A.J. Wright
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface ListenedEvents
{
    /**
     * Which events the listener is listening for no order is determined.
     *
     * @return
     */
    Class<? extends IEvent>[] value();

}
