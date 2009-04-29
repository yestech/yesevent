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
 * Represents a base class for a service that wishes to integrate a {@link IEventMulticaster}.
 * By default it is configured to be {@link javax.annotation.Resource} named "camelEventMulticaster" by Spring.
 *
 * @author $Author: $
 * @version $Revision: $
 */
public abstract class BaseCamelContainer extends BaseContainer {
    protected BaseCamelContainer() {
        super();
    }

    @Resource(name = "camelEventMulticaster")
    public void setEventMulticaster(IEventMulticaster<IEvent, Object> eventMulticaster) {
        super.setEventMulticaster(eventMulticaster);
    }
}