/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Message;
import org.apache.camel.CamelContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.DefaultProducerTemplate;

/**
 * A <a href="http://camel.apache.org">camel</a> based event multicaster implementation.
 * <br/>
 * An example with Spring Config:
 * <pre>
 * &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *       xmlns:camel="http://camel.apache.org/schema/spring"
 *       xsi:schemaLocation="
 *          http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
 *          http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd"&gt;
 *
 *    &lt;camel:camelContext id="context"&gt;
 *       &lt;camel:jmxAgent id="contextAgent" disabled="true"/&gt;
 *      &lt;!-- will be default endpoint added to event fired --&gt;
 *        &lt;camel:endpoint uri="direct:processEvent" /&gt;
 *        &lt;camel:route&gt;
 *            &lt;camel:from uri="direct:processEvent"/&gt;
 *            &lt;camel:to uri="bean:someBean?method=processEvent"/&gt;
 *        &lt;/camel:route&gt;
 *    &lt;/camel:camelContext&gt;
 *
 *     &lt;bean id="eventMulticastor" class="org.yestech.event.CamelEventMulticaster" p:defaultContext-ref="context"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 * <br/>
 * If the caller would like to get the Raw camel {@link Exchange} as the result the event must contain the {@link EventResultType}
 * with a class of {@link Exchange}.
 *
 * @param <EVENT> An implementation of ICamelEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
@SuppressWarnings({"unchecked"})
public class CamelEventMulticaster<EVENT extends ICamelEvent, RESULT> extends BaseEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(CamelEventMulticaster.class);
    private Map<String, CamelContext> contexts = new HashMap<String, CamelContext>();
    private CamelContext defaultContext;

    public Map<String, CamelContext> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, CamelContext> contexts) {
        this.contexts = contexts;
    }

    public CamelContext getDefaultContext() {
        return defaultContext;
    }

    public void setDefaultContext(CamelContext defaultContext) {
        this.defaultContext = defaultContext;
    }

    @Override
    public RESULT process(final EVENT event) {
        CamelContext context = defaultContext;
        if (context == null) {
            context = contexts.get(event.getEventName());
        }
        if (context == null) {
            throw new RuntimeException("context not found....");
        }
        final DefaultProducerTemplate template = (DefaultProducerTemplate) context.createProducerTemplate();
        Object result = null;
        if (StringUtils.isNotBlank(event.getDefaultEndPointUri())) {
            Endpoint endpoint = context.getEndpoint(event.getDefaultEndPointUri());
            template.setDefaultEndpoint(endpoint);
            Exchange exchange = endpoint.createExchange();
            Message message = new DefaultMessage();
            message.setBody(event);
            exchange.setIn(message);
            exchange = template.send(exchange);
            if (wantsExchange(event)) {
                result = exchange;
            } else {
                if (exchange.hasOut()) {
                    result = exchange.getOut().getBody();
                } else {
                    result = exchange.getIn().getBody();
                }
            }
        } else {
            throw new RuntimeException("need to set a defaultEndPointUri");
        }

        validate(event, result);

        return (RESULT) result;
    }

    private boolean wantsExchange(EVENT event) {
        EventResultType resultType = EventUtils.getResultType(event);
        return (resultType != null && resultType.getClass().isAssignableFrom(Exchange.class)) ? true : false;
    }
}
