package org.restcomm.perfcorder.analyzer;

import java.io.InputStream;


public final class DataFile {
    private final long timestamp;
    private final InputStream content;

    public DataFile(long timestamp, InputStream content) {
        this.timestamp = timestamp;
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public InputStream getContent() {
        return content;
    }
    
    
    
}
