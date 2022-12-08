package simulation;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class NarrowBridgeSimulation implements Runnable{
    private JTextArea logs;
    private boolean started;

    private final LinkedList<Bus> busQueue = new LinkedList<>();
    private final List<Bus> busesOnTheBridge = new LinkedList<>();

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
                SwingUtilities.invokeLater(() -> logs.append("Rogal ty kurwo\n"));
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
}
