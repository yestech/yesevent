/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import org.apache.camel.Exchange;

/**
 * Interface for all camel based events
 *
 * @author A.J. Wright
 * @see EventResultType
 * @see IListener
 */
public interface ICamelEvent extends IEvent, Exchange
{
    String getDefaultEndPointUri();

    void setDefaultEndPointUri(String uri);
}