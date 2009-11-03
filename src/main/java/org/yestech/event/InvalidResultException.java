/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

/**
 * Thrown if the multicaster returns a different result then the event passed in requires.
 * 
 * @see org.yestech.event.annotation.EventResultType
 * @see org.yestech.event.event.IEvent
 */
public class InvalidResultException extends RuntimeException
{
    private static final long serialVersionUID = -6049911995211890956L;

    public InvalidResultException()
    {
    }

    public InvalidResultException(Throwable cause)
    {
        super(cause);
    }

    public InvalidResultException(String message)
    {
        super(message);
    }

    public InvalidResultException(String message, Throwable cause)
    {
        super(message, cause);
    }
}