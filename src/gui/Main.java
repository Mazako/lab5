package gui;

import simulation.NarrowBridgeSimulation;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JTextArea logs = new JTextArea();
        NarrowBridgeSimulation narrowBridgeSimulation = new NarrowBridgeSimulation(logs);
        GraphicSimulationPaintPanel paintPanel = new GraphicSimulationPaintPanel(narrowBridgeSimulation.getAllBuses());
        SwingUtilities.invokeLater(() -> new MainFrame(logs, narrowBridgeSimulation));
        SwingUtilities.invokeLater(() -> new GraphicSimulationFrame(paintPanel));
        new Thread(narrowBridgeSimulation).start();
        new Thread(() -> {
            while (true) {
                paintPanel.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    System.exit(1);
                }
            }
        }).start();
    }
}
