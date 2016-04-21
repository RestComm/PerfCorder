package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class PerfCorderHTMLViewGenerator {

    private final TransformerFactory factory;
    private final Templates htmlViewTmp;


    public PerfCorderHTMLViewGenerator() throws TransformerConfigurationException {
        factory = TransformerFactory.newInstance();
        InputStream testsuiteStream = PerfCorderHTMLViewGenerator.class.getResourceAsStream("/htmlTestCaseTemplate.xsl");
        Source testsuiteXslt = new StreamSource(testsuiteStream);
        htmlViewTmp = factory.newTemplates(testsuiteXslt);
    }

    /**
     * 
     * @param analysisSrc The xml with analysis of performance
     * @param out generated  HTML view
     * @throws TransformerException
     * @throws IOException 
     */
    public void generateView(StreamSource analysisSrc, OutputStream out) throws TransformerException, IOException {
        Transformer testsuiteTrans = htmlViewTmp.newTransformer();
        testsuiteTrans.transform(analysisSrc, new StreamResult(out));
    }
}
