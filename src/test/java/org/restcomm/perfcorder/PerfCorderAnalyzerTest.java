/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.restcomm.perfcorder;

import org.restcomm.perfcorder.analyzer.PerfCorderAnalyzer;
import org.restcomm.perfcorder.analyzer.PerfCorderAnalysis;
import org.restcomm.perfcorder.analyzer.PerfCorderAnalyzeApp;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jim
 */
public class PerfCorderAnalyzerTest {

    public PerfCorderAnalyzerTest() {
    }

    @Test
    public void testAnalyze() throws IOException, JAXBException {
        InputStream resourceAsStream = PerfCorderAnalyzeApp.class.getResourceAsStream("/perfTest-0655-TSCleanMsg-OutNot-70CAPS.zip");
        PerfCorderAnalyzer analyzer = new PerfCorderAnalyzer(resourceAsStream, 0);
        PerfCorderAnalysis analysis = analyzer.analyze();
        Assert.assertNotNull(analysis);
        JAXBContext jaxbContext = JAXBContext.newInstance(PerfCorderAnalysis.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.marshal(analysis, System.out);
    }

}
