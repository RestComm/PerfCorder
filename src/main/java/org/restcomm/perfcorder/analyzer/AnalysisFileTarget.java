package org.restcomm.perfcorder.analyzer;


public final class AnalysisFileTarget {
    private final String path;
    private final char separator;
    private final boolean headerIncluded;

    public AnalysisFileTarget(String path, char separator, boolean headerIncluded) {
        this.path = path;
        this.separator = separator;
        this.headerIncluded = headerIncluded;
    }

    public String getPath() {
        return path;
    }

    public char getSeparator() {
        return separator;
    }

    public boolean isHeaderIncluded() {
        return headerIncluded;
    }


    
    




    


    
    
}
