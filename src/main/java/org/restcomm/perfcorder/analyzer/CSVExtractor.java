package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.mobicents.qa.report.sipp.OpenCsvReader;

public class CSVExtractor {

    /**
     *
     * @param file
     * @return csv file in the form of a list of String array.
     * @throws IOException
     */
    public static List<String[]> extractFile(DataFile dFile, AnalysisFileTarget file) throws IOException {
        InputStream in = dFile.getContent();
        if (in != null) {
            InputStreamReader reader = new InputStreamReader(in);
            int stripWithHeader = 0;
            if (file.isHeaderIncluded()) {
                stripWithHeader = stripWithHeader + 1;
            }
            OpenCsvReader csvReader = new OpenCsvReader(reader, file.getSeparator(), '"', stripWithHeader);
            List<String[]> readAll = csvReader.readAll();
            return readAll;
        } else {
            //log warning
            return null;
        }
    }
}
