package org.restcomm.perfcorder.jenkins;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.Shell;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class ScriptExecutor implements Serializable {

    protected PerfCorderLog log;

    private BuildListener listener;

    public ScriptExecutor(PerfCorderLog log, BuildListener listener) {
        this.log = log;
        this.listener = listener;
    }

    public int executeScriptPathAndGetExitCode(FilePath workspace, String scriptFilePath, Launcher launcher) throws PerfCorderException {

        if (scriptFilePath == null) {
            throw new NullPointerException("The scriptFilePath object must be set.");
        }

        FilePath filePath = getFilePath(workspace, scriptFilePath);
        if (filePath == null) {
            throw new PerfCorderException(String.format("The script file path '%s' doesn't exist.", scriptFilePath));
        }

        return executeScript(workspace, filePath, launcher);
    }

    private String getResolvedContentWithEnvVars(FilePath filePath) throws PerfCorderException {
        String scriptContentResolved;
        try {
            log.info("Resolving environment variables for the script content.");
            scriptContentResolved =
                    filePath.act(new FilePath.FileCallable<String>() {
                        public void checkRoles(RoleChecker checker) throws SecurityException{}
                        public String invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
                            String scriptContent = Util.loadFile(f);
                            return Util.replaceMacro(scriptContent, EnvVars.masterEnvVars);
                        }
                    });
        } catch (IOException ioe) {
            throw new PerfCorderException("Error to resolve environment variables", ioe);
        } catch (InterruptedException ie) {
            throw new PerfCorderException("Error to resolve environment variables", ie);
        }
        return scriptContentResolved;
    }

    private int executeScript(FilePath workspace, FilePath script, final Launcher launcher) throws PerfCorderException {

        assert script != null;
        assert launcher != null;

        String scriptContent = getResolvedContentWithEnvVars(script);
        log.info(String.format("Evaluating the script: \n %s", scriptContent));
        FilePath tmpFile;
        try {
            final CommandInterpreter batchRunner;
            if (launcher.isUnix()) {
                batchRunner = new Shell(scriptContent);
            } else {
                batchRunner = new BatchFile(scriptContent);
            }
            tmpFile = batchRunner.createScriptFile(workspace);
            return launcher.launch().cmds(batchRunner.buildCommandLine(tmpFile)).stdout(listener).pwd(workspace).join();
        } catch (InterruptedException ie) {
            throw new PerfCorderException("Error to execute script", ie);
        } catch (IOException ioe) {
            throw new PerfCorderException("Error to execute script", ioe);
        }
    }


    private FilePath getFilePath(final FilePath workspace, final String givenPath) throws PerfCorderException {

        try {
            return workspace.act(new FilePath.FileCallable<FilePath>() {
                public void checkRoles(RoleChecker checker) throws SecurityException{}

                public FilePath invoke(File ws, VirtualChannel channel) throws IOException, InterruptedException {
                    File givenFile = new File(givenPath);
                    if (givenFile.exists()) {
                        return new FilePath(channel, givenFile.getPath());
                    }

                    FilePath filePath = new FilePath(workspace, givenPath);
                    if (filePath.exists()) {
                        return filePath;
                    }
                    return null;
                }
            });
        } catch (IOException ioe) {
            throw new PerfCorderException("Error to resolve script path", ioe);
        } catch (InterruptedException ie) {
            throw new PerfCorderException("Error to resolve script path", ie);
        }
    }

}
