package org.openjump.ui.action;

/**
 * a method to mark plugins as enable checked. this is new. older plugins only
 * implemented a static createEnableCheck() method.
 * 
 * @author ed
 * 
 */
public interface EnableChecked {
  public EnableCheck getEnableCheck();
}
