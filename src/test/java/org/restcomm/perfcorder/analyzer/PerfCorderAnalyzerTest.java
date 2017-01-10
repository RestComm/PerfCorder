/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.restcomm.perfcorder.analyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

/**
 *
 * @author jim
 */
public class PerfCorderAnalyzerTest {

    public PerfCorderAnalyzerTest() {
    }
    
    @Test
    public void testFilesXML() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AnalysisFileTargetSet.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(51200);
        jaxbMarshaller.marshal(DefaultTargetBuilder.build(), oStream);
        byte[] toByteArray2 = oStream.toByteArray();
        String result2 = new String(toByteArray2);
    }
    

    @Test
    public void testAnalyze() throws IOException, JAXBException, TransformerConfigurationException, TransformerException {
        InputStream resourceAsStream = PerfCorderAnalyzeApp.class.getResourceAsStream("/perfTest.zip");
        JAXBContext targetsContext = JAXBContext.newInstance(AnalysisFileTargetSet.class);
        InputStream targetsStream = PerfCorderAnalyzeApp.class.getResourceAsStream("/defaultFileTargets.xml");
        AnalysisFileTargetSet targetSet = (AnalysisFileTargetSet) targetsContext.createUnmarshaller().unmarshal(targetsStream);

        PerfCorderAnalyzer analyzer = new PerfCorderAnalyzer(resourceAsStream, 5, targetSet.getFiles());
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
        Assert.assertNotNull(analysis.getMeasMap().get("DiameterResponseTime"));
        Assert.assertNotNull(analysis.getMeasMap().get("JavaThreads"));
        
        
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
        Assert.assertTrue(result2.contains("HTTPElapsed"));
        Assert.assertTrue(result2.contains("SIPTotalCallCreated"));
        Assert.assertTrue(result2.contains("JavaThreads"));        
    }

}
