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
 * @see org.yestech.event.EventResultType
 * @see org.yestech.event.IEvent
 */
public class InvalidResultException extends RuntimeException
{

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