package me.lachlanap.physicsplayground.ui;

/**
 *
 * @author lachlan
 */
public interface Tool {

    public void mouseDown(double x, double y);

    public void mouseDrag(double x, double y);

    public void mouseUp(double x, double y);
}
