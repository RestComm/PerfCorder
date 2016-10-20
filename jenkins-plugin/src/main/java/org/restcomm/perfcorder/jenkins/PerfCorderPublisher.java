
package org.restcomm.perfcorder.jenkins;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;

/**
 *
 * @author jaime
 */
public class PerfCorderPublisher extends Recorder {

    @Override
    public BuildStepDescriptor getDescriptor() {
        return super.getDescriptor(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return super.perform(build, launcher, listener); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    
}
