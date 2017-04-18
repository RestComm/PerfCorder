package org.restcomm.perfcorder.jenkins;

import hudson.model.TaskListener;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class PerfCorderLog implements Serializable {

    private TaskListener listener;

    public PerfCorderLog(TaskListener listener) {
        this.listener = listener;
    }

    public TaskListener getListener() {
        return listener;
    }

    public void info(String message) {
        listener.getLogger().println("[PostBuildScript] - " + message);
    }

    public void error(String message) {
        listener.getLogger().println("[PostBuildScript] - [ERROR] - " + message);
    }
}
