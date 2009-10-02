/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

/**
 * Interface for all camel based events.  If the caller would like to get the
 * Raw camel {@link Exchange} as the result the event must contain the {@link EventResultType}
 * with a class of {@link Exchange}.

 *
 * @see EventResultType
 */
public interface ICamelEvent extends IEvent
{

    String getDefaultEndPointUri();

    /**
     * Sets the entry point into the camel route.
     *
     * @param uri The URI for the camel route to set as default route.
     */
    void setDefaultEndPointUri(String uri);
}