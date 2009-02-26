package org.yestech.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticaster<EVENT extends IEvent, RESULT extends Serializable> implements IEventMulticaster<EVENT,RESULT>
{

    private final Multimap<Class, IListener> listenerMap = new ArrayListMultimap<Class, IListener>();
    private List<IListener> listeners;

    public void setListeners(List<IListener> listeners)
    {
        this.listeners = listeners;
    }

    public void init() {

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
    public RESULT process(EVENT event) {
        Collection<IListener> list = listenerMap.get(event.getClass());
        ResultReference<RESULT> ref = new ResultReference<RESULT>();

        if (list != null && !list.isEmpty()) {
            for (IListener listener : list)
            {
                listener.handle(event, ref);
            }
        }
        return ref.getResult();
    }


}
