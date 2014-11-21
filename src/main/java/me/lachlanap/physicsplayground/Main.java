package me.lachlanap.physicsplayground;

import javax.swing.SwingUtilities;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.UI;

/**
 *
 * @author lachlan
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                World world = new World();
                UI ui = new UI(world);
                ui.show();
            }
        });
    }
}
