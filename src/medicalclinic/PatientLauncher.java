package medicalclinic;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import medicalclinic.gui.PatientGUI;

import javax.swing.*;
import java.util.UUID;

public class PatientLauncher {

    public static void main(String[] args) {
        // Demander le nom du patient avec une boîte de dialogue
        String patientName = JOptionPane.showInputDialog(
            null,
            "Entrez votre nom (sera utilisé comme identifiant) :",
            "Enregistrement Patient",
            JOptionPane.QUESTION_MESSAGE);

        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = "patient" + UUID.randomUUID().toString().substring(0, 8);
        }

        final String patientId = patientName.replaceAll("\\s+", "_").toLowerCase();

        // Configurer la plateforme JADE
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");

        // Créer un conteneur pour le patient
        ContainerController container = runtime.createAgentContainer(profile);

        try {
            // Créer l'agent patient
            AgentController patientAgent = container.createNewAgent(
                patientId,
                "medicalclinic.agents.PatientAgent",
                null);
            patientAgent.start();

            // Lancer l'interface graphique du patient
            SwingUtilities.invokeLater(() -> {
                PatientGUI patientGUI = new PatientGUI(patientId);
                patientGUI.setVisible(true);
            });

        } catch (StaleProxyException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Erreur lors du lancement de l'agent patient: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
