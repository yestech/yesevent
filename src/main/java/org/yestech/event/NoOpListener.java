/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

import javax.annotation.Resource;
import org.yestech.event.multicaster.IEventMulticaster;

/**
 * Base class for {@link org.yestech.event.IListener} that doesnt do anything basically its a NoOp.
 *
 */
public abstract class NoOpListener<EVENT extends IEvent, RESULT> implements IListener<EVENT, RESULT> {

    @Override
    public IEventMulticaster<EVENT, RESULT> getMulticaster() {
        return null;
    }

    @Override
    @Resource(name = "eventMulticaster")
    public void setMulticaster(IEventMulticaster<EVENT, RESULT> multicaster) {
    }

    @Override
    public void deregister() {
    }

    @Override
    public void register() {
    }
}