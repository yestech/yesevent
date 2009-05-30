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

import org.apache.camel.Exchange;

/**
 * A Listener used with {@link org.yestech.event.CamelEventMulticaster} so camel knows explicitly which method to call.
 *
 * @author Artie Copeland
 * @version $Revision: $
 */
public interface ICamelListener {
    public void process(Exchange exchange);
}
