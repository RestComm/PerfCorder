/*
 */
package org.restcomm.perfcorder.collector;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.perfcorder.collector.jmx.JMXUtil;

/**
 *
 * @author jimmy
 */
public class GCPauseViewTest {

    public GCPauseViewTest() {
    }

    @Test
    public void testSimplePrint() throws Exception {
        GCPauseView view = new GCPauseView(JMXUtil.retrieveLocalPID(), null);
        VMInfo vmInfo = view.getVmInfo();
        //force a gc event
        vmInfo.getMemoryMXBean().gc();
        //wait for notification
        for (int i = 0; i < 10; i++) {
            if (view.getInfo() != null) {
                break;
            }
            Thread.sleep(100);
        }
        String printView = view.printView();
        Assert.assertNotSame("", printView);
    }
}
