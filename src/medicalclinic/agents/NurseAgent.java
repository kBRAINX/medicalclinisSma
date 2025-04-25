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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import medicalclinic.gui.NurseGUI;

public class NurseAgent extends Agent {
    private List<String> symptomQuestions;
    private AID currentPatientAID;
    private NurseGUI gui;
    private Gson gson = new Gson();

    @Override
    protected void setup() {
        // Initialiser les attributs
        symptomQuestions = new ArrayList<>();
        initializeSymptomQuestions();

        // Créer et afficher l'interface graphique
        gui = new NurseGUI(this);
        gui.setVisible(true);
        gui.displayMessage("Agent Infirmier démarré");

        // S'enregistrer auprès du DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("nurse");
        sd.setName("clinic-nurse");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            gui.displayMessage("Enregistré auprès du service d'annuaire");
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur d'enregistrement: " + fe.getMessage());
        }

        // Ajouter les comportements
        addBehaviour(new WaitForPatientBehaviour());

        gui.displayMessage("Agent Infirmier démarré et prêt");
    }

    // Initialise les questions standard sur les symptômes
    private void initializeSymptomQuestions() {
        symptomQuestions.add("Depuis quand ressentez-vous ces symptômes ?");
        symptomQuestions.add("Avez-vous de la fièvre ? Si oui, quelle est votre température ?");
        symptomQuestions.add("Ressentez-vous des douleurs ? Si oui, où sont-elles localisées ?");
        symptomQuestions.add("Avez-vous des difficultés à respirer ?");
        symptomQuestions.add("Avez-vous observé des changements dans votre appétit ou votre poids ?");
        symptomQuestions.add("Prenez-vous des médicaments actuellement ? Si oui, lesquels ?");
        symptomQuestions.add("Avez-vous des allergies connues ?");
    }

    // Salue le patient
    public void greetPatient(AID patientAID) {
        // Stocker l'AID du patient courant
        currentPatientAID = patientAID;

        // Créer et envoyer le message de salutation
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(patientAID);
        msg.setContent("Bonjour, je suis l'infirmier(ère). Je vais vous poser quelques questions pour mieux comprendre vos symptômes.");
        msg.setConversationId("nurse-greeting");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Patient " + patientAID.getLocalName() + " salué");
    }

    // Pose les questions sur les symptômes
    public void askSymptomQuestions() {
        // Créer et configurer le message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(currentPatientAID);

        // Création du formulaire de questions (structure JSON)
        JsonObject form = new JsonObject();
        form.addProperty("formId", "symptomQuestions");
        form.addProperty("title", "Questionnaire de Symptômes");

        JsonArray fields = new JsonArray();

        // Ajouter chaque question comme un champ du formulaire
        for (int i = 0; i < symptomQuestions.size(); i++) {
            JsonObject field = new JsonObject();
            field.addProperty("name", "question" + i);
            field.addProperty("label", symptomQuestions.get(i));
            field.addProperty("type", "text");
            field.addProperty("required", true);
            fields.add(field);
        }

        form.add("fields", fields);

        // Envoyer le formulaire
        msg.setContent(form.toString());
        msg.setConversationId("nurse-questions");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Questions sur les symptômes envoyées à " + currentPatientAID.getLocalName());

        // Ajouter un comportement pour attendre les réponses
        addBehaviour(new WaitForAnswersBehaviour());
    }

    // Vérifie si toutes les questions ont été répondues
    private boolean verifyAllQuestionsAnswered(HashMap<String, String> answers) {
        // Vérifier chaque question
        for (int i = 0; i < symptomQuestions.size(); i++) {
            String key = "question" + i;
            // Vérifier si la réponse existe et n'est pas vide
            if (!answers.containsKey(key) || answers.get(key).isEmpty()) {
                return false;
            }
        }
        // Toutes les questions ont été répondues
        return true;
    }

    // Envoie les informations sur les symptômes à la réceptionniste
    private void sendSymptomInfoToReceptionist(String patientId, HashMap<String, String> symptomsInfo) {
        // Rechercher l'agent réceptionniste
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                AID receptionistAID = result[0].getName();

                // Ajouter l'ID du patient aux informations sur les symptômes
                symptomsInfo.put("patientId", patientId);

                // Créer et configurer le message
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receptionistAID);
                msg.setConversationId("symptom-info");

                // Convertir les informations en JSON et envoyer
                String jsonData = gson.toJson(symptomsInfo);
                msg.setContent(jsonData);
                send(msg);

                // Analyser les symptômes pour détecter une urgence
                boolean isUrgent = analyzeSymptomResponses(symptomsInfo);

                // Journaliser l'action
                gui.displayMessage("Informations sur les symptômes envoyées à la réceptionniste");

                // Si c'est une urgence, notifier la réceptionniste
                if (isUrgent) {
                    notifyReceptionistOfUrgency(patientId);
                }
            } else {
                gui.displayMessage("Erreur: Réceptionniste non trouvée");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la recherche de la réceptionniste: " + fe.getMessage());
        }
    }

    // Informe le patient que les informations ont été transmises
    private void informPatientOfDataTransmission() {
        // Créer et configurer le message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(currentPatientAID);
        msg.setContent("Merci pour vos réponses. Vos informations ont été transmises à la réceptionniste. Veuillez attendre qu'un médecin soit disponible.");
        msg.setConversationId("nurse-feedback");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Patient informé que ses informations ont été transmises");
    }

    // Analyse les réponses pour identifier des urgences potentielles
    private boolean analyzeSymptomResponses(HashMap<String, String> answers) {
        // Vérifier des conditions d'urgence potentielles
        boolean urgencePotentielle = false;

        // Parcourir toutes les réponses
        for (String question : answers.keySet()) {
            String answer = answers.get(question).toLowerCase();

            // Vérifier si les réponses contiennent des mots-clés indiquant une urgence
            if (answer.contains("insupportable") ||
                answer.contains("extrême") ||
                answer.contains("très forte douleur") ||
                (answer.contains("fièvre") && answer.contains("40")) ||
                (answer.contains("difficulté à respirer") && answer.contains("grave"))) {

                urgencePotentielle = true;
                gui.displayMessage("ALERTE: Situation d'urgence potentielle détectée!");
                break;
            }
        }

        return urgencePotentielle;
    }

    // Notifie la réceptionniste d'une urgence
    private void notifyReceptionistOfUrgency(String patientId) {
        // Rechercher l'agent réceptionniste
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                AID receptionistAID = result[0].getName();

                // Créer et configurer le message d'urgence
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receptionistAID);
                msg.setContent("URGENT_CASE:" + patientId);
                msg.setConversationId("urgent-notification");
                send(msg);

                // Journaliser l'action
                gui.displayMessage("Notification d'urgence envoyée pour le patient " + patientId);
            } else {
                gui.displayMessage("Erreur: Impossible de notifier l'urgence - Réceptionniste non trouvée");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la notification d'urgence: " + fe.getMessage());
        }
    }

    // Catégorise les symptômes
    private HashMap<String, String> categorizeSymptoms(HashMap<String, String> answers) {
        // Créer une structure pour les symptômes catégorisés
        HashMap<String, String> categorizedSymptoms = new HashMap<>();

        // Catégories standard de symptômes
        String[] categories = {"Douleur", "Respiratoire", "Digestif", "Température", "Allergie", "Médicaments actuels", "Durée"};

        // Initialiser toutes les catégories comme vides
        for (String category : categories) {
            categorizedSymptoms.put(category, "");
        }

        // Parcourir toutes les réponses et les affecter à des catégories
        for (String question : answers.keySet()) {
            String answer = answers.get(question);
            String questionLower = question.toLowerCase();

            if (questionLower.contains("douleur") || questionLower.contains("mal")) {
                categorizedSymptoms.put("Douleur", answer);
            }
            else if (questionLower.contains("respir") || questionLower.contains("toux")) {
                categorizedSymptoms.put("Respiratoire", answer);
            }
            else if (questionLower.contains("digest") || questionLower.contains("estomac") || questionLower.contains("nausée")) {
                categorizedSymptoms.put("Digestif", answer);
            }
            else if (questionLower.contains("fièvre") || questionLower.contains("tempér")) {
                categorizedSymptoms.put("Température", answer);
            }
            else if (questionLower.contains("allergi")) {
                categorizedSymptoms.put("Allergie", answer);
            }
            else if (questionLower.contains("médic")) {
                categorizedSymptoms.put("Médicaments actuels", answer);
            }
            else if (questionLower.contains("quand") || questionLower.contains("depuis")) {
                categorizedSymptoms.put("Durée", answer);
            }
        }

        // Ajouter l'ID du patient
        if (answers.containsKey("patientId")) {
            categorizedSymptoms.put("patientId", answers.get("patientId"));
        }

        return categorizedSymptoms;
    }

    // Comportement pour attendre l'arrivée d'un patient
    private class WaitForPatientBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages INFORM avec conversationId "patient-location"
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("patient-location"));

            ACLMessage msg = receive(mt);

            if (msg != null && "PATIENT_IN_WAITING_ROOM".equals(msg.getContent())) {
                AID patientAID = msg.getSender();

                // Saluer le patient
                greetPatient(patientAID);

                // Poser les questions sur les symptômes
                askSymptomQuestions();
            } else {
                block();
            }
        }
    }

    // Comportement pour attendre les réponses aux questions
    private class WaitForAnswersBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages avec conversationId "symptom-answers"
            MessageTemplate mt = MessageTemplate.MatchConversationId("symptom-answers");
            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    // Récupérer les réponses du patient
                    HashMap<String, String> answers = gson.fromJson(
                        msg.getContent(),
                        new TypeToken<HashMap<String, String>>(){}.getType());

                    // Vérifier si toutes les questions ont été répondues
                    if (verifyAllQuestionsAnswered(answers)) {
                        // Catégoriser les symptômes
                        HashMap<String, String> categorizedSymptoms = categorizeSymptoms(answers);

                        // Envoyer les informations à la réceptionniste
                        sendSymptomInfoToReceptionist(currentPatientAID.getLocalName(), answers);

                        // Informer le patient que les informations ont été transmises
                        informPatientOfDataTransmission();

                        // Journaliser l'action
                        gui.displayMessage("Réponses reçues et traitées");

                        // Afficher les réponses dans l'interface
                        for (String key : answers.keySet()) {
                            gui.displayMessage(key + ": " + answers.get(key));
                        }

                        // Réinitialiser le patient actuel
                        currentPatientAID = null;

                        // Supprimer ce comportement
                        myAgent.removeBehaviour(this);
                    } else {
                        // Demander au patient de compléter toutes les questions
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(currentPatientAID);
                        response.setContent("Veuillez répondre à toutes les questions s'il vous plaît.");
                        response.setConversationId("nurse-feedback");
                        send(response);

                        // Journaliser l'action
                        gui.displayMessage("Réponses incomplètes, demande de compléter envoyée");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gui.displayMessage("Erreur lors du traitement des réponses: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        // Se désinscrire du Directory Facilitator
        try {
            DFService.deregister(this);
            gui.displayMessage("Infirmier désinscrit du DF");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Fermer l'interface graphique
        if (gui != null) {
            gui.dispose();
        }

        // Journaliser la terminaison
        System.out.println("Agent Infirmier " + getAID().getName() + " terminé.");
    }
}
