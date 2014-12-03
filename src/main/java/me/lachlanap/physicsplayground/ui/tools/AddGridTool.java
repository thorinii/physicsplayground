package me.lachlanap.physicsplayground.ui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.View;

/**
 *
 * @author lachlan
 */
public class AddGridTool implements Tool {

    private final World world;
    private final double radius;

    private boolean on;
    private double startX, startY;
    private double endX, endY;

    public AddGridTool(World world, double radius) {
        this.world = world;
        this.radius = radius;
    }

    @Override
    public void mouseDown(double x, double y) {
        on = true;
        startX = x;
        endY = y;

        endX = x;
        startY = y;
    }

    @Override
    public void mouseDrag(double x, double y) {
        endX = x;
        startY = y;
    }

    @Override
    public void mouseUp(double mouseX, double mouseY) {
        on = false;
        endX = mouseX;
        startY = mouseY;

        if (startX < endX && startY < endY) {
            double width = endX - startX;
            double height = endY - startY;

            double diameter = radius * 2;

            int widthN = (int) (width / diameter);
            int heightN = (int) (height / diameter);

            if (widthN <= 0 || heightN <= 0)
                return;

            int[][] objectIds = new int[widthN][heightN];

            // Create the objects
            for (int x = 0; x < widthN; x++) {
                for (int y = 0; y < heightN; y++) {
                    double posX = startX + x * diameter + Math.random() * radius * 0.01;
                    double posY = startY + y * diameter + Math.random() * radius * 0.01;

                    if (y % 2 == 1)
                        posX -= radius;

                    objectIds[x][y] = world.addObject(posX, posY,
                                                      radius * 0.95);
                }
            }

            // Constrain on x axis
            for (int x = 0; x < widthN - 1; x++) {
                for (int y = 0; y < heightN; y++) {
                    int a = objectIds[x][y];
                    int b = objectIds[x + 1][y];

                    world.addConstraint(a, b, 0);
                }
            }

            // Constrain on y axis
            for (int x = 0; x < widthN; x++) {
                for (int y = 0; y < heightN - 1; y++) {
                    int a = objectIds[x][y];
                    int b = objectIds[x][y + 1];

                    world.addConstraint(a, b, 0);
                }
            }

            // Constrain on diagonal
            for (int x = 0; x < widthN - 1; x++) {
                for (int y = 0; y < heightN - 1; y += 2) {
                    int a = objectIds[x][y];
                    int b = objectIds[x + 1][y + 1];

                    world.addConstraint(a, b, 0);
                }
            }

            // Constrain on diagonal
            for (int x = 1; x < widthN; x++) {
                for (int y = 1; y < heightN - 1; y += 2) {
                    int a = objectIds[x][y];
                    int b = objectIds[x - 1][y + 1];

                    world.addConstraint(a, b, 0);
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g, int x, int y, View view) {
        if (on) {
            g.setColor(Color.GREEN);

            double dx, dy;
            dx = endX - startX;
            dy = startY - endY;
            g.drawRect((int) view.absoluteToViewX(startX), (int) view.absoluteToViewY(endY),
                       (int) view.relativeToView(dx), (int) -view.relativeToView(dy));
        } else {
            g.setColor(Color.GREEN);
            g.drawLine(x - 10, y, x + 10, y);
            g.drawLine(x, y - 10, x, y + 10);
        }
    }

    @Override
    public String getLabel() {
        return "Add Grid (" + radius + "m)";
    }
}
