package me.lachlanap.physicsplayground.ui;

import java.awt.Graphics2D;

/**
 *
 * @author lachlan
 */
public interface Tool {

    public void mouseDown(double x, double y);

    public void mouseDrag(double x, double y);

    public void mouseUp(double x, double y);

    public void draw(Graphics2D g, int x, int y, View view);
}
