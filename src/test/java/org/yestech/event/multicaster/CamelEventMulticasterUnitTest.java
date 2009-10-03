/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.multicaster;

import org.yestech.event.annotation.EventResultType;
import org.yestech.event.*;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Registry;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.bean.ParameterMappingStrategy;
import org.apache.camel.component.bean.DefaultParameterMappingStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.runner.RunWith;

/**
 * 
 */
@RunWith(JMock.class)
public class CamelEventMulticasterUnitTest {

    private DefaultCamelContext camelContext;
    private DefaultProducerTemplate template;
    private CamelEventMulticaster<TestCamelEvent, String> multicaster;
    private Mockery context = new Mockery();
    private Registry registry;

    @Before
    public void setUp() throws Exception {
        registry = context.mock(Registry.class, "registry");
        multicaster = new CamelEventMulticaster<TestCamelEvent, String>();
        camelContext = new DefaultCamelContext(registry);
        camelContext.start();
        template = (DefaultProducerTemplate) camelContext.createProducerTemplate();
        template.start();
    }

    @After
    public void tearDown() throws Exception {
        template.stop();
        camelContext.stop();
    }

    @Test
    public void testProcessDefaultContextWithExchangeResult() throws Exception {
        CamelEventMulticaster<TestRawCamelEvent, Exchange> multicaster = new CamelEventMulticaster<TestRawCamelEvent, Exchange>();
        multicaster.setDefaultContext(camelContext);
        final String defaultEndPoint = "direct:test";
        context.checking(new Expectations() {

            {
                oneOf(registry).lookup("direct");
                will(returnValue(new DirectComponent()));
                oneOf(registry).lookup("bean");
                will(returnValue(new BeanComponent()));
                oneOf(registry).lookup("finished");
                will(returnValue(new TestBean()));
                oneOf(registry).lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class);
                will(returnValue(new DefaultParameterMappingStrategy()));
            }
        });
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        TestRawCamelEvent event = new TestRawCamelEvent();
        event.setDefaultEndPointUri(defaultEndPoint);
        final Exchange result = multicaster.process(event);
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result.getIn().getBody());
    }

    @Test
    public void testProcessDefaultContext() throws Exception {
        multicaster.setDefaultContext(camelContext);
        final String defaultEndPoint = "direct:test";
        context.checking(new Expectations() {

            {
                oneOf(registry).lookup("direct");
                will(returnValue(new DirectComponent()));
                oneOf(registry).lookup("bean");
                will(returnValue(new BeanComponent()));
                oneOf(registry).lookup("finished");
                will(returnValue(new TestBean()));
                oneOf(registry).lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class);
                will(returnValue(new DefaultParameterMappingStrategy()));
            }
        });
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        TestCamelEvent event = new TestCamelEvent();
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    @Test
    public void testProcessMultipleContext() throws Exception {
        TestCamelEvent event = new TestCamelEvent();
        multicaster.getContexts().put(event.getEventName(), camelContext);
        final String defaultEndPoint = "direct:test";
        context.checking(new Expectations() {

            {
                oneOf(registry).lookup("direct");
                will(returnValue(new DirectComponent()));
                oneOf(registry).lookup("bean");
                will(returnValue(new BeanComponent()));
                oneOf(registry).lookup("finished");
                will(returnValue(new TestBean()));
                oneOf(registry).lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class);
                will(returnValue(new DefaultParameterMappingStrategy()));
            }
        });
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    @Test
    public void testProcessDefaultContextInOut() throws Exception {
        TestCamelEvent event = new TestCamelEvent();
        multicaster.getContexts().put(event.getEventName(), camelContext);
        final String defaultEndPoint = "direct:test";
        final String lastEndPoint = "bean:finished?method=talk";
        
        context.checking(new Expectations() {

            {
                oneOf(registry).lookup("direct");
                will(returnValue(new DirectComponent()));
                oneOf(registry).lookup("bean");
                will(returnValue(new BeanComponent()));
                oneOf(registry).lookup("finished");
                will(returnValue(new TestBean()));
                oneOf(registry).lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class);
                will(returnValue(new DefaultParameterMappingStrategy()));
            }
        });
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).inOut().to(lastEndPoint);
            }
        });
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    private static class TestCamelEvent extends BaseCamelEvent {

        @Override
        public String getEventName() {
            return "testEvent";
        }

    }

    @EventResultType(Exchange.class)
    private static class TestRawCamelEvent extends BaseCamelEvent {

        @Override
        public String getEventName() {
            return "testRawEvent";
        }

    }

    public static class TestBean {

        private boolean called;
        public static final String TALKING = "talking....";

        public boolean isCalled() {
            return called;
        }

        public void setCalled(boolean called) {
            this.called = called;
        }

        public String talk() {
            return TALKING;
        }
    }
}
