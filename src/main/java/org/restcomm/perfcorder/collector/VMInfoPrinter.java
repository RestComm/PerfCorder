package org.restcomm.perfcorder.collector;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.restcomm.perfcorder.collector.jmx.LocalVirtualMachine;



/**
 * PerfCorder collects info about an external process through JMX beans
 *
 *
 */
public class VMInfoPrinter
{

  private static Logger                              logger;

  private static OptionParser createOptionParser()
  {
    OptionParser parser = new OptionParser();
    parser.acceptsAll(Arrays.asList(new String[] { "help", "?", "h" }),
        "shows this help").forHelp();

    parser
        .acceptsAll(Arrays.asList(new String[] { "p", "pid" }),
            "PID to connect to").withRequiredArg().ofType(Integer.class);

    return parser;
  }

  public static void main(String[] args) throws Exception
  {
    Locale.setDefault(Locale.US);

    logger = Logger.getLogger("perfcorder");

    OptionParser parser = createOptionParser();
    OptionSet a = parser.parse(args);

    if (a.has("help"))
    {
      System.out.println("perfcorder - java monitoring for the command-line");
      System.out.println("Usage: perfcorder.sh [PID]");
      System.out.println("");
      parser.printHelpOn(System.out);
      System.exit(0);
    }

    Integer pid = null;

    //to support PID as non option argument
    if (a.nonOptionArguments().size() > 0)
    {
      pid = Integer.valueOf((String) a.nonOptionArguments().get(0));
    }

    if (a.hasArgument("pid"))
    {
      pid = (Integer) a.valueOf("pid");
    }
    
        LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
                .getLocalVirtualMachine(pid);
      VMInfo vmInfo_ = VMInfo.processNewVM(localVirtualMachine, pid);
      System.out.println (vmInfo_.getRuntimeMXBean().getBootClassPath());
      System.out.println (vmInfo_.getRuntimeMXBean().getClassPath());
      System.out.println (vmInfo_.getRuntimeMXBean().getInputArguments());
      System.out.println (vmInfo_.getRuntimeMXBean().getLibraryPath());
      System.out.println (vmInfo_.getRuntimeMXBean().getManagementSpecVersion());
      System.out.println (vmInfo_.getRuntimeMXBean().getName());
      System.out.println (vmInfo_.getRuntimeMXBean().getSpecName());
      System.out.println (vmInfo_.getRuntimeMXBean().getSpecVersion());
      System.out.println (vmInfo_.getRuntimeMXBean().getStartTime());
      System.out.println (vmInfo_.getRuntimeMXBean().getUptime());
      System.out.println (vmInfo_.getRuntimeMXBean().getVmName());
      System.out.println (vmInfo_.getRuntimeMXBean().getVmVendor());
      System.out.println (vmInfo_.getRuntimeMXBean().getVmVersion());
  }


  public VMInfoPrinter()
  {
  }


}
