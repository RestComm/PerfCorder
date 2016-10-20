package org.restcomm.perfcorder.analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extracts zip file, and calculates statistics for the given targets.
 *
 *
 */
public final class PerfCorderAnalyzer {

    private final Map<String, DataFile> uncompressZip;
    private final List<AnalysisFileTarget> files;

    private static AnalysisFileTarget SETTINGS_TARGET;
    private static final String SETTINGS_LOC = "data/meta/perfcorder_settings.csv";

    static {
        SETTINGS_TARGET = new AnalysisFileTarget(SETTINGS_LOC, ',', true);
    }

    /**
     * Percentage of lines to strip at the beginning and end of CSV file. These
     * rows will not be cover in the generated stats
     */
    private final int linesToStripRatio;

    public PerfCorderAnalyzer(InputStream zipFile, int linesToStripRatio, List<AnalysisFileTarget> files) throws FileNotFoundException, IOException {
        assert linesToStripRatio >= 0 && linesToStripRatio <= 100 : "ratio must be a [0,100] percentage";
        uncompressZip = ZipExtractor.uncompressZip(zipFile);
        this.files = files;
        this.linesToStripRatio = linesToStripRatio;
    }

    public PerfCorderAnalysis analyze() throws IOException {
        long startTS = extractTimestamp("data/meta/startTimestamp");
        long endTS = extractTimestamp("data/meta/endTimestamp");
        PerfCorderCollectionSettings settings = extractSettings();
        PerfCorderAnalysis perfCorderAnalysis = new PerfCorderAnalysis(startTS, endTS, settings);

        Map<AnalysisFileTarget, List<String[]>> dataFiles = extractDataFiles();
        for (AnalysisFileTarget file : dataFiles.keySet()) {
            List<String[]> dataFile = dataFiles.get(file);
            Map<AnalysisMeasTarget, AnalysisMeasResults> results = null;
            FileAnalyser analyser = null;
            analyser = new StatsCalculator();
            results = analyser.analyzeTarget(dataFile,
                    file,
                    linesToStripRatio,
                    perfCorderAnalysis);
            for (AnalysisMeasTarget key : results.keySet()) {
                AnalysisMeasResults measResults = results.get(key);
                perfCorderAnalysis.addMeas(key, measResults);
            }

            analyser = new PerRowMeasAnalyzer();
            results = analyser.analyzeTarget(dataFile,
                    file,
                    linesToStripRatio,
                    perfCorderAnalysis);
            for (AnalysisMeasTarget key : results.keySet()) {
                AnalysisMeasResults measResults = results.get(key);
                perfCorderAnalysis.addMeas(key, measResults);
            }

        }
        return perfCorderAnalysis;
    }

    private PerfCorderCollectionSettings extractSettings() {
        DataFile zEntry = uncompressZip.get("data/meta/perfcorder_settings.csv");
        PerfCorderCollectionSettings settings = new PerfCorderCollectionSettings();
        try {
            List<String[]> extractFile = CSVExtractor.extractFile(zEntry, SETTINGS_TARGET);
            settings.setMeasIntervalSeconds(Integer.valueOf(extractFile.get(1)[0]));
            settings.setConfDir(extractFile.get(1)[1]);
            settings.setOutputDir(extractFile.get(1)[1]);
            settings.setPatternMode(extractFile.get(0)[0]);
            settings.setJavaPID(extractFile.get(1)[3]);
        } catch (IOException ex) {
            Logger.getLogger(PerfCorderAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return settings;
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
        for (AnalysisFileTarget file : files) {
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
