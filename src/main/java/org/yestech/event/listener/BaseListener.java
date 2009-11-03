/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.listener;

import javax.annotation.Resource;
import org.yestech.event.multicaster.IEventMulticaster;
import org.yestech.event.event.IEvent;

/**
 * Base class for {@link IListener}.
 *
 */
public abstract class BaseListener<EVENT extends IEvent, RESULT> implements IListener<EVENT, RESULT> {

    private IEventMulticaster<EVENT, RESULT> multicaster;

    @Override
    public IEventMulticaster<EVENT, RESULT> getMulticaster() {
        return multicaster;
    }

    @Override
    @Resource(name = "eventMulticaster")
    public void setMulticaster(IEventMulticaster<EVENT, RESULT> multicaster) {
        this.multicaster = multicaster;
    }

    @Override
    public void deregister() {
        multicaster.deregisterListener(this);
    }

    @Override
    public void register() {
        multicaster.registerListener(this);
    }
}
