package org.restcomm.perfcorder.jenkins;

import hudson.model.Action;
import java.util.logging.Logger;

public final class PerfCorderProjectAction implements Action {


  private static final String PLUGIN_NAME = "performance";

  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger
          .getLogger(PerfCorderProjectAction.class.getName());


  public String getDisplayName() {
    return Messages.ProjectAction_DisplayName();
  }

  public String getIconFileName() {
    return "graph.gif";
  }

  public String getUrlName() {
    return PLUGIN_NAME;
  }
}








