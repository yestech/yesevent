/*Copyright(c) 2004-2008, iovation, inc. All rights reserved.
 *
 * Original Author:  ${user}
 * Original Date:    ${date}
 * Last Modified Date: $$DateTime$$
 */
package org.yestech.event;

import java.io.Serializable;
import java.util.List;

/**
 * @author $$Author$$
 * @version $$Revision$$
 */
public interface IEventMulticaster<EVENT extends IEvent, RESULT extends Serializable>
{
    void process(EVENT event, RESULT result);

}