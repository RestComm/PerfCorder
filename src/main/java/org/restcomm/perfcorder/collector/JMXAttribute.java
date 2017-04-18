package org.restcomm.perfcorder.collector;


public class JMXAttribute {
    private String name;
    private Boolean delta = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDelta() {
        return delta;
    }

    public void setDelta(Boolean delta) {
        this.delta = delta;
    }
    
    
}
