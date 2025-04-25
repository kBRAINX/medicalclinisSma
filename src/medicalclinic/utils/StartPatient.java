package medicalclinic.utils;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import medicalclinic.gui.PatientGUI;

/**
 * Classe utilitaire pour démarrer un nouveau patient
 * Affiche une boîte de dialogue pour saisir le nom du patient
 * puis lance un agent Patient avec l'interface graphique correspondante
 */
public class StartPatient {
    public static void main(String[] args) {
        System.out.println("Préparation du lancement d'un nouvel agent Patient...");

        // Interface pour obtenir le nom du patient
        JFrame frame = new JFrame("Nouveau Patient");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(450, 250);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 250));

        JLabel titleLabel = new JLabel("Enregistrement d'un nouveau patient");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(240, 240, 250));

        JLabel nameLabel = new JLabel("Votre nom:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel infoLabel = new JLabel("Ces informations serviront uniquement à créer votre dossier médical");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);

        JButton submitButton = new JButton("Lancer");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(100, 150, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(new JLabel()); // Espace vide
        formPanel.add(infoLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 250));
        buttonPanel.add(submitButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setVisible(true);

        // Gestionnaire d'événements pour le bouton de soumission
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer le nom du patient
                String patientName = nameField.getText().trim();

                if (patientName.isEmpty()) {
                    patientName = "patient" + UUID.randomUUID().toString().substring(0, 8);
                }

                final String patientId = patientName.replaceAll("\\s+", "_").toLowerCase();

                // Fermer la fenêtre de dialogue
                frame.dispose();

                // Lancer l'agent Patient
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        startPatientAgent(patientId);
                    }
                });
            }
        });
    }

    private static void startPatientAgent(String patientId) {
        System.out.println("Lancement de l'agent Patient " + patientId + "...");

        try {
            // Configurer la plateforme JADE
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");

            // Créer un conteneur pour le patient
            ContainerController container = runtime.createAgentContainer(profile);

            // Créer et lancer l'interface graphique du patient
            final PatientGUI gui = new PatientGUI(patientId);

            // Créer l'agent patient
            AgentController patientAgent = container.createNewAgent(
                patientId,
                "medicalclinic.agents.PatientAgent",
                new Object[] { gui });

            // L'agent va s'associer à l'interface graphique
            gui.setVisible(true);

            // Démarrer l'agent
            patientAgent.start();

            System.out.println("Agent Patient " + patientId + " démarré avec succès.");

        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du lancement de l'agent Patient: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Erreur lors du lancement de l'agent patient: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
