
package org.restcomm.perfcorder.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.*;

import java.io.IOException;

public class PerfCorderPublisher extends Recorder {

    @Extension
    public static class MyDescriptor extends BuildStepDescriptor<hudson.tasks.Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Stop PerfCorder";
        }
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return new MyDescriptor();
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
