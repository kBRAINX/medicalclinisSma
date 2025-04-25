package medicalclinic.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import medicalclinic.agents.PatientAgent;

public class PatientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private PatientAgent patientAgent;
    private String patientId;

    // Panels
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel messagePanel;

    // Components
    private JLabel statusLabel;
    private JLabel locationLabel;
    private JTextArea messagesArea;
    private JScrollPane messageScrollPane;
    private JButton submitButton;

    // Form data
    private HashMap<String, JComponent> formFields;
    private String currentFormId;

    public PatientGUI(String patientId) {
        this.patientId = patientId;
        this.formFields = new HashMap<>();

        // Setup the frame
        setTitle("Patient " + patientId + " - Cabinet Médical");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (patientAgent != null) {
                    patientAgent.exitSystem();
                }
                dispose();
            }
        });

        // Initialize the main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 250));

        // Create status panel at the top
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(240, 240, 250));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)));

        // Create ID and location labels
        JLabel idLabel = new JLabel("Identifiant: " + patientId);
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));

        locationLabel = new JLabel("Emplacement: Entrée");
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statusLabel = new JLabel("Statut: En attente");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statusPanel.add(idLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statusPanel.add(locationLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statusPanel.add(statusLabel);

        // Create main content panel (center)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(240, 240, 250));

        // Form panel (initially empty)
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Formulaire"));
        formPanel.setBackground(Color.WHITE);

        // Submit button (initially hidden)
        submitButton = new JButton("Soumettre");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(100, 150, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });
        submitButton.setVisible(false);

        // Message panel
        messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Messages"));
        messagePanel.setBackground(Color.WHITE);

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        messagesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messagesArea.setBackground(new Color(250, 250, 250));

        // Make the text area automatically scroll to the bottom
        DefaultCaret caret = (DefaultCaret) messagesArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        messageScrollPane = new JScrollPane(messagesArea);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);

        // Add panels to content panel
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(submitButton, BorderLayout.CENTER);
        contentPanel.add(messagePanel, BorderLayout.SOUTH);

        // Add all panels to main panel
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Set the content pane
        setContentPane(mainPanel);

        // Initialize with a welcome message
        displayMessage("Bienvenue au Cabinet Médical. Veuillez attendre...");
    }

    // Set the patient agent reference
    public void setPatientAgent(PatientAgent agent) {
        this.patientAgent = agent;
    }

    // Display a message in the messages area
    public void displayMessage(String message) {
        messagesArea.append(message + "\n\n");
    }

    // Update the patient's location
    public void updateLocation(String location) {
        locationLabel.setText("Emplacement: " + location);
        displayMessage("Vous êtes maintenant dans: " + location);
    }

    // Display the personal information form
    public void displayPersonalForm(String formJson) {
        clearForm();

        try {
            JsonObject formObj = JsonParser.parseString(formJson).getAsJsonObject();
            String formId = formObj.get("formId").getAsString();
            String title = formObj.get("title").getAsString();

            currentFormId = formId;

            formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title));

            JsonArray fields = formObj.getAsJsonArray("fields");

            for (JsonElement fieldElement : fields) {
                JsonObject field = fieldElement.getAsJsonObject();
                String name = field.get("name").getAsString();
                String label = field.get("label").getAsString();
                String type = field.get("type").getAsString();
                boolean required = field.has("required") && field.get("required").getAsBoolean();

                JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
                fieldPanel.setBackground(Color.WHITE);
                fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                JLabel fieldLabel = new JLabel(label + (required ? " *" : ""));
                fieldLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JComponent inputComponent;

                if ("date".equals(type)) {
                    JFormattedTextField dateField = new JFormattedTextField();
                    dateField.setColumns(20);
                    dateField.setToolTipText("Format: YYYY-MM-DD");
                    dateField.setFont(new Font("Arial", Font.PLAIN, 14));
                    inputComponent = dateField;
                } else {
                    JTextField textField = new JTextField();
                    textField.setColumns(20);
                    textField.setFont(new Font("Arial", Font.PLAIN, 14));
                    inputComponent = textField;
                }

                fieldPanel.add(fieldLabel, BorderLayout.NORTH);
                fieldPanel.add(inputComponent, BorderLayout.CENTER);

                formPanel.add(fieldPanel);
                formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                formFields.put(name, inputComponent);
            }

            submitButton.setVisible(true);

            // Repaint
            formPanel.revalidate();
            formPanel.repaint();

            displayMessage("Veuillez remplir le formulaire " + title);

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Erreur lors de l'affichage du formulaire : " + e.getMessage());
        }
    }

    // Display questions from the nurse
    public void displayNurseQuestions(String questionsJson) {
        displayForm("Questionnaire de l'Infirmier", questionsJson);
    }

    // Display questions from the doctor
    public void displayDoctorQuestions(String questionsJson) {
        displayForm("Questionnaire du Médecin", questionsJson);
    }

    // Generic method to display a form
    private void displayForm(String formTitle, String formJson) {
        clearForm();

        try {
            JsonObject formObj = JsonParser.parseString(formJson).getAsJsonObject();
            String formId = formObj.get("formId").getAsString();

            currentFormId = formId;

            formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), formTitle));

            JsonArray fields = formObj.getAsJsonArray("fields");

            for (JsonElement fieldElement : fields) {
                JsonObject field = fieldElement.getAsJsonObject();
                String name = field.get("name").getAsString();
                String label = field.get("label").getAsString();
                boolean required = field.has("required") && field.get("required").getAsBoolean();

                JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
                fieldPanel.setBackground(Color.WHITE);
                fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel fieldLabel = new JLabel(label + (required ? " *" : ""));
                fieldLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JTextArea textArea = new JTextArea(3, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setFont(new Font("Arial", Font.PLAIN, 14));
                JScrollPane scrollPane = new JScrollPane(textArea);

                fieldPanel.add(fieldLabel, BorderLayout.NORTH);
                fieldPanel.add(scrollPane, BorderLayout.CENTER);

                formPanel.add(fieldPanel);
                formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                formFields.put(name, textArea);
            }

            submitButton.setVisible(true);

            // Repaint
            formPanel.revalidate();
            formPanel.repaint();

            displayMessage("Veuillez répondre aux questions.");

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Erreur lors de l'affichage des questions : " + e.getMessage());
        }
    }

    // Display the diagnosis from the doctor
    public void displayDiagnosis(String diagnosis) {
        clearForm();

        JTextArea diagnosisArea = new JTextArea(diagnosis);
        diagnosisArea.setEditable(false);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        diagnosisArea.setFont(new Font("Arial", Font.BOLD, 14));
        diagnosisArea.setBackground(new Color(240, 240, 255));
        diagnosisArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(diagnosisArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Diagnostic et Prescription"));

        formPanel.setLayout(new BorderLayout());
        formPanel.add(scrollPane, BorderLayout.CENTER);

        // Add an exit button
        JButton exitButton = new JButton("Terminer et Quitter");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setBackground(new Color(100, 150, 255));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (patientAgent != null) {
                    patientAgent.exitSystem();
                }
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(exitButton);

        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.setVisible(false);

        // Repaint
        formPanel.revalidate();
        formPanel.repaint();

        displayMessage("Consultation terminée. Vous pouvez maintenant quitter le cabinet médical.");
    }

    // Clear the form panel
    private void clearForm() {
        formPanel.removeAll();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formFields.clear();
    }

    // Submit the form data
    private void submitForm() {
        if (patientAgent == null) {
            displayMessage("Erreur: Agent patient non initialisé.");
            return;
        }

        HashMap<String, String> formData = new HashMap<>();

        for (String fieldName : formFields.keySet()) {
            JComponent component = formFields.get(fieldName);
            String value = "";

            if (component instanceof JTextField) {
                value = ((JTextField) component).getText();
            } else if (component instanceof JTextArea) {
                value = ((JTextArea) component).getText();
            }

            formData.put(fieldName, value);
        }

        if ("personalInfo".equals(currentFormId)) {
            patientAgent.fillPersonalForm(formData);
            statusLabel.setText("Statut: Formulaire personnel soumis");
        } else if ("symptomQuestions".equals(currentFormId)) {
            patientAgent.answerNurseQuestions(formData);
            statusLabel.setText("Statut: Symptômes communiqués");
        } else if ("doctorQuestions".equals(currentFormId)) {
            patientAgent.answerDoctorQuestions(formData);
            statusLabel.setText("Statut: En consultation");
        }

        // Hide the submit button
        submitButton.setVisible(false);
    }
}
