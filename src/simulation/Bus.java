package simulation;

import java.util.concurrent.ThreadLocalRandom;
public class Bus implements Runnable{

    public static final int MIN_BOARDING_TIME = 1000;
    public static final int MAX_BOARDING_TIME = 10000;
    public static final int GETTING_TO_BRIDGE_TIME = 500;
    public static final int CROSSING_BRIDGE_TIME = 3000;
    public static final int GETTING_PARKING_TIME = 500;
    public static final int UNLOADING_TIME = 500;
    private static int busesId;
    private final int id;
    private final NarrowBridgeSimulation simulationCallback;
    private final DrivingDirection drivingDirection;

    public Bus(NarrowBridgeSimulation simulationCallback, DrivingDirection drivingDirection) {
        this.simulationCallback = simulationCallback;
        this.drivingDirection = drivingDirection;
        this.id = (++busesId) % 100;
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
        Thread.sleep(GETTING_TO_BRIDGE_TIME);
    }

    void rideTheBridge() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Przejeżdżam przez most", id, drivingDirection));
        Thread.sleep(CROSSING_BRIDGE_TIME);
    }

    void goToTheParking() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Jadę na parking", id, drivingDirection));
        Thread.sleep(GETTING_PARKING_TIME);
    }

    void unloading() throws InterruptedException {
        simulationCallback.writeToLog(String.format("[%d -> %s]: Rozdładowywanie pasażerów", id, drivingDirection));
        Thread.sleep(UNLOADING_TIME);
    }

    public int getId() {
        return id;
    }

    public DrivingDirection getDrivingDirection() {
        return drivingDirection;
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
