package medicalclinic.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import medicalclinic.agents.NurseAgent;

public class NurseGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private NurseAgent nurseAgent;

    // Components
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JLabel statusLabel;
    private JLabel patientLabel;

    // Date format for logs
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public NurseGUI(NurseAgent agent) {
        this.nurseAgent = agent;

        // Setup the frame
        setTitle("Infirmier - Cabinet Médical");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ensure the agent is properly terminated
                if (nurseAgent != null) {
                    nurseAgent.doDelete();
                }
                dispose();
            }
        });

        // Initialize the main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 245, 255));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(100, 150, 255));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Interface Infirmier");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);

        statusLabel = new JLabel("Statut: En attente");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);

        patientLabel = new JLabel("Patient: Aucun");
        patientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientLabel.setForeground(Color.WHITE);

        statusPanel.add(patientLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        statusPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        // Create log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Journal d'activité"));
        logPanel.setBackground(Color.WHITE);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setBackground(new Color(250, 250, 250));

        // Make the text area automatically scroll to the bottom
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);

        // Set the content pane
        setContentPane(mainPanel);

        // Initialize with a welcome message
        displayMessage("Système démarré. En attente de patients dans la salle d'attente...");
    }

    // Display a message in the log area
    public void displayMessage(String message) {
        String timestamp = dateFormat.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
    }

    // Update the current patient
    public void setCurrentPatient(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            patientLabel.setText("Patient: Aucun");
        } else {
            patientLabel.setText("Patient: " + patientId);
        }
    }

    // Update the status
    public void setStatus(String status) {
        statusLabel.setText("Statut: " + status);
    }
}
