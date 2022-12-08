package gui;

import simulation.NarrowBridgeSimulation;

import javax.swing.*;
import java.awt.*;

public class GraphicSimulationPaintPanel extends JPanel {

    private final NarrowBridgeSimulation narrowBridgeSimulation;
    public GraphicSimulationPaintPanel(NarrowBridgeSimulation narrowBridgeSimulation) {
        this.narrowBridgeSimulation = narrowBridgeSimulation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
