/*Copyright(c) 2004-2008, iovation, inc. All rights reserved.
 *
 * Original Author:  ${user}
 * Original Date:    ${date}
 * Last Modified Date: $$DateTime$$
 */
package org.yestech.event.guice;

import com.google.inject.Binder;
import org.yestech.event.IEventMulticaster;
import org.yestech.event.IListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $$Author$$
 * @version $$Revision$$
 */
public class MulticasterBinder
{
    private IEventMulticaster multicasterClass;
    private List<IListener> list = new ArrayList<IListener>();

    public MulticasterBinder(IEventMulticaster multicasterClass) {

        this.multicasterClass = multicasterClass;
    }


    public void addListener(IListener listener) {
        list.add(listener);
    }

    @SuppressWarnings({"unchecked"})
    public void bind(Binder binder) {
        for (IListener listener : list)
        {
            binder.bind((Class) listener.getClass()).toInstance(listener);
        }
        binder.bind(IEventMulticaster.class).toInstance(multicasterClass);
        multicasterClass.init(list);
    }
}
