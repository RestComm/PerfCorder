package org.restcomm.perfcorder.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PerfCorderAnalysis {
    private long startTimeStamp;
    private long endTimeStamp;
    
    private Map<String, AnalysisMeasResults> measMap = new HashMap();

    public Map<String, AnalysisMeasResults> getMeasMap() {
        return measMap;
    }

    public void setMeasMap(Map<String, AnalysisMeasResults> measMap) {
        this.measMap = measMap;
    }
    
    
    
    public void addMeas(AnalysisMeasTarget target, AnalysisMeasResults stats) {
        measMap.put(target.getLabel(), stats);
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }
    
    
}
