package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatsCalculator implements FileAnalyser<CSVColumnMeasTarget> {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PerfCorderAnalyzeApp.class.getName());

    @Override
    public Map<AnalysisMeasTarget, AnalysisMeasResults> analyzeTarget(List<String[]> readAll, List<CSVColumnMeasTarget> targets, int linesToStripRatio, PerfCorderAnalysis analysis) throws IOException {
        Map<AnalysisMeasTarget, AnalysisMeasResults> measMap = new HashMap();
        Map<CSVColumnMeasTarget, DescriptiveStatistics> statsMap = new HashMap();
        //init empty stats to add values later
        for (CSVColumnMeasTarget target : targets) {
            statsMap.put(target, new DescriptiveStatistics());
        }

        int thresholdRows = (readAll.size() * linesToStripRatio) / 100;
        int lastRowRow = readAll.size() - thresholdRows;

        for (int i = thresholdRows; i < lastRowRow; i++) {
            String[] readNext = readAll.get(i);
            for (int j = 0; j < targets.size(); j++) {
                CSVColumnMeasTarget target = targets.get(j);
                int column = target.getColumn();
                if (column < readNext.length) {
                    String nextCol = readNext[column];
                    double nexValue = target.transformIntoDouble(nextCol);
                    if (nexValue != AnalysisMeasTarget.INVALID_STRING) {
                        DescriptiveStatistics stats = statsMap.get(target);
                        stats.addValue(nexValue);
                    }
                } else {
                    LOGGER.warn("Attempted invalid column:" + target.getLabel());
                }
            }
        }

        for (CSVColumnMeasTarget target : statsMap.keySet()) {
            String graph = GraphGenerator.generateGraph(target, readAll, analysis);
            AnalysisMeasResults measResults = transformIntoResults(statsMap.get(target), graph);
            if (measResults.getCount() > 0.0) {
                measMap.put(target, measResults);
            } else {
                LOGGER.warn("Excluding target(" + target.getLabel() + ") becuase no samples were found");
            }
        }
        return measMap;
    }

    private static AnalysisMeasResults transformIntoResults(DescriptiveStatistics stats, String graph) {
        AnalysisMeasResults results = new AnalysisMeasResults();
        results.setMax(stats.getMax());
        results.setMin(stats.getMin());
        results.setPercentile5(stats.getPercentile(5));
        results.setPercentile25(stats.getPercentile(25));
        results.setMedian(stats.getPercentile(50));
        results.setPercentile75(stats.getPercentile(75));
        results.setPercentile95(stats.getPercentile(95));
        results.setSum(stats.getSum());
        results.setSumSquares(stats.getSumsq());
        results.setMean(stats.getMean());
        results.setCount(stats.getN());
        results.setStdDev(stats.getStandardDeviation());
        results.setVariance(stats.getVariance());
        results.setKurtosis(stats.getKurtosis());
        results.setSkewness(stats.getSkewness());
        results.setGeometricMean(stats.getGeometricMean());
        results.setQuadraticMean(stats.getQuadraticMean());

        results.setGraph(graph);
        return results;
    }
}
