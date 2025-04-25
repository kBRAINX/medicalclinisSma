package medicalclinic.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class WaitingPatientInfo implements Serializable {
    private String patientId;
    private HashMap<String, String> symptomsInfo;
    private Date timeAdded;
    private boolean isUrgent;

    // Constructeur
    public WaitingPatientInfo(String patientId, HashMap<String, String> symptomsInfo) {
        this.patientId = patientId;
        this.symptomsInfo = symptomsInfo;
        this.timeAdded = new Date();
        this.isUrgent = false;
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

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        this.isUrgent = urgent;
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

    // Méthode pour calculer la priorité d'attente
    public int getPriority() {
        if (isUrgent) {
            // Les cas urgents ont une priorité plus élevée
            return 100;
        } else {
            // La priorité normale est basée sur le temps d'attente
            return getWaitingTimeInMinutes();
        }
    }

    // Méthode pour obtenir une description formatée
    public String getFormattedDescription() {
        StringBuilder description = new StringBuilder();
        description.append(patientId);

        if (isUrgent) {
            description.append(" ⚠️ URGENT");
        }

        description.append(" (").append(getWaitingTimeInMinutes()).append(" min)");

        return description.toString();
    }
}
