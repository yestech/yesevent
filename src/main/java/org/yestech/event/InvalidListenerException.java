/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

/**
 *
 *
 */
public class InvalidListenerException extends RuntimeException
{
    private static final long serialVersionUID = 2430241584539319262L;

    public InvalidListenerException()
    {
    }

    public InvalidListenerException(Throwable cause)
    {
        super(cause);
    }

    public InvalidListenerException(String message)
    {
        super(message);
    }

    public InvalidListenerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
