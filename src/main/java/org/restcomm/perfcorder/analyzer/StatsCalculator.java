package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatsCalculator implements FileAnalyser<CSVColumnMeasTarget> {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PerfCorderAnalyzeApp.class.getName());

    private void autogenerateTargets(List<String[]> readAll,AnalysisFileTarget fileTarget) {
        if (fileTarget.getCsvTargets().isEmpty()) {
            List<CSVColumnMeasTarget> targets = new ArrayList();
            
            String[] colNames = readAll.get(0);
            for(int i = 0 ; i < colNames.length ; i++) {
                CSVColumnMeasTarget target = new CSVColumnMeasTarget(fileTarget.getCategory() + "-" + colNames[i], i);
                targets.add(target);
            }
            fileTarget.setCsvTargets(targets);
        }
    }
    
    @Override
    public Map<AnalysisMeasTarget, AnalysisMeasResults> analyzeTarget(List<String[]> readAll,AnalysisFileTarget fileTarget,  PerfCorderAnalysis analysis) throws IOException {
        Map<AnalysisMeasTarget, AnalysisMeasResults> measMap = new HashMap();
        Map<CSVColumnMeasTarget, DescriptiveStatistics> statsMap = new HashMap();
        
        autogenerateTargets(readAll, fileTarget);
        
        //init empty stats to add values later
        for (CSVColumnMeasTarget target : fileTarget.getCsvTargets()) {
            statsMap.put(target, new DescriptiveStatistics());
            if (target.getColumn() == CSVColumnMeasTarget.SEARCH_COL_INDX_BY_NAME) {
                String[] colNames = readAll.get(0);
                List<String> asList = Arrays.asList(colNames);
                int indexOf = -1;
                if (target.getColumnName().isEmpty()) {
                    indexOf = asList.indexOf(target.getLabel());
                } else {
                    indexOf = asList.indexOf(target.getColumnName());
                }
                LOGGER.info("Index found:"+ indexOf + ", for column:" + target.getLabel());
                target.setColumn(indexOf);
            }            
        }

        int thresholdRows = (readAll.size() * analysis.getSamplesToStripRatio()) / 100;
        int lastRowRow = readAll.size() - thresholdRows;

        for (int i = thresholdRows; i < lastRowRow; i++) {
            String[] readNext = readAll.get(i);
            for (int j = 0; j < fileTarget.getCsvTargets().size(); j++) {
                CSVColumnMeasTarget target = fileTarget.getCsvTargets().get(j);
                int column = target.getColumn();
                if (column < readNext.length && column >= 0) {
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
            measResults.setCategory(fileTarget.getCategory());
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
