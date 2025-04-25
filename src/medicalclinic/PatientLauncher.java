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

/**
 * Classe de lancement d'un agent patient individuel
 */
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
            // Créer d'abord l'interface graphique du patient
            PatientGUI patientGUI = new PatientGUI(patientId);

            // Créer l'agent patient avec l'interface comme argument
            AgentController patientAgent = container.createNewAgent(
                patientId,
                "medicalclinic.agents.PatientAgent",
                new Object[] { patientGUI });

            // Afficher l'interface graphique
            patientGUI.setVisible(true);

            // Démarrer l'agent patient
            patientAgent.start();

            System.out.println("Agent patient " + patientId + " démarré avec succès.");

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
