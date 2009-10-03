/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.*;
import javax.annotation.Resource;

/**
 * Represents a base class for a service that wishes to integrate a {@link org.yestech.event.IEventMulticaster}.
 * By default it is configured to be {@link Resource} named "eventMulticaster" by Spring.
 *
 */
public abstract class BaseServiceContainer {
    private IEventMulticaster<IEvent, Object> eventMulticaster;

    protected BaseServiceContainer() {
        super();
    }

    public IEventMulticaster<IEvent, Object> getEventMulticaster() {
        return eventMulticaster;
    }

    @Resource(name = "eventMulticaster")
    public void setEventMulticaster(IEventMulticaster<IEvent, Object> eventMulticaster) {
        this.eventMulticaster = eventMulticaster;
    }
}
