package org.restcomm.perfcorder.collector;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;

@Startup
@Singleton
public class MeasCollectorEJB {
    //using this as opposed to byte version to have JConsole compatible data
    private static final int BYTES_PER_MEGA = 1000000;
    
    private int retrievePID() throws Exception {
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
    
    static class GCListener implements NotificationListener {
        //implement the notifier callback handler

        @Override
        public void handleNotification(Notification notification, Object handback) {
            //we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                //get the information associated with this notification
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                //get all the info and pretty print it
                long duration = info.getGcInfo().getDuration();
                String gctype = info.getGcAction();
                if ("end of minor GC".equals(gctype)) {
                    gctype = "Young";
                } else if ("end of major GC".equals(gctype)) {
                    gctype = "Old";
                }

                //Get the information about each memory space, and calculate
                //total
                Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
                Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
                long memUsedBefore = 0;
                long memUsedAfter = 0;
                for (Map.Entry<String, MemoryUsage> entry : mem.entrySet()) {
                    String name = entry.getKey();
                    MemoryUsage memdetail = entry.getValue();
                    MemoryUsage before = membefore.get(name);
                    memUsedBefore = memUsedBefore + before.getUsed();
                    memUsedAfter = memUsedAfter + memdetail.getUsed();
                }

                //convert mem from bytes into M
                memUsedBefore = memUsedBefore / BYTES_PER_MEGA;
                memUsedAfter = memUsedAfter / BYTES_PER_MEGA;

                String format = String.format("%d,%d,%d,%s,%d,%s,%s,%d, %d", duration,
                        memUsedBefore,
                        memUsedAfter,
                        gctype,
                        info.getGcInfo().getId(),
                        info.getGcName(),
                        info.getGcCause(),
                        info.getGcInfo().getStartTime(),
                        info.getGcInfo().getEndTime());
                System.out.println(format);
            }
        }
    }    

    @PostConstruct
    public void init() throws Exception {
        int currentPID = retrievePID();
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(currentPID);
        VMInfo vmInfo_ = VMInfo.processNewVM(localVirtualMachine, currentPID);
        Collection<GarbageCollectorMXBean> gcbeans = vmInfo_.getGcMXBeans();
        //Install a notifcation handler for each bean
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = new GCPausePrinter.GCListener();
            emitter.addNotificationListener(listener, null, null);
        }        

    }

    @Schedules({
        @Schedule(second = "30")
    })
    public void collect() {

    }
}
