package org.yestech.event;

import com.google.common.collect.Multimap;
import com.google.inject.*;
import org.yestech.event.guice.MulticasterBinder;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticasterTest
{

    Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGuiceWiring()
    {

        Module guiceModule = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind(Foo.class).to(FooImpl.class);

                MulticasterBinder mcBinder = new MulticasterBinder(new DefaultEventMulticaster());
                mcBinder.addListener(new Listener1());
                mcBinder.addListener(new Listener2());
                mcBinder.addListener(new Listener3());
                mcBinder.bind(binder());
            }
        };

        Injector injector = Guice.createInjector(guiceModule);
        IEventMulticaster multicaster = injector.getInstance(IEventMulticaster.class);
        assertNotNull(multicaster);
        assertTrue(multicaster instanceof IEventMulticaster);
        DefaultEventMulticaster mc = (DefaultEventMulticaster) multicaster;
        Multimap<Class, IListener> listenerMap = mc.getListenerMap();
        assertNotNull(listenerMap);
        Collection<IListener> eventList = listenerMap.get(Event1.class);
        assertEquals(2, eventList.size());
        Listener1 iListener = (Listener1) eventList.iterator().next();
        Foo foo = iListener.getFoo();
        assertNotNull(foo);


    }

    public interface Foo
    {
    }

    public static class FooImpl implements Foo
    {
    }

    public static class Event1 implements IEvent
    {
    }

    public static class Event2 implements IEvent
    {
    }


    @ListenedEvents(Event1.class)
    public class Listener1 implements IListener
    {
        private Foo foo;

        @Inject
        public void setFoo(Foo foo)
        {
            this.foo = foo;
        }

        public Foo getFoo()
        {
            return foo;
        }

        @Override
        public void handle(IEvent iEvent, Serializable serializable)
        {

        }
    }

    @ListenedEvents({Event1.class, Event2.class})
    public class Listener2 implements IListener
    {
        @Override
        public void handle(IEvent iEvent, Serializable serializable)
        {

        }
    }

    @ListenedEvents(Event2.class)
    public class Listener3 implements IListener
    {
        @Override
        public void handle(IEvent iEvent, Serializable serializable)
        {

        }
    }


}
