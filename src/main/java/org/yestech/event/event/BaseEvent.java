/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event.event;

import java.util.UUID;

/**
 */
public abstract class BaseEvent<TYPE> implements IEvent {
    private TYPE type;
    private String eventName = UUID.randomUUID().toString();

    protected BaseEvent() {
    }

    protected BaseEvent(TYPE type) {
        this();
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEvent)) return false;

        BaseEvent baseEvent = (BaseEvent) o;

        if (type != null ? !type.equals(baseEvent.type) : baseEvent.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
