/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.restcomm.perfcorder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.restcomm.perfcorder.analyzer.PerfCorderAnalyzer;
import org.restcomm.perfcorder.analyzer.PerfCorderAnalysis;
import org.restcomm.perfcorder.analyzer.PerfCorderAnalyzeApp;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.restcomm.perfcorder.analyzer.PerfCorderHTMLViewGenerator;

/**
 *
 * @author jim
 */
public class PerfCorderAnalyzerTest {

    public PerfCorderAnalyzerTest() {
    }

    @Test
    public void testAnalyze() throws IOException, JAXBException, TransformerConfigurationException, TransformerException {
        InputStream resourceAsStream = PerfCorderAnalyzeApp.class.getResourceAsStream("/perfTest.zip");
        PerfCorderAnalyzer analyzer = new PerfCorderAnalyzer(resourceAsStream, 5);
        PerfCorderAnalysis analysis = analyzer.analyze();
        Assert.assertNotNull(analysis);
        Assert.assertNotNull(analysis.getMeasMap().get("Mem"));
        Assert.assertNotNull(analysis.getMeasMap().get("Cpu"));
        Assert.assertNotNull(analysis.getMeasMap().get("GcPauseDuration"));
        Assert.assertNotNull(analysis.getMeasMap().get("GcMemBefore"));
        Assert.assertNotNull(analysis.getMeasMap().get("GcMemAfter"));
        Assert.assertNotNull(analysis.getMeasMap().get("HTTPElapsed"));
        Assert.assertNotNull(analysis.getMeasMap().get("HTTPSampleCount"));
        Assert.assertNotNull(analysis.getMeasMap().get("HTTPErrorCount"));
        Assert.assertNotNull(analysis.getMeasMap().get("SIPTotalCallCreated"));
        Assert.assertNotNull(analysis.getMeasMap().get("ObjHistogram-org.mobicents.as7.Attribute"));

        //transform into xml
        JAXBContext jaxbContext = JAXBContext.newInstance(PerfCorderAnalysis.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(51200); 
        jaxbMarshaller.marshal(analysis, oStream);
        
        //Assert generated xml
        byte[] toByteArray = oStream.toByteArray();
        String result = new String(toByteArray);
        Assert.assertTrue(result.contains("Mem"));
        Assert.assertTrue(result.contains("HTTPElapsed"));
        Assert.assertTrue(result.contains("SIPTotalCallCreated"));
        Assert.assertTrue(result.contains("GcPauseDuration"));

        
        ByteArrayInputStream iStream = new ByteArrayInputStream(toByteArray);
        StreamSource streamSource2 = new StreamSource(iStream);
        PerfCorderHTMLViewGenerator htmlGen = new PerfCorderHTMLViewGenerator();
        ByteArrayOutputStream oStream2 = new ByteArrayOutputStream(51200); 
        htmlGen.generateView(streamSource2, oStream2);
        byte[] toByteArray2 = oStream2.toByteArray();        
        String result2 = new String(toByteArray2);
        
        
    }

}
