/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.guice;

import com.google.inject.Binder;
import org.yestech.event.IEventMulticaster;
import org.yestech.event.IListener;
import org.yestech.event.DefaultEventMulticaster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $$Author$$
 * @version $$Revision$$
 */
public class MulticasterBinder
{
    private DefaultEventMulticaster multicasterClass;
    private List<IListener> list = new ArrayList<IListener>();

    public MulticasterBinder(DefaultEventMulticaster multicasterClass) {

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
        multicasterClass.setListeners(list);
        multicasterClass.init();
    }
}
