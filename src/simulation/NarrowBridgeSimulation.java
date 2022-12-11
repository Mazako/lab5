package simulation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NarrowBridgeSimulation implements Runnable{
    private static final int ANTI_DEADLOCK_CARS_LIMIT = 6;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private JTextArea logs;
    private boolean started;

    private final LinkedList<Bus> busQueue = new LinkedList<>();
    private final List<Bus> busesOnTheBridge = new LinkedList<>();

    private final List<Bus> allBuses;

    private int maxBusesOnBridge = 1;

    private volatile boolean renovation = false;

    private SimulationTypes simulationType = SimulationTypes.ONLY_ONE;

    private Bus.DrivingDirection allowedDirection;

    private int drivenCarsInOneDirection = 0;
    private int pauseDelay;
    private int westProbability = 50;
    public NarrowBridgeSimulation(JTextArea logs) {
        this.logs = logs;
        this.started = false;
        allBuses =  Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {
        this.started = true;
        while (started) {
            new Thread(() -> {
                Bus.DrivingDirection direction = chooseRandomDirection();
                Bus bus = new Bus(this, direction);
                allBuses.add(bus);
                new Thread(bus).start();
            }).start();
            try {
                Thread.sleep(5000 - pauseDelay);
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null,
                        "Wystąpił błąd symulacji. program zostanie zamkniety",
                        "BŁĄD KRYTYCZNY",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        }
    }

    private Bus.DrivingDirection chooseRandomDirection() {
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 101);
        System.out.println(randomNumber + " - " + westProbability);
        return randomNumber <= westProbability ? Bus.DrivingDirection.WEST : Bus.DrivingDirection.EAST;
    }

     synchronized void writeToLog(String message) {
        SwingUtilities.invokeLater(() -> logs.append(message + "\n"));
    }

    synchronized void getOnTheBridge(Bus bus) throws InterruptedException {
        busQueue.add(bus);
        support.firePropertyChange("Changed sizes", busQueue.size() - 1, busQueue.size());
        while(!busCanGetOnBridge(bus)) {
            bus.setWaitingOnBridge(true);
            writeToLog(String.format("[%d -> %s]: czeka przed mostem", bus.getId(), bus.getDrivingDirection()));
            wait();
        }
        bus.setWaitingOnBridge(false);
        busQueue.remove(bus);
        busesOnTheBridge.add(bus);
        if (simulationType == SimulationTypes.UNIDIRECTIONAL) {
            allowedDirection = bus.getDrivingDirection();
            drivenCarsInOneDirection++;
        }
        support.firePropertyChange("Changed sizes", busQueue.size() - 1, busQueue.size());
    }

    private synchronized boolean busCanGetOnBridge(Bus bus) {
        if (renovation)
            return  false;
        if (simulationType == SimulationTypes.ONLY_ONE || simulationType == SimulationTypes.HIGHWAY) {
            return busesOnTheBridge.size() < maxBusesOnBridge;
        } else if (simulationType == SimulationTypes.BIDIRECTIONAL) {
            long count = busesOnTheBridge.stream()
                    .filter(b -> b.getDrivingDirection() == bus.getDrivingDirection())
                    .count();
            return count < maxBusesOnBridge / 2;
        } else if (simulationType == SimulationTypes.UNIDIRECTIONAL) {
            if (busesOnTheBridge.size() < maxBusesOnBridge && drivenCarsInOneDirection <= ANTI_DEADLOCK_CARS_LIMIT) {
                return allowedDirection == null || bus.getDrivingDirection() == allowedDirection;
            }
        }
        return false;
    }

    synchronized void getOffTheBridge(Bus bus) throws InterruptedException {
        busesOnTheBridge.remove(bus);
        writeToLog(String.format("[%d -> %s]: Opuszcza most",bus.getId(), bus.getDrivingDirection()));
        if (simulationType == SimulationTypes.UNIDIRECTIONAL) {
            if (busesOnTheBridge.isEmpty()) {
                allowedDirection = null;
                drivenCarsInOneDirection = 0;
            } else if (drivenCarsInOneDirection >= ANTI_DEADLOCK_CARS_LIMIT) {
                drivenCarsInOneDirection = 0;
                allowedDirection = allowedDirection == Bus.DrivingDirection.EAST ? Bus.DrivingDirection.WEST : Bus.DrivingDirection.EAST;
            }
        }
        support.firePropertyChange("Changed sizes", busesOnTheBridge.size() - 1, busesOnTheBridge.size());
        notifyAll();
    }

    public synchronized void changeRules(SimulationTypes simulationType) throws InterruptedException {
        renovation = true;
        while (!busesOnTheBridge.isEmpty()) {
            wait();
        }
        writeToLog("Rozpoczynam renowację mostu");
        wait(2000);
        switch (simulationType) {
            case ONLY_ONE:
                maxBusesOnBridge = 1;
                break;
            case HIGHWAY:
                maxBusesOnBridge = 9999;
                break;
            case BIDIRECTIONAL:
                maxBusesOnBridge = 4;
                break;
            case UNIDIRECTIONAL:
                maxBusesOnBridge = 2;
                break;
        }
        this.simulationType = simulationType;
        writeToLog("RENOWACJA MOSTU ZAKOŃCZONA.");
        renovation = false;
        notifyAll();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public synchronized String getWaitingBusesStringMessage() {
        return busQueue.stream()
                .map(Bus::toString)
                .reduce("", (start, current) -> start + " " + current);
    }

    public synchronized String getBussesOnBridgeMessage() {
        return busesOnTheBridge.stream()
                .map(Bus::toString)
                .reduce("", (start, current) -> start + " " + current);
    }
    public JTextArea getLogs() {
        return logs;
    }

    public void setLogs(JTextArea logs) {
        this.logs = logs;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getPauseDelay() {
        return pauseDelay;
    }

    public void setPauseDelay(int pauseDelay) {
        this.pauseDelay = pauseDelay;
    }

    public List<Bus> getAllBuses() {
        return allBuses;
    }

    public void setWestProbability(int westProbability) {
        this.westProbability = westProbability;
    }
}
