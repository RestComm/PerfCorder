
package org.restcomm.perfcorder.jenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

public class PerfCorderBuilder extends Builder {

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "execute PerfCorder task";
        }
    }
    
    private final String processPattern;

    @DataBoundConstructor
    public PerfCorderBuilder(String pattern) {
        this.processPattern = pattern;
    }

    public String getProcessPattern() {
        return processPattern;
    }



    @Override
    public hudson.model.Descriptor<Builder> getDescriptor() {
        return new Descriptor();
    }

    @Override
    public boolean prebuild(Build build, BuildListener listener) {
        boolean continueBuild = false;
        
        return continueBuild;
    }
    
    
    
    
}
