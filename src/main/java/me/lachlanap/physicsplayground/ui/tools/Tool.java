package me.lachlanap.physicsplayground.ui.tools;

import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.ui.View;

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
