package medicalclinic.database;

import medicalclinic.models.Disease;
import medicalclinic.models.Medication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreatmentDatabase {
    private static TreatmentDatabase instance;
    private HashMap<String, List<Medication>> standardTreatments;
    private HashMap<String, String> treatmentGuidelines;

    private TreatmentDatabase() {
        standardTreatments = new HashMap<>();
        treatmentGuidelines = new HashMap<>();
        initializeTreatments();
    }

    public static synchronized TreatmentDatabase getInstance() {
        if (instance == null) {
            instance = new TreatmentDatabase();
        }
        return instance;
    }

    private void initializeTreatments() {
        // Angine de Poitrine (CAR001)
        List<Medication> angineTreatments = new ArrayList<>();
        angineTreatments.add(new Medication("Nitroglycérine", "0.4mg",
            "Sous la langue en cas de douleur", 30, "Au besoin", true));
        angineTreatments.add(new Medication("Aspirine", "75mg",
            "1 comprimé par jour", 30, "Quotidien", true));
        angineTreatments.add(new Medication("Bêta-bloquant (Métoprolol)", "50mg",
            "1 comprimé matin et soir", 30, "Deux fois par jour", true));
        standardTreatments.put("CAR001", angineTreatments);
        treatmentGuidelines.put("CAR001",
            "Évitez les efforts physiques intenses. Limitez la consommation de caféine. " +
                "Prenez la nitroglycérine dès les premiers signes d'une crise. " +
                "Consultez immédiatement en cas de douleur thoracique prolongée (>15 minutes).");

        // Arythmie Cardiaque (CAR002)
        List<Medication> arrhythmiaTreatments = new ArrayList<>();
        arrhythmiaTreatments.add(new Medication("Amiodarone", "200mg",
            "1 comprimé par jour", 30, "Quotidien", true));
        arrhythmiaTreatments.add(new Medication("Warfarine", "2mg",
            "1 comprimé le soir selon INR", 30, "Quotidien", true));
        standardTreatments.put("CAR002", arrhythmiaTreatments);
        treatmentGuidelines.put("CAR002",
            "Contrôlez régulièrement votre pouls. Limitez la consommation d'alcool et de caféine. " +
                "Si vous prenez des anticoagulants, surveillez tout signe de saignement. " +
                "Consultez immédiatement en cas de palpitations sévères, étourdissements ou évanouissements.");

        // Hypertension (CAR003)
        List<Medication> hypertensionTreatments = new ArrayList<>();
        hypertensionTreatments.add(new Medication("Amlodipine", "5mg",
            "1 comprimé le matin", 30, "Quotidien", true));
        hypertensionTreatments.add(new Medication("Hydrochlorothiazide", "12.5mg",
            "1 comprimé le matin", 30, "Quotidien", true));
        standardTreatments.put("CAR003", hypertensionTreatments);
        treatmentGuidelines.put("CAR003",
            "Réduisez votre consommation de sel. Pratiquez une activité physique régulière modérée. " +
                "Mesurez votre tension artérielle régulièrement. " +
                "Maintenir un poids santé et limiter la consommation d'alcool.");

        // Bronchite (RES001)
        List<Medication> bronchitisTreatments = new ArrayList<>();
        bronchitisTreatments.add(new Medication("Amoxicilline", "500mg",
            "1 comprimé 3 fois par jour", 7, "Trois fois par jour", true));
        bronchitisTreatments.add(new Medication("Sirop pour la toux", "15ml",
            "1 cuillère à soupe 3 fois par jour", 7, "Trois fois par jour", false));
        standardTreatments.put("RES001", bronchitisTreatments);
        treatmentGuidelines.put("RES001",
            "Buvez beaucoup d'eau. Évitez l'exposition à la fumée et aux polluants. " +
                "Utilisez un humidificateur si l'air est sec. " +
                "Terminez tous les antibiotiques prescrits, même si vous vous sentez mieux.");

        // Asthme (RES002)
        List<Medication> asthmaTreatments = new ArrayList<>();
        asthmaTreatments.add(new Medication("Salbutamol", "100μg",
            "2 inhalations en cas de crise", 30, "Au besoin", true));
        asthmaTreatments.add(new Medication("Fluticasone", "250μg",
            "2 inhalations matin et soir", 30, "Deux fois par jour", true));
        standardTreatments.put("RES002", asthmaTreatments);
        treatmentGuidelines.put("RES002",
            "Identifiez et évitez les déclencheurs. Gardez toujours votre inhalateur de secours avec vous. " +
                "Suivez un plan d'action contre l'asthme. " +
                "Consultez immédiatement si vous avez des difficultés respiratoires malgré l'utilisation de votre inhalateur.");

        // Reflux Gastro-œsophagien (DIG001)
        List<Medication> gerdTreatments = new ArrayList<>();
        gerdTreatments.add(new Medication("Oméprazole", "20mg",
            "1 comprimé 30 minutes avant le petit déjeuner", 30, "Quotidien", true));
        gerdTreatments.add(new Medication("Antiacide (Gaviscon)", "10ml",
            "Après les repas et au coucher", 30, "Trois à quatre fois par jour", false));
        standardTreatments.put("DIG001", gerdTreatments);
        treatmentGuidelines.put("DIG001",
            "Évitez de vous allonger dans les 3 heures suivant un repas. " +
                "Évitez les aliments épicés, gras, acides et la caféine. " +
                "Dormez avec la tête surélevée de 15 cm. " +
                "Perdez du poids si nécessaire.");

        // Syndrome du Côlon Irritable (DIG002)
        List<Medication> ibsTreatments = new ArrayList<>();
        ibsTreatments.add(new Medication("Méberérine", "135mg",
            "1 comprimé avant les repas", 30, "Trois fois par jour", false));
        ibsTreatments.add(new Medication("Probiotiques", "5g",
            "1 sachet dans un verre d'eau le matin", 30, "Quotidien", false));
        standardTreatments.put("DIG002", ibsTreatments);
        treatmentGuidelines.put("DIG002",
            "Identifiez et évitez les aliments déclencheurs. " +
                "Mangez régulièrement et en petites quantités. " +
                "Gérez le stress avec des techniques de relaxation. " +
                "Tenez un journal alimentaire pour identifier les aliments problématiques.");

        // Rhume Commun (GEN001)
        List<Medication> coldTreatments = new ArrayList<>();
        coldTreatments.add(new Medication("Paracétamol", "500mg",
            "1-2 comprimés toutes les 6 heures si nécessaire", 5, "Au besoin", false));
        coldTreatments.add(new Medication("Spray nasal salin", "",
            "1-2 pulvérisations dans chaque narine selon besoin", 7, "Plusieurs fois par jour", false));
        standardTreatments.put("GEN001", coldTreatments);
        treatmentGuidelines.put("GEN001",
            "Reposez-vous suffisamment. Buvez beaucoup de liquides. " +
                "Utilisez un humidificateur ou prenez des douches chaudes pour soulager la congestion. " +
                "Lavez-vous les mains fréquemment pour éviter de propager l'infection.");

        // Grippe (GEN002)
        List<Medication> fluTreatments = new ArrayList<>();
        fluTreatments.add(new Medication("Paracétamol", "1000mg",
            "1 comprimé toutes les 6 heures si fièvre ou douleurs", 5, "Au besoin", false));
        fluTreatments.add(new Medication("Oseltamivir", "75mg",
            "1 gélule deux fois par jour", 5, "Deux fois par jour", true));
        standardTreatments.put("GEN002", fluTreatments);
        treatmentGuidelines.put("GEN002",
            "Restez à la maison et reposez-vous. Buvez beaucoup de liquides. " +
                "Évitez tout contact avec des personnes vulnérables. " +
                "Consultez si les symptômes s'aggravent ou persistent plus d'une semaine.");
    }

    public List<Medication> getStandardTreatment(String diseaseId) {
        return standardTreatments.getOrDefault(diseaseId, new ArrayList<>());
    }

    public String getGuidelinesForDisease(String diseaseId) {
        return treatmentGuidelines.getOrDefault(diseaseId, "");
    }

    public List<Medication> selectTreatmentForPatient(Disease disease, int age, int weight) {
        if (disease == null) {
            return new ArrayList<>();
        }

        List<Medication> standardTreatment = getStandardTreatment(disease.getId());
        List<Medication> personalizedTreatment = new ArrayList<>(standardTreatment);

        // Ajuster le traitement en fonction de l'âge et du poids
        for (Medication medication : personalizedTreatment) {
            adjustDosage(medication, age, weight);
        }

        return personalizedTreatment;
    }

    private void adjustDosage(Medication medication, int age, int weight) {
        // Réduction de dosage pour les personnes âgées
        if (age > 65) {
            String currentDosage = medication.getDosage();
            try {
                if (currentDosage.contains("mg")) {
                    String mgPart = currentDosage.substring(0, currentDosage.indexOf("mg")).trim();
                    int dosage = Integer.parseInt(mgPart);
                    int adjustedDosage = (int) (dosage * 0.75); // 75% du dosage standard pour les personnes âgées
                    medication.setDosage(adjustedDosage + "mg");
                }
            } catch (Exception e) {
                // Si le format n'est pas standard, garder le dosage tel quel
            }
        }

        // Ajustement pour les enfants (moins de 12 ans)
        if (age < 12) {
            String currentDosage = medication.getDosage();
            try {
                if (currentDosage.contains("mg")) {
                    String mgPart = currentDosage.substring(0, currentDosage.indexOf("mg")).trim();
                    int dosage = Integer.parseInt(mgPart);
                    // Utilisation de la formule de Young pour l'ajustement pédiatrique
                    double childFactor = age / (age + 12.0);
                    int adjustedDosage = (int) (dosage * childFactor);
                    medication.setDosage(adjustedDosage + "mg");
                }
            } catch (Exception e) {
                // Si le format n'est pas standard, garder le dosage tel quel
            }
        }
    }
}
