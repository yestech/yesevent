package org.yestech.event.event;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 *
 */

public class EventFactoryUnitTest {
    @Before
    public void setUp() {
        // Add your code here
    }

    @Test
    public void testCreate() {
        ITestEvent event = EventFactory.create(ITestEvent.class);
        event.setName("Name");
        event.setValid(true);

        assertEquals("Name", event.getName());
        assertTrue(event.isValid());

    }
}
