package org.restcomm.perfcorder.analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts zip file, and calculates statistics for the given targets.
 *
 *
 */
public final class PerfCorderAnalyzer {

    private final Map<String, DataFile> uncompressZip;
    private static final List<AnalysisFileTarget> FILES = new ArrayList();
    private static final Map<String, List<AnalysisMeasTarget>> TARGETS = new HashMap();

    /**
     * Percentage of lines to strip at the beginning and end of CSV file. These rows
     * will not be cover in the generated stats
     */
    private final int linesToStripRatio;

    static {
        FILES.add(new AnalysisFileTarget("data/periodic/java/jvmtop.txt", ',', true));
        List<AnalysisMeasTarget> jvmTargets = new ArrayList<>();
        jvmTargets.add(new AnalysisMeasTarget("Mem", 0));
        jvmTargets.add(new AnalysisMeasTarget("Cpu", 1));
        TARGETS.put("data/periodic/java/jvmtop.txt", jvmTargets);

        FILES.add(new AnalysisFileTarget("data/periodic/sip/sipp.csv", ';', true));
        List<AnalysisMeasTarget> sipTargets = new ArrayList<>();
        sipTargets.add(new AnalysisMeasTarget("SIPTotalCallCreated", 12));
        sipTargets.add(new AnalysisMeasTarget("SIPCurrentCall", 13));
        sipTargets.add(new AnalysisMeasTarget("SIPSuccessCalls", 14));
        sipTargets.add(new AnalysisMeasTarget("SIPFailedCalls", 16));
        sipTargets.add(new AnalysisMeasTarget("SIPRetransmissions", 44));
        sipTargets.add(new AnalysisMeasTarget("SIPResTimePartition1", 64));
        sipTargets.add(new AnalysisMeasTarget("SIPResTimePartition100", 65));
        sipTargets.add(new AnalysisMeasTarget("SIPResTimePartition500", 66));
        sipTargets.add(new AnalysisMeasTarget("SIPResTimePartition1000", 67));
        sipTargets.add(new AnalysisMeasTarget("SIPResTimePartition2000", 68));
        TARGETS.put("data/periodic/sip/sipp.csv", sipTargets);

        FILES.add(new AnalysisFileTarget("data/periodic/java/jgcstat.txt", ',', true));
        List<AnalysisMeasTarget> jgcTargets = new ArrayList<>();
        jgcTargets.add(new AnalysisMeasTarget("GcPauseDuration", 0));
        jgcTargets.add(new AnalysisMeasTarget("GcMemBefore", 1));
        jgcTargets.add(new AnalysisMeasTarget("GcMemAfter", 2));
        TARGETS.put("data/periodic/java/jgcstat.txt", jgcTargets);
        
        FILES.add(new AnalysisFileTarget("data/periodic/java/objs.hist", ' ', false));
        List<AnalysisMeasTarget> objTargets = new ArrayList<>();
        objTargets.add(new AnalysisMeasTarget("ObjHistCount", 1));
        objTargets.add(new AnalysisMeasTarget("ObjHistSize", 2));
        TARGETS.put("data/periodic/java/objs.hist", objTargets);

        FILES.add(new AnalysisFileTarget("data/periodic/http/jmeter.csv", ',', true));
        //timeStamp,elapsed,label,responseCode,success,Latency,SampleCount,ErrorCount,IdleTime,Connect
        List<AnalysisMeasTarget> httpTargets = new ArrayList<>();
        httpTargets.add(new AnalysisMeasTarget("HTTPElapsed", 1));
        httpTargets.add(new AnalysisMeasTarget("HTTPLatency", 5));
        httpTargets.add(new AnalysisMeasTarget("HTTPSampleCount", 6));
        httpTargets.add(new AnalysisMeasTarget("HTTPErrorCount", 7));
        httpTargets.add(new AnalysisMeasTarget("HTTPIdleTime", 8));
        httpTargets.add(new AnalysisMeasTarget("HTTPConnect", 9));
        TARGETS.put("data/periodic/http/jmeter.csv", httpTargets);

    }

    public PerfCorderAnalyzer(InputStream zipFile, int linesToStripRatio) throws FileNotFoundException, IOException {
        assert linesToStripRatio >= 0 && linesToStripRatio <= 100 : "ratio must be a [0,100] percentage";
        uncompressZip = ZipExtractor.uncompressZip(zipFile);
        this.linesToStripRatio = linesToStripRatio;
    }

    public PerfCorderAnalysis analyze() throws IOException {
        PerfCorderAnalysis perfCorderAnalysis = new PerfCorderAnalysis();
        perfCorderAnalysis.setStartTimeStamp(extractTimestamp("data/meta/startTimestamp"));
        perfCorderAnalysis.setEndTimeStamp(extractTimestamp("data/meta/endTimestamp"));

        Map<AnalysisFileTarget, List<String[]>> dataFiles = extractDataFiles();
        for (AnalysisFileTarget file : dataFiles.keySet()) {
            List<String[]> dataFile = dataFiles.get(file);
            Map<AnalysisMeasTarget, AnalysisMeasResults> results = StatsCalculator.analyzeTarget(dataFile, 
                    TARGETS.get(file.getPath()), 
                    linesToStripRatio,
                    perfCorderAnalysis);
            for (AnalysisMeasTarget key : results.keySet()) {
                AnalysisMeasResults measResults = results.get(key);
                perfCorderAnalysis.addMeas(key, measResults);
            }
        }
        return perfCorderAnalysis;
    }



    private long extractTimestamp(String filePath) {
        DataFile zEntry = uncompressZip.get(filePath);
        long timestamp = 0;
        if (zEntry != null) {
            timestamp = zEntry.getTimestamp();
        }
        return timestamp;
    }

    private Map<AnalysisFileTarget, List<String[]>> extractDataFiles() throws IOException {
        Map<AnalysisFileTarget, List<String[]>> dataFiles = new HashMap();
        for (AnalysisFileTarget file : FILES) {
            DataFile dFile = uncompressZip.get(file.getPath());
            if (dFile != null) {
                List<String[]> extractFile = CSVExtractor.extractFile(dFile, file);
                if (extractFile != null) {
                    dataFiles.put(file, extractFile);
                }
            }
        }
        return dataFiles;
    }


}
