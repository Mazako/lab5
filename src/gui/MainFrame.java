package gui;

import simulation.NarrowBridgeSimulation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Hashtable;

public class MainFrame extends JFrame implements ChangeListener {
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 800;
    private final JLabel drivingLimitationsLabel = new JLabel("Ograniczenie ruchu: ");
    private final JLabel drivingIntensityLabel = new JLabel("Natężenie ruchu: ");
    private final JLabel atBridgeLabel = new JLabel("Na moście: ");
    private final JLabel queueLabel = new JLabel("    Kolejka: ");
    private final JComboBox<?> drivingLimitationsComboBox = new JComboBox<>();
    private final JSlider drivingIntensitySlider = new JSlider();
    private final JTextField atBridgeTextField =  new JTextField();
    private final JTextField queueTextField = new JTextField();
    private final JScrollPane logPanel = new JScrollPane();
    private final JTextArea logTextArea;

    private final NarrowBridgeSimulation narrowBridgeSimulation;

    public MainFrame(JTextArea logs, NarrowBridgeSimulation narrowBridgeSimulation) {
        this.logTextArea = logs;
        this.narrowBridgeSimulation = narrowBridgeSimulation;
        Font labelFont = new Font(Font.SERIF, Font.PLAIN, 18);
        Font textFieldFont = new Font(Font.MONOSPACED, Font.ITALIC, 18);
        this.setTitle("Symulacja przejazdu przez wąski most");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        drivingLimitationsLabel.setFont(labelFont);
        this.add(drivingLimitationsLabel);

        drivingLimitationsComboBox.setFont(labelFont);
        drivingLimitationsComboBox.setPreferredSize(new Dimension(300, 20));
        this.add(drivingLimitationsComboBox);

        drivingIntensityLabel.setFont(labelFont);
        this.add(drivingIntensityLabel);

        drivingIntensitySlider.setPaintLabels(true);
        drivingIntensitySlider.setPreferredSize(new Dimension(370, 50));
        drivingIntensitySlider.setMaximum(1000);
        drivingIntensitySlider.setMajorTickSpacing(500);
        drivingIntensitySlider.setPaintTicks(true);
        drivingIntensitySlider.setValue(0);
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
        sliderLabels.put(0, new JLabel("Małe(2s)"));
        sliderLabels.put(500, new JLabel("Średnie(1.5s)"));
        sliderLabels.put(1000, new JLabel("Duże(1s)"));
        drivingIntensitySlider.setLabelTable(sliderLabels);
        drivingIntensitySlider.addChangeListener(this);
        this.add(drivingIntensitySlider);

        atBridgeLabel.setFont(labelFont);
        this.add(atBridgeLabel);

        atBridgeTextField.setPreferredSize(new Dimension(430, 28));
        atBridgeTextField.setEnabled(false);
        atBridgeTextField.setFont(textFieldFont);
        this.add(atBridgeTextField);

        queueLabel.setFont(labelFont);
        this.add(queueLabel);

        queueTextField.setPreferredSize(new Dimension(430, 28));
        queueTextField.setFont(textFieldFont);
        queueTextField.setEnabled(false);
        this.add(queueTextField);

        logPanel.setPreferredSize(new Dimension(550, 600));
        logPanel.setViewportView(logTextArea);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
        logTextArea.append("chujxD");
        this.add(logPanel);
        this.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == drivingIntensitySlider) {
            narrowBridgeSimulation.setPauseDelay(drivingIntensitySlider.getValue());
        }
    }
}
