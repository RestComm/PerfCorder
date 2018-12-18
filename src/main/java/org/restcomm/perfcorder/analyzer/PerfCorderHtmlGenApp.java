package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
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
public class PerfCorderHtmlGenApp {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PerfCorderHtmlGenApp.class.getName());

    private static void printInfo() {
        System.out.println("Usage: java -jar 'thisFile'");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n"), "System.err"));
        logger.setLevel(org.apache.log4j.Level.INFO);
        logger.info("HTML Tool starting ... ");
        try {

            PerfCorderHTMLViewGenerator htmlGen = new PerfCorderHTMLViewGenerator();
            StreamSource streamSource = new StreamSource(System.in);
            htmlGen.generateView(streamSource,  System.out);
            exit(0);
        } catch (IOException | TransformerException ex) {
            Logger.getLogger(PerfCorderHtmlGenApp.class.getName()).log(Level.SEVERE, null, ex);
            printInfo();
            exit(-1);
        }
    }

}
