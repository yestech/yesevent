/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

/**
 * Interface for all camel based events
 *
 * @author A.J. Wright
 * @see EventResultType
 * @see IListener
 */
public interface ICamelEvent extends IEvent
{
    String getEventName();

    String getDefaultEndPointUri();

    void setDefaultEndPointUri(String uri);
}