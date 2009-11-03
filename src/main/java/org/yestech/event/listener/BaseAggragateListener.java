/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.listener;

import org.springframework.beans.factory.annotation.Required;
import org.yestech.event.event.IEvent;
import org.yestech.event.ResultReference;

import java.util.List;

/**
 * A base class for a simple lister that executes a sequence of listeners.
 *
 */
@SuppressWarnings("unchecked")
public abstract class BaseAggragateListener<EVENT extends IEvent, RESULT> implements IListener {
    private List<IListener> listeners;

    public List getListeners() {
        return listeners;
    }

    @Required
    public void setListeners(List listeners) {
        this.listeners = listeners;
    }

    @Override
    public void handle(IEvent event, ResultReference result) {
        for (IListener listener : listeners) {
            listener.handle(event, result);
        }
    }
}