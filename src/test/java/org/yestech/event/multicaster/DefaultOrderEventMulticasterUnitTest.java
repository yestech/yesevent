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
import org.yestech.event.annotation.ListenedEvents;
import org.yestech.event.*;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Lists;
import com.google.inject.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class DefaultOrderEventMulticasterUnitTest
{

    @Test
    public void testAsyncListener() throws InterruptedException
    {
        DefaultOrderEventMulticaster multicastor = new DefaultOrderEventMulticaster();
        List<IListener> listeners = newArrayList();
        TestAsyncListener asyncListener = new TestAsyncListener();
        listeners.add(asyncListener);
        multicastor.setListeners(listeners);
        multicastor.init();
        multicastor.process(new Event1());

        Thread.currentThread().sleep(1000);
        assertTrue(asyncListener.isCalled());
    }

    @Test(expected = InvalidListenerException.class)
    public void testFailOnInvalid()
    {
        DefaultOrderEventMulticaster multicaster = new DefaultOrderEventMulticaster();
        multicaster.setListeners(Lists.<IListener>newArrayList(new InvalidListener()));
        multicaster.init();
    }

    @Test(expected = InvalidResultException.class)
    public void testInvalidResult()
    {

        DefaultOrderEventMulticaster multicaster = new DefaultOrderEventMulticaster();
        multicaster.setCheckResultType(true);
        ReturnsWrongListener listener = new ReturnsWrongListener();
        multicaster.setListeners(Lists.<IListener>newArrayList(listener));
        multicaster.init();

        multicaster.process(new RequiredIntegerEvent());
    }


    @ListenedEvents(Event1.class)
    @AsyncListener
    public static class TestAsyncListener extends BaseListener<Event1, TestResult>
    {

        private boolean called;

        public boolean isCalled()
        {
            return called;
        }

        public void setCalled(boolean called)
        {
            this.called = called;
        }

        public void handle(Event1 event1, ResultReference<TestResult> result)
        {
            called = true;
        }
    }

    public static class TestResult implements Serializable
    {

    }

    public interface Foo
    {
    }

    public static class FooImpl implements Foo
    {
    }

    public static class Event1 extends BaseEvent
    {
    }

    public static class Event2 extends BaseEvent
    {
    }


    public class InvalidListener extends BaseListener
    {

        @Override
        public void handle(IEvent iEvent, ResultReference result)
        {
        }
    }


    @ListenedEvents(Event1.class)
    public class Listener1 extends BaseListener
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

        public void handle(IEvent iEvent, ResultReference ref)
        {

        }
    }

    @ListenedEvents({Event1.class, Event2.class})
    public class Listener2 extends BaseListener
    {
        public void handle(IEvent iEvent, ResultReference ref)
        {

        }
    }

    @ListenedEvents(Event2.class)
    public class Listener3 extends BaseListener
    {
        public void handle(IEvent event, ResultReference ref)
        {

        }
    }

    @EventResultType(Integer.class)
    public class RequiredIntegerEvent  extends BaseEvent {

    }

    @ListenedEvents(RequiredIntegerEvent.class)
    public class ReturnsWrongListener extends BaseListener
    {


        public void handle(IEvent iEvent, ResultReference ref)
        {
            ref.setResult(new FooImpl());
        }
    }


}
