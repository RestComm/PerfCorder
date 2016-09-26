package org.restcomm.perfcorder.analyzer;


public final class CSVColumnMeasTarget extends AnalysisMeasTarget {
    private final int column;

    public CSVColumnMeasTarget(String label, int column) {
        super(label);
        this.column = column;
    }


    public int getColumn() {
        return column;
    }
    
    
}
