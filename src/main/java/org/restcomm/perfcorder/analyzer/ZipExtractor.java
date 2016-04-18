package org.restcomm.perfcorder.analyzer;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ZipExtractor.class.getName());

    private static final int BUFFER = 100000;
    private static final int MAX_FILE_SIZE = 9000000;

    
    public static Map<String, DataFile> uncompressZip(InputStream inputStream) throws FileNotFoundException, IOException {
        Map<String, DataFile> zipEntries = new HashMap();
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getSize() < MAX_FILE_SIZE) {
                    InputStream iStream = extractIStream(zis, entry);
                    zipEntries.put(entry.getName(), new DataFile(entry.getTime(), iStream));
                } else {
                    logger.warn("Entry bigger than allowed:" + entry.getName());
                }
            }
        }

        return zipEntries;
    }

    private static InputStream extractIStream(ZipInputStream zipStream, ZipEntry entry) throws IOException {
        InputStream iStream = null;
        int count;
        if (entry.getSize() > 0 && entry.getSize() < MAX_FILE_SIZE) {
            byte data[] = new byte[BUFFER];

            // write the files to readers
            PipedInputStream in = new PipedInputStream(MAX_FILE_SIZE);
            try (PipedOutputStream out = new PipedOutputStream(in)) {
                while ((count = zipStream.read(data, 0, BUFFER))
                        != -1) {
                    out.write(data, 0, count);
                }
                out.flush();
            }
            iStream = in;
        } else {
            logger.warn("Entry bigger than allowed:" + entry.getName());
        }
        return iStream;
    }
}
