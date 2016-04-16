package org.restcomm.perfcorder.analyzer;

import java.util.Objects;


public final class AnalysisMeasTarget {
    private final String label;
    private final int column;

    public AnalysisMeasTarget(String label, int column) {
        this.label = label;
        this.column = column;
    }

    public String getLabel() {
        return label;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.label);
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
        final AnalysisMeasTarget other = (AnalysisMeasTarget) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    

    public static final double INVALID_STRING = -1;
    
    public double transformIntoDouble(String value) {
        //remove percent char
        String transStr = value.replaceAll("%", "");
        if (transStr.isEmpty()) {
            return INVALID_STRING;
        } else {
            return Double.valueOf(transStr);
        }
    }
    
    
}
