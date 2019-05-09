/*
 */
package org.restcomm.perfcorder.collector;

import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.restcomm.perfcorder.collector.jmx.JMXUtil;

/**
 *
 * @author jimmy
 */
public class VMDetailStatViewTest {

    public VMDetailStatViewTest() {
    }

    @Test
    public void testSimplePrint() throws Exception {
        VMDetailStatView view = new VMDetailStatView(JMXUtil.retrieveLocalPID(), null);
        String printView = view.printView();
        Assert.assertNotNull(printView);
    }

}
