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
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import medicalclinic.agents.ReceptionistAgent;
import medicalclinic.models.Consultation;
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
    private JLabel statusLabel;
    private JLabel countLabel;

    // Current selected patient
    private String selectedPatientId;

    // Date format for logs
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ReceptionistGUI(ReceptionistAgent agent) {
        this.receptionistAgent = agent;

        // Setup the frame
        setTitle("Réceptionniste - Cabinet Médical");
        setSize(1100, 800);
        setMinimumSize(new Dimension(900, 700));
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
        JPanel headerPanel = createHeaderPanel();

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // Create left panel (lists)
        JPanel listsPanel = createListsPanel();
        listsPanel.setPreferredSize(new Dimension(300, 500));

        // Create right panel (patient details and log)
        JPanel rightPanel = createRightPanel();

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

    // Create header panel
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(80, 130, 190));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Réception - Cabinet Médical");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);

        statusLabel = new JLabel("État: Prêt");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);

        countLabel = new JLabel("Patients: 0 | En attente: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(Color.WHITE);

        statusPanel.add(countLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        statusPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    // Create lists panel (left side)
    private JPanel createListsPanel() {
        JPanel listsPanel = new JPanel(new BorderLayout(0, 10));

        // Patients list
        JPanel patientsPanel = new JPanel(new BorderLayout());
        patientsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Patients Enregistrés"));

        patientsListModel = new DefaultListModel<>();
        patientsList = new JList<>(patientsListModel);
        patientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsList.setFont(new Font("Arial", Font.PLAIN, 14));
        patientsList.setCellRenderer(new PatientListCellRenderer());

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

        // Search panel for patients
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Rechercher un patient");

        JButton searchButton = new JButton("Rechercher");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        patientsPanel.add(searchPanel, BorderLayout.NORTH);

        // Waiting list
        JPanel waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Liste d'Attente"));

        waitingListModel = new DefaultListModel<>();
        waitingList = new JList<>(waitingListModel);
        waitingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        waitingList.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingList.setCellRenderer(new WaitingListCellRenderer());

        waitingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && waitingList.getSelectedValue() != null) {
                    String selectedItem = waitingList.getSelectedValue();
                    String patientId = selectedItem.split(" ")[0];

                    // Afficher les détails du patient
                    selectedPatientId = patientId;
                    displayPatientDetails(patientId);

                    // Permettre de prioriser un patient (pourrait être implémenté)
                    int response = JOptionPane.showConfirmDialog(
                        ReceptionistGUI.this,
                        "Voulez-vous marquer ce patient comme prioritaire?",
                        "Priorisation du patient",
                        JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
                        // TODO: Implémenter la priorisation du patient
                        displayMessage("Patient " + patientId + " marqué comme prioritaire");
                    }
                }
            }
        });

        JScrollPane waitingScrollPane = new JScrollPane(waitingList);
        waitingPanel.add(waitingScrollPane, BorderLayout.CENTER);

        // Add both lists to the lists panel
        listsPanel.add(patientsPanel, BorderLayout.CENTER);
        listsPanel.add(waitingPanel, BorderLayout.SOUTH);

        return listsPanel;
    }

    // Create right panel (patient details and log)
    private JPanel createRightPanel() {
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

        // Clear log button
        JButton clearLogButton = new JButton("Effacer le journal");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        logPanel.add(clearLogButton, BorderLayout.SOUTH);

        // Add panels to right panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(patientDetailsPanel);
        splitPane.setBottomComponent(logPanel);
        splitPane.setResizeWeight(0.6); // 60% hauteur pour les détails
        splitPane.setDividerSize(8);

        rightPanel.add(splitPane, BorderLayout.CENTER);

        return rightPanel;
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

        countLabel.setText("Patients: " + patientRecords.size() + " | En attente: " + waitingListModel.size());
    }

    // Update the waiting list
    public void updateWaitingPatients(LinkedList<WaitingPatientInfo> waitingPatients) {
        waitingListModel.clear();

        for (WaitingPatientInfo info : waitingPatients) {
            String patientId = info.getPatientId();
            int waitingTime = info.getWaitingTimeInMinutes();
            String display = patientId + " (" + waitingTime + " min)";

            if (info.isUrgent()) {
                display = "⚠️ " + display;
            }

            waitingListModel.addElement(display);
        }

        if (countLabel != null) {
            int patientsCount = patientsListModel.size();
            countLabel.setText("Patients: " + patientsCount + " | En attente: " + waitingPatients.size());
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
            createPatientDetailsContent(detailsPanel, record);
        }

        // Add the details panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        patientDetailsPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh the panel
        patientDetailsPanel.revalidate();
        patientDetailsPanel.repaint();
    }

    // Create detailed content for patient panel
    private void createPatientDetailsContent(JPanel detailsPanel, PatientRecord record) {
        String fullName = record.getFullName();
        HashMap<String, String> personalInfo = record.getPersonalInfo();

        // Create header with patient name and ID
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel nameLabel = new JLabel("Nom: " + fullName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel idLabel = new JLabel("ID: " + record.getPatientId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(idLabel, BorderLayout.EAST);

        detailsPanel.add(headerPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create tabs for different information
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Tab for personal information
        JPanel personalPanel = new JPanel();
        personalPanel.setLayout(new BoxLayout(personalPanel, BoxLayout.Y_AXIS));
        personalPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] personalFields = {"birthDate", "gender", "address", "city", "phone", "email",
            "insuranceInfo", "emergencyContact", "previousMedicalHistory"};
        String[] personalLabels = {"Date de naissance", "Genre", "Adresse", "Ville", "Téléphone", "Email",
            "Assurance", "Contact d'urgence", "Antécédents médicaux"};

        for (int i = 0; i < personalFields.length; i++) {
            String field = personalFields[i];
            if (personalInfo.containsKey(field)) {
                JPanel fieldPanel = new JPanel(new BorderLayout());
                fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

                JLabel fieldLabel = new JLabel(personalLabels[i] + ":");
                fieldLabel.setFont(new Font("Arial", Font.BOLD, 14));

                JLabel valueLabel = new JLabel(personalInfo.get(field));
                valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                fieldPanel.add(fieldLabel, BorderLayout.WEST);
                fieldPanel.add(valueLabel, BorderLayout.CENTER);

                personalPanel.add(fieldPanel);
                personalPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Tab for symptoms information
        JPanel symptomsPanel = new JPanel();
        symptomsPanel.setLayout(new BoxLayout(symptomsPanel, BoxLayout.Y_AXIS));
        symptomsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        HashMap<String, String> symptomsInfo = record.getSymptomsInfo();
        if (symptomsInfo != null && !symptomsInfo.isEmpty()) {
            for (Map.Entry<String, String> entry : symptomsInfo.entrySet()) {
                if (!entry.getKey().equals("patientId")) {
                    JPanel symptomPanel = new JPanel(new BorderLayout());
                    symptomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                    JLabel symptomLabel = new JLabel(entry.getKey() + ":");
                    symptomLabel.setFont(new Font("Arial", Font.BOLD, 14));

                    JTextArea symptomValue = new JTextArea(entry.getValue());
                    symptomValue.setFont(new Font("Arial", Font.PLAIN, 14));
                    symptomValue.setLineWrap(true);
                    symptomValue.setWrapStyleWord(true);
                    symptomValue.setEditable(false);

                    JScrollPane valueScroll = new JScrollPane(symptomValue);
                    valueScroll.setPreferredSize(new Dimension(400, 50));

                    symptomPanel.add(symptomLabel, BorderLayout.NORTH);
                    symptomPanel.add(valueScroll, BorderLayout.CENTER);

                    symptomsPanel.add(symptomPanel);
                    symptomsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } else {
            JLabel noSymptomsLabel = new JLabel("Aucune information sur les symptômes");
            noSymptomsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            symptomsPanel.add(noSymptomsLabel);
        }

        // Tab for consultation history
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<Consultation> consultations = record.getConsultationHistory();
        if (consultations != null && !consultations.isEmpty()) {
            for (Consultation consultation : consultations) {
                JPanel consultPanel = createConsultationPanel(consultation);
                historyPanel.add(consultPanel);
                historyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        } else {
            JLabel noHistoryLabel = new JLabel("Aucun historique de consultation");
            noHistoryLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            historyPanel.add(noHistoryLabel);
        }

        // Add all tabs
        tabbedPane.addTab("Informations personnelles", new JScrollPane(personalPanel));
        tabbedPane.addTab("Symptômes", new JScrollPane(symptomsPanel));
        tabbedPane.addTab("Historique des consultations", new JScrollPane(historyPanel));

        detailsPanel.add(tabbedPane);
    }

    // Create a panel for a single consultation
    private JPanel createConsultationPanel(Consultation consultation) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Consultation du " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(consultation.getTimestamp())));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Doctor information
        JLabel doctorLabel = new JLabel("Médecin: " + consultation.getDoctorId());
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(doctorLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Diagnosis
        JLabel diagnosisTitle = new JLabel("Diagnostic:");
        diagnosisTitle.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(diagnosisTitle);

        JTextArea diagnosisText = new JTextArea(consultation.getDiagnosis());
        diagnosisText.setFont(new Font("Arial", Font.PLAIN, 14));
        diagnosisText.setLineWrap(true);
        diagnosisText.setWrapStyleWord(true);
        diagnosisText.setEditable(false);
        diagnosisText.setBackground(new Color(250, 250, 250));
        diagnosisText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane diagnosisScroll = new JScrollPane(diagnosisText);
        diagnosisScroll.setPreferredSize(new Dimension(450, 100));
        contentPanel.add(diagnosisScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Disease
        if (consultation.getDisease() != null) {
            JLabel diseaseLabel = new JLabel("Maladie: " + consultation.getDisease().getName());
            diseaseLabel.setFont(new Font("Arial", Font.BOLD, 14));
            contentPanel.add(diseaseLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Prescriptions
        JLabel prescriptionsTitle = new JLabel("Prescriptions:");
        prescriptionsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(prescriptionsTitle);

        if (consultation.getPrescriptions() != null && !consultation.getPrescriptions().isEmpty()) {
            JTextArea prescriptionsText = new JTextArea();
            prescriptionsText.setFont(new Font("Arial", Font.PLAIN, 14));
            prescriptionsText.setLineWrap(true);
            prescriptionsText.setWrapStyleWord(true);
            prescriptionsText.setEditable(false);
            prescriptionsText.setBackground(new Color(250, 250, 250));
            prescriptionsText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            for (int i = 0; i < consultation.getPrescriptions().size(); i++) {
                prescriptionsText.append((i+1) + ". " + consultation.getPrescriptions().get(i).getName() +
                    " - " + consultation.getPrescriptions().get(i).getDosage() +
                    " - " + consultation.getPrescriptions().get(i).getInstructions() + "\n");
            }

            JScrollPane prescriptionsScroll = new JScrollPane(prescriptionsText);
            prescriptionsScroll.setPreferredSize(new Dimension(450, 80));
            contentPanel.add(prescriptionsScroll);
        } else {
            JLabel noPrescriptionsLabel = new JLabel("Aucune prescription");
            noPrescriptionsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            contentPanel.add(noPrescriptionsLabel);
        }

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // Patient list cell renderer
    private class PatientListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

            // Customize appearance
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Return the modified label
            return label;
        }
    }

    // Waiting list cell renderer
    private class WaitingListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

            // Add visual indication for urgent cases
            String text = (String) value;
            if (text.startsWith("⚠️")) {
                label.setForeground(Color.RED);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }

            // Customize appearance
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Return the modified label
            return label;
        }
    }

    // Update patient record
    public void updatePatientRecord(PatientRecord record) {
        if (record != null && record.getPatientId().equals(selectedPatientId)) {
            displayPatientDetails(selectedPatientId);
        }
    }

    // Set status
    public void setStatus(String status) {
        statusLabel.setText("État: " + status);
    }
}
