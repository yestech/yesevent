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
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CamelEventMulticasterUnitTest {

    private DefaultCamelContext camelContext;
    private DefaultProducerTemplate template;
    private CamelEventMulticaster<TestCamelEvent, String> multicaster;

    @Mock
    private Registry registry;

    @Before
    public void setUp() throws Exception {
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
        initializeRegistry();

        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        TestRawCamelEvent event = new TestRawCamelEvent();
        event.setDefaultEndPointUri(defaultEndPoint);
        final Exchange result = multicaster.process(event);
        verifyRegistry();
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result.getIn().getBody());
    }

    @Test
    public void testProcessDefaultContext() throws Exception {
        multicaster.setDefaultContext(camelContext);
        final String defaultEndPoint = "direct:test";
        initializeRegistry();
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        TestCamelEvent event = new TestCamelEvent();
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        initializeRegistry();
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    @Test
    public void testProcessMultipleContext() throws Exception {
        TestCamelEvent event = new TestCamelEvent();
        multicaster.getContexts().put(event.getEventName(), camelContext);
        final String defaultEndPoint = "direct:test";
        initializeRegistry();
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        verifyRegistry();
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    @Test
    public void testProcessDefaultContextInOut() throws Exception {
        TestCamelEvent event = new TestCamelEvent();
        multicaster.getContexts().put(event.getEventName(), camelContext);
        final String defaultEndPoint = "direct:test";
        final String lastEndPoint = "bean:finished?method=talk";
        initializeRegistry();
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).inOut().to(lastEndPoint);
            }
        });
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        verifyRegistry();
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

    private void initializeRegistry() {
        when(registry.lookup("direct")).thenReturn(new DirectComponent());
        when(registry.lookup("bean")).thenReturn(new BeanComponent());
        when(registry.lookup("finished")).thenReturn(new TestBean());
        when(registry.lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class)).thenReturn(new DefaultParameterMappingStrategy());
    }

    private void verifyRegistry() {
        verify(registry).lookup("direct");
        verify(registry).lookup("bean");
        verify(registry).lookup("finished");
        verify(registry).lookup("CamelBeanParameterMappingStrategy", ParameterMappingStrategy.class);
    }
}
