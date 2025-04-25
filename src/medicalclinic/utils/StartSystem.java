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

            // Créer plusieurs agents médecins avec différentes spécialités et expérience
            System.out.println("Création des agents Médecins...");
            createDoctors(container);

            System.out.println("\nSystème multi-agents du Cabinet Médical démarré avec succès!");
            System.out.println("Pour lancer un patient, exécutez la classe PatientLauncher ou StartPatient.");

        } catch (StaleProxyException e) {
            System.err.println("Erreur lors du démarrage des agents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée les agents médecins avec leurs spécialités et expertises
     */
    private static void createDoctors(ContainerController container) throws StaleProxyException {
        // Médecins généralistes
        createDoctor(container, "doctor0", "generaliste", "100", "Médecin généraliste", "15");
        createDoctor(container, "doctor4", "generaliste", "104", "Médecin généraliste", "8");

        // Spécialistes
        createDoctor(container, "doctor1", "cardiologue", "101", "Spécialiste en cardiologie", "12");
        createDoctor(container, "doctor2", "pneumologue", "102", "Spécialiste en pneumologie", "10");
        createDoctor(container, "doctor3", "gastroenterologue", "103", "Spécialiste en gastroentérologie", "7");
        createDoctor(container, "doctor5", "infectiologue", "105", "Spécialiste en maladies infectieuses", "9");
        createDoctor(container, "doctor6", "neurologue", "106", "Spécialiste en neurologie", "11");
        createDoctor(container, "doctor7", "endocrinologue", "107", "Spécialiste en endocrinologie", "6");
    }

    /**
     * Crée un agent médecin avec les paramètres spécifiés
     */
    private static void createDoctor(ContainerController container, String id, String specialty,
                                     String roomNumber, String qualification, String experience)
        throws StaleProxyException {
        Object[] doctorArgs = {specialty, roomNumber, qualification, experience};
        AgentController doctorAgent = container.createNewAgent(
            id,
            "medicalclinic.agents.DoctorAgent",
            doctorArgs);
        doctorAgent.start();
        System.out.println("Agent Médecin " + specialty + " (salle " + roomNumber + ", "
            + experience + " ans d'expérience) démarré.");
    }
}
