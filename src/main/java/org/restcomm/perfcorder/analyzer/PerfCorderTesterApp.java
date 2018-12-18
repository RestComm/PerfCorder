package org.restcomm.perfcorder.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.exit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/**
 * Analyze input PerfCorder file against goals, and produces JUnit XML in std output
 */
public class PerfCorderTesterApp {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PerfCorderTesterApp.class.getName());

    private static void printInfo() {
        System.out.println("Usage: java -jar 'thisFile' goalfile");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n"), "System.err"));
        logger.setLevel(org.apache.log4j.Level.INFO);
        logger.info("Testing Tool starting ... ");
        if (args.length <= 0 || args.length > 2) {
            printInfo();
            exit(-1);
        }
        try {
            InputStream iStream = null;
            iStream = new FileInputStream(args[0]);

         
            PerfCorderTester tester = new PerfCorderTester();
            StreamSource streamSource = new StreamSource(System.in);
            tester.testAnalysis(streamSource, iStream, System.out);
            exit(0);
        } catch (IOException | TransformerException ex) {
            Logger.getLogger(PerfCorderTesterApp.class.getName()).log(Level.SEVERE, null, ex);
            printInfo();
            exit(-1);
        }
    }

}
