package simulation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NarrowBridgeSimulation implements Runnable{

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private JTextArea logs;
    private boolean started;

    private final LinkedList<Bus> busQueue = new LinkedList<>();
    private final List<Bus> busesOnTheBridge = new LinkedList<>();

    private int maxBusesOnBridge = 1;

    private int pauseDelay;
    public NarrowBridgeSimulation(JTextArea logs) {
        this.logs = logs;
        this.started = false;
    }

    @Override
    public void run() {
        this.started = true;
        while (started) {
            new Thread(() -> {
                Bus.DrivingDirection direction = chooseRandomDirection();
                Bus bus = new Bus(this, direction);
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
        return ThreadLocalRandom.current().nextInt(0, 2) == 0 ? Bus.DrivingDirection.EAST : Bus.DrivingDirection.WEST;
    }

     synchronized void writeToLog(String message) {
        SwingUtilities.invokeLater(() -> logs.append(message + "\n"));
    }

    synchronized void getOnTheBridge(Bus bus) throws InterruptedException {
        busQueue.add(bus);
        support.firePropertyChange("Changed sizes", busQueue.size() - 1, busQueue.size());
        while(busesOnTheBridge.size() >= maxBusesOnBridge) {
            writeToLog(String.format("[%d -> %s]: czeka przed mostem", bus.getId(), bus.getDrivingDirection()));
            wait();
        }
        busQueue.remove(bus);
        busesOnTheBridge.add(bus);
        support.firePropertyChange("Changed sizes", busQueue.size() - 1, busQueue.size());
    }

    synchronized void getOffTheBridge(Bus bus) {
        busesOnTheBridge.remove(bus);
        writeToLog(String.format("[%d -> %s]: Opuszcza most",bus.getId(), bus.getDrivingDirection()));
        support.firePropertyChange("Changed sizes", busesOnTheBridge.size() - 1, busesOnTheBridge.size());
        notify();
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

    public LinkedList<Bus> getBusQueue() {
        return busQueue;
    }
}
