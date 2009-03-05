/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Multimap;
import com.google.inject.*;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import org.yestech.event.guice.MulticasterBinder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author A.J. Wright
 */
public class DefaultEventMulticasterTest {

    Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGuiceWiring() {

        Module guiceModule = new AbstractModule() {
            @Override
            protected void configure() {
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

    @Test
    public void testAsyncListener() throws InterruptedException {
        DefaultEventMulticaster multicastor = new DefaultEventMulticaster();
        List<IListener> listeners = newArrayList();
        TestAsyncListener asyncListener = new TestAsyncListener();
        listeners.add(asyncListener);
        multicastor.setListeners(listeners);
        multicastor.init();
        multicastor.process(new Event1());
        Thread.currentThread().sleep(100);
        assertTrue(asyncListener.isCalled());
    }

    @ListenedEvents(Event1.class)
    @AsyncListener
    public static class TestAsyncListener implements IListener<Event1, TestResult> {

        private boolean called;

        public boolean isCalled() {
            return called;
        }

        public void setCalled(boolean called) {
            this.called = called;
        }

        public void handle(Event1 event1, ResultReference<TestResult> result) {
            called = true;
        }
    }

    public static class TestResult implements Serializable {

    }

    public interface Foo {
    }

    public static class FooImpl implements Foo {
    }

    public static class Event1 implements IEvent {
    }

    public static class Event2 implements IEvent {
    }


    @ListenedEvents(Event1.class)
    public class Listener1 implements IListener {
        private Foo foo;

        @Inject
        public void setFoo(Foo foo) {
            this.foo = foo;
        }

        public Foo getFoo() {
            return foo;
        }

        public void handle(IEvent iEvent, ResultReference ref) {

        }
    }

    @ListenedEvents({Event1.class, Event2.class})
    public class Listener2 implements IListener {
        public void handle(IEvent iEvent, ResultReference ref) {

        }
    }

    @ListenedEvents(Event2.class)
    public class Listener3 implements IListener {
        public void handle(IEvent event, ResultReference ref) {

        }
    }


}
