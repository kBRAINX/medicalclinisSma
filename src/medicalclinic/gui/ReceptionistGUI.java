package medicalclinic.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import medicalclinic.agents.ReceptionistAgent;
import medicalclinic.models.PatientRecord;
import medicalclinic.models.WaitingPatientInfo;

public class ReceptionistGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private ReceptionistAgent receptionistAgent;

    // Components
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JList<String> patientsList;
    private DefaultListModel<String> patientsListModel;
    private JList<String> waitingList;
    private DefaultListModel<String> waitingListModel;
    private JPanel patientDetailsPanel;

    // Current selected patient
    private String selectedPatientId;

    // Date format for logs
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ReceptionistGUI(ReceptionistAgent agent) {
        this.receptionistAgent = agent;

        // Setup the frame
        setTitle("Réceptionniste - Cabinet Médical");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ensure the agent is properly terminated
                if (receptionistAgent != null) {
                    receptionistAgent.doDelete();
                }
                dispose();
            }
        });

        // Initialize the main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(80, 130, 190));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Réception - Cabinet Médical");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // Create left panel (lists)
        JPanel listsPanel = new JPanel(new BorderLayout(0, 10));
        listsPanel.setPreferredSize(new Dimension(250, 500));

        // Patients list
        JPanel patientsPanel = new JPanel(new BorderLayout());
        patientsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Patients Enregistrés"));

        patientsListModel = new DefaultListModel<>();
        patientsList = new JList<>(patientsListModel);
        patientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsList.setFont(new Font("Arial", Font.PLAIN, 14));

        patientsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && patientsList.getSelectedValue() != null) {
                    selectedPatientId = patientsList.getSelectedValue();
                    displayPatientDetails(selectedPatientId);
                }
            }
        });

        JScrollPane patientsScrollPane = new JScrollPane(patientsList);
        patientsPanel.add(patientsScrollPane, BorderLayout.CENTER);

        // Waiting list
        JPanel waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Liste d'Attente"));

        waitingListModel = new DefaultListModel<>();
        waitingList = new JList<>(waitingListModel);
        waitingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        waitingList.setFont(new Font("Arial", Font.PLAIN, 14));

        waitingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && waitingList.getSelectedValue() != null) {
                    // TODO: Implement action on double-click (e.g., prioritize patient)
                }
            }
        });

        JScrollPane waitingScrollPane = new JScrollPane(waitingList);
        waitingPanel.add(waitingScrollPane, BorderLayout.CENTER);

        // Add both lists to the lists panel
        listsPanel.add(patientsPanel, BorderLayout.CENTER);
        listsPanel.add(waitingPanel, BorderLayout.SOUTH);

        // Create right panel (patient details and log)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));

        // Patient details panel
        patientDetailsPanel = new JPanel(new BorderLayout());
        patientDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Détails du Patient"));
        patientDetailsPanel.setBackground(Color.WHITE);

        // Initially empty
        JLabel noPatientLabel = new JLabel("Sélectionnez un patient pour voir les détails");
        noPatientLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noPatientLabel.setHorizontalAlignment(SwingConstants.CENTER);
        patientDetailsPanel.add(noPatientLabel, BorderLayout.CENTER);

        // Log panel
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

        // Add panels to right panel
        rightPanel.add(patientDetailsPanel, BorderLayout.CENTER);
        rightPanel.add(logPanel, BorderLayout.SOUTH);

        // Add both main panels to the content panel
        contentPanel.add(listsPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

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

    // Update the list of patients
    public void updatePatientsList(HashMap<String, PatientRecord> patientRecords) {
        patientsListModel.clear();

        for (String patientId : patientRecords.keySet()) {
            patientsListModel.addElement(patientId);
        }
    }

    // Update the waiting list
    public void updateWaitingPatients(LinkedList<WaitingPatientInfo> waitingPatients) {
        waitingListModel.clear();

        for (WaitingPatientInfo info : waitingPatients) {
            String patientId = info.getPatientId();
            int waitingTime = info.getWaitingTimeInMinutes();
            waitingListModel.addElement(patientId + " (" + waitingTime + " min)");
        }
    }

    // Display patient details
    public void updatePatientRecord(PatientRecord record) {
        if (record != null && record.getPatientId().equals(selectedPatientId)) {
            displayPatientDetails(selectedPatientId);
        }
    }

    // Display patient details in the details panel
    private void displayPatientDetails(String patientId) {
        patientDetailsPanel.removeAll();

        // Create a panel for the patient details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Get the patient record from the agent
        PatientRecord record = null;
        // TODO: Get the actual patient record from the agent
        // For now, we'll just display placeholder information

        if (record == null) {
            // No record found, display placeholder
            JLabel nameLabel = new JLabel("Patient: " + patientId);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel statusLabel = new JLabel("Statut: Dossier incomplet");
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            detailsPanel.add(nameLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(statusLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel placeholderLabel = new JLabel("Informations complètes non disponibles");
            placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            placeholderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsPanel.add(placeholderLabel);
        } else {
            // Display patient information
            String fullName = record.getFullName();
            HashMap<String, String> personalInfo = record.getPersonalInfo();

            JLabel nameLabel = new JLabel("Nom: " + fullName);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel idLabel = new JLabel("ID: " + record.getPatientId());
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

            JLabel addressLabel = new JLabel("Adresse: " +
                personalInfo.getOrDefault("address", "Non renseigné"));
            addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            detailsPanel.add(nameLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(idLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(ageLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(contactLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            detailsPanel.add(addressLabel);

            // Display consultation history if available
            if (record.getConsultationHistory() != null && !record.getConsultationHistory().isEmpty()) {
                detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                JLabel historyLabel = new JLabel("Historique des consultations:");
                historyLabel.setFont(new Font("Arial", Font.BOLD, 16));
                historyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailsPanel.add(historyLabel);

                // TODO: Implement consultation history display
            }
        }

        // Add the details panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        patientDetailsPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh the panel
        patientDetailsPanel.revalidate();
        patientDetailsPanel.repaint();
    }
}
