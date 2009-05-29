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

/**
 * @author Artie Copeland
 * @version $Revision: $
 */
public abstract class BaseCamelEvent implements ICamelEvent {
    private String defaultEndPointUrl;

    public String getDefaultEndPointUrl() {
        return defaultEndPointUrl;
    }

    public void setDefaultEndPointUrl(String defaultEndPointUrl) {
        this.defaultEndPointUrl = defaultEndPointUrl;
    }
}
