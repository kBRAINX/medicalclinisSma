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
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import medicalclinic.gui.ReceptionistGUI;
import medicalclinic.models.PatientRecord;
import medicalclinic.models.WaitingPatientInfo;

public class ReceptionistAgent extends Agent {
    private HashMap<String, PatientRecord> patientRecords;
    private HashMap<String, Boolean> doctorAvailability;
    private ArrayList<AID> doctorAIDs;
    private LinkedList<WaitingPatientInfo> waitingPatients;
    private ReceptionistGUI gui;
    private Gson gson = new Gson();

    @Override
    protected void setup() {
        // Initialiser les structures de données
        patientRecords = new HashMap<>();
        doctorAvailability = new HashMap<>();
        doctorAIDs = new ArrayList<>();
        waitingPatients = new LinkedList<>();

        // Créer et afficher l'interface graphique
        gui = new ReceptionistGUI(this);
        gui.setVisible(true);
        gui.displayMessage("Agent Réceptionniste démarré");

        // S'enregistrer auprès du DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        sd.setName("clinic-receptionist");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            gui.displayMessage("Enregistré auprès du service d'annuaire");
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur d'enregistrement: " + fe.getMessage());
        }

        // Ajouter les comportements
        addBehaviour(new PatientConnectionBehaviour());
        addBehaviour(new ReceiveInfoBehaviour());
        addBehaviour(new DoctorStatusBehaviour());

        // Rechercher les médecins disponibles
        findDoctors();

