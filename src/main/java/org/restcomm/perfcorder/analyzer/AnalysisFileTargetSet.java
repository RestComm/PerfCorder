package org.restcomm.perfcorder.analyzer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AnalysisFileTargetSet {

    private List<AnalysisFileTarget> files = new ArrayList();

    public AnalysisFileTargetSet() {
    }

    AnalysisFileTargetSet(List<AnalysisFileTarget> files) {
        this.files = files;
    }

    public List<AnalysisFileTarget> getFiles() {
        return files;
    }

    public void setFiles(List<AnalysisFileTarget> files) {
        this.files = files;
    }
    
    
}
