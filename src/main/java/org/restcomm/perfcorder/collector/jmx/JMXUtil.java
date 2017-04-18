package org.restcomm.perfcorder.collector.jmx;

public class JMXUtil {

    public static int retrieveLocalPID() throws Exception {
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
}
