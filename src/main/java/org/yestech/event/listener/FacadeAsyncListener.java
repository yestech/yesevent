/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.listener;

import org.yestech.event.annotation.AsyncListener;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import org.yestech.event.event.IEvent;
import org.yestech.event.ResultReference;

/**
 * A simple listener that executes a sequence of listeners in the background.
 *
 */
@SuppressWarnings("unchecked")
@AsyncListener
public class FacadeAsyncListener<EVENT extends IEvent, RESULT> extends BaseListener implements IListener {
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
