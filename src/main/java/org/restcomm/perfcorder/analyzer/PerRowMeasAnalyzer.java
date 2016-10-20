package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerRowMeasAnalyzer implements FileAnalyser<PerRowFileMeasTarget> {

    @Override
    public Map<AnalysisMeasTarget, AnalysisMeasResults> analyzeTarget(List<String[]> readAll, AnalysisFileTarget fileTarget, int linesToStripRatio, PerfCorderAnalysis analysis) throws IOException {
        Map<AnalysisMeasTarget, AnalysisMeasResults> measMap = new HashMap();
        for (PerRowFileMeasTarget target : fileTarget.getPerRowTargets()) {
            for (String[] readNext : readAll) {
                String measName = readNext[target.getLabelColumn()];
                String measCount = readNext[target.getCountColumn()];
                String measSum = readNext[target.getSumColumn()];
                AnalysisMeasResults results = new AnalysisMeasResults();
                results.setCategory(fileTarget.getCategory());
                results.setCount(target.transformIntoDouble(measCount));
                results.setSum(target.transformIntoDouble(measSum));
                AnalysisMeasTarget newTarget = new AnalysisMeasTarget(target.getLabel() + "-" + measName);
                measMap.put(newTarget, results);
            }
        }
        return measMap;

    }

}
