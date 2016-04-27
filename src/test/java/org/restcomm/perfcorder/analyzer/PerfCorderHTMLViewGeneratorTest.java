package org.restcomm.perfcorder.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
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
public class PerfCorderHTMLViewGeneratorTest {
    
    public PerfCorderHTMLViewGeneratorTest() {
    }

    @Test
    public void testSomeMethod() throws TransformerConfigurationException, TransformerException, IOException {
        InputStream resourceAsStream = PerfCorderAnalyzeApp.class.getResourceAsStream("/analysis.xml");
        StreamSource streamSource = new StreamSource(resourceAsStream);
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(51200);        
        PerfCorderHTMLViewGenerator htmlGen = new PerfCorderHTMLViewGenerator(); 
        htmlGen.generateView(streamSource, oStream);
        byte[] toByteArray = oStream.toByteArray();
        Assert.assertTrue(toByteArray.length > 0);
        String result = new String(toByteArray);
        System.out.println(result);        
    }
    
}
