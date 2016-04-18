package org.restcomm.perfcorder.analyzer;

import java.util.Objects;


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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnalysisFileTarget other = (AnalysisFileTarget) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }
    
    


    
    




    


    
    
}
