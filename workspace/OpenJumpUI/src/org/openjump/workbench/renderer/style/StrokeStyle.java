package org.openjump.workbench.renderer.style;

import java.awt.Color;

public interface StrokeStyle {
	/**
     * @param c
     */
    public void setLineColor(Color c);

    /**
     * @param w
     */
    public void setLineWidth(int w);

    /**
     * @param a
     */
    public void setAlpha(int a);
}
