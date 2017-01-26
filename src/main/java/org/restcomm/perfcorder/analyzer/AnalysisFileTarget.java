package org.restcomm.perfcorder.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AnalysisFileTarget {
    private String path;
    private char separator;
    private int linesToSkip;
    
    private String category = "";
    
    private List<CSVColumnMeasTarget> csvTargets = new ArrayList();
    private List<PerRowFileMeasTarget> perRowTargets = new ArrayList();    

    public AnalysisFileTarget() {
    }

    
    
    public AnalysisFileTarget(String path, char separator, int linesToSkip) {
        this.path = path;
        this.separator = separator;
        this.linesToSkip = linesToSkip;
    }

    public String getPath() {
        return path;
    }

    public char getSeparator() {
        return separator;
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

    public void addCSVTarget(CSVColumnMeasTarget target) {
        csvTargets.add(target);
    }

    public List<CSVColumnMeasTarget> getCsvTargets() {
        return csvTargets;
    }

    public void setCsvTargets(List<CSVColumnMeasTarget> csvTargets) {
        this.csvTargets = csvTargets;
    }

    public List<PerRowFileMeasTarget> getPerRowTargets() {
        return perRowTargets;
    }

    public void setPerRowTargets(List<PerRowFileMeasTarget> perRowTargets) {
        this.perRowTargets = perRowTargets;
    }
    
    public void addPerRowTarget(PerRowFileMeasTarget target) {
        perRowTargets.add(target);
    }    

    public void setPath(String path) {
        this.path = path;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLinesToSkip() {
        return linesToSkip;
    }

    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    
    
}
