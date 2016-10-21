package org.restcomm.perfcorder.analyzer;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PerfCorderAnalysis {
    
    private long startTimeStamp;
    private long endTimeStamp;
    private PerfCorderCollectionSettings settings;
    private int samplesToStripRatio;
    
    private Map<String, AnalysisMeasResults> measMap = new HashMap();

    public PerfCorderAnalysis() {
    }

    
    
    public PerfCorderAnalysis(long startTimeStamp, long endTimeStamp, PerfCorderCollectionSettings settings, int samplesToStripRatio) {
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.settings = settings;
        this.samplesToStripRatio = samplesToStripRatio;
    }
    
    
    
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

    public PerfCorderCollectionSettings getSettings() {
        return settings;
    }

    public void setSettings(PerfCorderCollectionSettings settings) {
        this.settings = settings;
    }

    public int getSamplesToStripRatio() {
        return samplesToStripRatio;
    }

    public void setSamplesToStripRatio(int samplesToStripRatio) {
        this.samplesToStripRatio = samplesToStripRatio;
    }
    
    
}
