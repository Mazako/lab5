package gui;

import javax.swing.*;
import java.awt.*;

public class GraphicSimulationFrame extends JFrame {

    public static final int WINDOW_WIDTH = 817;
    public static final int WINDOW_HEIGHT = 840;
    private final GraphicSimulationPaintPanel graphicSimulationPaintPanel;

    public GraphicSimulationFrame(GraphicSimulationPaintPanel graphicSimulationPaintPanel) {
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.graphicSimulationPaintPanel = graphicSimulationPaintPanel;
        this.add(graphicSimulationPaintPanel);
        this.setVisible(true);
    }

}
