package com.yestech.event;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT extends Serializable>
{

    private final HashMap<Class, List<IListener>> listenerMap = new HashMap<Class, List<IListener>>();


    @Inject
    public DefaultEventMulticaster(List<IListener> listeners) {

        for (IListener listener : listeners)
        {

            ListenedEvents listenedEvents = listener.getClass().getAnnotation(ListenedEvents.class);
            if (listenedEvents != null) {
                for (Class<? extends IEvent> eventClass : listenedEvents.value())
                {

                    List<IListener> list = listenerMap.get(eventClass);
                    if(list == null) {
                        list = new ArrayList<IListener>();
                        listenerMap.put(eventClass, list);
                    }
                    list.add(listener);
                }
            }
        }
    }

    Map<Class, List<IListener>> getListenerMap() {
        return listenerMap;
    }


    @SuppressWarnings({"unchecked"})
    public List<RESULT> process(EVENT event) {
        List<RESULT> results = new ArrayList<RESULT>();

        List<IListener> list = listenerMap.get(event.getClass());
        if (list != null && !list.isEmpty()) {
            for (IListener listener : list)
            {
                RESULT result = (RESULT) listener.handle(event);
                results.add(result);
            }
        }

        return results;
    }


}
