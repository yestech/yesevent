package com.yestech.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT extends Serializable> implements IEventMulticaster<EVENT,RESULT>
{

    private final Multimap<Class, IListener> listenerMap = new ArrayListMultimap<Class, IListener>();


    public void init(List<IListener> listeners) {

        for (IListener listener : listeners)
        {

            ListenedEvents listenedEvents = listener.getClass().getAnnotation(ListenedEvents.class);
            if (listenedEvents != null) {
                for (Class<? extends IEvent> eventClass : listenedEvents.value())
                {
                    listenerMap.put(eventClass, listener);
                }
            }
        }
    }

    Multimap<Class, IListener> getListenerMap() {
        return listenerMap;
    }


    @SuppressWarnings({"unchecked"})
    public void process(EVENT event, RESULT result) {
        Collection<IListener> list = listenerMap.get(event.getClass());
        if (list != null && !list.isEmpty()) {
            for (IListener listener : list)
            {
                listener.handle(event, result);
            }
        }
    }


}
