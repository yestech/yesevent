/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * A <a href="http://camel.apache.org">camel</a> based event multicaster implementation
 *
 * @param <EVENT> An implementation of ICamelEvent, The event type the multicaster will handle.
 * @param <RESULT> A serializable result that result type can handle.
 */
@Service("camelEventMulticaster")
@SuppressWarnings({"unchecked"})
public class CamelEventMulticaster<EVENT extends ICamelEvent, RESULT> implements IEventMulticaster<EVENT, RESULT> {

    private static final Logger logger = LoggerFactory.getLogger(CamelEventMulticaster.class);
    private ProducerTemplate template;
    private CamelContext context;

    public ProducerTemplate getTemplate() {
        return template;
    }

//    @Resource(name = "camelTemplate")
    public void setTemplate(ProducerTemplate template) {
        this.template = template;
    }

    public CamelContext getContext() {
        return context;
    }

//    @Resource(name = "camelContext")
    public void setContext(CamelContext context) {
        this.context = context;
    }

    @PreDestroy
    public void destroy() {
    }


    public RESULT process(final EVENT event) {
        ResultReference<RESULT> ref = new ResultReference<RESULT>();

        template.send(event.getDefaultEndPointUri(), event);
        loadResult(event, ref);
        Object result = ref.getResult();

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

    private void loadResult(EVENT event, ResultReference<RESULT> ref) {
        Message message = event.getOut();
        if (message == null || message.getBody() == null) {
            message = event.getIn();
            if (message != null) {
                RESULT result = (RESULT) message.getBody();
                ref.setResult(result);
            }
        }
    }

}