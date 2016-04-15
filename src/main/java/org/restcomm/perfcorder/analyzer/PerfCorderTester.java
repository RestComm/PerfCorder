package org.restcomm.perfcorder.analyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class PerfCorderTester {

    private final TransformerFactory factory;
    private final Templates testsuiteTmp;

    /**
     * includes from goal templates will be resolved using classLoader resources
     */
    class ClasspathResourceURIResolver implements URIResolver {

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            //resource must havbe leading /
            String resLoc = href;
            if (!href.startsWith("/"))
            {
                resLoc = "/" + href;
            }
            return new StreamSource(PerfCorderTester.class.getResourceAsStream(resLoc));
        }
    }

    public PerfCorderTester() throws TransformerConfigurationException {
        factory = TransformerFactory.newInstance();
        factory.setURIResolver(new ClasspathResourceURIResolver());
        InputStream testsuiteStream = PerfCorderTester.class.getResourceAsStream("/junitTestSuiteTemplate.xsl");
        Source testsuiteXslt = new StreamSource(testsuiteStream);
        testsuiteTmp = factory.newTemplates(testsuiteXslt);
    }

    /**
     * 
     * @param analysisSrc The xml with analysis of performance
     * @param goalStream contains XSLT template with perf goals
     * @param out generated  JUnit XML report with test results
     * @throws TransformerException
     * @throws IOException 
     */
    public void testAnalysis(StreamSource analysisSrc, InputStream goalStream, OutputStream out) throws TransformerException, IOException {
        Transformer testsuiteTrans = testsuiteTmp.newTransformer();
        Source goalXslt = new StreamSource(goalStream);
        Transformer goalTransformer = factory.newTransformer(goalXslt);
        ByteArrayOutputStream oStream = new ByteArrayOutputStream(512);
        goalTransformer.transform(analysisSrc, new StreamResult(oStream));
        byte[] toByteArray = oStream.toByteArray();
        InputStream iStream = new ByteArrayInputStream(toByteArray);
        Source testCaseSource = new StreamSource(iStream);
        testsuiteTrans.transform(testCaseSource, new StreamResult(out));

    }
}
