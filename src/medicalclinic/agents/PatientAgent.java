package medicalclinic.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import medicalclinic.gui.PatientGUI;

/**
 * Agent patient pour le cabinet médical.
 * Représente un patient interagissant avec le système.
 */
public class PatientAgent extends Agent {
    private String id;
    private HashMap<String, String> personalInfo;
    private HashMap<String, String> symptomsInfo;
    private AID receptionistAID;
    private AID nurseAID;
    private AID doctorAID;
    private String location;
    private PatientGUI gui;
    private Gson gson = new Gson();

    @Override
    protected void setup() {
        id = getAID().getLocalName();
        personalInfo = new HashMap<>();
        symptomsInfo = new HashMap<>();
        location = "Entrée";

        // Récupérer l'interface graphique passée en argument
        Object[] args = getArguments();
        if (args != null && args.length > 0 && args[0] instanceof PatientGUI) {
            gui = (PatientGUI) args[0];
            gui.setPatientAgent(this);
            gui.displayMessage("Agent patient " + id + " démarré");
        } else {
            System.err.println("Interface graphique non trouvée pour le patient " + id);
        }

        // Trouver l'agent réceptionniste
        findReceptionist();

        // Ajouter le comportement pour la réception des messages
        addBehaviour(new ReceiveMessageBehaviour());

        // Informer l'utilisateur que l'agent a démarré
        System.out.println("Agent patient " + id + " démarré");

        // Envoyer un message de connexion à la réceptionniste
        informReceptionistOfConnection();
    }

