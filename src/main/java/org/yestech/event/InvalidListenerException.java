package org.yestech.event;

/**
 *
 *
 */
public class InvalidListenerException extends RuntimeException
{

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
