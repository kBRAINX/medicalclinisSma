package medicalclinic.utils;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Classe utilitaire pour démarrer le système multi-agents du cabinet médical
 * Cette classe lance les agents principaux : Réceptionniste, Infirmier et Médecins
 */
public class StartSystem {
    public static void main(String[] args) {
        System.out.println("Démarrage du système multi-agents du Cabinet Médical...");

        // Configurer la plateforme JADE
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true"); // Active l'interface d'administration JADE

        // Créer le conteneur principal
        ContainerController container = runtime.createMainContainer(profile);

        try {
            // Créer l'agent réceptionniste
            System.out.println("Création de l'agent Réceptionniste...");
            AgentController receptionistAgent = container.createNewAgent(
                "receptionist",
                "medicalclinic.agents.ReceptionistAgent",
                null);
            receptionistAgent.start();
            System.out.println("Agent Réceptionniste démarré.");

            // Créer l'agent infirmier
            System.out.println("Création de l'agent Infirmier...");
            AgentController nurseAgent = container.createNewAgent(
                "nurse",
                "medicalclinic.agents.NurseAgent",
                null);
            nurseAgent.start();
            System.out.println("Agent Infirmier démarré.");

            // Créer plusieurs agents médecins avec différentes spécialités
            System.out.println("Création des agents Médecins...");
            String[] specialties = {"generaliste", "cardiologue", "pneumologue", "gastroenterologue", "generaliste"};
            String[] roomNumbers = {"100", "101", "102", "103", "104"};

            for (int i = 0; i < specialties.length; i++) {
                Object[] doctorArgs = {specialties[i], roomNumbers[i]};
                AgentController doctorAgent = container.createNewAgent(
                    "doctor" + i,
                    "medicalclinic.agents.DoctorAgent",
                    doctorArgs);
                doctorAgent.start();
                System.out.println("Agent Médecin " + specialties[i] + " (salle " + roomNumbers[i] + ") démarré.");
            }

            System.out.println("\nSystème multi-agents du Cabinet Médical démarré avec succès!");
            System.out.println("Pour lancer un patient, exécutez la classe StartPatient.");

        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du démarrage des agents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
