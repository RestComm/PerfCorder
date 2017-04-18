package org.restcomm.perfcorder.collector;

import java.util.List;

public class JMXOperation {

    private String name;
    private Boolean delta = true;
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

    public Boolean getDelta() {
        return delta;
    }

    public void setDelta(Boolean delta) {
        this.delta = delta;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        for (String arg : arguments) {
            builder.append(arg);
        }
        return builder.toString();
    }

}
