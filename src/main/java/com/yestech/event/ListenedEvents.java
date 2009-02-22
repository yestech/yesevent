package com.yestech.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
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
