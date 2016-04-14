package org.restcomm.perfcorder.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PerfCorderAnalysis {
    
    private Map<String, AnalysisMeasResults> measMap = new HashMap();
    private List graphs;

    public Map<String, AnalysisMeasResults> getMeasMap() {
        return measMap;
    }

    public void setMeasMap(Map<String, AnalysisMeasResults> measMap) {
        this.measMap = measMap;
    }
    
    
    
    public void addMeas(AnalysisMeasTarget target, AnalysisMeasResults stats) {
        measMap.put(target.getLabel(), stats);
    }
}
