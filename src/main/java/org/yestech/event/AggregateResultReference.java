/*
 * Copyright LGPL3
 * YES Technology Association
 * http://yestech.org
 *
 * http://www.opensource.org/licenses/lgpl-3.0.html
 */
package org.yestech.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents an Aggregation of {@link ResultReference}.
 *
 */
final public class AggregateResultReference implements Serializable {
    private Map<Enum<?>, ResultReference> references = new HashMap<Enum<?>, ResultReference>();

    /**
     * Adds a result to the aggregate.
     *
     * @param <T> Type contained in the result reference
     * @param token Token associated with the result
     * @param result The Result
     */
    public <T> void addResult(Enum<?> token, ResultReference<T> result) {
        references.put(token, result);
    }

    /**
     * Returns the Result contained in the associated {@link ResultReference}.
     *
     * @param <T> Type contained in the result reference
     * @param token Token associated with the result
     * @return The Result contained in the associated {@link ResultReference}.
     */
    public <T> T getResult(Enum<?> token) {
        final ResultReference<T> result = references.get(token);
        if (result == null) {
            return null;
        }
        return result.getResult();
    }

    /**
     * Checks if a result is available.
     *
     * @param token Token to lookup
     * @return true if available else false
     */
    public boolean containsResult(Enum<?> token) {
        return references.containsKey(token);
    }

    /**
     * Returns all the associated {@link ResultReference}.
     *
     * @return the results
     */
    public Iterator<ResultReference> getResults() {
        return references.values().iterator();
    }

    /**
     * Returns the number of results available.
     *
     * @return the number of results.
     */
    public int getTotal() {
        return references.size();
    }
}
