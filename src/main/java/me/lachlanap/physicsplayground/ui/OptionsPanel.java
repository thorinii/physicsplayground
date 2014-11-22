package me.lachlanap.physicsplayground.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
class OptionsPanel extends JPanel {

    private final World world;
    private final WorldAdvancer advancer;

    public OptionsPanel(World world, WorldAdvancer worldAdvancer, WorldRenderer worldRenderer) {
        this.world = world;
        this.advancer = worldAdvancer;

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addButton(new Dampener(), "Dampen Velocity");
        addButton(new ToggleEWO(), "Toggle Experimental World Option");
    }

    private void addButton(final Runnable task, String name) {
        JButton button = new JButton(name);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                task.run();
            }
        });
        add(button);
    }

    private class Dampener implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < world.getObjects(); i++)
                world.setVelocity(i, 0, 0);
        }
    }


    private class ToggleEWO implements Runnable {

        @Override
        public void run() {
            world.setEWO(!world.isEWO());
        }
    }
}
