package org.restcomm.perfcorder.analyzer;

import static org.restcomm.perfcorder.analyzer.AnalysisMeasTarget.INVALID_STRING;

public class DefaultConverter implements ColumnConverter {

    public DefaultConverter() {
    }

    
    
    @Override
    public double convert(String value) {
        String transStr = value.replaceAll("%", "");
        if (transStr.isEmpty()) {
            return INVALID_STRING;
        } else {
            try {
                return Double.valueOf(transStr);
            } catch (Exception nExp) {
                return 0.0;
            }
        }
    }

}
