/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.event;

import org.yestech.event.annotation.EventResultType;

/**
 * Utility methods for Framework.
 *
 */
public class EventUtils {

    /**
     * Returns the {@link EventResultType} associated with an event.
     * 
     * @param <EVENT>
     * @param event
     * @return
     */
    public static <EVENT> EventResultType getResultType(EVENT event) {
        return event.getClass().getAnnotation(EventResultType.class);
    }
}
