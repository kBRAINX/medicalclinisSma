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
            } else {
                System.out.println("Aucune réceptionniste trouvée");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
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
        } else {
            System.out.println("Impossible d'envoyer le message: réceptionniste non trouvée");
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
                }
            } else {
                System.out.println("Aucun infirmier trouvé");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
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
                gui.displayMessage("Arrivé dans la salle de consultation du médecin");
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
                gui.displayMessage("Quitter le cabinet médical");
            }
        }

        // Terminer l'agent
        doDelete();
    }

    // Définit l'interface graphique
    public void setGUI(PatientGUI gui) {
        this.gui = gui;
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
                    if (gui != null) {
                        gui.displayMessage("Réceptionniste: " + content);
                    }
                }
                else if ("personal-form".equals(conversationId)) {
                    // Formulaire d'informations personnelles
                    if (gui != null) {
                        gui.displayPersonalForm(content);
                    }
                }
                else if ("move-request".equals(conversationId)) {
                    // Demande de déplacement
                    if ("MOVE_TO_WAITING_ROOM".equals(content)) {
                        moveToWaitingRoom();
                    }
                    else if (content.startsWith("MOVE_TO_DOCTOR_")) {
                        // Extraire le numéro de salle
                        int roomNumber = Integer.parseInt(content.substring("MOVE_TO_DOCTOR_".length()));
                        doctorAID = msg.getSender();
                        moveToDoctorRoom(roomNumber);
                    }
                }
                else if ("nurse-questions".equals(conversationId)) {
                    // Questions de l'infirmier
                    if (gui != null) {
                        gui.displayNurseQuestions(content);
                    }
                }
                else if ("doctor-questions".equals(conversationId)) {
                    // Questions du médecin
                    if (gui != null) {
                        gui.displayDoctorQuestions(content);
                    }
                }
                else if ("diagnosis".equals(conversationId)) {
                    // Diagnostic du médecin
                    if (gui != null) {
                        gui.displayDiagnosis(content);
                    }
                }
                else if ("doctor-assignment".equals(conversationId)) {
                    // Affectation à un médecin
                    try {
                        JsonObject assignment = JsonParser.parseString(content).getAsJsonObject();
                        doctorAID = msg.getSender();
                        String doctorName = assignment.get("doctorName").getAsString();
                        String specialty = assignment.get("specialty").getAsString();
                        int roomNumber = assignment.get("roomNumber").getAsInt();

                        if (gui != null) {
                            gui.displayMessage("Vous avez été affecté au Dr. " + doctorName +
                                " (" + specialty + ") en salle " + roomNumber);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if ("nurse-greeting".equals(conversationId) ||
                    "doctor-greeting".equals(conversationId) ||
                    "nurse-feedback".equals(conversationId)) {
                    // Message de salutation ou feedback
                    if (gui != null) {
                        gui.displayMessage(content);
                    }
                }
                else if ("waiting-info".equals(conversationId) ||
                    "waiting-position".equals(conversationId)) {
                    // Information sur l'attente
                    if (gui != null) {
                        gui.displayMessage(content);
                    }
                }
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent patient " + id + " terminé");
    }
}
