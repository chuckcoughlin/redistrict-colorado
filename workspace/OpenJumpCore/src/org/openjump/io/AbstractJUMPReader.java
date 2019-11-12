package org.openjump.io;

import java.util.ArrayList;
import java.util.Collection;

import org.openjump.geometry.feature.FeatureCollection;
import org.openjump.task.DummyTaskMonitor;
import org.openjump.task.TaskMonitor;
import org.openjump.task.TaskMonitorSupport;

/**
 * Partial implementation of JUMPReader implementing getExceptions method.
 */
abstract public class AbstractJUMPReader implements JUMPReader, TaskMonitorSupport {

  private Collection<Exception> exceptions = null;
  private TaskMonitor taskMonitor = new DummyTaskMonitor();

  /**
   * Read the specified file using the filename given by the "File" property and
   * any other parameters.
   */
  public abstract FeatureCollection read(DriverProperties dp) throws Exception;

  protected void addException(Exception e) {
    if (exceptions == null)
      exceptions = new ArrayList<>();

    exceptions.add(e);
  }

  /**
   * @return exceptions collected during the reading process.
   */
  public Collection<Exception> getExceptions() {
    if (exceptions == null)
      exceptions = new ArrayList<>();
    return exceptions;
  }

  public void setTaskMonitor(TaskMonitor taskMonitor) {
    this.taskMonitor = taskMonitor;
  }

  public TaskMonitor getTaskMonitor() {
    return taskMonitor;
  }

}
