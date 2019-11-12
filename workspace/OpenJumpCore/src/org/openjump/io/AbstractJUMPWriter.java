package org.openjump.io;

import org.openjump.geometry.feature.FeatureCollection;
import org.openjump.task.TaskMonitor;
import org.openjump.task.TaskMonitorSupport;

public abstract class AbstractJUMPWriter implements JUMPWriter, TaskMonitorSupport {

  private TaskMonitor taskMonitor;

  @Override
  abstract public void write(FeatureCollection featureCollection,
      DriverProperties dp) throws Exception;

  public void setTaskMonitor(TaskMonitor taskMonitor) {
    this.taskMonitor = taskMonitor;
  }

  public TaskMonitor getTaskMonitor() {
    return taskMonitor;
  }
}
