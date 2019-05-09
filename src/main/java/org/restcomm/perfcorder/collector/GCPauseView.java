/*
 */
package org.restcomm.perfcorder.collector;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import org.apache.log4j.Logger;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

public class GCPauseView extends AbstractConsoleView {

    private static Logger logger = org.apache.log4j.Logger.getLogger(GCPauseView.class.getName());

    //using this as opposed to byte version to have JConsole compatible data
    private static final int BYTES_PER_MEGA = 1000000;

    private final VMInfo vmInfo;
    private GarbageCollectionNotificationInfo info;
    private long memUsedBefore = 0;
    private long memUsedAfter = 0;
    private long oldMemUsedAfter = 0;
    private String gctype = "";
    private long duration = 0;
    private StateListener sListener;

    class GCListener implements NotificationListener {
        //implement the notifier callback handler

        @Override
        public void handleNotification(Notification notification, Object handback) {
            //we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                //get the information associated with this notification
                info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                //get all the info and pretty print it
                duration = info.getGcInfo().getDuration();
                gctype = info.getGcAction();
                if ("end of minor GC".equals(gctype)) {
                    gctype = "Young";
                } else if ("end of major GC".equals(gctype)) {
                    gctype = "Old";
                }

                //Get the information about each memory space, and calculate
                //total
                Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
                Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
                memUsedBefore = 0;
                memUsedAfter = 0;
                oldMemUsedAfter = 0;
                for (Map.Entry<String, MemoryUsage> entry : mem.entrySet()) {
                    String name = entry.getKey();
                    MemoryUsage memdetail = entry.getValue();
                    MemoryUsage before = membefore.get(name);
                    memUsedBefore = memUsedBefore + before.getUsed();
                    memUsedAfter = memUsedAfter + memdetail.getUsed();
                    if (name.equals("PS Old Gen")) {
                        oldMemUsedAfter = memdetail.getUsed();
                    }
                }

                //convert mem from bytes into M
                memUsedBefore = memUsedBefore / BYTES_PER_MEGA;
                memUsedAfter = memUsedAfter / BYTES_PER_MEGA;
                oldMemUsedAfter = oldMemUsedAfter / BYTES_PER_MEGA;

                String newLine = String.format("%d,%d,%d,%s,%d,%s,%s,%d,%d,%d", duration,
                        memUsedBefore,
                        memUsedAfter,
                        gctype,
                        info.getGcInfo().getId(),
                        info.getGcName(),
                        info.getGcCause(),
                        info.getGcInfo().getStartTime(),
                        info.getGcInfo().getEndTime(),
                        oldMemUsedAfter);
                System.out.println(newLine);
            }
        }
    }

    private void createListener() {
        sListener = new StateListener() {
            public void stateChanged(VMInfo vmInfo_, VMInfoState newState) {
                if (newState.equals(VMInfoState.ATTACHED)) {
                    Collection<GarbageCollectorMXBean> gcbeans = vmInfo_.getGcMXBeans();
                    logger.info("Registering GC notifications again");
                    if (gcbeans != null) {
                        //Install a notifcation handler for each bean
                        for (GarbageCollectorMXBean gcbean : gcbeans) {
                            NotificationEmitter emitter = (NotificationEmitter) gcbean;
                            NotificationListener listener = new GCListener();
                            emitter.addNotificationListener(listener, null, null);
                        }
                    }
                }
            }
        };
    }

    public GCPauseView(int vmid, Integer width) throws Exception {
        super(width);
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(vmid);
        vmInfo = VMInfo.processNewVM(localVirtualMachine, vmid);
        createListener();
        vmInfo.addListener(sListener);
    }

    public GCPauseView(String url, Integer width) throws Exception {
        super(width);
        ProxyClient proxyClient = ProxyClient.getProxyClient(url,
                System.getenv("PERF_USER"),
                System.getenv("PERF_PSW"));
        proxyClient.connect();
        vmInfo = new VMInfo(proxyClient, null, null);
        createListener();
        vmInfo.addListener(sListener);
    }

    @Override
    public String printView() throws Exception {
        String line = "";
        if (info != null) {
            line = String.format("%d,%d,%d,%s,%d,%s,%s,%d,%d,%d", duration,
                    memUsedBefore,
                    memUsedAfter,
                    gctype,
                    info.getGcInfo().getId(),
                    info.getGcName(),
                    info.getGcCause(),
                    info.getGcInfo().getStartTime(),
                    info.getGcInfo().getEndTime(),
                    oldMemUsedAfter);
        }
        return line;
    }

    @Override
    public String printHeader() throws Exception {
        return "Dur,MemBefore,MemAfter,gcType,gcId,gcName,gcCause,startTime, endTime, OldMemAfter";
    }

    public VMInfo getVmInfo() {
        return vmInfo;
    }

    public GarbageCollectionNotificationInfo getInfo() {
        return info;
    }

}
