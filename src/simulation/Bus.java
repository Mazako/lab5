package simulation;

import gui.GraphicSimulationPaintPanel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Bus implements Runnable{

    public static final int MIN_BOARDING_TIME = 1000;
    public static final int MAX_BOARDING_TIME = 10000;
    public static final int GETTING_TO_BRIDGE_TIME = 500;
    public static final int CROSSING_BRIDGE_TIME = 3000;
    public static final int GETTING_PARKING_TIME = 500;
    public static final int UNLOADING_TIME = 1000;
    private static int busesId;

    private static final List<Integer> AVAILABLE_Y = new ArrayList<>() ;
    private static final List<Integer> RESERVE_Y = new ArrayList<>();
    private static final List<Integer> AVAILABLE_Y_PERFECT_COPY;
    private static final List<Integer> RESERVE_Y_PERFECT_COPY;

    static {
        IntStream.iterate(0, x -> x < 800 , x -> x + 40)
                .skip(1L)
                .forEach(AVAILABLE_Y::add);
        IntStream.iterate(60, x -> x < 760, x-> x + 40)
                .forEach(RESERVE_Y::add);
        AVAILABLE_Y_PERFECT_COPY = new ArrayList<>(AVAILABLE_Y);
        RESERVE_Y_PERFECT_COPY = new ArrayList<>(RESERVE_Y);
     }
    private final int id;
    private final NarrowBridgeSimulation simulationCallback;
    private final DrivingDirection drivingDirection;
    private boolean waitingOnBridge;
    private int x;
    private int y;
    public Bus(NarrowBridgeSimulation simulationCallback, DrivingDirection drivingDirection) {
        this.simulationCallback = simulationCallback;
        this.drivingDirection = drivingDirection;
        this.id = (++busesId) % 100;
        this.x = drivingDirection == DrivingDirection.EAST ? 0 :  GraphicSimulationPaintPanel.PAINT_PANEL_WIDTH;
        if (!AVAILABLE_Y.isEmpty()) {
            this.y = AVAILABLE_Y.get(ThreadLocalRandom.current().nextInt(0, AVAILABLE_Y.size()));
            AVAILABLE_Y.remove((Integer) this.y);
        } else if (!RESERVE_Y.isEmpty()) {
            this.y = RESERVE_Y.get(ThreadLocalRandom.current().nextInt(0, RESERVE_Y.size()));
            RESERVE_Y.remove((Integer) this.y);
        } else {
            this.y = ThreadLocalRandom.current().nextInt(0, 800);
        }
        this.waitingOnBridge = false;
    }


    @Override
    public void run() {
        try {
            boarding();
            goToTheBridge();
            simulationCallback.getOnTheBridge(this);
            rideTheBridge();
            simulationCallback.getOffTheBridge(this);
            goToTheParking();
            unloading();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void boarding() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Zbieram pasażerów", id, drivingDirection));
        Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_BOARDING_TIME, MAX_BOARDING_TIME));
    }

    void goToTheBridge() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Jadę na most", id, drivingDirection));
        int distance = GraphicSimulationPaintPanel.PARKING_WIDTH + GraphicSimulationPaintPanel.ROAD_TO_BRIDGE_WIDTH;
        for (int dx = 0; dx < distance; dx++) {
            this.x += drivingDirection == DrivingDirection.EAST ? 1 : -1;
            Thread.sleep((long) GETTING_TO_BRIDGE_TIME / distance);
        }
    }

    void rideTheBridge() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Przejeżdżam przez most", id, drivingDirection));
        int distance = GraphicSimulationPaintPanel.BRIDGE_WIDTH + GraphicSimulationPaintPanel.BRIDGE_QUEUE_WIDTH;
        for (int dx = 0; dx <= distance; dx++) {
            this.x += drivingDirection == DrivingDirection.EAST ? 1 : -1;
            Thread.sleep((long) CROSSING_BRIDGE_TIME / distance);
        }

    }

    void goToTheParking() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Jadę na parking", id, drivingDirection));
        int distance = GraphicSimulationPaintPanel.ROAD_TO_BRIDGE_WIDTH + GraphicSimulationPaintPanel.PARKING_WIDTH;
        for (int dx = 0; dx < distance; dx++) {
            this.x += drivingDirection == DrivingDirection.EAST ? 1 : -1;
            Thread.sleep(GETTING_PARKING_TIME / distance);
        }
        Thread.sleep(GETTING_PARKING_TIME);
    }

    void unloading() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Rozdładowywanie pasażerów", id, drivingDirection));
        if (AVAILABLE_Y_PERFECT_COPY.contains(this.y)) {
            AVAILABLE_Y.add(this.y);
        } else if (RESERVE_Y_PERFECT_COPY.contains(this.y)) {
            RESERVE_Y.add(this.y);
        }
        Thread.sleep(UNLOADING_TIME);
        simulationCallback.getAllBuses().remove(this);
    }

    public void drawBus(Graphics2D g2d) {
        g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
        switch (drivingDirection) {
            case EAST:
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x + 8, y + 10, 10, 10);
                g2d.fillOval(x + 30, y + 10, 10, 10);
                g2d.setColor(waitingOnBridge ? Color.MAGENTA : Color.GREEN);
                g2d.fillPolygon(new int[]{x + 5, x + 30, x + 45, x + 45, x + 5}, new int[]{y - 20, y - 20, y, y + 15, y + 15}, 5);
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(id), x + 5, y + 13);
                break;
            case WEST:
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - 20, y + 10, 10, 10);
                g2d.fillOval(x - 40, y + 10, 10, 10);
                g2d.setColor(waitingOnBridge ? Color.MAGENTA : Color.YELLOW);
                g2d.fillPolygon(new int[]{x - 5, x - 30, x - 45, x - 45, x - 5}, new int[]{y - 20, y - 20, y, y + 15, y + 15}, 5);
                g2d.setColor(Color.BLACK);
                if (id < 10)
                    g2d.drawString(String.valueOf(id), x - 20, y + 13);
                else
                    g2d.drawString(String.valueOf(id), x - 35, y + 13);
                break;


        }
    }

    public int getId() {
        return id;
    }

    public DrivingDirection getDrivingDirection() {
        return drivingDirection;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isWaitingOnBridge() {
        return waitingOnBridge;
    }

    public void setWaitingOnBridge(boolean waitingOnBridge) {
        this.waitingOnBridge = waitingOnBridge;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }


    enum DrivingDirection {
        EAST("W"),
        WEST("Z");

        private final String directionSymbol;

        DrivingDirection(String directionSymbol) {
            this.directionSymbol = directionSymbol;
        }

        @Override
        public String toString() {
            return directionSymbol;
        }
    }
}
