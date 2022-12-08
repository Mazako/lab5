package gui;

import simulation.NarrowBridgeSimulation;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JTextArea logs = new JTextArea();
        NarrowBridgeSimulation narrowBridgeSimulation = new NarrowBridgeSimulation(logs);
        GraphicSimulationPaintPanel paintPanel = new GraphicSimulationPaintPanel(narrowBridgeSimulation);
        SwingUtilities.invokeLater(() -> new MainFrame(logs, narrowBridgeSimulation));
        new Thread(narrowBridgeSimulation).start();
    }
}
