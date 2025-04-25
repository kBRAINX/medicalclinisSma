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
import java.util.Map;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import medicalclinic.database.DiseaseDatabase;
import medicalclinic.gui.ReceptionistGUI;
import medicalclinic.models.Disease;
import medicalclinic.models.PatientRecord;
import medicalclinic.models.WaitingPatientInfo;

public class ReceptionistAgent extends Agent {
    private HashMap<String, PatientRecord> patientRecords;
    private HashMap<String, Boolean> doctorAvailability;
    private HashMap<String, DoctorInfo> doctorInfos; // Informations détaillées sur les médecins
    private ArrayList<AID> doctorAIDs;
    private LinkedList<WaitingPatientInfo> waitingPatients;
    private ReceptionistGUI gui;
    private Gson gson = new Gson();

    // Classe interne pour stocker les informations des médecins
    private static class DoctorInfo {
        private String id;
        private String specialty;
        private String qualification;
        private int experience;
        private List<String> expertises;
        private int roomNumber;

        public DoctorInfo(String id, String specialty, String qualification, int experience,
                          List<String> expertises, int roomNumber) {
            this.id = id;
            this.specialty = specialty;
            this.qualification = qualification;
            this.experience = experience;
            this.expertises = expertises;
            this.roomNumber = roomNumber;
        }

        public String getId() { return id; }
        public String getSpecialty() { return specialty; }
        public String getQualification() { return qualification; }
        public int getExperience() { return experience; }
        public List<String> getExpertises() { return expertises; }
        public int getRoomNumber() { return roomNumber; }
    }

