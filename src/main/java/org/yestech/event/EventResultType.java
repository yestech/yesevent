package org.yestech.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

/**
 * Can be annoted on an event to ensure the multicaster returns the right result type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
@Inherited
public @interface EventResultType
{

    Class value();

}
