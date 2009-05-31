/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

/*
 *
 * Author:  Artie Copeland
 * Last Modified Date: $DateTime: $
 */
package org.yestech.event;

import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * A simple lister that executes a sequence of listeners.
 *
 * @author Artie Copeland
 * @version $Revision: $
 */
@SuppressWarnings("unchecked")
public abstract class AggragateListener<EVENT extends IEvent, RESULT> implements IListener {
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