/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.event;

/**
 */
public abstract class BaseCamelEvent<TYPE> extends BaseEvent<TYPE> implements ICamelEvent {
    private String defaultEndPointUri;

    protected BaseCamelEvent() {
    }

    protected BaseCamelEvent(TYPE type) {
        super(type);
    }

    @Override
    public String getDefaultEndPointUri() {
        return defaultEndPointUri;
    }

    @Override
    public void setDefaultEndPointUri(String uri) {
        this.defaultEndPointUri = uri;
    }
}
