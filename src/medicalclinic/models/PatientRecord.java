package medicalclinic.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientRecord implements Serializable {
    private String patientId;
    private HashMap<String, String> personalInfo;
    private HashMap<String, String> symptomsInfo;
    private List<Consultation> consultationHistory;

    public PatientRecord(String patientId) {
        this.patientId = patientId;
        this.personalInfo = new HashMap<>();
        this.symptomsInfo = new HashMap<>();
        this.consultationHistory = new ArrayList<>();
    }

    // Méthodes pour mettre à jour et accéder aux informations
    public void updatePersonalInfo(HashMap<String, String> info) {
        personalInfo.putAll(info);
    }

    public void updateSymptomsInfo(HashMap<String, String> info) {
        symptomsInfo.putAll(info);
    }

    public void addConsultation(Consultation consultation) {
        consultationHistory.add(consultation);
    }

    // Getters
    public String getPatientId() {
        return patientId;
    }

    public HashMap<String, String> getPersonalInfo() {
        return personalInfo;
    }

    public HashMap<String, String> getSymptomsInfo() {
        return symptomsInfo;
    }

    public List<Consultation> getConsultationHistory() {
        return consultationHistory;
    }

    // Méthodes utilitaires
    public String getFullName() {
        String firstName = personalInfo.getOrDefault("firstName", "");
        String lastName = personalInfo.getOrDefault("lastName", "");
        return firstName + " " + lastName;
    }

    public boolean hasCompletePersonalInfo() {
        return personalInfo.containsKey("firstName") &&
            personalInfo.containsKey("lastName") &&
            personalInfo.containsKey("birthDate") &&
            personalInfo.containsKey("address") &&
            personalInfo.containsKey("phone");
    }
}
