/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */

package org.yestech.event;

import java.io.Serializable;

/**
 * Utility to allow an object to be set back in the result
 *
 * @param <T> The type of result
 */
public class ResultReference<T> implements Serializable
{

    protected T result;

    public T getResult()
    {
        return result;
    }

    public void setResult(T result)
    {
        this.result = result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ResultReference)) return false;

        ResultReference that = (ResultReference) o;

        //noinspection RedundantIfStatement
        if (result != null ? !result.equals(that.result) : that.result != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return result != null ? result.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("ResultReference");
        sb.append("{result=").append(result);
        sb.append('}');
        return sb.toString();
    }
}
