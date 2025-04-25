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
    private List<SymptomQuestion> symptomQuestions;
    private AID currentPatientAID;
    private NurseGUI gui;
    private Gson gson = new Gson();

    // Classe interne pour représenter une question avec un titre et une description
    private static class SymptomQuestion {
        private String id;
        private String title;
        private String description;
        private boolean required;
        private String category;

        public SymptomQuestion(String id, String title, String description, boolean required, String category) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.required = required;
            this.category = category;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public boolean isRequired() { return required; }
        public String getCategory() { return category; }
    }

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

    // Initialise les questions détaillées sur les symptômes
    private void initializeSymptomQuestions() {
        // Questions générales
        symptomQuestions.add(new SymptomQuestion(
            "symptomDuration",
            "Durée des symptômes",
            "Depuis quand ressentez-vous ces symptômes ?",
            true,
            "Général"));

        symptomQuestions.add(new SymptomQuestion(
            "mainSymptoms",
            "Symptômes principaux",
            "Quels sont vos symptômes principaux ? Décrivez-les de façon précise.",
            true,
            "Général"));

        symptomQuestions.add(new SymptomQuestion(
            "painLocation",
            "Localisation des douleurs",
            "Si vous ressentez des douleurs, où sont-elles localisées précisément ?",
            true,
            "Douleur"));

        symptomQuestions.add(new SymptomQuestion(
            "painIntensity",
            "Intensité de la douleur",
            "Sur une échelle de 1 à 10, quelle est l'intensité de votre douleur ?",
            true,
            "Douleur"));

        // Symptômes corporels
        symptomQuestions.add(new SymptomQuestion(
            "fever",
            "Fièvre",
            "Avez-vous de la fièvre ? Si oui, quelle est votre température ?",
            true,
            "Température"));

        symptomQuestions.add(new SymptomQuestion(
            "breathing",
            "Respiration",
            "Avez-vous des difficultés à respirer ? Si oui, dans quelles circonstances ?",
            true,
            "Respiratoire"));

        symptomQuestions.add(new SymptomQuestion(
            "coughing",
            "Toux",
            "Avez-vous de la toux ? Si oui, est-elle sèche ou grasse ? Y a-t-il des expectorations ?",
            true,
            "Respiratoire"));

        symptomQuestions.add(new SymptomQuestion(
            "digestive",
            "Problèmes digestifs",
            "Avez-vous des problèmes digestifs (nausées, vomissements, diarrhée, constipation) ?",
            true,
            "Digestif"));

        symptomQuestions.add(new SymptomQuestion(
            "skin",
            "Manifestations cutanées",
            "Avez-vous remarqué des changements au niveau de votre peau (éruptions, démangeaisons) ?",
            true,
            "Dermatologique"));

        // Facteurs influents
        symptomQuestions.add(new SymptomQuestion(
            "allergies",
            "Allergies",
            "Avez-vous des allergies connues ? Si oui, lesquelles ?",
            true,
            "Médical"));

        symptomQuestions.add(new SymptomQuestion(
            "currentMedications",
            "Médicaments actuels",
            "Prenez-vous des médicaments actuellement ? Si oui, lesquels ?",
            true,
            "Médical"));

        symptomQuestions.add(new SymptomQuestion(
            "recentTravel",
            "Voyages récents",
            "Avez-vous voyagé récemment ? Si oui, où et quand ?",
            true,
            "Facteurs de risque"));

        symptomQuestions.add(new SymptomQuestion(
            "chronic",
            "Maladies chroniques",
            "Souffrez-vous de maladies chroniques (diabète, hypertension, etc.) ?",
            true,
            "Médical"));

        // Questions spécifiques
        symptomQuestions.add(new SymptomQuestion(
            "weightChange",
            "Changement de poids",
            "Avez-vous observé des changements dans votre poids récemment ?",
            true,
            "Général"));

        symptomQuestions.add(new SymptomQuestion(
            "sleeping",
            "Troubles du sommeil",
            "Avez-vous des problèmes de sommeil ? Si oui, lesquels ?",
            true,
            "Neurologique"));

        symptomQuestions.add(new SymptomQuestion(
            "headache",
            "Maux de tête",
            "Souffrez-vous de maux de tête ? Si oui, quelle est leur localisation et intensité ?",
            true,
            "Neurologique"));

        symptomQuestions.add(new SymptomQuestion(
            "additionalInfo",
            "Informations supplémentaires",
            "Y a-t-il d'autres symptômes ou informations que vous souhaitez mentionner ?",
            false,
            "Général"));
    }

    // Salue le patient
    public void greetPatient(AID patientAID) {
        // Stocker l'AID du patient courant
        currentPatientAID = patientAID;

        // Créer et envoyer le message de salutation
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(patientAID);
        msg.setContent("Bonjour, je suis l'infirmier(ère). Je vais vous poser quelques questions pour mieux comprendre vos symptômes et établir un premier bilan de santé. Veuillez répondre à toutes les questions de manière précise pour que nous puissions vous orienter vers le médecin approprié.");
        msg.setConversationId("nurse-greeting");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Patient " + patientAID.getLocalName() + " salué");
        gui.setCurrentPatient(patientAID.getLocalName());
        gui.setStatus("Consultation en cours");
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
        for (SymptomQuestion question : symptomQuestions) {
            JsonObject field = new JsonObject();
            field.addProperty("name", question.getId());
            field.addProperty("label", question.getTitle() + " : " + question.getDescription());
            field.addProperty("type", "text");
            field.addProperty("required", question.isRequired());
            field.addProperty("category", question.getCategory());
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

    // Vérifie si toutes les questions requises ont été répondues
    private boolean verifyAllQuestionsAnswered(HashMap<String, String> answers) {
        // Vérifier chaque question requise
        for (SymptomQuestion question : symptomQuestions) {
            if (question.isRequired()) {
                String key = question.getId();
                // Vérifier si la réponse existe et n'est pas vide
                if (!answers.containsKey(key) || answers.get(key).isEmpty()) {
                    return false;
                }
            }
        }
        // Toutes les questions requises ont été répondues
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
        msg.setContent("Merci pour vos réponses. J'ai bien noté toutes vos informations sur les symptômes que vous présentez. Ces informations ont été transmises à la réceptionniste qui va maintenant vous orienter vers le médecin le plus approprié pour votre cas. Veuillez patienter dans la salle d'attente jusqu'à ce que la réceptionniste vous appelle.");
        msg.setConversationId("nurse-feedback");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Patient informé que ses informations ont été transmises");
        gui.setStatus("En attente");
        gui.setCurrentPatient(null);
    }

    // Analyse les réponses pour identifier des urgences potentielles
    private boolean analyzeSymptomResponses(HashMap<String, String> answers) {
        // Vérifier des conditions d'urgence potentielles
        boolean urgencePotentielle = false;

        // Parcourir toutes les réponses
        for (String questionId : answers.keySet()) {
            String answer = answers.get(questionId).toLowerCase();

            // Vérifier si les réponses contiennent des mots-clés indiquant une urgence
            if (answer.contains("insupportable") ||
                answer.contains("extrême") ||
                answer.contains("très forte douleur") ||
                (questionId.equals("fever") && (answer.contains("40") || answer.contains("41") || answer.contains("42"))) ||
                (questionId.equals("breathing") && (answer.contains("très difficile") || answer.contains("impossible"))) ||
                (questionId.equals("painIntensity") && (answer.contains("9") || answer.contains("10"))) ||
                answer.contains("sang") && (questionId.equals("coughing") || questionId.equals("digestive"))) {

                urgencePotentielle = true;
                gui.displayMessage("ALERTE: Situation d'urgence potentielle détectée - " + getQuestionTitleById(questionId) + ": " + answer);
                break;
            }
        }

        return urgencePotentielle;
    }

    // Retourne le titre d'une question à partir de son ID
    private String getQuestionTitleById(String id) {
        for (SymptomQuestion question : symptomQuestions) {
            if (question.getId().equals(id)) {
                return question.getTitle();
            }
        }
        return id;
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

        // Initialiser toutes les catégories comme vides
        for (SymptomQuestion question : symptomQuestions) {
            String category = question.getCategory();
            if (!categorizedSymptoms.containsKey(category)) {
                categorizedSymptoms.put(category, "");
            }

            // Ajouter la réponse si elle existe
            String key = question.getId();
            if (answers.containsKey(key) && !answers.get(key).isEmpty()) {
                String currentValue = categorizedSymptoms.get(category);
                String newValue = question.getTitle() + ": " + answers.get(key);

                if (currentValue.isEmpty()) {
                    categorizedSymptoms.put(category, newValue);
                } else {
                    categorizedSymptoms.put(category, currentValue + " | " + newValue);
                }
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
                        gui.displayMessage("Résumé des réponses du patient :");
                        for (String key : answers.keySet()) {
                            if (!key.equals("patientId")) {
                                String questionTitle = getQuestionTitleById(key);
                                gui.displayMessage("- " + questionTitle + " : " + answers.get(key));
                            }
                        }

                        // Réinitialiser le patient actuel
                        currentPatientAID = null;

                        // Supprimer ce comportement
                        myAgent.removeBehaviour(this);
                    } else {
                        // Demander au patient de compléter toutes les questions
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(currentPatientAID);
                        response.setContent("Veuillez répondre à toutes les questions requises s'il vous plaît. Ces informations sont importantes pour votre prise en charge.");
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
