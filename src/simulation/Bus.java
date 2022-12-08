package simulation;

public class Bus implements Runnable{

    private NarrowBridgeSimulation simulationCallback;
    private DrivingDirection drivingDirection;

    public Bus(NarrowBridgeSimulation simulationCallback, DrivingDirection drivingDirection) {
        this.simulationCallback = simulationCallback;
        this.drivingDirection = drivingDirection;
    }

    @Override
    public void run() {

    }

    public enum DrivingDirection {
        LEFT,RIGHT;
    }
}
