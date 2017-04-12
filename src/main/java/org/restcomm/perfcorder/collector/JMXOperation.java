package org.restcomm.perfcorder.collector;

import java.util.List;


public class JMXOperation {
    private String name;
    private List<String> arguments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }


    
}
