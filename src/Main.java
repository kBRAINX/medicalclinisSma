import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        // Configurer la plateforme JADE
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true"); // Active l'interface d'administration JADE

        // Créer le conteneur principal
        ContainerController container = runtime.createMainContainer(profile);

        try {
            // Créer l'agent réceptionniste
            AgentController receptionistAgent = container.createNewAgent(
                "receptionist",
                "medicalclinic.agents.ReceptionistAgent",
                null);
            receptionistAgent.start();

            // Créer l'agent infirmier
            AgentController nurseAgent = container.createNewAgent(
                "nurse",
                "medicalclinic.agents.NurseAgent",
                null);
            nurseAgent.start();

            // Créer plusieurs agents médecins avec différentes spécialités
            String[] specialties = {"generaliste", "cardiologue", "pneumologue", "gastroenterologue", "generaliste"};
            String[] roomNumbers = {"100", "101", "102", "103", "104"};

            for (int i = 0; i < specialties.length; i++) {
                Object[] doctorArgs = {specialties[i], roomNumbers[i]};
                AgentController doctorAgent = container.createNewAgent(
                    "doctor" + i,
                    "medicalclinic.agents.DoctorAgent",
                    doctorArgs);
                doctorAgent.start();
            }

            System.out.println("Tous les agents du cabinet médical ont été démarrés avec succès.");
            System.out.println("Pour lancer un patient, exécutez la classe medicalclinic.PatientLauncher.");

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
