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
import org.restcomm.perfcorder.collector.jmx.ProxyClient;

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

    private static void printHeaderLine() {
        System.out.println("Dur,MemBefore,MemAfter,gcType,gcId,gcName,gcCause,startTime, endTime, OldMemAfter");
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

        String targetJVM = null;

        //to support PID as non option argument
        if (a.nonOptionArguments().size() > 0) {
            targetJVM = (String) a.nonOptionArguments().get(0);
        }

        if (a.hasArgument("pid")) {
            targetJVM = (String) a.valueOf("pid");
        }

        GCPauseView view = null;
        try {
            Integer pid = Integer.valueOf(targetJVM);
            view = new GCPauseView(pid, null);
        } catch (Exception e) {
            view = new GCPauseView(targetJVM, null);
        }

        System.out.println(view.printHeader());

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
            view.getVmInfo().update();
            currentIt = currentIt + 1;
        }
    }

    public GCPausePrinter() {
    }

}
