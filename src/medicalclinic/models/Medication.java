package medicalclinic.models;

import java.io.Serializable;

public class Medication implements Serializable {
    private String name;
    private String dosage;
    private String instructions;
    private int duration;
    private String frequency;
    private boolean isCritical;

    // Constructeur par défaut
    public Medication() {
        this.duration = 7; // Par défaut, une semaine
        this.isCritical = false;
    }

    // Constructeur avec paramètres basiques
    public Medication(String name, String dosage, String instructions) {
        this();
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Constructeur complet
    public Medication(String name, String dosage, String instructions, int duration,
                      String frequency, boolean isCritical) {
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
        this.duration = duration;
        this.frequency = frequency;
        this.isCritical = isCritical;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }
}
