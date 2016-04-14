package org.restcomm.perfcorder.analyzer;


public class AnalysisMeasResults {
    private double median;
    private double percentile5;
    private double percentile25;
    private double percentile75;
    private double percentile95;
    private double min;
    private double max;

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getPercentile5() {
        return percentile5;
    }

    public void setPercentile5(double percentile5) {
        this.percentile5 = percentile5;
    }

    public double getPercentile25() {
        return percentile25;
    }

    public void setPercentile25(double percentile25) {
        this.percentile25 = percentile25;
    }

    public double getPercentile75() {
        return percentile75;
    }

    public void setPercentile75(double percentile75) {
        this.percentile75 = percentile75;
    }

    public double getPercentile95() {
        return percentile95;
    }

    public void setPercentile95(double percentile95) {
        this.percentile95 = percentile95;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
    
    
    
}
