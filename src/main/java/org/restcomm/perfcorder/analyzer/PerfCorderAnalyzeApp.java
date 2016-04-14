/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.restcomm.perfcorder.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.exit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/**
 * Analyze input PerfCorder file, and produces analysis XML in std output
 */
public class PerfCorderAnalyzeApp {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PerfCorderAnalyzeApp.class.getName());

    private static void printInfo() {
        System.out.println("Usage: java -jar 'thisFile' perfCorderFile|URL linesToStrip");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%c %-5p %m%n"), "System.err"));
        logger.setLevel(org.apache.log4j.Level.INFO);
        logger.info("Analyze Tool starting ... ");
        if (args.length <= 0 || args.length > 2) {
            printInfo();
            exit(-1);
        }
        try {
            InputStream iStream = null;
            try {
                URL url = new URL(args[0]);
                iStream = url.openStream();
            } catch (MalformedURLException mExp ) {
                logger.info("args0 is not URL, try with FS.");
            }
            if (iStream == null) {
                iStream = new FileInputStream(args[0]);
            }
            int linesToStrip = Integer.valueOf(args[1]);
            PerfCorderAnalyzer analyzer = new PerfCorderAnalyzer(iStream, linesToStrip);
            PerfCorderAnalysis analysis = analyzer.analyze();
            JAXBContext jaxbContext = JAXBContext.newInstance(PerfCorderAnalysis.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(analysis, System.out);
            exit(0);
        } catch (IOException | JAXBException | NumberFormatException ex) {
            Logger.getLogger(PerfCorderAnalyzeApp.class.getName()).log(Level.SEVERE, null, ex);
            printInfo();
            exit(-1);
        }
    }

}
