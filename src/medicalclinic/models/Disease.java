package medicalclinic.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Disease implements Serializable {
    private String id;
    private String name;
    private String description;
    private String category;
    private List<String> commonSymptoms;
    private List<String> recommendedTreatments;
    private HashMap<String, String> additionalInfo;

    // Constructeur par défaut
    public Disease() {
        this.commonSymptoms = new ArrayList<>();
        this.recommendedTreatments = new ArrayList<>();
        this.additionalInfo = new HashMap<>();
    }

    // Constructeur avec identifiant et nom
    public Disease(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    // Constructeur complet
    public Disease(String id, String name, String description, String category) {
        this(id, name);
        this.description = description;
        this.category = category;
    }

    // Méthodes pour ajouter des symptômes et traitements
    public void addCommonSymptom(String symptom) {
        commonSymptoms.add(symptom);
    }

    public void addRecommendedTreatment(String treatment) {
        recommendedTreatments.add(treatment);
    }

    public void addAdditionalInfo(String key, String value) {
        additionalInfo.put(key, value);
    }

    // Méthode pour vérifier si la maladie correspond à un ensemble de symptômes
    public int matchSymptoms(HashMap<String, String> patientSymptoms) {
        if (commonSymptoms.isEmpty() || patientSymptoms.isEmpty()) {
            return 0;
        }

        int matches = 0;
        int total = commonSymptoms.size();

        for (String symptom : commonSymptoms) {
            for (String value : patientSymptoms.values()) {
                if (value.toLowerCase().contains(symptom.toLowerCase())) {
                    matches++;
                    break;
                }
            }
        }

        return (matches * 100) / total;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getCommonSymptoms() {
        return commonSymptoms;
    }

    public List<String> getRecommendedTreatments() {
        return recommendedTreatments;
    }

    public HashMap<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
