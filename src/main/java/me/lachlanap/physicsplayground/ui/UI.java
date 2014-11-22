package me.lachlanap.physicsplayground.ui;

import java.awt.BorderLayout;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyEventPostProcessor;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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

        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new UIKeyListener());
    }

    public void show() {
        frame.setVisible(true);
        worldAdvancer.start();
    }

    private class UIKeyListener implements KeyEventPostProcessor {

        @Override
        public boolean postProcessKeyEvent(KeyEvent e) {
            if (SwingUtilities.isDescendingFrom(e.getComponent(), optionsPanel))
                return true;

            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        worldAdvancer.setActive(!worldAdvancer.isActive());
                        break;
                }
            }

            return true;
        }

    }

}
