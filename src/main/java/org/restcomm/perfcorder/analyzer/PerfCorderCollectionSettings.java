package org.restcomm.perfcorder.analyzer;


public class PerfCorderCollectionSettings {
    private int measIntervalSeconds = 4;
    private String confDir = "";
    private String outputDir = "";
    private String patternMode = "";
    private String javaPID = "";

    public int getMeasIntervalSeconds() {
        return measIntervalSeconds;
    }

    public void setMeasIntervalSeconds(int measIntervalSeconds) {
        this.measIntervalSeconds = measIntervalSeconds;
    }

    public String getConfDir() {
        return confDir;
    }

    public void setConfDir(String confDir) {
        this.confDir = confDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getPatternMode() {
        return patternMode;
    }

    public void setPatternMode(String patternMode) {
        this.patternMode = patternMode;
    }

    public String getJavaPID() {
        return javaPID;
    }

    public void setJavaPID(String javaPID) {
        this.javaPID = javaPID;
    }
    
    
    
}
