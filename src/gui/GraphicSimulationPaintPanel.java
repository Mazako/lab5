package gui;

import simulation.Bus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.List;

public class GraphicSimulationPaintPanel extends JPanel implements MouseMotionListener {

    public static final int PAINT_PANEL_WIDTH = GraphicSimulationFrame.WINDOW_WIDTH - 17;
    public static final int PAINT_PANEL_HEIGHT = GraphicSimulationFrame.WINDOW_HEIGHT - 40;
    public static final Font AREA_FONT = new Font(Font.DIALOG, Font.PLAIN, 48);
    public static final int PARKING_WIDTH = 60;
    public static final int ROAD_TO_BRIDGE_WIDTH = 200;

    public static final int BRIDGE_WIDTH = 160;

    public static final int BRIDGE_QUEUE_WIDTH = 60;
    private final List<Bus> allBuses;
    public GraphicSimulationPaintPanel(List<Bus> allBuses)  {
        this.allBuses = allBuses;
        this.addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        paintParking(g2d);
        paintRoadToBus(g2d);
        paintQueueToBridge(g2d);
        paintBridge(g2d);
        paintTexts(g2d);
        synchronized (allBuses) {
            allBuses.forEach(bus -> bus.drawBus(g2d));
        }
    }

    private void paintTexts(Graphics2D g2d) {
        g2d.setFont(AREA_FONT);
        g2d.setColor(Color.WHITE);
        AffineTransform normalTransform = g2d.getTransform();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-Math.PI / 2); //obrot o 90st
        g2d.setTransform(affineTransform);
        g2d.drawString("Parking", -750, PARKING_WIDTH);
        g2d.drawString("Parking", -350, PARKING_WIDTH);
        g2d.drawString("Parking", -350, PAINT_PANEL_WIDTH + 3 * PARKING_WIDTH);
        g2d.drawString("Parking", -750, PAINT_PANEL_WIDTH + 3 * PARKING_WIDTH);

        g2d.drawString("Droga", -740, (int) (PARKING_WIDTH + 0.75 * ROAD_TO_BRIDGE_WIDTH));
        g2d.drawString("Droga", -340, (int) (PARKING_WIDTH + 0.75 * ROAD_TO_BRIDGE_WIDTH));
        g2d.drawString("Droga", -340, (int) (PAINT_PANEL_WIDTH + 0.25 * PARKING_WIDTH));
        g2d.drawString("Droga", -740, (int) (PAINT_PANEL_WIDTH + 0.25 * PARKING_WIDTH));

        g2d.drawString("ZOLL", -730, PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH + 2 * BRIDGE_QUEUE_WIDTH);
        g2d.drawString("ZOLL", -330, PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH + 2 * BRIDGE_QUEUE_WIDTH);
        g2d.drawString("ZOLL", -330, PAINT_PANEL_WIDTH - 140);
        g2d.drawString("ZOLL", -730, PAINT_PANEL_WIDTH - 140);

        g2d.drawString("Nysa", -330, PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH + BRIDGE_QUEUE_WIDTH +  BRIDGE_WIDTH + 25);
        g2d.drawString("Nysa", -730, PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH + BRIDGE_QUEUE_WIDTH +  BRIDGE_WIDTH + 25);
        g2d.setTransform(normalTransform);

    }

    private void paintBridge(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.fillRect(PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH + BRIDGE_QUEUE_WIDTH, 0, BRIDGE_WIDTH, PAINT_PANEL_HEIGHT);
    }

    private void paintQueueToBridge(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillRect(PARKING_WIDTH + ROAD_TO_BRIDGE_WIDTH, 0, BRIDGE_QUEUE_WIDTH, PAINT_PANEL_HEIGHT);
        g2d.fillRect(PAINT_PANEL_WIDTH - ROAD_TO_BRIDGE_WIDTH - PARKING_WIDTH - BRIDGE_QUEUE_WIDTH, 0, BRIDGE_QUEUE_WIDTH, PAINT_PANEL_HEIGHT);
    }

    private void paintRoadToBus(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(PARKING_WIDTH , 0, ROAD_TO_BRIDGE_WIDTH, PAINT_PANEL_HEIGHT);
        g2d.fillRect(PAINT_PANEL_WIDTH - PARKING_WIDTH - ROAD_TO_BRIDGE_WIDTH, 0, ROAD_TO_BRIDGE_WIDTH, PAINT_PANEL_HEIGHT);

    }

    private void paintParking(Graphics2D g2d) {
        g2d.setColor(Color.PINK);
        g2d.fillRect(0, 0, PARKING_WIDTH, PAINT_PANEL_HEIGHT);
        g2d.fillRect(PAINT_PANEL_WIDTH - PARKING_WIDTH, 0, PARKING_WIDTH, PAINT_PANEL_HEIGHT);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());
    }
}
