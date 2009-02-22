/*Copyright(c) 2004-2008, iovation, inc. All rights reserved.
 *
 * Original Author:  ${user}
 * Original Date:    ${date}
 * Last Modified Date: $$DateTime$$
 */
package com.yestech.event;

import java.io.Serializable;

/**
 * @author $$Author$$
 * @version $$Revision$$
 */
public interface IListener<EVENT extends IEvent, RESULT extends Serializable>
{

    RESULT handle(EVENT event);

}
