package org.restcomm.perfcorder.collector;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

public class JMX2CSVViewTest {

    public JMX2CSVViewTest() {
    }

    private int retrieveLocalPID() throws Exception {
        java.lang.management.RuntimeMXBean runtime
                = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt
                = (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method
                = mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        return (Integer) pid_method.invoke(mgmt);

    }

    @Test
    public void testSomeMethod() throws Exception {
        JMX2CSVDescriptor desc = new JMX2CSVDescriptor();
        desc.setAbsolute(true);
        desc.setObjectName("java.lang:type=Threading");
        desc.setAttributes(Arrays.asList("CurrentThreadCpuTime", "CurrentThreadUserTime"));
        JMXOperation jmxOperation = new JMXOperation();
        jmxOperation.setName("getThreadAllocatedBytes");
        jmxOperation.setArguments(Arrays.asList("1"));   
        //desc.setOperations(Arrays.asList(jmxOperation));
        JMX2CSVView view = new JMX2CSVView(retrieveLocalPID(), desc);
        String printView = view.printView();
        Assert.assertNotNull(printView);
        String header = view.printHeader();        
        Assert.assertTrue(header.contains("CurrentThreadCpuTime"));
    }

}
