package org.restcomm.perfcorder.analyzer;


public final class PerRowFileMeasTarget extends AnalysisMeasTarget {
    private int labelColumn;
    private int countColumn;
    private int sumColumn;

    public PerRowFileMeasTarget(String label) {
        super(label);
    }

    public PerRowFileMeasTarget() {
    }

    
    
    
    public PerRowFileMeasTarget(String label, int labelColumn, int sumColumn, int countColumn ) {
        super(label);
        this.labelColumn = labelColumn;
        this.countColumn = countColumn;
        this.sumColumn = sumColumn;
    }

    public int getLabelColumn() {
        return labelColumn;
    }

    public int getCountColumn() {
        return countColumn;
    }

    public int getSumColumn() {
        return sumColumn;
    }

    public void setLabelColumn(int labelColumn) {
        this.labelColumn = labelColumn;
    }

    public void setCountColumn(int countColumn) {
        this.countColumn = countColumn;
    }

    public void setSumColumn(int sumColumn) {
        this.sumColumn = sumColumn;
    }
    
    


   
    
}