    // Recherche l'agent réceptionniste dans le DF
    private void findReceptionist() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                receptionistAID = result[0].getName();
                System.out.println("Réceptionniste trouvée: " + receptionistAID.getName());
                if (gui != null) {
                    gui.displayMessage("Connexion au cabinet médical établie");
                }
            } else {
                System.out.println("Aucune réceptionniste trouvée");
                if (gui != null) {
                    gui.displayMessage("Impossible de se connecter au cabinet médical - Réessayez plus tard");
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            if (gui != null) {
                gui.displayMessage("Erreur lors de la recherche de la réceptionniste: " + fe.getMessage());
            }
        }
    }

    // Informe la réceptionniste de la connexion du patient
    private void informReceptionistOfConnection() {
        if (receptionistAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(receptionistAID);
            msg.setContent("PATIENT_CONNECTED");
            send(msg);
            System.out.println("Message de connexion envoyé à la réceptionniste");

            if (gui != null) {
                gui.displayMessage("En attente d'accueil par la réceptionniste...");
            }
        } else {
            System.out.println("Impossible d'envoyer le message: réceptionniste non trouvée");

            if (gui != null) {
                gui.displayMessage("Impossible de contacter la réceptionniste - Veuillez réessayer plus tard");
            }
        }
    }

    // Envoie les informations personnelles à la réceptionniste
    public void fillPersonalForm(HashMap<String, String> formData) {
        personalInfo.putAll(formData);

        if (receptionistAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(receptionistAID);
            msg.setConversationId("personal-info");
            msg.setContent(gson.toJson(personalInfo));
            send(msg);

            if (gui != null) {
                gui.displayMessage("Informations personnelles envoyées");
            }
        } else {
            if (gui != null) {
                gui.displayMessage("Erreur: Impossible d'envoyer vos informations - Réceptionniste non disponible");
            }
        }
    }

    // Envoie les réponses aux questions de l'infirmier
    public void answerNurseQuestions(HashMap<String, String> answers) {
        symptomsInfo.putAll(answers);

        if (nurseAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(nurseAID);
            msg.setConversationId("symptom-answers");
            msg.setContent(gson.toJson(symptomsInfo));
            send(msg);

            if (gui != null) {
                gui.displayMessage("Réponses aux questions de l'infirmier envoyées");
                gui.displayMessage("L'infirmier analyse vos symptômes...");
            }
        } else {
            if (gui != null) {
                gui.displayMessage("Erreur: Impossible d'envoyer vos réponses - Infirmier non disponible");
            }
        }
    }

    // Envoie les réponses aux questions du médecin
    public void answerDoctorQuestions(HashMap<String, String> answers) {
        if (doctorAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(doctorAID);
            msg.setConversationId("doctor-consultation");
            msg.setContent(gson.toJson(answers));
            send(msg);

            if (gui != null) {
                gui.displayMessage("Réponses aux questions du médecin envoyées");
                gui.displayMessage("Le médecin analyse vos réponses et prépare son diagnostic...");
            }
        } else {
            if (gui != null) {
                gui.displayMessage("Erreur: Impossible d'envoyer vos réponses - Médecin non disponible");
            }
        }
    }

    // Déplace le patient vers la salle d'attente
    public void moveToWaitingRoom() {
        // Trouver l'infirmier
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("nurse");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                nurseAID = result[0].getName();

                // Informer l'infirmier
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(nurseAID);
                msg.setContent("PATIENT_IN_WAITING_ROOM");
                msg.setConversationId("patient-location");
                send(msg);

                // Mettre à jour l'emplacement
                location = "Salle d'attente";
                if (gui != null) {
                    gui.updateLocation(location);
                    gui.displayMessage("Vous vous rendez dans la salle d'attente pour rencontrer l'infirmier");
                }
            } else {
                System.out.println("Aucun infirmier trouvé");
                if (gui != null) {
                    gui.displayMessage("Impossible de trouver l'infirmier - Veuillez attendre dans la salle d'attente");
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            if (gui != null) {
                gui.displayMessage("Erreur lors de la recherche de l'infirmier: " + fe.getMessage());
            }
        }
    }

    // Déplace le patient vers la salle du médecin
    public void moveToDoctorRoom(int roomNumber) {
        if (doctorAID != null) {
            // Informer le médecin de l'arrivée
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(doctorAID);
            msg.setContent("PATIENT_ARRIVED");
            msg.setConversationId("patient-location");
            send(msg);

            // Mettre à jour l'emplacement
            location = "Salle de consultation " + roomNumber;
            if (gui != null) {
                gui.updateLocation(location);
                gui.displayMessage("Vous entrez dans la salle de consultation " + roomNumber);
            }
        } else {
            if (gui != null) {
                gui.displayMessage("Erreur: Impossible de trouver le médecin - Veuillez retourner à l'accueil");
            }
        }
    }

    // Quitte le système
    public void exitSystem() {
        if (receptionistAID != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(receptionistAID);
            msg.setContent("PATIENT_EXIT");
            msg.setConversationId("patient-location");
            send(msg);

            if (gui != null) {
                gui.displayMessage("Vous quittez le cabinet médical. Merci de votre visite et prompt rétablissement!");
            }
        }

        // Terminer l'agent
        doDelete();
    }

    // Comportement pour recevoir les messages
    private class ReceiveMessageBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                String content = msg.getContent();
                String conversationId = msg.getConversationId();

                // Traiter selon le type de conversation
                if ("welcome".equals(conversationId)) {
                    // Message de bienvenue de la réceptionniste
                    handleWelcomeMessage(content, msg.getSender());
                }
                else if ("personal-form".equals(conversationId)) {
                    // Formulaire d'informations personnelles
                    handlePersonalFormMessage(content);
                }
                else if ("move-request".equals(conversationId)) {
                    // Demande de déplacement
                    handleMoveRequestMessage(content, msg.getSender());
                }
                else if ("nurse-questions".equals(conversationId)) {
                    // Questions de l'infirmier
                    handleNurseQuestionsMessage(content);
                }
                else if ("doctor-questions".equals(conversationId)) {
                    // Questions du médecin
                    handleDoctorQuestionsMessage(content);
                }
                else if ("diagnosis".equals(conversationId)) {
                    // Diagnostic du médecin
                    handleDiagnosisMessage(content);
                }
                else if ("doctor-assignment".equals(conversationId)) {
                    // Affectation à un médecin
                    handleDoctorAssignmentMessage(content, msg.getSender());
                }
                else if ("nurse-greeting".equals(conversationId) ||
                    "doctor-greeting".equals(conversationId) ||
                    "nurse-feedback".equals(conversationId) ||
                    "receptionist-feedback".equals(conversationId)) {
                    // Message de salutation ou feedback
                    handleGreetingOrFeedbackMessage(content, conversationId, msg.getSender());
                }
                else if ("waiting-info".equals(conversationId) ||
                    "waiting-position".equals(conversationId)) {
                    // Information sur l'attente
                    handleWaitingInfoMessage(content);
                }
                else if ("urgent-info".equals(conversationId)) {
                    // Information sur l'urgence
                    handleUrgentInfoMessage(content);
                }
            } else {
                block();
            }
        }

        // Message de bienvenue
        private void handleWelcomeMessage(String content, AID sender) {
            if (gui != null) {
                gui.displayMessage("Réceptionniste: " + content);
            }
            // Stocker l'AID de la réceptionniste
            receptionistAID = sender;
        }

        // Formulaire d'informations personnelles
        private void handlePersonalFormMessage(String content) {
            if (gui != null) {
                gui.displayPersonalForm(content);
            }
        }

        // Demande de déplacement
        private void handleMoveRequestMessage(String content, AID sender) {
            if ("MOVE_TO_WAITING_ROOM".equals(content)) {
                moveToWaitingRoom();
            }
            else if (content.startsWith("MOVE_TO_DOCTOR_")) {
                // Extraire le numéro de salle
                int roomNumber = Integer.parseInt(content.substring("MOVE_TO_DOCTOR_".length()));
                doctorAID = sender;
                moveToDoctorRoom(roomNumber);
            }
        }

        // Questions de l'infirmier
        private void handleNurseQuestionsMessage(String content) {
            if (gui != null) {
                gui.displayNurseQuestions(content);
            }
        }

        // Questions du médecin
        private void handleDoctorQuestionsMessage(String content) {
            if (gui != null) {
                gui.displayDoctorQuestions(content);
            }
        }

        // Diagnostic du médecin
        private void handleDiagnosisMessage(String content) {
            if (gui != null) {
                gui.displayDiagnosis(content);
            }
        }

        // Affectation à un médecin
        private void handleDoctorAssignmentMessage(String content, AID sender) {
            try {
                JsonObject assignment = JsonParser.parseString(content).getAsJsonObject();
                doctorAID = sender;
                String doctorName = assignment.get("doctorName").getAsString();
                String specialty = assignment.get("specialty").getAsString();
                int roomNumber = assignment.get("roomNumber").getAsInt();

                if (gui != null) {
                    gui.displayMessage("Vous avez été affecté au Dr. " + doctorName +
                        " (" + specialty + ") en salle " + roomNumber);

                    // Afficher d'autres informations si disponibles
                    if (assignment.has("qualification")) {
                        gui.displayMessage("Qualification: " + assignment.get("qualification").getAsString());
                    }

                    if (assignment.has("experience")) {
                        gui.displayMessage("Expérience: " + assignment.get("experience").getAsString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (gui != null) {
                    gui.displayMessage("Erreur lors du traitement de l'affectation au médecin: " + e.getMessage());
                }
            }
        }

        // Message de salutation ou feedback
        private void handleGreetingOrFeedbackMessage(String content, String conversationId, AID sender) {
            if (gui != null) {
                if (conversationId.contains("nurse")) {
                    gui.displayMessage("Infirmier: " + content);
                    nurseAID = sender;
                } else if (conversationId.contains("doctor")) {
                    gui.displayMessage("Médecin: " + content);
                    doctorAID = sender;
                } else if (conversationId.contains("receptionist")) {
                    gui.displayMessage("Réceptionniste: " + content);
                }
            }
        }

        // Information sur l'attente
        private void handleWaitingInfoMessage(String content) {
            if (gui != null) {
                gui.displayMessage("Information: " + content);
            }
        }

        // Information sur l'urgence
        private void handleUrgentInfoMessage(String content) {
            if (gui != null) {
                gui.displayMessage("⚠️ URGENT: " + content);
            }
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent patient " + id + " terminé");

        if (gui != null) {
            gui.displayMessage("Session terminée");
        }
    }

    /**
     * Getters et setters
     */

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setGUI(PatientGUI gui) {
        this.gui = gui;
    }

    public HashMap<String, String> getPersonalInfo() {
        return personalInfo;
    }

    public HashMap<String, String> getSymptomsInfo() {
        return symptomsInfo;
    }
}
