/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.annotation.AsyncListener;
import org.yestech.event.annotation.EventResultType;
import org.yestech.event.*;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Lists;
import com.google.inject.*;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import org.yestech.event.guice.MulticasterBinder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.yestech.event.annotation.RegisterEvent;
import org.yestech.event.annotation.RegisteredEvents;
import org.yestech.event.multicaster.DefaultEventMulticaster.ListenerAdapter;

/**
 * 
 */
public class DefaultEventMulticasterUnitTest {

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
        Multimap<Class, ListenerAdapter> listenerMap = mc.getListenerMap();
        assertNotNull(listenerMap);
        Collection<ListenerAdapter> eventList = listenerMap.get(Event1.class);
        assertEquals(2, eventList.size());
        Listener1 iListener = (Listener1) eventList.iterator().next().getAdaptee();
        Foo foo = iListener.getFoo();
        assertNotNull(foo);


    }

    @Test
    public void testOrderOfExecution() {
        DefaultEventMulticaster multicastor = new DefaultEventMulticaster();
        Listener2 listener2 = new Listener2();
        Listener1 listener1 = new Listener1();
        List<IListener> listeners = newArrayList();
        listeners.add(listener2);
        listeners.add(listener1);
        multicastor.addListeners(listeners);
        multicastor.init();
        multicastor.getListenerMap();
        Multimap<Class, ListenerAdapter> listenerMap = multicastor.getListenerMap();
        assertNotNull(listenerMap);
        Collection<ListenerAdapter> eventList = listenerMap.get(Event1.class);
        assertEquals(2, eventList.size());
        ListenerAdapter[] listenersArray = eventList.toArray(new ListenerAdapter[2]);
        assertEquals(listener1, listenersArray[0].getAdaptee());
        assertEquals(listener2, listenersArray[1].getAdaptee());
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

        Thread.sleep(1000);
        assertTrue(asyncListener.isCalled());
    }

    @Test(expected = InvalidListenerException.class)
    public void testFailOnInvalid() {
        DefaultEventMulticaster multicaster = new DefaultEventMulticaster();
        multicaster.setListeners(Lists.<IListener>newArrayList(new InvalidListener()));
        multicaster.init();
    }

    @Test(expected = InvalidResultException.class)
    public void testInvalidResult() {

        DefaultEventMulticaster multicaster = new DefaultEventMulticaster();
        multicaster.setCheckResultType(true);
        ReturnsWrongListener listener = new ReturnsWrongListener();
        multicaster.setListeners(Lists.<IListener>newArrayList(listener));
        multicaster.init();

        multicaster.process(new RequiredIntegerEvent());
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = Event1.class)
    })
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

    public static class Event1 extends BaseEvent {
    }

    public static class Event2 extends BaseEvent {
    }

    public class InvalidListener implements IListener {

        @Override
        public void handle(IEvent iEvent, ResultReference result) {
        }
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = Event1.class, order = 1)
    })
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

        @Override
        public String toString() {
            return "Listener1: " + super.toString();
        }
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = Event1.class, order = 2),
        @RegisterEvent(event = Event2.class)
    })
    public class Listener2 implements IListener {

        public void handle(IEvent iEvent, ResultReference ref) {
        }

        @Override
        public String toString() {
            return "Listener2: " + super.toString();
        }
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = Event2.class)
    })
    public class Listener3 implements IListener {

        public void handle(IEvent event, ResultReference ref) {
        }
    }

    @EventResultType(Integer.class)
    public class RequiredIntegerEvent extends BaseEvent {
    }

    @RegisteredEvents(events = {
        @RegisterEvent(event = RequiredIntegerEvent.class)
    })
    public class ReturnsWrongListener implements IListener {

        public void handle(IEvent iEvent, ResultReference ref) {
            ref.setResult(new FooImpl());
        }
    }
}
