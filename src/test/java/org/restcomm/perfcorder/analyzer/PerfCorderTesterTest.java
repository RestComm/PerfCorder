/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.restcomm.perfcorder.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jim
 */
public class PerfCorderTesterTest {
    
    public PerfCorderTesterTest() {
    }

    @Test
    public void testTester() throws TransformerConfigurationException, TransformerException, IOException {
        InputStream analysisStream = PerfCorderTester.class.getResourceAsStream("/analysis.xml");
        InputStream goalStream = PerfCorderTester.class.getResourceAsStream("/PerfGoals.xsl");
        
        PerfCorderTester tester = new PerfCorderTester();
        StreamSource streamSource = new StreamSource(analysisStream);
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(512);
        tester.testAnalysis(streamSource, goalStream, oStream);
        byte[] toByteArray = oStream.toByteArray();
        Assert.assertTrue(toByteArray.length > 0);
        String result = new String(toByteArray);
        System.out.println(result);
    }
    
}
