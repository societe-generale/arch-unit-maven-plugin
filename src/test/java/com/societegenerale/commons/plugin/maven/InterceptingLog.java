package com.societegenerale.commons.plugin.maven;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.Logger;

class InterceptingLog extends DefaultLog {

  List<String> infoLogs = new ArrayList<String>();

  List<String> warnLogs = new ArrayList<String>();

  List<String> debugLogs = new ArrayList<>();

  private boolean isInfoEnabled;

  InterceptingLog(Logger logger) {
    super(logger);
    this.isInfoEnabled = true;
  }

  public boolean isInfoEnabled() {
    return isInfoEnabled;
  }

  public void setInfoEnabled(boolean isInfoEnabled) {
    this.isInfoEnabled = isInfoEnabled;
  }

  @Override
  public void info(CharSequence content) {
    if (this.isInfoEnabled) {
      infoLogs.add(content.toString());
    }
  }

  @Override
  public void warn(CharSequence content) {
    warnLogs.add(content.toString());
  }

  @Override
  public void debug(CharSequence content) {
    debugLogs.add(content.toString());
  }
}
