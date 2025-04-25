package medicalclinic.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import medicalclinic.agents.DoctorAgent;
import medicalclinic.models.PatientRecord;

public class DoctorGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private DoctorAgent doctorAgent;

    // Components
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JLabel statusLabel;
    private JLabel patientLabel;
    private JLabel specialtyLabel;
    private JLabel roomLabel;
    private JPanel patientInfoPanel;

    // Date format for logs
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public DoctorGUI(DoctorAgent agent) {
        this.doctorAgent = agent;

        // Setup the frame
        setTitle("Médecin - Cabinet Médical");
        setSize(900, 700);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ensure the agent is properly terminated
                if (doctorAgent != null) {
                    doctorAgent.doDelete();
                }
                dispose();
            }
        });

        // Initialize the main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 120, 200));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title and specialty information
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Interface Médecin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        specialtyLabel = new JLabel("Spécialité: " + (doctorAgent != null ? doctorAgent.getSpecialty() : "N/A"));
        specialtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        specialtyLabel.setForeground(Color.WHITE);
        specialtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        roomLabel = new JLabel("Salle: " + (doctorAgent != null ? doctorAgent.getRoomNumber() : "N/A"));
        roomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roomLabel.setForeground(Color.WHITE);
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(specialtyLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(roomLabel);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);

        statusLabel = new JLabel("Statut: " + (doctorAgent != null && doctorAgent.isAvailable() ? "Disponible" : "Occupé"));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);

        patientLabel = new JLabel("Patient: Aucun");
        patientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientLabel.setForeground(Color.WHITE);

        statusPanel.add(patientLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        statusPanel.add(statusLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        // Create split pane for content
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3); // Top panel gets 30% of space

        // Patient info panel (top)
        patientInfoPanel = new JPanel(new BorderLayout());
        patientInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Informations du Patient"));
        patientInfoPanel.setBackground(Color.WHITE);

        // Initially empty
        JLabel noPatientLabel = new JLabel("Aucun patient en consultation pour le moment");
        noPatientLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noPatientLabel.setHorizontalAlignment(SwingConstants.CENTER);
        patientInfoPanel.add(noPatientLabel, BorderLayout.CENTER);

        // Log panel (bottom)
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

        // Add panels to split pane
        splitPane.setTopComponent(patientInfoPanel);
        splitPane.setBottomComponent(logPanel);

        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Set the content pane
        setContentPane(mainPanel);

        // Initialize with a welcome message
        displayMessage("Système démarré. En attente de patients...");
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
            statusLabel.setText("Statut: Disponible");
        } else {
            patientLabel.setText("Patient: " + patientId);
            statusLabel.setText("Statut: En consultation");
        }
    }

    // Display patient information
    public void displayPatientInfo(PatientRecord patientRecord) {
        patientInfoPanel.removeAll();

        if (patientRecord == null) {
            JLabel noPatientLabel = new JLabel("Aucun patient en consultation pour le moment");
            noPatientLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noPatientLabel.setHorizontalAlignment(SwingConstants.CENTER);
            patientInfoPanel.add(noPatientLabel, BorderLayout.CENTER);
        } else {
            // Create a panel with patient information
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Patient basic info
            String fullName = patientRecord.getFullName();
            HashMap<String, String> personalInfo = patientRecord.getPersonalInfo();
            HashMap<String, String> symptomsInfo = patientRecord.getSymptomsInfo();

            // Add personal information
            JLabel nameLabel = new JLabel("Nom: " + fullName);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel idLabel = new JLabel("ID: " + patientRecord.getPatientId());
            idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel ageLabel = new JLabel("Date de naissance: " +
                personalInfo.getOrDefault("birthDate", "Non renseigné"));
            ageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            ageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel contactLabel = new JLabel("Contact: " +
                personalInfo.getOrDefault("phone", "Non renseigné"));
            contactLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(idLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(ageLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(contactLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Add symptoms information
            JLabel symptomsTitle = new JLabel("Symptômes:");
            symptomsTitle.setFont(new Font("Arial", Font.BOLD, 16));
            symptomsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(symptomsTitle);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JPanel symptomsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            symptomsPanel.setBackground(new Color(245, 245, 250));
            symptomsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            for (String key : symptomsInfo.keySet()) {
                if (key.equals("patientId")) continue;

                JPanel symptomPanel = new JPanel(new BorderLayout());
                symptomPanel.setBackground(new Color(245, 245, 250));

                JLabel questionLabel = new JLabel(key + ":");
                questionLabel.setFont(new Font("Arial", Font.BOLD, 13));

                JLabel answerLabel = new JLabel(symptomsInfo.get(key));
                answerLabel.setFont(new Font("Arial", Font.PLAIN, 13));

                symptomPanel.add(questionLabel, BorderLayout.WEST);
                symptomPanel.add(answerLabel, BorderLayout.CENTER);

                symptomsPanel.add(symptomPanel);
            }

            if (symptomsInfo.isEmpty() || symptomsInfo.size() == 1) {
                JLabel noSymptomsLabel = new JLabel("Aucun symptôme renseigné");
                noSymptomsLabel.setFont(new Font("Arial", Font.ITALIC, 13));
                symptomsPanel.add(noSymptomsLabel);
            }

            JScrollPane symptomsScrollPane = new JScrollPane(symptomsPanel);
            symptomsScrollPane.setPreferredSize(new Dimension(500, 200));
            symptomsScrollPane.setBorder(null);

            infoPanel.add(symptomsScrollPane);

            // Add info panel to the patient info panel
            JScrollPane infoScrollPane = new JScrollPane(infoPanel);
            infoScrollPane.setBorder(null);
            patientInfoPanel.add(infoScrollPane, BorderLayout.CENTER);

            // Update the patient label
            setCurrentPatient(patientRecord.getPatientId());
        }

        // Refresh the panel
        patientInfoPanel.revalidate();
        patientInfoPanel.repaint();
    }
}
