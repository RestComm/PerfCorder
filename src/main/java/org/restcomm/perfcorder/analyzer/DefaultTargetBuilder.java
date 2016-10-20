package org.restcomm.perfcorder.analyzer;

import java.util.ArrayList;
import java.util.List;


public class DefaultTargetBuilder {
    public static AnalysisFileTargetSet build(){
        List<AnalysisFileTarget> files = new ArrayList();
        
        AnalysisFileTarget topFileTarget = new AnalysisFileTarget("data/periodic/java/jvmtop.txt", ',', true);
        topFileTarget.setCategory("Java");
        topFileTarget.addCSVTarget(new CSVColumnMeasTarget("Mem", 0));
        topFileTarget.addCSVTarget(new CSVColumnMeasTarget("Cpu", 1));
        files.add(topFileTarget);

        AnalysisFileTarget analysisFileTarget = new AnalysisFileTarget("data/periodic/sip/sipp.csv", ';', true);
        analysisFileTarget.setCategory("SIP");
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("SIPTotalCallCreated", 12));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("SIPCurrentCall", 13));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("SIPSuccessCalls", 14));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("SIPFailedCalls", 16));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("Retransmissions(P)", CSVColumnMeasTarget.SEARCH_COL_INDX_BY_NAME));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("ResponseTime1(P)", CSVColumnMeasTarget.SEARCH_COL_INDX_BY_NAME, "org.restcomm.perfcorder.analyzer.SIPPTimeConverter"));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("ResponseTime2(P)", CSVColumnMeasTarget.SEARCH_COL_INDX_BY_NAME, "org.restcomm.perfcorder.analyzer.SIPPTimeConverter"));
        analysisFileTarget.addCSVTarget(new CSVColumnMeasTarget("CallLength(P)", CSVColumnMeasTarget.SEARCH_COL_INDX_BY_NAME, "org.restcomm.perfcorder.analyzer.SIPPTimeConverter"));
        files.add(analysisFileTarget);

        AnalysisFileTarget jgcTarget = new AnalysisFileTarget("data/periodic/java/jgcstat.txt", ',', true);
        jgcTarget.setCategory("Java");
        jgcTarget.addCSVTarget(new CSVColumnMeasTarget("GcPauseDuration", 0, 7));
        jgcTarget.addCSVTarget(new CSVColumnMeasTarget("GcMemBefore", 1, 7));
        jgcTarget.addCSVTarget(new CSVColumnMeasTarget("GcMemAfter", 2, 7));
        files.add(jgcTarget);

        AnalysisFileTarget jmeterTarget = new AnalysisFileTarget("data/periodic/http/jmeter.csv", ' ', false);
        jmeterTarget.setCategory("HTTP");
        //summary +  11852 in 00:00:30 =  395.1/s Avg:    22 Min:    11 Max:   180 Err:     0 (0.00%) Active: 10 Started: 10 Finished: 0
        jmeterTarget.addCSVTarget(new CSVColumnMeasTarget("HTTPElapsed", 12));
        jmeterTarget.addCSVTarget(new CSVColumnMeasTarget("HTTPSampleCount", 2));
        jmeterTarget.addCSVTarget(new CSVColumnMeasTarget("HTTPErrorCount", 14));
        files.add(jmeterTarget);

        AnalysisFileTarget histTarget = new AnalysisFileTarget("data/periodic/java/objs.hist", ' ', false);
        histTarget.setCategory("Java");
        histTarget.addPerRowTarget(new PerRowFileMeasTarget("ObjHistogram", 3, 2, 1));
        files.add(histTarget); 
        
        return new AnalysisFileTargetSet(files);
    }
}
