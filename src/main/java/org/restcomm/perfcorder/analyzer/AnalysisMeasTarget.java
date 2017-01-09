package org.restcomm.perfcorder.analyzer;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalysisMeasTarget {

    private String label;

    private String converterClass = "org.restcomm.perfcorder.analyzer.DefaultConverter";
    private transient ColumnConverter converter = new DefaultConverter();

    public AnalysisMeasTarget() {
    }

    public AnalysisMeasTarget(String label) {
        this.label = label;
    }
    
    public AnalysisMeasTarget(String label, String converterClass) {
        this.label = label;
        this.converterClass = converterClass;
        try {
            converter = (ColumnConverter) Class.forName(converterClass).newInstance();
        } catch (Exception ex) {
            Logger.getLogger(AnalysisMeasTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        return converter.convert(value);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getConverterClass() {
        return converterClass;
    }

    public void setConverterClass(String converterClass) {
        this.converterClass = converterClass;
        try {
            converter = (ColumnConverter) Class.forName(converterClass).newInstance();
        } catch (Exception ex) {
            Logger.getLogger(AnalysisMeasTarget.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }



}
