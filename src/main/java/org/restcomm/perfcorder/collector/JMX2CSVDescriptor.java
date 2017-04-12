package org.restcomm.perfcorder.collector;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JMX2CSVDescriptor {

    private String objectName;
    private boolean absolute = false;
    List<String> attributes = new ArrayList();
    List<JMXOperation> operations = new ArrayList();

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<JMXOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<JMXOperation> operations) {
        this.operations = operations;
    }
}
