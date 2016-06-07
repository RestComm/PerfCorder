package org.restcomm.perfcorder.collector;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;

/**
 * PerfCorder collects info about an external process through JMX beans
 *
 *
 */
public class GCPausePrinter {

    private static Logger logger = org.apache.log4j.Logger.getLogger(GCPausePrinter.class.getName());

    private static OptionParser createOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList(new String[]{"help", "?", "h"}),
                "shows this help").forHelp();
        parser
                .acceptsAll(Arrays.asList(new String[]{"n", "iteration"}),
                        "perfcorder will exit after n output iterations").withRequiredArg()
                .ofType(Integer.class);
        parser
                .acceptsAll(Arrays.asList(new String[]{"d", "delay"}),
                        "delay between each output iteration").withRequiredArg()
                .ofType(Integer.class);
        parser
                .acceptsAll(Arrays.asList(new String[]{"p", "pid"}),
                        "PID to connect to").withRequiredArg().ofType(Integer.class);

        return parser;
    }
    
    //using this as opposed to byte version to have JConsole compatible data
    private static final int BYTES_PER_MEGA = 1000000;

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
                for (Entry<String, MemoryUsage> entry : mem.entrySet()) {
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
    
    private static void printHeaderLine() {
        System.out.println("Dur,MemBefore,MemAfter,gcType,gcId,gcName,gcCause,startTime, endTime");
    }

    private static volatile boolean shutdown = false;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n"), "System.err"));
        logger.setLevel(org.apache.log4j.Level.INFO);
        logger = Logger.getLogger("perfcorder");

        OptionParser parser = createOptionParser();
        OptionSet a = parser.parse(args);

        if (a.has("help")) {
            System.out.println("perfcorder - java monitoring for the command-line");
            System.out.println("Usage: perfcorder.sh [PID]");
            System.out.println("");
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        int delay = 1;

        Integer iterations = Integer.MAX_VALUE;

        if (a.hasArgument("delay")) {
            delay = (Integer) (a.valueOf("delay"));
            if (delay < 1) {
                throw new IllegalArgumentException("Delay cannot be set below 1");
            }
        }

        if (a.hasArgument("n")) {
            iterations = (Integer) a.valueOf("n");
        }

        Integer pid = null;

        //to support PID as non option argument
        if (a.nonOptionArguments().size() > 0) {
            pid = Integer.valueOf((String) a.nonOptionArguments().get(0));
        }

        if (a.hasArgument("pid")) {
            pid = (Integer) a.valueOf("pid");
        }

        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(pid);
        VMInfo vmInfo_ = VMInfo.processNewVM(localVirtualMachine, pid);
        Collection<GarbageCollectorMXBean> gcbeans = vmInfo_.getGcMXBeans();
        printHeaderLine();
        //Install a notifcation handler for each bean
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = new GCListener();
            emitter.addNotificationListener(listener, null, null);
        }

        //register a shutdown hook so process is stopped gracefully
        Runtime.getRuntime().addShutdownHook(new Thread("StopPrinterHook") {
            @Override
            public void run() {
                shutdown = true;
            }
        });

        int currentIt = 0;
        while (!shutdown && currentIt < iterations) {
            Thread.sleep(delay * 1000);
            currentIt = currentIt + 1;
        }
    }

    public GCPausePrinter() {
    }

}