    @Override
    protected void setup() {
        // Initialiser les structures de données
        patientRecords = new HashMap<>();
        doctorAvailability = new HashMap<>();
        doctorInfos = new HashMap<>();
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
        addBehaviour(new RequestDoctorInfoBehaviour());

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

                // Initialiser les informations de base du médecin
                String specialty = getDoctorSpecialty(doctorAID.getLocalName());

                // Créer l'instance DoctorInfo avec des valeurs par défaut
                DoctorInfo doctorInfo = new DoctorInfo(
                    doctorAID.getLocalName(),
                    specialty,
                    getDoctorQualification(doctorAID.getLocalName()),
                    5, // Expérience par défaut
                    new ArrayList<>(), // Expertises vides par défaut
                    getDoctorRoomNumber(doctorAID.getLocalName())
                );

                doctorInfos.put(doctorAID.getLocalName(), doctorInfo);

                gui.displayMessage("Médecin trouvé: " + doctorAID.getLocalName() +
                    " - Spécialité: " + specialty);

                // Demander des informations détaillées sur le médecin
                requestDoctorInfo(doctorAID);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
            gui.displayMessage("Erreur lors de la recherche des médecins: " + fe.getMessage());
        }
    }

    // Demande des informations détaillées sur un médecin
    private void requestDoctorInfo(AID doctorAID) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(doctorAID);
        request.setContent("REQUEST_DOCTOR_INFO");
        request.setConversationId("doctor-info-request");
        send(request);

        gui.displayMessage("Demande d'informations détaillées envoyée au médecin " + doctorAID.getLocalName());
    }

    // Envoie un message de bienvenue au patient
    private void sendWelcomeMessage(AID patientAID) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(patientAID);
        msg.setContent("Bienvenue au Cabinet Médical. Je suis la réceptionniste et je vais vous accompagner tout au long de votre parcours dans notre établissement. Pour commencer, merci de remplir le formulaire suivant avec vos informations personnelles.");
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
        birthDateField.addProperty("label", "Date de naissance (AAAA-MM-JJ)");
        birthDateField.addProperty("type", "date");
        birthDateField.addProperty("required", true);
        fields.add(birthDateField);

        // Champ "gender" (Genre)
        JsonObject genderField = new JsonObject();
        genderField.addProperty("name", "gender");
        genderField.addProperty("label", "Genre");
        genderField.addProperty("type", "text");
        genderField.addProperty("required", true);
        fields.add(genderField);

        // Champ "address" (Adresse)
        JsonObject addressField = new JsonObject();
        addressField.addProperty("name", "address");
        addressField.addProperty("label", "Adresse");
        addressField.addProperty("type", "text");
        addressField.addProperty("required", true);
        fields.add(addressField);

        // Champ "city" (Ville)
        JsonObject cityField = new JsonObject();
        cityField.addProperty("name", "city");
        cityField.addProperty("label", "Ville");
        cityField.addProperty("type", "text");
        cityField.addProperty("required", true);
        fields.add(cityField);

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

        // Champ "emergencyContact" (Contact d'urgence)
        JsonObject emergencyContactField = new JsonObject();
        emergencyContactField.addProperty("name", "emergencyContact");
        emergencyContactField.addProperty("label", "Contact d'urgence (nom et téléphone)");
        emergencyContactField.addProperty("type", "text");
        emergencyContactField.addProperty("required", true);
        fields.add(emergencyContactField);

        // Champ "previousMedicalHistory" (Antécédents médicaux)
        JsonObject medicalHistoryField = new JsonObject();
        medicalHistoryField.addProperty("name", "previousMedicalHistory");
        medicalHistoryField.addProperty("label", "Antécédents médicaux importants");
        medicalHistoryField.addProperty("type", "text");
        medicalHistoryField.addProperty("required", false);
        fields.add(medicalHistoryField);

        form.add("fields", fields);

        // Envoyer le formulaire
        msg.setContent(form.toString());
        msg.setConversationId("personal-form");
        send(msg);

        gui.displayMessage("Formulaire d'informations personnelles envoyé à " + patientAID.getLocalName());
    }

    // Vérifie si un formulaire est complet
    private boolean verifyFormCompletion(HashMap<String, String> formData) {
        String[] requiredFields = {"firstName", "lastName", "birthDate", "gender", "address", "city", "phone", "emergencyContact"};

        for (String field : requiredFields) {
            if (!formData.containsKey(field) || formData.get(field).isEmpty()) {
                gui.displayMessage("Formulaire incomplet: champ '" + field + "' manquant");
                return false;
            }
        }

        return true;
    }

    // Détermine les scores de compatibilité entre les symptômes du patient et les médecins
    private Map<String, Integer> calculateDoctorCompatibilityScores(HashMap<String, String> symptomsInfo) {
        Map<String, Integer> compatibilityScores = new HashMap<>();

        // Utiliser la base de données des maladies pour trouver les correspondances
        List<Disease> potentialDiseases = DiseaseDatabase.getInstance().findDiseasesBySymptoms(symptomsInfo);

        gui.displayMessage("Analyse des symptômes pour l'affectation au médecin:");
        for (Disease disease : potentialDiseases) {
            int matchScore = Integer.parseInt(disease.getAdditionalInfo().getOrDefault("matchScore", "0"));
            gui.displayMessage("- Maladie potentielle: " + disease.getName() +
                " (" + disease.getCategory() + ") - Score: " + matchScore + "%");
        }

        // Pour chaque médecin, calculer un score de compatibilité
        for (Map.Entry<String, DoctorInfo> entry : doctorInfos.entrySet()) {
            String doctorId = entry.getKey();
            DoctorInfo doctorInfo = entry.getValue();

            // Score initial basé sur la disponibilité
            int score = doctorAvailability.getOrDefault(doctorId, false) ? 50 : 0;

            // Si le médecin n'est pas disponible, il aura un score bas mais pas nul
            // pour pouvoir quand même être considéré en cas d'urgence ou si aucun médecin
            // n'est disponible

            // Analyser chaque maladie potentielle
            for (Disease disease : potentialDiseases) {
                int diseaseMatchScore = Integer.parseInt(disease.getAdditionalInfo().getOrDefault("matchScore", "0"));

                // Ajuster le score en fonction de la spécialité du médecin et de la catégorie de la maladie
                if (doctorInfo.getSpecialty().toLowerCase().contains(disease.getCategory().toLowerCase()) ||
                    disease.getCategory().toLowerCase().contains(doctorInfo.getSpecialty().toLowerCase())) {
                    // Bonus majeur si la spécialité correspond directement à la catégorie de la maladie
                    score += diseaseMatchScore * 1.5;
                }

                // Bonus pour les expertises spécifiques
                for (String expertise : doctorInfo.getExpertises()) {
                    if (disease.getName().toLowerCase().contains(expertise.toLowerCase()) ||
                        expertise.toLowerCase().contains(disease.getName().toLowerCase())) {
                        score += diseaseMatchScore * 1.3;
                        break;
                    }
                }

                // Bonus léger pour les médecins généralistes (ils peuvent traiter la plupart des maladies)
                if ("generaliste".equals(doctorInfo.getSpecialty().toLowerCase())) {
                    score += diseaseMatchScore * 0.8;
                }

                // Bonus pour l'expérience du médecin
                score += Math.min(doctorInfo.getExperience() * 2, 20); // Maximum de 20 points pour l'expérience
            }

            // Enregistrer le score final
            compatibilityScores.put(doctorId, score);
        }

        // Afficher les scores pour le débogage
        gui.displayMessage("Scores de compatibilité médecin-patient:");
        for (Map.Entry<String, Integer> score : compatibilityScores.entrySet()) {
            DoctorInfo doctor = doctorInfos.get(score.getKey());
            String specialty = doctor != null ? doctor.getSpecialty() : "Inconnu";
            gui.displayMessage("- " + score.getKey() + " (" + specialty + "): " + score.getValue() + " points");
        }

        return compatibilityScores;
    }

    // Attribue un patient à un médecin approprié
    private AID assignPatientToDoctor(String patientId, HashMap<String, String> symptomsInfo) {
        gui.displayMessage("Début du processus d'affectation pour le patient " + patientId);

        // Calculer les scores de compatibilité
        Map<String, Integer> doctorScores = calculateDoctorCompatibilityScores(symptomsInfo);

        // Trier les médecins par score décroissant
        List<Map.Entry<String, Integer>> sortedDoctors = new ArrayList<>(doctorScores.entrySet());
        sortedDoctors.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Sélectionner le médecin avec le score le plus élevé qui est disponible
        for (Map.Entry<String, Integer> entry : sortedDoctors) {
            String doctorId = entry.getKey();
            int score = entry.getValue();

            // Vérifier si le médecin est disponible
            if (doctorAvailability.getOrDefault(doctorId, false)) {
                // Trouver l'AID du médecin
                for (AID doctorAID : doctorAIDs) {
                    if (doctorAID.getLocalName().equals(doctorId)) {
                        // Marquer le médecin comme occupé
                        doctorAvailability.put(doctorId, false);

                        DoctorInfo doctorInfo = doctorInfos.get(doctorId);
                        String specialty = doctorInfo != null ? doctorInfo.getSpecialty() : "Inconnu";

                        gui.displayMessage("Médecin sélectionné: " + doctorId +
                            " (" + specialty + ") avec un score de compatibilité de " + score + " points");

                        return doctorAID;
                    }
                }
            }
        }

        // Si tous les médecins sont occupés, mais qu'il s'agit d'une urgence potentielle,
        // on pourrait quand même sélectionner le meilleur médecin même s'il est occupé
        // Cette logique pourrait être ajoutée ici

        gui.displayMessage("Aucun médecin disponible pour le moment");
        return null;
    }

    // Informe le patient de l'affectation à un médecin
    private void informPatientOfDoctorAssignment(AID patientAID, AID doctorAID, int roomNumber) {
        // Récupérer les informations du médecin
        DoctorInfo doctorInfo = doctorInfos.get(doctorAID.getLocalName());
        String doctorName = getDoctorName(doctorAID.getLocalName());
        String specialty = doctorInfo != null ? doctorInfo.getSpecialty() : getDoctorSpecialty(doctorAID.getLocalName());

        // Informer d'abord le patient de l'affectation (détails du médecin)
        ACLMessage assignmentMsg = new ACLMessage(ACLMessage.INFORM);
        assignmentMsg.addReceiver(patientAID);

        // Créer un objet JSON avec les informations d'affectation
        JsonObject assignment = new JsonObject();
        assignment.addProperty("doctorId", doctorAID.getLocalName());
        assignment.addProperty("doctorName", doctorName);
        assignment.addProperty("specialty", specialty);
        assignment.addProperty("roomNumber", roomNumber);

        // Ajouter des informations supplémentaires si disponibles
        if (doctorInfo != null) {
            assignment.addProperty("qualification", doctorInfo.getQualification());
            assignment.addProperty("experience", doctorInfo.getExperience() + " ans");

            JsonArray expertisesArray = new JsonArray();
            for (String expertise : doctorInfo.getExpertises()) {
                expertisesArray.add(expertise);
            }
            assignment.add("expertises", expertisesArray);
        }

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
            doctorName + " (" + specialty + ") dans la salle " + roomNumber);
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
        else if (info.containsKey("patientId") || info.containsKey("symptomDuration") ||
            info.containsKey("fever") || info.containsKey("mainSymptoms")) {
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
        waitingMsg.setContent("Vous avez été placé en liste d'attente. Tous nos médecins sont actuellement occupés. Veuillez patienter dans la salle d'attente jusqu'à ce qu'un médecin soit disponible. Notre équipe fera de son mieux pour vous prendre en charge dans les meilleurs délais.");
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
                int roomNumber = doctorInfos.containsKey(doctorAID.getLocalName()) ?
                    doctorInfos.get(doctorAID.getLocalName()).getRoomNumber() :
                    getDoctorRoomNumber(doctorAID.getLocalName());

                informPatientOfDoctorAssignment(patientAID, doctorAID, roomNumber);

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

                // Mettre à jour l'information du temps d'attente pour tous les patients
                updateWaitingTimesDisplay();
            }
        } else {
            gui.displayMessage("Aucun patient en attente.");
        }
    }

    // Met à jour l'affichage des temps d'attente
    private void updateWaitingTimesDisplay() {
        // Mettre à jour l'interface avec les temps d'attente actualisés
        gui.updateWaitingPatients(waitingPatients);

        // Informer les patients de leur position mise à jour
        for (int i = 0; i < waitingPatients.size(); i++) {
            WaitingPatientInfo waitingInfo = waitingPatients.get(i);
            AID patientAID = new AID(waitingInfo.getPatientId(), AID.ISLOCALNAME);

            ACLMessage positionMsg = new ACLMessage(ACLMessage.INFORM);
            positionMsg.addReceiver(patientAID);
            positionMsg.setContent("Mise à jour: Votre position dans la file d'attente: " + (i + 1) +
                ". Temps d'attente estimé: " + waitingInfo.getWaitingTimeInMinutes() + " minutes.");
            positionMsg.setConversationId("waiting-position");
            send(positionMsg);
        }
    }

    // Retourne la spécialité d'un médecin
    private String getDoctorSpecialty(String doctorId) {
        // Vérifier d'abord dans les informations détaillées
        if (doctorInfos.containsKey(doctorId)) {
            return doctorInfos.get(doctorId).getSpecialty();
        }

        // Sinon, déduire à partir de l'ID
        if (doctorId.contains("cardio")) {
            return "cardiologue";
        } else if (doctorId.contains("pneumo")) {
            return "pneumologue";
        } else if (doctorId.contains("gastro")) {
            return "gastroenterologue";
        } else if (doctorId.contains("infect")) {
            return "infectiologue";
        } else if (doctorId.contains("neuro")) {
            return "neurologue";
        } else if (doctorId.contains("endocr")) {
            return "endocrinologue";
        } else if (doctorId.contains("doctor1")) {
            return "cardiologue";
        } else if (doctorId.contains("doctor2")) {
            return "pneumologue";
        } else if (doctorId.contains("doctor3")) {
            return "gastroenterologue";
        } else if (doctorId.contains("doctor4")) {
            return "infectiologue";
        } else {
            return "generaliste";
        }
    }

    // Retourne la qualification d'un médecin
    private String getDoctorQualification(String doctorId) {
        String specialty = getDoctorSpecialty(doctorId);

        if ("cardiologue".equals(specialty)) {
            return "Spécialiste en cardiologie";
        } else if ("pneumologue".equals(specialty)) {
            return "Spécialiste en pneumologie";
        } else if ("gastroenterologue".equals(specialty)) {
            return "Spécialiste en gastroentérologie";
        } else if ("infectiologue".equals(specialty)) {
            return "Spécialiste en maladies infectieuses";
        } else if ("neurologue".equals(specialty)) {
            return "Spécialiste en neurologie";
        } else if ("endocrinologue".equals(specialty)) {
            return "Spécialiste en endocrinologie";
        } else {
            return "Médecin généraliste";
        }
    }

    // Retourne le nom d'un médecin
    private String getDoctorName(String doctorId) {
        if (doctorId.contains("cardio") || doctorId.equals("doctor1")) {
            return "Dr. Dupont";
        } else if (doctorId.contains("pneumo") || doctorId.equals("doctor2")) {
            return "Dr. Martin";
        } else if (doctorId.contains("gastro") || doctorId.equals("doctor3")) {
            return "Dr. Durand";
        } else if (doctorId.contains("infect") || doctorId.equals("doctor4")) {
            return "Dr. Koné";
        } else if (doctorId.contains("neuro")) {
            return "Dr. Diallo";
        } else if (doctorId.contains("endocr")) {
            return "Dr. Touré";
        } else {
            return "Dr. Petit";
        }
    }

    // Retourne le numéro de salle d'un médecin
    private int getDoctorRoomNumber(String doctorId) {
        // Vérifier d'abord dans les informations détaillées
        if (doctorInfos.containsKey(doctorId)) {
            return doctorInfos.get(doctorId).getRoomNumber();
        }

        // Sinon, déduire à partir de l'ID
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
        } else if (doctorId.contains("cardio")) {
            return 201;
        } else if (doctorId.contains("pneumo")) {
            return 202;
        } else if (doctorId.contains("gastro")) {
            return 203;
        } else if (doctorId.contains("infect")) {
            return 204;
        } else if (doctorId.contains("neuro")) {
            return 205;
        } else if (doctorId.contains("endocr")) {
            return 206;
        } else {
            // Si l'ID ne correspond pas à un format attendu, on utilise une valeur par défaut
            return 100 + Integer.parseInt(doctorId.replaceAll("[^0-9]", "0")) % 10;
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

                        // Vérifier si le formulaire est complet
                        if (verifyFormCompletion(personalInfo)) {
                            updatePatientRecord(senderAID.getLocalName(), personalInfo);

                            // Demander au patient de se déplacer vers la salle d'attente
                            ACLMessage moveMsg = new ACLMessage(ACLMessage.REQUEST);
                            moveMsg.addReceiver(senderAID);
                            moveMsg.setContent("MOVE_TO_WAITING_ROOM");
                            moveMsg.setConversationId("move-request");
                            send(moveMsg);

                            gui.displayMessage("Patient " + senderAID.getLocalName() +
                                " : formulaire complet, dirigé vers la salle d'attente");

                            // Confirmer la réception des informations
                            ACLMessage confirmMsg = new ACLMessage(ACLMessage.INFORM);
                            confirmMsg.addReceiver(senderAID);
                            confirmMsg.setContent("Vos informations personnelles ont été enregistrées avec succès. " +
                                "Veuillez maintenant vous rendre dans la salle d'attente où un infirmier vous recevra " +
                                "pour recueillir vos symptômes.");
                            confirmMsg.setConversationId("receptionist-feedback");
                            send(confirmMsg);
                        } else {
                            // Demander au patient de compléter le formulaire
                            ACLMessage incompleteMsg = new ACLMessage(ACLMessage.INFORM);
                            incompleteMsg.addReceiver(senderAID);
                            incompleteMsg.setContent("Certaines informations requises sont manquantes. " +
                                "Veuillez compléter tous les champs obligatoires du formulaire.");
                            incompleteMsg.setConversationId("receptionist-feedback");
                            send(incompleteMsg);

                            gui.displayMessage("Patient " + senderAID.getLocalName() +
                                " : formulaire incomplet, demande de compléter envoyée");
                        }
                    }
                    else if ("symptom-info".equals(conversationId)) {
                        // Traiter les informations sur les symptômes
                        HashMap<String, String> symptomsInfo = gson.fromJson(
                            msg.getContent(),
                            new TypeToken<HashMap<String, String>>(){}.getType());

                        String patientId = symptomsInfo.get("patientId");
                        updatePatientRecord(patientId, symptomsInfo);

                        // Confirmer la réception des informations à l'infirmier
                        ACLMessage confirmMsg = new ACLMessage(ACLMessage.INFORM);
                        confirmMsg.addReceiver(senderAID);
                        confirmMsg.setContent("Informations sur les symptômes du patient " + patientId + " reçues. " +
                            "Affectation à un médecin en cours.");
                        confirmMsg.setConversationId("symptom-info-confirm");
                        send(confirmMsg);

                        // Attribuer un médecin au patient
                        AID patientAID = new AID(patientId, AID.ISLOCALNAME);
                        AID doctorAID = assignPatientToDoctor(patientId, symptomsInfo);

                        if (doctorAID != null) {
                            // Envoyer les informations du patient au médecin
                            sendPatientInfoToDoctor(doctorAID, patientId);

                            // Informer le patient de l'affectation
                            int roomNumber = doctorInfos.containsKey(doctorAID.getLocalName()) ?
                                doctorInfos.get(doctorAID.getLocalName()).getRoomNumber() :
                                getDoctorRoomNumber(doctorAID.getLocalName());

                            informPatientOfDoctorAssignment(patientAID, doctorAID, roomNumber);
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
                            gui.displayMessage("⚠️ NOTIFICATION D'URGENCE pour le patient " + patientId);

                            // Priorité aux cas urgents - Réaffecter immédiatement un médecin
                            if (patientRecords.containsKey(patientId)) {
                                HashMap<String, String> symptomsInfo = patientRecords.get(patientId).getSymptomsInfo();

                                // Forcer l'attribution d'un médecin même s'il est occupé
                                // On pourrait implémenter une logique plus sophistiquée ici

                                // Pour l'instant, simplement mettre le patient en premier dans la file d'attente
                                // et essayer de l'affecter en priorité lors de la prochaine disponibilité

                                // Vérifier s'il est déjà dans la liste d'attente
                                boolean alreadyWaiting = false;
                                for (WaitingPatientInfo info : waitingPatients) {
                                    if (info.getPatientId().equals(patientId)) {
                                        waitingPatients.remove(info);
                                        alreadyWaiting = true;
                                        break;
                                    }
                                }

                                // Créer une nouvelle info d'attente avec priorité
                                WaitingPatientInfo urgentInfo = new WaitingPatientInfo(patientId, symptomsInfo);
                                urgentInfo.setUrgent(true); // Marquer comme urgent

                                // Ajouter en tête de liste
                                waitingPatients.addFirst(urgentInfo);
                                gui.updateWaitingPatients(waitingPatients);

                                gui.displayMessage("Patient " + patientId + " marqué comme URGENT et " +
                                    (alreadyWaiting ? "déplacé" : "ajouté") + " en tête de la file d'attente");

                                // Informer le patient
                                AID patientAID = new AID(patientId, AID.ISLOCALNAME);
                                ACLMessage urgentMsg = new ACLMessage(ACLMessage.INFORM);
                                urgentMsg.addReceiver(patientAID);
                                urgentMsg.setContent("Votre cas a été identifié comme urgent. Vous êtes maintenant " +
                                    "en tête de la file d'attente. Un médecin va vous prendre en charge " +
                                    "dès que possible.");
                                urgentMsg.setConversationId("urgent-info");
                                send(urgentMsg);
                            }
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
                    else if ("doctor-info".equals(conversationId)) {
                        // Traiter les informations détaillées du médecin
                        JsonObject doctorInfoJson = gson.fromJson(msg.getContent(), JsonObject.class);
                        String doctorId = senderAID.getLocalName();

                        String specialty = doctorInfoJson.has("specialty") ?
                            doctorInfoJson.get("specialty").getAsString() : getDoctorSpecialty(doctorId);

                        String qualification = doctorInfoJson.has("qualification") ?
                            doctorInfoJson.get("qualification").getAsString() : getDoctorQualification(doctorId);

                        int experience = doctorInfoJson.has("experience") ?
                            doctorInfoJson.get("experience").getAsInt() : 5;

                        int roomNumber = doctorInfoJson.has("roomNumber") ?
                            doctorInfoJson.get("roomNumber").getAsInt() : getDoctorRoomNumber(doctorId);

                        List<String> expertises = new ArrayList<>();
                        if (doctorInfoJson.has("expertises")) {
                            JsonArray expertisesArray = doctorInfoJson.getAsJsonArray("expertises");
                            for (int i = 0; i < expertisesArray.size(); i++) {
                                expertises.add(expertisesArray.get(i).getAsString());
                            }
                        }

                        // Mettre à jour les informations du médecin
                        DoctorInfo doctorInfo = new DoctorInfo(
                            doctorId, specialty, qualification, experience, expertises, roomNumber);
                        doctorInfos.put(doctorId, doctorInfo);

                        gui.displayMessage("Informations détaillées reçues du médecin " + doctorId +
                            " : " + specialty + ", " + qualification + ", " + experience + " ans d'expérience");
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

    // Comportement pour demander des informations aux médecins
    private class RequestDoctorInfoBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Configurer un filtre pour les messages INFORM avec conversationId "doctor-info"
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("doctor-info-request-response"));

            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    // Traiter les informations du médecin
                    JsonObject doctorInfoJson = gson.fromJson(msg.getContent(), JsonObject.class);
                    String doctorId = msg.getSender().getLocalName();

                    String specialty = doctorInfoJson.has("specialty") ?
                        doctorInfoJson.get("specialty").getAsString() : getDoctorSpecialty(doctorId);

                    String qualification = doctorInfoJson.has("qualification") ?
                        doctorInfoJson.get("qualification").getAsString() : getDoctorQualification(doctorId);

                    int experience = doctorInfoJson.has("experience") ?
                        doctorInfoJson.get("experience").getAsInt() : 5;

                    int roomNumber = doctorInfoJson.has("roomNumber") ?
                        doctorInfoJson.get("roomNumber").getAsInt() : getDoctorRoomNumber(doctorId);

                    List<String> expertises = new ArrayList<>();
                    if (doctorInfoJson.has("expertises")) {
                        JsonArray expertisesArray = doctorInfoJson.getAsJsonArray("expertises");
                        for (int i = 0; i < expertisesArray.size(); i++) {
                            expertises.add(expertisesArray.get(i).getAsString());
                        }
                    }

                    // Mettre à jour les informations du médecin
                    DoctorInfo doctorInfo = new DoctorInfo(
                        doctorId, specialty, qualification, experience, expertises, roomNumber);
                    doctorInfos.put(doctorId, doctorInfo);

                    gui.displayMessage("Informations détaillées reçues du médecin " + doctorId +
                        " : " + specialty + ", " + qualification + ", " + experience + " ans d'expérience");
                } catch (Exception e) {
                    e.printStackTrace();
                    gui.displayMessage("Erreur lors du traitement des informations du médecin: " + e.getMessage());
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
