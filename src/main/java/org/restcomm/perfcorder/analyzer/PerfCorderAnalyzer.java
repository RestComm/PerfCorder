package org.restcomm.perfcorder.analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.mobicents.qa.report.sipp.OpenCsvReader;

/**
 * Extracts zip file, and calculates statistics for the given targets.
 *
 *
 */
public final class PerfCorderAnalyzer {

    private final Map<String, InputStream> uncompressZip;
    private static final List<AnalysisFileTarget> FILES = new ArrayList();
    private static final Map<String, List<AnalysisMeasTarget>> TARGETS = new HashMap();

    /**
     * Nmber of lines to strip at the beginning and end of CSV file. These rows
     * will not be cover in the generated stats
     */
    private final int linesToStrip;

    static {
        FILES.add(new AnalysisFileTarget("data/periodic/java/jvmtop.txt", ' ', false));
        List<AnalysisMeasTarget> jvmTargets = new ArrayList<>();
        jvmTargets.add(new AnalysisMeasTarget("Mem", 3));
        jvmTargets.add(new AnalysisMeasTarget("Cpu", 5));
        TARGETS.put("data/periodic/java/jvmtop.txt", jvmTargets);

        FILES.add(new AnalysisFileTarget("data/periodic/sip/sipp.csv",';',true));
        List<AnalysisMeasTarget> sipTargets = new ArrayList<>();
        sipTargets.add(new AnalysisMeasTarget("SIPTotalCallCreated", 12));
        sipTargets.add(new AnalysisMeasTarget("SIPCurrentCall", 13));
        sipTargets.add(new AnalysisMeasTarget("SIPSuccessCalls", 14));
        sipTargets.add(new AnalysisMeasTarget("SIPFailedCalls", 16));
        sipTargets.add(new AnalysisMeasTarget("SIPRetransmissions", 44));
        TARGETS.put("data/periodic/sip/sipp.csv", sipTargets);
        
        FILES.add(new AnalysisFileTarget("data/periodic/sip/sipp_rtt.csv",';',true));
        List<AnalysisMeasTarget> sipResTargets = new ArrayList<>();
        sipResTargets.add(new AnalysisMeasTarget("SIPResponseTime", 2));
        TARGETS.put("data/periodic/sip/sipp_rtt.csv", sipResTargets);        

        FILES.add(new AnalysisFileTarget("data/periodic/java/jgcstat.txt", ',', false));
        List<AnalysisMeasTarget> jgcTargets = new ArrayList<>();
        jgcTargets.add(new AnalysisMeasTarget("GcPauseDuration", 0));
        jgcTargets.add(new AnalysisMeasTarget("GcMemBefore", 1));
        jgcTargets.add(new AnalysisMeasTarget("GcMemAfter", 2));

        TARGETS.put("data/periodic/java/jgcstat.txt", jgcTargets);

    }

    public PerfCorderAnalyzer(InputStream zipFile, int linesToStrip) throws FileNotFoundException, IOException {
        uncompressZip = ZipExtractor.uncompressZip(zipFile);
        this.linesToStrip = linesToStrip;
    }

    public PerfCorderAnalysis analyze() throws IOException {
        PerfCorderAnalysis perfCorderAnalysis = new PerfCorderAnalysis();
        for (AnalysisFileTarget file : FILES) {
            Map<AnalysisMeasTarget, DescriptiveStatistics> results = analyzeTarget(file, TARGETS.get(file.getPath()));
            for (AnalysisMeasTarget key : results.keySet()) {
                AnalysisMeasResults measResults = transformIntoResults(results.get(key));
                perfCorderAnalysis.addMeas(key, measResults);
            }
        }
        return perfCorderAnalysis;
    }

    private AnalysisMeasResults transformIntoResults(DescriptiveStatistics stats) {
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
        return results;
    }

    private Map<AnalysisMeasTarget, DescriptiveStatistics> analyzeTarget(AnalysisFileTarget file, List<AnalysisMeasTarget> targets) throws IOException {
        Map<AnalysisMeasTarget, DescriptiveStatistics> statsMap = new HashMap();
        InputStream in = uncompressZip.get(file.getPath());
        if (in != null) {
            InputStreamReader reader = new InputStreamReader(in);
            int stripWithHeader = linesToStrip;
            if (file.isHeaderIncluded()) {
                stripWithHeader = stripWithHeader + 1;
            }
            OpenCsvReader csvReader = new OpenCsvReader(reader, file.getSeparator(), '"' , stripWithHeader);
            for (AnalysisMeasTarget target : targets) {
                statsMap.put(target, new DescriptiveStatistics());
            }
            List<String[]> readAll = csvReader.readAll();

            for (int i = 0; i < readAll.size() - linesToStrip; i++) {
                String[] readNext = readAll.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    AnalysisMeasTarget target = targets.get(j);
                    int column = target.getColumn();
                    String nextCol = readNext[column];
                    double nexValue = target.transformIntoDouble(nextCol);
                    if (nexValue != AnalysisMeasTarget.INVALID_STRING) {
                        DescriptiveStatistics stats = statsMap.get(target);
                        stats.addValue(nexValue);
                    }
                }
            };
        }
        return statsMap;
    }
}
