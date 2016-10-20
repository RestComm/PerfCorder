package org.restcomm.perfcorder.analyzer;


public final class CSVColumnMeasTarget extends AnalysisMeasTarget {
    static final int SEARCH_COL_INDX_BY_NAME = -1;
    private int column;
    private int auxTimestampColumn = -1;

    public CSVColumnMeasTarget() {
    }
    
    public CSVColumnMeasTarget(String label, int column) {
        super(label);
        this.column = column;
    }
    
    public CSVColumnMeasTarget(String label, int column,String pattern) {
        super(label,pattern);
        this.column = column;
    }    
    
    public CSVColumnMeasTarget(String label, int column, int auxCol) {
        super(label);
        this.column = column;
        auxTimestampColumn = auxCol;
    }    

    
    
    /**
     * 
     * @param label 
     */
    public CSVColumnMeasTarget(String label) {
        super(label);
        column = SEARCH_COL_INDX_BY_NAME;
    }    


    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getAuxTimestampColumn() {
        return auxTimestampColumn;
    }

    public void setAuxTimestampColumn(int auxTimestampColumn) {
        this.auxTimestampColumn = auxTimestampColumn;
    } 
    
}
