package org.yestech.event;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Last Modified Date: $DateTime: $
 *
 * @author Greg Crow
 * @version $Revision: $
 */
public class AggregateResultReferenceUnitTest {
    protected enum KeyEnum {
        ONE,TWO
    }

    @Test
    public void testAddGetContainsTotal() {
        AggregateResultReference aggResultRef = new AggregateResultReference();
        ResultReference<String> ref1 = new ResultReference<String>();
        ref1.setResult("testing1");

        aggResultRef.addResult(KeyEnum.ONE, ref1);
        assertEquals(ref1.getResult(), aggResultRef.getResult(KeyEnum.ONE));

        assertTrue(aggResultRef.containsResult(KeyEnum.ONE));
        assertFalse(aggResultRef.containsResult(KeyEnum.TWO));
        assertEquals(1,aggResultRef.getTotal());
    }
}
