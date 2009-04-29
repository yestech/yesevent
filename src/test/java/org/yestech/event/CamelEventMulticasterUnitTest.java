/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

/*
 *
 * Author:  Artie Copeland
 * Last Modified Date: $DateTime: $
 */
package org.yestech.event;

import org.apache.camel.*;
import org.apache.camel.spi.Registry;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.bean.ParameterMappingStrategy;
import org.apache.camel.component.bean.DefaultParameterMappingStrategy;
import org.apache.camel.test.CamelTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

/**
 * @author Artie Copeland
 * @version $Revision: $
 */
@RunWith(JMock.class)
public class CamelEventMulticasterUnitTest {
    private CamelContext camelContext;
    private ProducerTemplate template;
    private CamelEventMulticaster<TestCamelEvent, String> multicaster;
    Mockery context = new Mockery();
     Registry registry;
    @Before
    public void setUp() throws Exception {
        registry = context.mock(Registry.class, "registry");
        multicaster = new CamelEventMulticaster<TestCamelEvent, String>();
        camelContext = new DefaultCamelContext(registry);
        camelContext.start();
        template = camelContext.createProducerTemplate();
        template.start();
        multicaster.setTemplate(template);
        multicaster.setContext(camelContext);
    }

    @After
    public void tearDown() throws Exception {
        template.stop();
        camelContext.stop();
    }

    @Test
    public void testProcess() throws Exception {
        final String defaultEndPoint = "direct:test";
        final ICamelEvent camelEvent = context.mock(ICamelEvent.class, "event");
        context.checking(new Expectations() {
            {
                oneOf(registry).lookup("direct");
                will(returnValue(new DirectComponent()));
                oneOf(registry).lookup("bean");
                will(returnValue(new BeanComponent()));
                oneOf(registry).lookup("finished");
                will(returnValue(new TestBean()));
                oneOf(registry).lookup("org.apache.camel.component.bean.ParameterMappingStrategy", ParameterMappingStrategy.class);
                will(returnValue(new DefaultParameterMappingStrategy()));
            }
        });
        camelContext.addRoutes(new RouteBuilder() {

            public void configure() {
                from(defaultEndPoint).to("bean:finished?method=talk");
            }
        });

        TestCamelEvent event = new TestCamelEvent(multicaster.getContext());
        event.setDefaultEndPointUri(defaultEndPoint);
        final String result = multicaster.process(event);
        assertNotNull(result);
        assertEquals(TestBean.TALKING, result);
    }

    private static class TestCamelEvent extends DefaultExchange implements ICamelEvent {
        private String defaultEndPointUri;

        private TestCamelEvent(CamelContext context) {
            super(context);
        }

        private TestCamelEvent(CamelContext context, ExchangePattern pattern) {
            super(context, pattern);
        }

        private TestCamelEvent(Exchange parent) {
            super(parent);
        }

        private TestCamelEvent(Endpoint fromEndpoint) {
            super(fromEndpoint);
        }

        private TestCamelEvent(Endpoint fromEndpoint, ExchangePattern pattern) {
            super(fromEndpoint, pattern);
        }

        public String getDefaultEndPointUri() {
            return defaultEndPointUri;
        }

        public void setDefaultEndPointUri(String defaultEndPointUri) {
            this.defaultEndPointUri = defaultEndPointUri;
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
