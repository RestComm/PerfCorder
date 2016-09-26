package org.restcomm.perfcorder.analyzer;

import java.util.Objects;

public class AnalysisMeasTarget {

    private final String label;

    public AnalysisMeasTarget(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
            try {
                return Double.valueOf(transStr);
            } catch (NumberFormatException nExp) {
                return 0.0;
            }
        }
    }

}
