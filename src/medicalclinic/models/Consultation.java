package medicalclinic.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Consultation implements Serializable {
    private String doctorId;
    private String diagnosis;
    private Date timestamp;
    private HashMap<String, String> symptoms;
    private List<Medication> prescriptions;
    private Disease disease;
    private String notes;

    public Consultation() {
        this.timestamp = new Date();
        this.symptoms = new HashMap<>();
        this.prescriptions = new ArrayList<>();
    }

    // Getters et Setters
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(HashMap<String, String> symptoms) {
        this.symptoms = symptoms;
    }

    public void addMedication(Medication medication) {
        prescriptions.add(medication);
    }

    public List<Medication> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Medication> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
