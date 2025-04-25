package medicalclinic.database;

import medicalclinic.models.Disease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiseaseDatabase {
    private static DiseaseDatabase instance;
    private HashMap<String, Disease> diseases;

    private DiseaseDatabase() {
        diseases = new HashMap<>();
        initializeDiseases();
    }

    public static synchronized DiseaseDatabase getInstance() {
        if (instance == null) {
            instance = new DiseaseDatabase();
        }
        return instance;
    }

    private void initializeDiseases() {
        // Initialiser les maladies cardiaques
        Disease angina = new Disease("CAR001", "Angine de Poitrine",
            "Douleur thoracique due à une insuffisance d'apport sanguin au cœur", "Cardiologie");
        angina.addCommonSymptom("douleur thoracique");
        angina.addCommonSymptom("oppression");
        angina.addCommonSymptom("essoufflement");
        angina.addRecommendedTreatment("Nitroglycérine");
        angina.addRecommendedTreatment("Bêta-bloquants");
        diseases.put(angina.getId(), angina);

        Disease arrhythmia = new Disease("CAR002", "Arythmie Cardiaque",
            "Trouble du rythme cardiaque", "Cardiologie");
        arrhythmia.addCommonSymptom("palpitation");
        arrhythmia.addCommonSymptom("irrégularité");
        arrhythmia.addCommonSymptom("étourdissement");
        arrhythmia.addRecommendedTreatment("Antiarythmiques");
        arrhythmia.addRecommendedTreatment("Anticoagulants");
        diseases.put(arrhythmia.getId(), arrhythmia);

        Disease hypertension = new Disease("CAR003", "Hypertension Artérielle",
            "Pression artérielle élevée", "Cardiologie");
        hypertension.addCommonSymptom("pression");
        hypertension.addCommonSymptom("mal de tête");
        hypertension.addCommonSymptom("vertige");
        hypertension.addRecommendedTreatment("Inhibiteurs de l'ECA");
        hypertension.addRecommendedTreatment("Diurétiques");
        diseases.put(hypertension.getId(), hypertension);

        // Initialiser les maladies respiratoires
        Disease bronchitis = new Disease("RES001", "Bronchite",
            "Inflammation des bronches", "Pneumologie");
        bronchitis.addCommonSymptom("toux productive");
        bronchitis.addCommonSymptom("mucus");
        bronchitis.addCommonSymptom("fièvre");
        bronchitis.addRecommendedTreatment("Antibiotiques");
        bronchitis.addRecommendedTreatment("Expectorants");
        diseases.put(bronchitis.getId(), bronchitis);

        Disease asthma = new Disease("RES002", "Asthme",
            "Maladie inflammatoire chronique des voies respiratoires", "Pneumologie");
        asthma.addCommonSymptom("respiration sifflante");
        asthma.addCommonSymptom("essoufflement");
        asthma.addCommonSymptom("toux");
        asthma.addRecommendedTreatment("Bronchodilatateurs");
        asthma.addRecommendedTreatment("Corticostéroïdes inhalés");
        diseases.put(asthma.getId(), asthma);

        // Initialiser les maladies digestives
        Disease gerd = new Disease("DIG001", "Reflux Gastro-œsophagien",
            "Remontée du contenu de l'estomac dans l'œsophage", "Gastroentérologie");
        gerd.addCommonSymptom("brûlure");
        gerd.addCommonSymptom("acide");
        gerd.addCommonSymptom("régurgitation");
        gerd.addRecommendedTreatment("Inhibiteurs de la pompe à protons");
        gerd.addRecommendedTreatment("Antiacides");
        diseases.put(gerd.getId(), gerd);

        Disease ibs = new Disease("DIG002", "Syndrome du Côlon Irritable",
            "Trouble fonctionnel intestinal", "Gastroentérologie");
        ibs.addCommonSymptom("douleur abdominale");
        ibs.addCommonSymptom("ballonnement");
        ibs.addCommonSymptom("diarrhée");
        ibs.addCommonSymptom("constipation");
        ibs.addRecommendedTreatment("Antispasmodiques");
        ibs.addRecommendedTreatment("Probiotiques");
        diseases.put(ibs.getId(), ibs);

        // Initialiser d'autres maladies (maladies générales)
        Disease commonCold = new Disease("GEN001", "Rhume Commun",
            "Infection virale des voies respiratoires supérieures", "Médecine Générale");
        commonCold.addCommonSymptom("nez qui coule");
        commonCold.addCommonSymptom("congestion nasale");
        commonCold.addCommonSymptom("mal de gorge");
        commonCold.addCommonSymptom("toux");
        commonCold.addRecommendedTreatment("Décongestionnants");
        commonCold.addRecommendedTreatment("Analgésiques");
        diseases.put(commonCold.getId(), commonCold);

        Disease influenza = new Disease("GEN002", "Grippe",
            "Infection virale affectant principalement les voies respiratoires", "Médecine Générale");
        influenza.addCommonSymptom("fièvre élevée");
        influenza.addCommonSymptom("courbatures");
        influenza.addCommonSymptom("fatigue");
        influenza.addCommonSymptom("mal de tête");
        influenza.addRecommendedTreatment("Antiviraux");
        influenza.addRecommendedTreatment("Antipyrétiques");
        diseases.put(influenza.getId(), influenza);
    }

    public Disease getDisease(String diseaseId) {
        return diseases.get(diseaseId);
    }

    public List<Disease> getAllDiseases() {
        return new ArrayList<>(diseases.values());
    }

    public List<Disease> getDiseasesByCategory(String category) {
        List<Disease> result = new ArrayList<>();
        for (Disease disease : diseases.values()) {
            if (disease.getCategory().equals(category)) {
                result.add(disease);
            }
        }
        return result;
    }

    public List<Disease> findDiseasesBySymptoms(HashMap<String, String> symptoms) {
        List<Disease> potentialDiseases = new ArrayList<>();
        for (Disease disease : diseases.values()) {
            int matchPercentage = disease.matchSymptoms(symptoms);
            if (matchPercentage > 50) { // Seuil de correspondance de 50%
                potentialDiseases.add(disease);
            }
        }
        // Trier les maladies par pourcentage de correspondance décroissant
        potentialDiseases.sort((d1, d2) -> {
            int match1 = d1.matchSymptoms(symptoms);
            int match2 = d2.matchSymptoms(symptoms);
            return Integer.compare(match2, match1);
        });
        return potentialDiseases;
    }
}
