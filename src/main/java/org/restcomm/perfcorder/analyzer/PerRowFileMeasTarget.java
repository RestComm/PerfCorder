package org.restcomm.perfcorder.analyzer;


public final class PerRowFileMeasTarget extends AnalysisMeasTarget {
    private int labelColumn;
    private int countColumn;
    private int sumColumn;

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


   
    
}