        gui.displayMessage("Agent Réceptionniste démarré et prêt");
    }

    // Recherche les médecins disponibles
    private void findDoctors() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("doctor");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            gui.displayMessage("Nombre de médecins trouvés: " + result.length);

            for (DFAgentDescription doctor : result) {
                AID doctorAID = doctor.getName();
                doctorAIDs.add(doctorAID);
                doctorAvailability.put(doctorAID.getLocalName(), true);
                gui.displayMessage("Médecin trouvé: " + doctorAID.getLocalName() +
                    " - Spécialité: " + getDoctorSpecialty(doctorAID.getLocalName()));
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la recherche des médecins: " + fe.getMessage());
        }
    }

    // Envoie un message de bienvenue au patient
    private void sendWelcomeMessage(AID patientAID) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(patientAID);
        msg.setContent("Bienvenue au Cabinet Médical. Veuillez remplir le formulaire suivant pour commencer votre consultation.");
        msg.setConversationId("welcome");
        send(msg);

        gui.displayMessage("Message de bienvenue envoyé à " + patientAID.getLocalName());
    }

    // Envoie un formulaire d'informations personnelles au patient
    private void sendPersonalInfoForm(AID patientAID) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(patientAID);

        // Création du formulaire (structure JSON)
        JsonObject form = new JsonObject();
        form.addProperty("formId", "personalInfo");
        form.addProperty("title", "Informations Personnelles");

        JsonArray fields = new JsonArray();

        // Champ "firstName" (Prénom)
        JsonObject firstNameField = new JsonObject();
        firstNameField.addProperty("name", "firstName");
        firstNameField.addProperty("label", "Prénom");
        firstNameField.addProperty("type", "text");
        firstNameField.addProperty("required", true);
        fields.add(firstNameField);

        // Champ "lastName" (Nom)
        JsonObject lastNameField = new JsonObject();
        lastNameField.addProperty("name", "lastName");
        lastNameField.addProperty("label", "Nom");
        lastNameField.addProperty("type", "text");
        lastNameField.addProperty("required", true);
        fields.add(lastNameField);

        // Champ "birthDate" (Date de naissance)
        JsonObject birthDateField = new JsonObject();
        birthDateField.addProperty("name", "birthDate");
        birthDateField.addProperty("label", "Date de naissance");
        birthDateField.addProperty("type", "date");
        birthDateField.addProperty("required", true);
        fields.add(birthDateField);

        // Champ "address" (Adresse)
        JsonObject addressField = new JsonObject();
        addressField.addProperty("name", "address");
        addressField.addProperty("label", "Adresse");
        addressField.addProperty("type", "text");
        addressField.addProperty("required", true);
        fields.add(addressField);

        // Champ "phone" (Téléphone)
        JsonObject phoneField = new JsonObject();
        phoneField.addProperty("name", "phone");
        phoneField.addProperty("label", "Téléphone");
        phoneField.addProperty("type", "tel");
        phoneField.addProperty("required", true);
        fields.add(phoneField);

        // Champ "email" (Email)
        JsonObject emailField = new JsonObject();
        emailField.addProperty("name", "email");
        emailField.addProperty("label", "Email");
        emailField.addProperty("type", "email");
        emailField.addProperty("required", false);
        fields.add(emailField);

        // Champ "insuranceInfo" (Informations d'assurance)
        JsonObject insuranceField = new JsonObject();
        insuranceField.addProperty("name", "insuranceInfo");
        insuranceField.addProperty("label", "Informations d'assurance");
        insuranceField.addProperty("type", "text");
        insuranceField.addProperty("required", false);
        fields.add(insuranceField);

        form.add("fields", fields);

        // Envoyer le formulaire
        msg.setContent(form.toString());
        msg.setConversationId("personal-form");
        send(msg);

        gui.displayMessage("Formulaire d'informations personnelles envoyé à " + patientAID.getLocalName());
    }

    // Vérifie si un formulaire est complet
    private boolean verifyFormCompletion(JsonObject form) {
        JsonArray fields = form.getAsJsonArray("fields");

        for (int i = 0; i < fields.size(); i++) {
            JsonObject field = fields.get(i).getAsJsonObject();
            if (field.get("required").getAsBoolean() && !field.has("value")) {
                return false;
            }
        }

        return true;
    }

    // Détermine la spécialité requise en fonction des symptômes
    private String determineRequiredSpecialty(HashMap<String, String> symptomsInfo) {
        // Parcourir toutes les réponses pour identifier des mots-clés
        for (String key : symptomsInfo.keySet()) {
            if (key.equals("patientId")) continue;

            String question = key;
            String answer = symptomsInfo.get(key).toLowerCase();

            // Vérifier si les réponses contiennent des mots-clés associés à certaines spécialités
            if ((question.contains("douleur") && answer.contains("poitrine")) ||
                answer.contains("cœur") || answer.contains("coeur") ||
                answer.contains("palpitation")) {
                return "cardiologue";
            }
            else if (question.contains("respiration") || answer.contains("toux") ||
                answer.contains("gorge") || answer.contains("pneumonie") ||
                answer.contains("poumon")) {
                return "pneumologue";
            }
            else if (question.contains("digestion") || answer.contains("estomac") ||
                answer.contains("ventre") || answer.contains("diarrhée") ||
                answer.contains("constipation")) {
                return "gastroenterologue";
            }
        }

        // Par défaut, affecter à un médecin généraliste
        return "generaliste";
    }

    // Attribue un patient à un médecin approprié
    private AID assignPatientToDoctor(String patientId, HashMap<String, String> symptomsInfo) {
        // Analyser les symptômes pour déterminer la spécialité requise
        String requiredSpecialty = determineRequiredSpecialty(symptomsInfo);
        gui.displayMessage("Spécialité requise pour le patient " + patientId + ": " + requiredSpecialty);

        // Rechercher un médecin disponible avec la spécialité requise
        for (AID doctorAID : doctorAIDs) {
            String doctorId = doctorAID.getLocalName();
            if (doctorAvailability.getOrDefault(doctorId, false) &&
                getDoctorSpecialty(doctorId).equals(requiredSpecialty)) {

                // Marquer le médecin comme occupé
                doctorAvailability.put(doctorId, false);
                gui.displayMessage("Médecin spécialiste trouvé: " + doctorId);
                return doctorAID;
            }
        }

        gui.displayMessage("Aucun spécialiste " + requiredSpecialty + " disponible, recherche d'un généraliste");

        // Si aucun spécialiste n'est disponible, chercher un généraliste disponible
        for (AID doctorAID : doctorAIDs) {
            String doctorId = doctorAID.getLocalName();
            if (doctorAvailability.getOrDefault(doctorId, false) &&
                getDoctorSpecialty(doctorId).equals("generaliste")) {

                // Marquer le médecin comme occupé
                doctorAvailability.put(doctorId, false);
                gui.displayMessage("Médecin généraliste trouvé: " + doctorId);
                return doctorAID;
            }
        }

        // Si aucun médecin n'est disponible
        gui.displayMessage("Aucun médecin disponible pour le moment");
        return null;
    }

    // Informe le patient de l'affectation à un médecin
    private void informPatientOfDoctorAssignment(AID patientAID, AID doctorAID, int roomNumber) {
        // Informer d'abord le patient de l'affectation (détails du médecin)
        ACLMessage assignmentMsg = new ACLMessage(ACLMessage.INFORM);
        assignmentMsg.addReceiver(patientAID);

        // Créer un objet JSON avec les informations d'affectation
        JsonObject assignment = new JsonObject();
        assignment.addProperty("doctorId", doctorAID.getLocalName());
        assignment.addProperty("doctorName", getDoctorName(doctorAID.getLocalName()));
        assignment.addProperty("specialty", getDoctorSpecialty(doctorAID.getLocalName()));
        assignment.addProperty("roomNumber", roomNumber);

        assignmentMsg.setContent(assignment.toString());
        assignmentMsg.setConversationId("doctor-assignment");
        send(assignmentMsg);

        // Puis demander au patient de se déplacer vers la salle de consultation
        ACLMessage moveMsg = new ACLMessage(ACLMessage.REQUEST);
        moveMsg.addReceiver(patientAID);
        moveMsg.setContent("MOVE_TO_DOCTOR_" + roomNumber);
        moveMsg.setConversationId("move-request");
        send(moveMsg);

        gui.displayMessage("Patient " + patientAID.getLocalName() + " affecté au Dr. " +
            getDoctorName(doctorAID.getLocalName()) + " dans la salle " + roomNumber);
    }

    // Envoie les informations du patient au médecin assigné
    private void sendPatientInfoToDoctor(AID doctorAID, String patientId) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(doctorAID);
        msg.setConversationId("patient-info");

        // Récupérer le dossier du patient
        PatientRecord record = patientRecords.get(patientId);
        if (record != null) {
            // Convertir le dossier en JSON et l'envoyer
            String jsonData = gson.toJson(record);
            msg.setContent(jsonData);
            send(msg);

            gui.displayMessage("Dossier du patient " + patientId + " envoyé au médecin " +
                doctorAID.getLocalName());
        } else {
            gui.displayMessage("Erreur: Dossier du patient " + patientId + " non trouvé");
        }
    }

    // Met à jour le dossier d'un patient
    private void updatePatientRecord(String patientId, HashMap<String, String> info) {
        // Récupérer ou créer le dossier du patient
        PatientRecord record = patientRecords.get(patientId);
        if (record == null) {
            record = new PatientRecord(patientId);
            patientRecords.put(patientId, record);
            gui.displayMessage("Nouveau dossier créé pour le patient " + patientId);
        }

        // Déterminer le type d'information et mettre à jour le dossier
        if (info.containsKey("firstName") && info.containsKey("lastName")) {
            record.updatePersonalInfo(info);
            gui.displayMessage("Informations personnelles mises à jour pour le patient " + patientId);
            gui.updatePatientsList(patientRecords);
        }
        else if (info.containsKey("patientId") || info.containsKey("question0")) {
            record.updateSymptomsInfo(info);
            gui.displayMessage("Informations sur les symptômes mises à jour pour le patient " + patientId);
        }

        // Mettre à jour l'interface
        gui.updatePatientRecord(record);
    }

    // Ajoute un patient à la liste d'attente
    private void addPatientToWaitingList(String patientId, HashMap<String, String> symptomsInfo) {
        // Créer un objet contenant les informations nécessaires pour l'attente
        WaitingPatientInfo waitingPatientInfo = new WaitingPatientInfo(patientId, symptomsInfo);

        // Ajouter à la fin de la liste d'attente
        waitingPatients.addLast(waitingPatientInfo);

        // Informer le patient de sa mise en attente
        AID patientAID = new AID(patientId, AID.ISLOCALNAME);
        ACLMessage waitingMsg = new ACLMessage(ACLMessage.INFORM);
        waitingMsg.addReceiver(patientAID);
        waitingMsg.setContent("Vous avez été placé en liste d'attente. Veuillez patienter jusqu'à ce qu'un médecin soit disponible.");
        waitingMsg.setConversationId("waiting-info");
        send(waitingMsg);

        // Position dans la file d'attente
        int position = waitingPatients.size();

        // Informer le patient de sa position
        ACLMessage positionMsg = new ACLMessage(ACLMessage.INFORM);
        positionMsg.addReceiver(patientAID);
        positionMsg.setContent("Votre position dans la file d'attente: " + position);
        positionMsg.setConversationId("waiting-position");
        send(positionMsg);

        gui.displayMessage("Patient " + patientId + " ajouté à la liste d'attente en position " + position);
        gui.updateWaitingPatients(waitingPatients);
    }

    // Gère la liste d'attente lorsqu'un médecin devient disponible
    private void manageWaitingList() {
        gui.displayMessage("Vérification des patients en attente...");

        // Vérifier s'il y a des patients en attente
        if (!waitingPatients.isEmpty()) {
            // Récupérer le premier patient de la liste d'attente
            WaitingPatientInfo waitingPatientInfo = waitingPatients.getFirst();
            String patientId = waitingPatientInfo.getPatientId();
            HashMap<String, String> symptomsInfo = waitingPatientInfo.getSymptomsInfo();

            // Essayer de trouver un médecin disponible
            AID doctorAID = assignPatientToDoctor(patientId, symptomsInfo);

            if (doctorAID != null) {
                // Créer l'AID du patient
                AID patientAID = new AID(patientId, AID.ISLOCALNAME);

                // Envoyer les informations du patient au médecin
                sendPatientInfoToDoctor(doctorAID, patientId);

                // Informer le patient de l'affectation
                informPatientOfDoctorAssignment(patientAID, doctorAID,
                    getDoctorRoomNumber(doctorAID.getLocalName()));

                // Retirer le patient de la liste d'attente
                waitingPatients.removeFirst();
                gui.updateWaitingPatients(waitingPatients);

                gui.displayMessage("Patient en attente " + patientId +
                    " maintenant affecté au médecin " + doctorAID.getLocalName());

                // Continuer à traiter la liste d'attente si possible
                manageWaitingList();
            } else {
                gui.displayMessage("Impossible d'affecter le patient en attente " + patientId +
                    " - toujours aucun médecin disponible");
            }
        }
    }

    // Retourne la spécialité d'un médecin
    private String getDoctorSpecialty(String doctorId) {
        // Dans un système réel, cette information serait récupérée à partir d'une base de données
        if (doctorId.contains("cardio")) {
            return "cardiologue";
        } else if (doctorId.contains("pneumo")) {
            return "pneumologue";
        } else if (doctorId.contains("gastro")) {
            return "gastroenterologue";
        } else {
            return "generaliste";
        }
    }

    // Retourne le nom d'un médecin
    private String getDoctorName(String doctorId) {
        // Dans un système réel, cette information serait récupérée à partir d'une base de données
        if (doctorId.contains("cardio") || doctorId.equals("doctor1")) {
            return "Dr. Dupont";
        } else if (doctorId.contains("pneumo") || doctorId.equals("doctor2")) {
            return "Dr. Martin";
        } else if (doctorId.contains("gastro") || doctorId.equals("doctor3")) {
            return "Dr. Durand";
        } else {
            return "Dr. Petit";
        }
    }

    // Retourne le numéro de salle d'un médecin
    private int getDoctorRoomNumber(String doctorId) {
        // Dans un système réel, cette information serait récupérée à partir d'une base de données
        if (doctorId.contains("doctor0")) {
            return 100;
        } else if (doctorId.contains("doctor1")) {
            return 101;
        } else if (doctorId.contains("doctor2")) {
            return 102;
        } else if (doctorId.contains("doctor3")) {
            return 103;
        } else if (doctorId.contains("doctor4")) {
            return 104;
        } else {
            // Si l'ID ne correspond pas à un format attendu, on utilise une valeur par défaut
            return 100 + Integer.parseInt(doctorId.replaceAll("[^0-9]", "")) % 5;
        }
    }

    // Comportement pour gérer les connexions patients
    private class PatientConnectionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages de type REQUEST
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchContent("PATIENT_CONNECTED"));

            ACLMessage msg = receive(mt);

            if (msg != null) {
                AID patientAID = msg.getSender();
                gui.displayMessage("Nouveau patient connecté: " + patientAID.getLocalName());

                // Envoyer un message de bienvenue
                sendWelcomeMessage(patientAID);

                // Envoyer le formulaire d'information personnelle
                sendPersonalInfoForm(patientAID);
            } else {
                block();
            }
        }
    }

    // Comportement pour recevoir les informations
    private class ReceiveInfoBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages de type INFORM
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);

            if (msg != null) {
                String conversationId = msg.getConversationId();
                AID senderAID = msg.getSender();

                try {
                    if ("personal-info".equals(conversationId)) {
                        // Traiter les informations personnelles
                        HashMap<String, String> personalInfo = gson.fromJson(
                            msg.getContent(),
                            new TypeToken<HashMap<String, String>>(){}.getType());

                        updatePatientRecord(senderAID.getLocalName(), personalInfo);

                        // Demander au patient de se déplacer vers la salle d'attente
                        ACLMessage moveMsg = new ACLMessage(ACLMessage.REQUEST);
                        moveMsg.addReceiver(senderAID);
                        moveMsg.setContent("MOVE_TO_WAITING_ROOM");
                        moveMsg.setConversationId("move-request");
                        send(moveMsg);

                        gui.displayMessage("Patient " + senderAID.getLocalName() +
                            " dirigé vers la salle d'attente");
                    }
                    else if ("symptom-info".equals(conversationId)) {
                        // Traiter les informations sur les symptômes
                        HashMap<String, String> symptomsInfo = gson.fromJson(
                            msg.getContent(),
                            new TypeToken<HashMap<String, String>>(){}.getType());

                        String patientId = symptomsInfo.get("patientId");
                        updatePatientRecord(patientId, symptomsInfo);

                        // Attribuer un médecin au patient
                        AID patientAID = new AID(patientId, AID.ISLOCALNAME);
                        AID doctorAID = assignPatientToDoctor(patientId, symptomsInfo);

                        if (doctorAID != null) {
                            // Envoyer les informations du patient au médecin
                            sendPatientInfoToDoctor(doctorAID, patientId);

                            // Informer le patient de l'affectation
                            informPatientOfDoctorAssignment(patientAID, doctorAID,
                                getDoctorRoomNumber(doctorAID.getLocalName()));
                        } else {
                            // Aucun médecin disponible, mettre le patient en attente
                            addPatientToWaitingList(patientId, symptomsInfo);
                        }
                    }
                    else if ("urgent-notification".equals(conversationId)) {
                        // Traiter la notification d'urgence
                        String content = msg.getContent();
                        if (content.startsWith("URGENT_CASE:")) {
                            String patientId = content.substring("URGENT_CASE:".length());
                            gui.displayMessage("NOTIFICATION D'URGENCE pour le patient " + patientId);

                            // Priorité aux cas urgents - à implémenter
                            // ...
                        }
                    }
                    else if ("patient-record-update".equals(conversationId)) {
                        // Traiter la mise à jour du dossier patient
                        PatientRecord updatedRecord = gson.fromJson(
                            msg.getContent(), PatientRecord.class);

                        if (updatedRecord != null) {
                            patientRecords.put(updatedRecord.getPatientId(), updatedRecord);
                            gui.displayMessage("Dossier patient mis à jour: " + updatedRecord.getPatientId());
                            gui.updatePatientsList(patientRecords);
                            gui.updatePatientRecord(updatedRecord);
                        }
                    }
                    else if ("patient-location".equals(conversationId) &&
                        "PATIENT_EXIT".equals(msg.getContent())) {
                        // Le patient quitte le système
                        gui.displayMessage("Patient " + senderAID.getLocalName() + " a quitté le cabinet");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gui.displayMessage("Erreur lors du traitement du message: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    // Comportement pour gérer les statuts des médecins
    private class DoctorStatusBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages INFORM avec conversationId "doctor-status"
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("doctor-status"));

            ACLMessage msg = receive(mt);

            if (msg != null) {
                String content = msg.getContent();
                AID doctorAID = msg.getSender();

                if ("CONSULTATION_COMPLETED".equals(content)) {
                    // Marquer le médecin comme disponible
                    doctorAvailability.put(doctorAID.getLocalName(), true);
                    gui.displayMessage("Médecin " + doctorAID.getLocalName() + " est maintenant disponible");

                    // Vérifier s'il y a des patients en attente
                    manageWaitingList();
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
            gui.displayMessage("Réceptionniste désinscrit du DF");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Fermer l'interface graphique
        if (gui != null) {
            gui.dispose();
        }

        // Journaliser la terminaison
        System.out.println("Agent Réceptionniste " + getAID().getName() + " terminé.");
    }
}
