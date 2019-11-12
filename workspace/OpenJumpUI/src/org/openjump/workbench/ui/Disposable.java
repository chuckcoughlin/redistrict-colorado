package org.openjump.workbench.ui;

/**
 * Interface describing a disposable object. executing dispose() should release
 * all memory used by the implementing object.
 */
public interface Disposable {
  void dispose();
}
