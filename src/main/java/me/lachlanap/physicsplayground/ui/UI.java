package me.lachlanap.physicsplayground.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class UI {

    private final World world;
    private final WorldRenderer worldRenderer;
    private final WorldAdvancer worldAdvancer;
    private final OptionsPanel optionsPanel;

    private final JFrame frame;

    public UI(World world) {
        this.world = world;

        this.worldRenderer = new WorldRenderer(world);
        this.worldAdvancer = new WorldAdvancer(world, worldRenderer);
        this.optionsPanel = new OptionsPanel(world, worldAdvancer, worldRenderer);

        this.frame = new JFrame("Physics Playground");
        setup();
    }

    private void setup() {
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout(5, 5));
        frame.add(optionsPanel, BorderLayout.WEST);
        frame.add(worldRenderer, BorderLayout.CENTER);
    }

    public void show() {
        frame.setVisible(true);
        worldAdvancer.start();
    }

}
