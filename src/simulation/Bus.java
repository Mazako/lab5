package simulation;

import gui.GraphicSimulationPaintPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bus implements Runnable{

    public static final int MIN_BOARDING_TIME = 1000;
    public static final int MAX_BOARDING_TIME = 10000;
    public static final int GETTING_TO_BRIDGE_TIME = 1500;
    public static final int CROSSING_BRIDGE_TIME = 3000;
    public static final int GETTING_PARKING_TIME = 1500;
    public static final int UNLOADING_TIME = 1000;
    private static int busesId;

    private static final List<Integer> AVAILABLE_Y = new ArrayList<>() ;

    static {
          IntStream.iterate(0, x -> x <= 790 , x -> x + 10)
                 .forEach(AVAILABLE_Y::add);
     }
    private final int id;
    private final NarrowBridgeSimulation simulationCallback;
    private final DrivingDirection drivingDirection;

    private int x;
    private int y;
    public Bus(NarrowBridgeSimulation simulationCallback, DrivingDirection drivingDirection) {
        this.simulationCallback = simulationCallback;
        this.drivingDirection = drivingDirection;
        this.id = (++busesId) % 100;
        this.x = drivingDirection == DrivingDirection.EAST ?
                GraphicSimulationPaintPanel.PARKING_WIDTH / 2 :
                GraphicSimulationPaintPanel.PAINT_PANEL_WIDTH - GraphicSimulationPaintPanel.PARKING_WIDTH / 2;
        this.y = AVAILABLE_Y.get(ThreadLocalRandom.current().nextInt(0, AVAILABLE_Y.size()));
        AVAILABLE_Y.remove((Integer) this.y);
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
        this.x = 150;
        Thread.sleep(GETTING_TO_BRIDGE_TIME);
    }

    void rideTheBridge() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Przejeżdżam przez most", id, drivingDirection));
        this.x = 400;
        Thread.sleep(CROSSING_BRIDGE_TIME);
    }

    void goToTheParking() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Jadę na parking", id, drivingDirection));
        this.x = 630;
        Thread.sleep(GETTING_PARKING_TIME);
    }

    void unloading() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Rozdładowywanie pasażerów", id, drivingDirection));
        this.x = 760;
        AVAILABLE_Y.add(this.y);
        Thread.sleep(UNLOADING_TIME);
        simulationCallback.getAllBuses().remove(this);
    }

    public void drawBus(Graphics2D g2d) {
        System.out.println("Rysu");
        g2d.fillRect(x - 30, y - 30, 60, 60);
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
