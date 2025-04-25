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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import medicalclinic.database.DiseaseDatabase;
import medicalclinic.database.TreatmentDatabase;
import medicalclinic.gui.DoctorGUI;
import medicalclinic.models.Consultation;
import medicalclinic.models.Disease;
import medicalclinic.models.Medication;
import medicalclinic.models.PatientRecord;

public class DoctorAgent extends Agent {
    private boolean available;
    private String specialty;
    private int roomNumber;
    private HashMap<String, String> patientResponses;
    private AID currentPatientAID;
    private PatientRecord currentPatientRecord;
    private DoctorGUI gui;
    private Gson gson = new Gson();

    @Override
    protected void setup() {
        // Initialiser les attributs
        available = true;
        patientResponses = new HashMap<>();

        // Récupérer les arguments (spécialité et numéro de salle)
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            specialty = (String) args[0];
            roomNumber = Integer.parseInt((String) args[1]);
        } else {
            specialty = "generaliste";
            roomNumber = 100;
        }

        // Créer et afficher l'interface graphique
        gui = new DoctorGUI(this);
        gui.setVisible(true);
        gui.displayMessage("Agent Médecin en cours de démarrage...");

        // S'enregistrer auprès du DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("doctor");
        sd.setName("clinic-doctor-" + specialty);
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            gui.displayMessage("Enregistré auprès du service d'annuaire");
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur d'enregistrement: " + fe.getMessage());
        }

        // Ajouter les comportements
        addBehaviour(new ReceivePatientInfoBehaviour());
        addBehaviour(new WaitForPatientArrivalBehaviour());

        gui.displayMessage("Agent Médecin " + specialty + " prêt dans la salle " + roomNumber);
    }

    // Méthode pour inviter le patient à venir dans la salle de consultation
    private void invitePatientToConsultation() {
        // Rechercher l'agent réceptionniste
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                AID receptionistAID = result[0].getName();

                // Créer et configurer le message pour demander à la réceptionniste d'inviter le patient
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(receptionistAID);

                // Créer un objet JSON avec les informations nécessaires
                JsonObject requestInfo = new JsonObject();
                requestInfo.addProperty("patientId", currentPatientRecord.getPatientId());
                requestInfo.addProperty("doctorId", getLocalName());
                requestInfo.addProperty("roomNumber", roomNumber);

                msg.setContent(requestInfo.toString());
                msg.setConversationId("invite-patient");
                send(msg);

                gui.displayMessage("Demande envoyée à la réceptionniste pour faire venir le patient " +
                    currentPatientRecord.getPatientId() + " dans la salle " + roomNumber);
            } else {
                gui.displayMessage("Erreur: Réceptionniste non trouvée, impossible d'inviter le patient");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la recherche de la réceptionniste: " + fe.getMessage());
        }
    }

    // Salue le patient
    private void greetPatient() {
        // Créer et configurer le message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(currentPatientAID);
        msg.setContent("Bonjour, je suis le Dr. " + getDoctorName() + ". Je vais vous examiner aujourd'hui.");
        msg.setConversationId("doctor-greeting");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Patient " + currentPatientAID.getLocalName() + " accueilli");
    }

    // Pose des questions supplémentaires
    private void askAdditionalQuestions() {
        // Créer et configurer le message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(currentPatientAID);

        // Création du formulaire de questions (structure JSON)
        JsonObject form = new JsonObject();
        form.addProperty("formId", "doctorQuestions");
        form.addProperty("title", "Questions Médicales Supplémentaires");

        JsonArray fields = new JsonArray();

        // Questions générales
        JsonObject medicalHistoryField = new JsonObject();
        medicalHistoryField.addProperty("name", "medicalHistory");
        medicalHistoryField.addProperty("label", "Avez-vous des antécédents médicaux particuliers ?");
        medicalHistoryField.addProperty("type", "text");
        medicalHistoryField.addProperty("required", true);
        fields.add(medicalHistoryField);

        JsonObject familyHistoryField = new JsonObject();
        familyHistoryField.addProperty("name", "familyHistory");
        familyHistoryField.addProperty("label", "Y a-t-il des maladies héréditaires dans votre famille ?");
        familyHistoryField.addProperty("type", "text");
        familyHistoryField.addProperty("required", true);
        fields.add(familyHistoryField);

        // Questions spécifiques selon la spécialité
        if ("cardiologue".equals(specialty)) {
            JsonObject chestPainField = new JsonObject();
            chestPainField.addProperty("name", "chestPain");
            chestPainField.addProperty("label", "Pouvez-vous décrire précisément vos douleurs thoraciques ?");
            chestPainField.addProperty("type", "text");
            chestPainField.addProperty("required", true);
            fields.add(chestPainField);

            JsonObject heartRateField = new JsonObject();
            heartRateField.addProperty("name", "heartRate");
            heartRateField.addProperty("label", "Avez-vous remarqué des palpitations ou des irrégularités dans votre rythme cardiaque ?");
            heartRateField.addProperty("type", "text");
            heartRateField.addProperty("required", true);
            fields.add(heartRateField);
        }
        else if ("pneumologue".equals(specialty)) {
            JsonObject breathingField = new JsonObject();
            breathingField.addProperty("name", "breathingDifficulties");
            breathingField.addProperty("label", "Quand ressentez-vous le plus de difficultés à respirer ?");
            breathingField.addProperty("type", "text");
            breathingField.addProperty("required", true);
            fields.add(breathingField);

            JsonObject coughField = new JsonObject();
            coughField.addProperty("name", "coughDetails");
            coughField.addProperty("label", "Pouvez-vous décrire votre toux ? Est-elle productive ?");
            coughField.addProperty("type", "text");
            coughField.addProperty("required", true);
            fields.add(coughField);
        }
        else if ("gastroenterologue".equals(specialty)) {
            JsonObject digestionField = new JsonObject();
            digestionField.addProperty("name", "digestionIssues");
            digestionField.addProperty("label", "Décrivez vos problèmes digestifs et leur fréquence.");
            digestionField.addProperty("type", "text");
            digestionField.addProperty("required", true);
            fields.add(digestionField);

            JsonObject dietField = new JsonObject();
            dietField.addProperty("name", "dietaryHabits");
            dietField.addProperty("label", "Quelles sont vos habitudes alimentaires ?");
            dietField.addProperty("type", "text");
            dietField.addProperty("required", true);
            fields.add(dietField);
        }

        form.add("fields", fields);

        // Envoyer le formulaire
        msg.setContent(form.toString());
        msg.setConversationId("doctor-questions");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Questions supplémentaires envoyées à " + currentPatientAID.getLocalName());

        // Ajouter un comportement pour attendre les réponses
        addBehaviour(new WaitForResponsesBehaviour());
    }

    // Établit un diagnostic
    private String diagnosisPatient() {
        // Initialiser le diagnostic
        StringBuilder diagnosis = new StringBuilder("Diagnostic: ");

        // Analyser les symptômes du dossier du patient
        HashMap<String, String> symptomsInfo = currentPatientRecord.getSymptomsInfo();

        // Identifier les maladies possibles
        List<Disease> possibleDiseases = DiseaseDatabase.getInstance().findDiseasesBySymptoms(symptomsInfo);
        Disease probableDisease = null;

        if (!possibleDiseases.isEmpty()) {
            probableDisease = possibleDiseases.get(0);
            diagnosis.append("Vous présentez les symptômes de " + probableDisease.getName() + ". ");
            diagnosis.append(probableDisease.getDescription() + " ");
        } else {
            diagnosis.append("Vos symptômes ne correspondent pas clairement à une pathologie connue. ");
        }

        // Analyser les réponses aux questions supplémentaires
        String medicalHistory = patientResponses.get("medicalHistory");
        String familyHistory = patientResponses.get("familyHistory");

        // Diagnostic spécifique selon la spécialité
        if ("cardiologue".equals(specialty)) {
            String chestPain = patientResponses.get("chestPain");
            String heartRate = patientResponses.get("heartRate");

            if (chestPain != null && chestPain.toLowerCase().contains("oppression")) {
                diagnosis.append("Les symptômes indiquent une possible angine de poitrine. ");
            }
            else if (heartRate != null && heartRate.toLowerCase().contains("palpitation")) {
                diagnosis.append("Vos palpitations suggèrent une possible arythmie cardiaque. ");
            }
            else {
                diagnosis.append("Un trouble cardiaque à préciser. ");
            }
        }
        else if ("pneumologue".equals(specialty)) {
            String breathingDifficulties = patientResponses.get("breathingDifficulties");
            String coughDetails = patientResponses.get("coughDetails");

            if (coughDetails != null && coughDetails.toLowerCase().contains("productive")) {
                diagnosis.append("Votre toux productive évoque une possible bronchite. ");
            }
            else if (breathingDifficulties != null && breathingDifficulties.toLowerCase().contains("effort")) {
                diagnosis.append("Vos difficultés respiratoires à l'effort suggèrent un possible asthme d'effort. ");
            }
            else {
                diagnosis.append("Une affection respiratoire à préciser. ");
            }
        }
        else if ("gastroenterologue".equals(specialty)) {
            String digestionIssues = patientResponses.get("digestionIssues");
            String dietaryHabits = patientResponses.get("dietaryHabits");

            if (digestionIssues != null && digestionIssues.toLowerCase().contains("brûlure")) {
                diagnosis.append("Vos symptômes évoquent un possible reflux gastro-œsophagien. ");
            }
            else if (digestionIssues != null && digestionIssues.toLowerCase().contains("douleur")) {
                diagnosis.append("Vos douleurs abdominales suggèrent un possible syndrome du côlon irritable. ");
            }
            else {
                diagnosis.append("Un trouble digestif à préciser. ");
            }
        }
        else {
            // Médecin généraliste
            diagnosis.append("Une condition générale à préciser. ");
        }

        // Recommandations générales
        diagnosis.append("\n\nRecommandations: ");

        if (medicalHistory != null && !medicalHistory.isEmpty()) {
            diagnosis.append("Compte tenu de vos antécédents médicaux, ");
        }

        if (familyHistory != null && !familyHistory.isEmpty()) {
            diagnosis.append("et de vos antécédents familiaux, ");
        }

        diagnosis.append("je vous recommande de suivre ce traitement et de revenir pour un suivi dans deux semaines.");

        return diagnosis.toString();
    }

    // Identifie la maladie la plus probable
    private Disease identifyDisease(HashMap<String, String> symptoms) {
        // Utiliser la base de données de maladies pour identifier la plus probable
        List<Disease> potentialDiseases = DiseaseDatabase.getInstance().findDiseasesBySymptoms(symptoms);

        if (!potentialDiseases.isEmpty()) {
            return potentialDiseases.get(0); // Retourne la maladie avec la meilleure correspondance
        }

        // Si aucune maladie n'est identifiée, créer une maladie générique
        Disease unknownDisease = new Disease("UNK001", "Condition à déterminer");
        unknownDisease.setCategory(this.specialty);
        return unknownDisease;
    }

    // Prescrit un traitement
    private void prescribeTreatment() {
        // Générer le diagnostic
        String diagnosisText = diagnosisPatient();

        // Déterminer la maladie la plus probable
        Disease probableDisease = identifyDisease(currentPatientRecord.getSymptomsInfo());

        // Créer une consultation
        Consultation consultation = new Consultation();
        consultation.setDoctorId(getLocalName());
        consultation.setDiagnosis(diagnosisText);
        consultation.setTimestamp(new Date());
        consultation.setSymptoms(currentPatientRecord.getSymptomsInfo());
        consultation.setDisease(probableDisease);

        // Sélectionner le traitement adapté au patient (supposons un âge et un poids moyens)
        int estimatedAge = 40; // Valeur par défaut
        int estimatedWeight = 70; // Valeur par défaut

        // Essayer d'extraire l'âge du dossier patient si disponible
        try {
            String birthDateStr = currentPatientRecord.getPersonalInfo().get("birthDate");
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                // Format simple: YYYY-MM-DD
                int birthYear = Integer.parseInt(birthDateStr.substring(0, 4));
                int currentYear = new Date().getYear() + 1900;
                estimatedAge = currentYear - birthYear;
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser l'âge par défaut
            gui.displayMessage("Impossible de déterminer l'âge exact du patient. Utilisation de valeurs par défaut.");
        }

        // Obtenir les médicaments recommandés pour cette maladie
        List<Medication> personalizedTreatment = TreatmentDatabase.getInstance()
            .selectTreatmentForPatient(probableDisease, estimatedAge, estimatedWeight);

        // Ajouter les médicaments à la prescription
        for (Medication medication : personalizedTreatment) {
            consultation.addMedication(medication);
        }

        // Ajouter les instructions générales
        String guidelines = TreatmentDatabase.getInstance().getGuidelinesForDisease(probableDisease.getId());
        consultation.setNotes(guidelines);

        // Ajouter la consultation au dossier du patient
        currentPatientRecord.addConsultation(consultation);

        // Préparer la réponse pour le patient
        StringBuilder response = new StringBuilder(diagnosisText);
        response.append("\n\nPrescription:\n");

        for (Medication med : consultation.getPrescriptions()) {
            response.append("- ").append(med.getName()).append(" (").append(med.getDosage()).append("): ")
                .append(med.getInstructions()).append(", pendant ").append(med.getDuration())
                .append(" jours, ").append(med.getFrequency()).append("\n");
        }

        if (guidelines != null && !guidelines.isEmpty()) {
            response.append("\nConsignes particulières:\n").append(guidelines);
        }

        // Envoyer le diagnostic et la prescription au patient
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(currentPatientAID);
        msg.setContent(response.toString());
        msg.setConversationId("diagnosis");
        send(msg);

        // Journaliser l'action
        gui.displayMessage("Diagnostic envoyé à " + currentPatientAID.getLocalName());
        gui.displayMessage(response.toString());

        // Mettre à jour le dossier patient auprès de la réceptionniste
        updatePatientRecordAtReceptionist(currentPatientRecord);

        // Marquer le médecin comme disponible
        available = true;

        // Informer la réceptionniste que la consultation est terminée
        informReceptionistConsultationCompleted();
    }

    // Informe la réceptionniste que la consultation est terminée
    private void informReceptionistConsultationCompleted() {
        // Rechercher l'agent réceptionniste
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                AID receptionistAID = result[0].getName();

                // Créer et configurer le message
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receptionistAID);
                msg.setContent("CONSULTATION_COMPLETED");
                msg.setConversationId("doctor-status");
                send(msg);

                // Journaliser l'action
                gui.displayMessage("Réceptionniste informée que la consultation est terminée");
            } else {
                gui.displayMessage("Erreur: Réceptionniste non trouvée");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la recherche de la réceptionniste: " + fe.getMessage());
        }
    }

    // Met à jour le dossier du patient auprès de la réceptionniste
    private void updatePatientRecordAtReceptionist(PatientRecord record) {
        // Rechercher l'agent réceptionniste
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("receptionist");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                AID receptionistAID = result[0].getName();

                // Créer et configurer le message
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receptionistAID);
                msg.setConversationId("patient-record-update");

                // Convertir le dossier en JSON et l'envoyer
                String jsonData = gson.toJson(record);
                msg.setContent(jsonData);
                send(msg);

                // Journaliser l'action
                gui.displayMessage("Dossier patient mis à jour envoyé à la réceptionniste");
            } else {
                gui.displayMessage("Erreur: Réceptionniste non trouvée");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la mise à jour du dossier: " + fe.getMessage());
        }
    }

    // Retourne le nom du médecin basé sur sa spécialité
    private String getDoctorName() {
        // Dans un système réel, cette information serait récupérée à partir d'une base de données
        if ("cardiologue".equals(specialty)) {
            return "Dupont (Cardiologue)";
        } else if ("pneumologue".equals(specialty)) {
            return "Martin (Pneumologue)";
        } else if ("gastroenterologue".equals(specialty)) {
            return "Durand (Gastroentérologue)";
        } else {
            return "Petit (Généraliste)";
        }
    }

    // Vérifie si le médecin est disponible
    public boolean isAvailable() {
        return available;
    }

    // Récupère la spécialité du médecin
    public String getSpecialty() {
        return specialty;
    }

    // Récupère le numéro de salle du médecin
    public int getRoomNumber() {
        return roomNumber;
    }

    // Comportement pour recevoir les informations du patient
    private class ReceivePatientInfoBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages avec conversationId "patient-info"
            MessageTemplate mt = MessageTemplate.MatchConversationId("patient-info");
            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    // Récupérer et stocker le dossier du patient
                    currentPatientRecord = gson.fromJson(msg.getContent(), PatientRecord.class);

                    // Marquer le médecin comme occupé
                    available = false;

                    // Journaliser l'action
                    gui.displayMessage("Dossier du patient reçu: " + currentPatientRecord.getPatientId());

                    // Afficher les informations du patient dans l'interface
                    gui.displayPatientInfo(currentPatientRecord);

                    // NOUVEAU: Inviter le patient à venir dans la salle de consultation
                    invitePatientToConsultation();

                } catch (Exception e) {
                    e.printStackTrace();
                    gui.displayMessage("Erreur lors de la réception du dossier patient: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    // Comportement pour attendre l'arrivée du patient
    private class WaitForPatientArrivalBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages INFORM avec conversationId "patient-location" et contenu "PATIENT_ARRIVED"
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("patient-location"),
                MessageTemplate.MatchContent("PATIENT_ARRIVED"));

            ACLMessage msg = receive(mt);

            if (msg != null) {
                // Stocker l'AID du patient
                currentPatientAID = msg.getSender();

                // Journaliser l'action
                gui.displayMessage("Patient " + currentPatientAID.getLocalName() + " arrivé en salle de consultation");

                // Saluer le patient
                greetPatient();

                // Poser des questions supplémentaires
                askAdditionalQuestions();
            } else {
                block();
            }
        }
    }

    // Comportement pour attendre les réponses du patient
    private class WaitForResponsesBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages avec conversationId "doctor-consultation"
            MessageTemplate mt = MessageTemplate.MatchConversationId("doctor-consultation");
            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    // Récupérer les réponses du patient
                    HashMap<String, String> responses = gson.fromJson(
                        msg.getContent(),
                        new TypeToken<HashMap<String, String>>(){}.getType());

                    // Stocker les réponses
                    patientResponses.putAll(responses);

                    // Journaliser l'action
                    gui.displayMessage("Réponses du patient reçues");

                    // Présenter un résumé des réponses dans l'interface
                    for (String key : responses.keySet()) {
                        gui.displayMessage(key + ": " + responses.get(key));
                    }

                    // Prescrire un traitement
                    prescribeTreatment();

                    // Supprimer ce comportement
                    myAgent.removeBehaviour(this);
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
            gui.displayMessage("Médecin désinscrit du DF");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Fermer l'interface graphique
        if (gui != null) {
            gui.dispose();
        }

        // Journaliser la terminaison
        System.out.println("Agent Médecin " + getAID().getName() + " terminé.");
    }
}
