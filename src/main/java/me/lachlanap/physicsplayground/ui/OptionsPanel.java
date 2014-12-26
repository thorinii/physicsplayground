package me.lachlanap.physicsplayground.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import me.lachlanap.physicsplayground.physics.PointObject;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.tools.*;

/**
 *
 * @author lachlan
 */
class OptionsPanel extends JPanel {

    private final World world;
    private final WorldAdvancer advancer;
    private final WorldRenderer renderer;

    public OptionsPanel(World world, WorldAdvancer worldAdvancer, WorldRenderer worldRenderer) {
        this.world = world;
        this.advancer = worldAdvancer;
        this.renderer = worldRenderer;

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        addButton(new Dampener(), "Dampen Velocity");
        addButton(new ToggleDeleteOnFloor(), "Toggle Delete on Floor");
        addButton(new ToggleEWO(), "Toggle Experimental World Option");

        addButton(new ToolSetter(new AddCircleTool(world, 0.5)), "Add Circles");
        addButton(new ToolSetter(new AddCircleTool(world, 1)), "Add Big Circles");

        addButton(new ToolSetter(new AddRopeTool(world, 0.1)), "Add Tiny Rope");
        addButton(new ToolSetter(new AddRopeTool(world, 1)), "Add Big Rope");

        addButton(new ToolSetter(new AddGridTool(world, 0.1)), "Add Tiny Grid");
        addButton(new ToolSetter(new AddGridTool(world, 0.5)), "Add Grid");
        addButton(new ToolSetter(new AddGridTool(world, 1)), "Add Big Grid");

        addButton(new ToolSetter(new AddPinnedCircleTool(world, 0.5)), "Add Pinned Circles");
        addButton(new ToolSetter(new AddPinnedCircleTool(world, 1)), "Add Pinned Big Circles");

        addButton(new ToolSetter(new FlowTool(world, 5)), "Flow Generator");
        addButton(new ToolSetter(new DeleteTool(world, 5)), "Delete");

        addButton(new ToolSetter(new PinTool(world, 5, true)), "Pin");
        addButton(new ToolSetter(new PinTool(world, 5, false)), "Unpin");

        addButton(new ToolSetter(new ConstraintTool(world, 0)), "Add Infinite Constraint");

        addButton(new ToolSetter(new InflowTool(world)), "Set Inflow");
    }

    private void addButton(final Runnable task, String name) {
        JButton button = new JButton(name);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                renderer.requestFocusInWindow();
                task.run();
            }
        });
        add(button);
    }

    private class Dampener implements Runnable {

        @Override
        public void run() {
            advancer.execute(new Runnable() {

                @Override
                public void run() {
                    PointObject o = new PointObject();
                    for (int i = 0; i < world.getObjects(); i++) {
                        world.updateObject(world.getObject(i, o).setVelocity(0, 0));
                    }
                }
            });
        }
    }


    private class ToggleDeleteOnFloor implements Runnable {

        @Override
        public void run() {
            world.setDeleteAtFloor(!world.isDeleteAtFloor());
        }
    }


    private class ToggleEWO implements Runnable {

        @Override
        public void run() {
            world.setEWO(!world.isEWO());
        }
    }

    private class ToolSetter implements Runnable {

        final Tool tool;

        public ToolSetter(Tool tool) {
            this.tool = tool;
        }

        @Override
        public void run() {
            renderer.setTool(tool);
        }
    }
}
