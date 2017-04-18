package org.restcomm.perfcorder.collector;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.perfcorder.collector.jmx.JMXUtil;

public class JMX2CSVViewTest {

    public JMX2CSVViewTest() {
    }



    @Test
    public void testSomeMethod() throws Exception {
        JMX2CSVDescriptor desc = new JMX2CSVDescriptor();
        desc.setObjectName("java.lang:type=Threading");
        JMXAttribute jmxAtt = new JMXAttribute();
        jmxAtt.setName("CurrentThreadCpuTime");
        JMXAttribute jmxAtt2 = new JMXAttribute();
        jmxAtt2.setName("TotalStartedThreadCount");
        jmxAtt2.setDelta(false);
        desc.setAttributes(Arrays.asList(jmxAtt,jmxAtt2));
        JMXOperation jmxOperation = new JMXOperation();
        jmxOperation.setName("getThreadCpuTime");
        jmxOperation.setArguments(Arrays.asList("1"));
        desc.setOperations(Arrays.asList(jmxOperation));
        JMX2CSVView view = new JMX2CSVView(JMXUtil.retrieveLocalPID(), desc);
        
        //invoke two times to force nonDelta calculation
        String printView = view.printView();
        printView = view.printView();
        Assert.assertNotNull(printView);
        String header = view.printHeader();
        Assert.assertTrue(header.contains("CurrentThreadCpuTime"));
    }

}
