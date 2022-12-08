package gui;

import javax.swing.*;
import java.awt.*;

public class GraphicSimulationFrame extends JFrame {

    private final GraphicSimulationPaintPanel graphicSimulationPaintPanel;

    public GraphicSimulationFrame(GraphicSimulationPaintPanel graphicSimulationPaintPanel) {
        this.setSize(MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT);
        this.setResizable(false);
        this.graphicSimulationPaintPanel = graphicSimulationPaintPanel;
    }

}
