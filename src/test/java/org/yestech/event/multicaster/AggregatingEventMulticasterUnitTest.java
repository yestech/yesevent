package org.yestech.event.multicaster;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.yestech.event.AggregateResultReference;
import org.yestech.event.BaseEvent;
import org.yestech.event.BaseListener;
import org.yestech.event.IAggregateListener;
import org.yestech.event.IEvent;
import org.yestech.event.ResultReference;
import org.yestech.event.annotation.RegisterEvent;
import org.yestech.event.annotation.RegisteredEvents;
import org.yestech.event.multicaster.AggregatingEventMulticaster.ListenerAdapter;
import static org.junit.Assert.*;

/**
 *
 */
public class AggregatingEventMulticasterUnitTest {
    final public static String RESULT_VAL = "works...";
    @Test
    public void testProcess() {
        AggregatingEventMulticaster<Event1, AggregateResultReference> multicastor = new AggregatingEventMulticaster<Event1, AggregateResultReference>();
        Listener1 listener1 = new Listener1();
        List<IAggregateListener> listeners = Lists.newArrayList();
        listeners.add(listener1);
        multicastor.addListeners(listeners);
        multicastor.init();
        multicastor.getListenerMap();
        Multimap<Class, ListenerAdapter> listenerMap = multicastor.getListenerMap();
        assertNotNull(listenerMap);
        Collection<ListenerAdapter> eventList = listenerMap.get(Event1.class);
        assertEquals(1, eventList.size());
        ListenerAdapter[] listenersArray = eventList.toArray(new ListenerAdapter[1]);
        assertEquals(listener1, listenersArray[0].getAdaptee());
        Event1 event = new Event1();
        AggregateResultReference result = multicastor.process(event);
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(RESULT_VAL, result.getResult(TestEnum.TEST));
    }


    public static class Event1 extends BaseEvent {
    }


   public interface Foo {
    }

    public static class FooImpl implements Foo {
    }

    public enum TestEnum {
        TEST
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = Event1.class, order = 1)
    })
    public class Listener1 extends BaseListener implements IAggregateListener {

        private Foo foo;

        @Override
        public Enum getToken() {
            return TestEnum.TEST;
        }

        @Override
        public void setToken(Enum token) {
            throw new UnsupportedOperationException("Not supported yet.");
        }


        @Inject
        public void setFoo(Foo foo) {
            this.foo = foo;
        }

        public Foo getFoo() {
            return foo;
        }

        public void handle(IEvent iEvent, ResultReference ref) {
            ref.setResult(RESULT_VAL);
        }

        @Override
        public String toString() {
            return "Listener1: " + super.toString();
        }

    }
}
