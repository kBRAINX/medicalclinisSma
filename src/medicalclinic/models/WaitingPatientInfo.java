package medicalclinic.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class WaitingPatientInfo implements Serializable {
    private String patientId;
    private HashMap<String, String> symptomsInfo;
    private Date timeAdded;

    // Constructeur
    public WaitingPatientInfo(String patientId, HashMap<String, String> symptomsInfo) {
        this.patientId = patientId;
        this.symptomsInfo = symptomsInfo;
        this.timeAdded = new Date();
    }

    // Méthodes d'accès
    public String getPatientId() {
        return patientId;
    }

    public HashMap<String, String> getSymptomsInfo() {
        return symptomsInfo;
    }

    public Date getTimeAdded() {
        return timeAdded;
    }

    // Méthode pour calculer le temps d'attente en millisecondes
    public long getWaitingTime() {
        Date currentTime = new Date();
        return currentTime.getTime() - timeAdded.getTime();
    }

    // Méthode pour formater le temps d'attente en minutes
    public int getWaitingTimeInMinutes() {
        return (int)(getWaitingTime() / 60000);
    }
}
