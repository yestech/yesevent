/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

/*
 *
 * Original Author:  Artie Copeland
 * Last Modified Date: $DateTime: $
 */
package org.yestech.event;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * Represents a base class for a service that wishes to integrate a {@link org.yestech.event.IEventMulticaster}.
 * By default it is configured to be {@link Resource} named "eventMulticaster" by Spring.
 *
 * @author $Author: $
 * @version $Revision: $
 */
public abstract class BaseContainer {
    private IEventMulticaster<IEvent, Object> eventMulticaster;

    protected BaseContainer() {
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
