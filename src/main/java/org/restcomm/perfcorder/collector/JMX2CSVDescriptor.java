package org.restcomm.perfcorder.collector;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JMX2CSVDescriptor {
    private String userName;
    private String password;
    private String url;
    private String objectName;
    List<JMXAttribute> attributes = new ArrayList();
    List<JMXOperation> operations = new ArrayList();

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public List<JMXAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<JMXAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<JMXOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<JMXOperation> operations) {
        this.operations = operations;
    }

    public String getUserName() {
        return userName;
}

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
