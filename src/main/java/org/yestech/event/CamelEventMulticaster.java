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
import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.direct.DirectEndpoint;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.DefaultProducerTemplate;

/**
 * A <a href="http://camel.apache.org">camel</a> based event multicaster implementation
 *
 * @param <EVENT> An implementation of ICamelEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
@SuppressWarnings({"unchecked"})
public class CamelEventMulticaster<EVENT extends ICamelEvent, RESULT> implements IEventMulticaster<EVENT, RESULT> {

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

    @PreDestroy
    public void destroy() {
    }

    @PostConstruct
    public void init() {
    }
    
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
            if (exchange.hasOut()) {
                result = exchange.getOut().getBody();
            } else {
                result = exchange.getIn().getBody();
            }
        } else {
            throw new RuntimeException("need to set a defaultEndPointUri");
        }
        if (result != null && event.getClass().isAnnotationPresent(EventResultType.class)) {
            EventResultType resultType = event.getClass().getAnnotation(EventResultType.class);

            if (resultType.value() != null) {
                if (!resultType.value().isAssignableFrom(result.getClass())) {
                    String msg = String.format("%s Requires that a type of %s was returned",
                            event.getClass().getSimpleName(),
                            resultType.getClass().getSimpleName());
                    logger.error(msg);
                    throw new InvalidResultException(msg);
                }
            }
        }

        return (RESULT) result;
    }
}