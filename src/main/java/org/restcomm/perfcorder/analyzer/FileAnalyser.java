package org.restcomm.perfcorder.analyzer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jaime
 */
public interface FileAnalyser<T extends AnalysisMeasTarget>  {
     Map<AnalysisMeasTarget, AnalysisMeasResults> analyzeTarget(List<String[]> readAll, AnalysisFileTarget target, PerfCorderAnalysis analysis) throws IOException;    
}
