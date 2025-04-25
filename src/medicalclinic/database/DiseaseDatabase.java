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
        // 1. MALADIES INFECTIEUSES TROPICALES

        // Paludisme (Malaria)
        Disease malaria = new Disease("INF001", "Paludisme (Malaria)",
            "Infection transmise par les moustiques Anophèles", "Maladies infectieuses");
        malaria.addCommonSymptom("fièvre");
        malaria.addCommonSymptom("frissons");
        malaria.addCommonSymptom("sueurs");
        malaria.addCommonSymptom("céphalées");
        malaria.addCommonSymptom("fatigue");
        malaria.addCommonSymptom("nausées");
        malaria.addCommonSymptom("vomissements");
        malaria.addRecommendedTreatment("Artémisinine");
        malaria.addRecommendedTreatment("Chloroquine");
        malaria.addRecommendedTreatment("Méfloquine");
        malaria.addAdditionalInfo("transmission", "Piqûre de moustique anophèle");
        malaria.addAdditionalInfo("prevention", "Moustiquaires imprégnées, répulsifs, prophylaxie médicamenteuse");
        diseases.put(malaria.getId(), malaria);

        // Tuberculose
        Disease tuberculosis = new Disease("INF002", "Tuberculose",
            "Infection bactérienne affectant principalement les poumons", "Maladies infectieuses");
        tuberculosis.addCommonSymptom("toux persistante");
        tuberculosis.addCommonSymptom("crachats sanglants");
        tuberculosis.addCommonSymptom("douleurs thoraciques");
        tuberculosis.addCommonSymptom("fièvre");
        tuberculosis.addCommonSymptom("sueurs nocturnes");
        tuberculosis.addCommonSymptom("fatigue");
        tuberculosis.addCommonSymptom("perte de poids");
        tuberculosis.addRecommendedTreatment("Isoniazide");
        tuberculosis.addRecommendedTreatment("Rifampicine");
        tuberculosis.addRecommendedTreatment("Pyrazinamide");
        tuberculosis.addRecommendedTreatment("Éthambutol");
        tuberculosis.addAdditionalInfo("transmission", "Voie aérienne");
        tuberculosis.addAdditionalInfo("prévention", "Vaccination BCG, dépistage précoce");
        diseases.put(tuberculosis.getId(), tuberculosis);

        // VIH/SIDA
        Disease hiv = new Disease("INF003", "VIH/SIDA",
            "Infection virale affectant le système immunitaire", "Maladies infectieuses");
        hiv.addCommonSymptom("fièvre persistante");
        hiv.addCommonSymptom("fatigue extrême");
        hiv.addCommonSymptom("perte de poids importante");
        hiv.addCommonSymptom("ganglions lymphatiques gonflés");
        hiv.addCommonSymptom("diarrhée chronique");
        hiv.addCommonSymptom("infections opportunistes récurrentes");
        hiv.addRecommendedTreatment("Antirétroviraux (ARV)");
        hiv.addRecommendedTreatment("Traitement préventif des infections opportunistes");
        hiv.addAdditionalInfo("transmission", "Contact sanguin, sexuel, materno-fœtal");
        hiv.addAdditionalInfo("prevention", "Préservatifs, PrEP, dépistage");
        diseases.put(hiv.getId(), hiv);

        // Fièvre typhoïde
        Disease typhoid = new Disease("INF004", "Fièvre typhoïde",
            "Infection bactérienne du tube digestif", "Maladies infectieuses");
        typhoid.addCommonSymptom("fièvre élevée persistante");
        typhoid.addCommonSymptom("céphalées sévères");
        typhoid.addCommonSymptom("douleurs abdominales");
        typhoid.addCommonSymptom("constipation ou diarrhée");
        typhoid.addCommonSymptom("éruption cutanée (taches roses)");
        typhoid.addCommonSymptom("faiblesse générale");
        typhoid.addRecommendedTreatment("Ciprofloxacine");
        typhoid.addRecommendedTreatment("Ceftriaxone");
        typhoid.addRecommendedTreatment("Azithromycine");
        typhoid.addAdditionalInfo("transmission", "Eau ou aliments contaminés");
        typhoid.addAdditionalInfo("prevention", "Hygiène, eau potable, vaccination");
        diseases.put(typhoid.getId(), typhoid);

        // Choléra
        Disease cholera = new Disease("INF005", "Choléra",
            "Infection intestinale aiguë", "Maladies infectieuses");
        cholera.addCommonSymptom("diarrhée aqueuse abondante");
        cholera.addCommonSymptom("vomissements");
        cholera.addCommonSymptom("déshydratation rapide");
        cholera.addCommonSymptom("crampes abdominales");
        cholera.addCommonSymptom("soif intense");
        cholera.addRecommendedTreatment("Réhydratation orale");
        cholera.addRecommendedTreatment("Antibiotiques (doxycycline, azithromycine)");
        cholera.addAdditionalInfo("transmission", "Eau ou aliments contaminés par Vibrio cholerae");
        cholera.addAdditionalInfo("prevention", "Eau potable, assainissement, hygiène, vaccination");
        diseases.put(cholera.getId(), cholera);

        // Méningite
        Disease meningitis = new Disease("INF006", "Méningite",
            "Inflammation des méninges, souvent d'origine infectieuse", "Maladies infectieuses");
        meningitis.addCommonSymptom("fièvre élevée");
        meningitis.addCommonSymptom("céphalées intenses");
        meningitis.addCommonSymptom("raideur de la nuque");
        meningitis.addCommonSymptom("photophobie");
        meningitis.addCommonSymptom("confusion mentale");
        meningitis.addCommonSymptom("vomissements");
        meningitis.addRecommendedTreatment("Ceftriaxone");
        meningitis.addRecommendedTreatment("Pénicilline");
        meningitis.addRecommendedTreatment("Ampicilline");
        meningitis.addAdditionalInfo("transmission", "Voie aérienne, contact direct avec sécrétions");
        meningitis.addAdditionalInfo("prevention", "Vaccination, antibioprophylaxie des contacts");
        diseases.put(meningitis.getId(), meningitis);

        // Fièvre jaune
        Disease yellowFever = new Disease("INF007", "Fièvre jaune",
            "Infection virale aiguë transmise par les moustiques", "Maladies infectieuses");
        yellowFever.addCommonSymptom("fièvre");
        yellowFever.addCommonSymptom("céphalées");
        yellowFever.addCommonSymptom("jaunisse");
        yellowFever.addCommonSymptom("douleurs musculaires");
        yellowFever.addCommonSymptom("nausées");
        yellowFever.addCommonSymptom("vomissements");
        yellowFever.addCommonSymptom("saignements");
        yellowFever.addRecommendedTreatment("Traitement symptomatique");
        yellowFever.addRecommendedTreatment("Réhydratation");
        yellowFever.addAdditionalInfo("transmission", "Piqûre de moustique Aedes");
        yellowFever.addAdditionalInfo("prevention", "Vaccination, protection contre les moustiques");
        diseases.put(yellowFever.getId(), yellowFever);

        // 2. MALADIES PARASITAIRES

        // Schistosomiase
        Disease schistosomiasis = new Disease("PAR001", "Schistosomiase (Bilharziose)",
            "Infection causée par des vers plats parasites", "Maladies parasitaires");
        schistosomiasis.addCommonSymptom("fièvre");
        schistosomiasis.addCommonSymptom("douleurs abdominales");
        schistosomiasis.addCommonSymptom("diarrhée");
        schistosomiasis.addCommonSymptom("sang dans les urines");
        schistosomiasis.addCommonSymptom("démangeaisons cutanées");
        schistosomiasis.addCommonSymptom("hépatomégalie");
        schistosomiasis.addRecommendedTreatment("Praziquantel");
        schistosomiasis.addAdditionalInfo("transmission", "Contact avec eau douce contaminée");
        schistosomiasis.addAdditionalInfo("prevention", "Éviter les baignades en eau douce contaminée");
        diseases.put(schistosomiasis.getId(), schistosomiasis);

        // Onchocercose
        Disease onchocerciasis = new Disease("PAR002", "Onchocercose (Cécité des rivières)",
            "Infection causée par un ver parasite transmis par les mouches noires", "Maladies parasitaires");
        onchocerciasis.addCommonSymptom("démangeaisons intenses");
        onchocerciasis.addCommonSymptom("éruptions cutanées");
        onchocerciasis.addCommonSymptom("nodules sous-cutanés");
        onchocerciasis.addCommonSymptom("troubles visuels");
        onchocerciasis.addRecommendedTreatment("Ivermectine");
        onchocerciasis.addAdditionalInfo("transmission", "Piqûre de simulies (mouches noires)");
        onchocerciasis.addAdditionalInfo("prevention", "Traitement de masse, lutte contre les vecteurs");
        diseases.put(onchocerciasis.getId(), onchocerciasis);

        // Filariose lymphatique
        Disease lymphaticFilariasis = new Disease("PAR003", "Filariose lymphatique",
            "Infection parasitaire affectant le système lymphatique", "Maladies parasitaires");
        lymphaticFilariasis.addCommonSymptom("lymphœdème");
        lymphaticFilariasis.addCommonSymptom("éléphantiasis");
        lymphaticFilariasis.addCommonSymptom("fièvre récurrente");
        lymphaticFilariasis.addCommonSymptom("douleurs ganglionnaires");
        lymphaticFilariasis.addRecommendedTreatment("Diéthylcarbamazine");
        lymphaticFilariasis.addRecommendedTreatment("Albendazole");
        lymphaticFilariasis.addRecommendedTreatment("Ivermectine");
        lymphaticFilariasis.addAdditionalInfo("transmission", "Piqûre de moustiques");
        lymphaticFilariasis.addAdditionalInfo("prevention", "Protection contre les moustiques, traitement de masse");
        diseases.put(lymphaticFilariasis.getId(), lymphaticFilariasis);

        // 3. MALADIES NON TRANSMISSIBLES

        // Hypertension artérielle
        Disease hypertension = new Disease("NCD001", "Hypertension artérielle",
            "Pression artérielle élevée, maladie chronique", "Maladies cardiovasculaires");
        hypertension.addCommonSymptom("céphalées");
        hypertension.addCommonSymptom("vertiges");
        hypertension.addCommonSymptom("acouphènes");
        hypertension.addCommonSymptom("troubles visuels");
        hypertension.addCommonSymptom("fatigue");
        hypertension.addRecommendedTreatment("Inhibiteurs de l'enzyme de conversion");
        hypertension.addRecommendedTreatment("Antagonistes des récepteurs de l'angiotensine II");
        hypertension.addRecommendedTreatment("Diurétiques");
        hypertension.addRecommendedTreatment("Bêta-bloquants");
        hypertension.addRecommendedTreatment("Inhibiteurs calciques");
        hypertension.addAdditionalInfo("facteurs de risque", "Âge, antécédents familiaux, surpoids, sédentarité, consommation excessive de sel");
        hypertension.addAdditionalInfo("prevention", "Alimentation équilibrée, activité physique, limitation du sel");
        diseases.put(hypertension.getId(), hypertension);

        // Diabète
        Disease diabetes = new Disease("NCD002", "Diabète",
            "Maladie métabolique caractérisée par une hyperglycémie chronique", "Maladies métaboliques");
        diabetes.addCommonSymptom("polyurie");
        diabetes.addCommonSymptom("polydipsie");
        diabetes.addCommonSymptom("polyphagie");
        diabetes.addCommonSymptom("perte de poids inexpliquée");
        diabetes.addCommonSymptom("fatigue");
        diabetes.addCommonSymptom("vision floue");
        diabetes.addCommonSymptom("cicatrisation lente");
        diabetes.addRecommendedTreatment("Metformine");
        diabetes.addRecommendedTreatment("Insuline");
        diabetes.addRecommendedTreatment("Sulfamides hypoglycémiants");
        diabetes.addRecommendedTreatment("Inhibiteurs de la DPP-4");
        diabetes.addAdditionalInfo("facteurs de risque", "Antécédents familiaux, surpoids, sédentarité, alimentation déséquilibrée");
        diabetes.addAdditionalInfo("prevention", "Alimentation équilibrée, activité physique régulière, contrôle du poids");
        diseases.put(diabetes.getId(), diabetes);

        // Drépanocytose
        Disease sickleCell = new Disease("NCD003", "Drépanocytose",
            "Maladie génétique affectant l'hémoglobine", "Maladies hématologiques");
        sickleCell.addCommonSymptom("crises douloureuses");
        sickleCell.addCommonSymptom("anémie");
        sickleCell.addCommonSymptom("jaunisse");
        sickleCell.addCommonSymptom("infections récurrentes");
        sickleCell.addCommonSymptom("fatigue");
        sickleCell.addCommonSymptom("retard de croissance");
        sickleCell.addRecommendedTreatment("Hydroxyurée");
        sickleCell.addRecommendedTreatment("Analgésiques");
        sickleCell.addRecommendedTreatment("Antibiotiques préventifs");
        sickleCell.addRecommendedTreatment("Supplémentation en acide folique");
        sickleCell.addAdditionalInfo("facteurs de risque", "Facteurs génétiques, deux parents porteurs du trait drépanocytaire");
        sickleCell.addAdditionalInfo("prevention", "Conseil génétique, dépistage néonatal");
        diseases.put(sickleCell.getId(), sickleCell);

        // Asthme
        Disease asthma = new Disease("RES001", "Asthme",
            "Maladie inflammatoire chronique des voies respiratoires", "Maladies respiratoires");
        asthma.addCommonSymptom("difficultés respiratoires");
        asthma.addCommonSymptom("respiration sifflante");
        asthma.addCommonSymptom("sensation d'oppression thoracique");
        asthma.addCommonSymptom("toux sèche");
        asthma.addCommonSymptom("réveils nocturnes");
        asthma.addRecommendedTreatment("Bronchodilatateurs à action rapide");
        asthma.addRecommendedTreatment("Corticostéroïdes inhalés");
        asthma.addRecommendedTreatment("Antagonistes des récepteurs des leucotriènes");
        asthma.addAdditionalInfo("facteurs déclenchants", "Allergènes, infections respiratoires, exercice, stress, pollution");
        asthma.addAdditionalInfo("prevention", "Éviction des allergènes, traitement de fond, éducation thérapeutique");
        diseases.put(asthma.getId(), asthma);

        // BPCO
        Disease copd = new Disease("RES002", "Bronchopneumopathie chronique obstructive (BPCO)",
            "Maladie respiratoire chronique caractérisée par un rétrécissement des voies aériennes", "Maladies respiratoires");
        copd.addCommonSymptom("dyspnée progressive");
        copd.addCommonSymptom("toux chronique");
        copd.addCommonSymptom("expectorations");
        copd.addCommonSymptom("fatigue");
        copd.addCommonSymptom("perte de poids");
        copd.addRecommendedTreatment("Bronchodilatateurs");
        copd.addRecommendedTreatment("Corticostéroïdes inhalés");
        copd.addRecommendedTreatment("Oxygénothérapie");
        copd.addAdditionalInfo("facteurs de risque", "Tabagisme, pollution atmosphérique, expositions professionnelles");
        copd.addAdditionalInfo("prevention", "Arrêt du tabac, éviction des polluants");
        diseases.put(copd.getId(), copd);

        // 4. MALADIES DIGESTIVES

        // Gastrite
        Disease gastritis = new Disease("DIG001", "Gastrite",
            "Inflammation de la muqueuse gastrique", "Maladies digestives");
        gastritis.addCommonSymptom("douleurs épigastriques");
        gastritis.addCommonSymptom("nausées");
        gastritis.addCommonSymptom("vomissements");
        gastritis.addCommonSymptom("ballonnements");
        gastritis.addCommonSymptom("perte d'appétit");
        gastritis.addRecommendedTreatment("Inhibiteurs de la pompe à protons");
        gastritis.addRecommendedTreatment("Antiacides");
        gastritis.addRecommendedTreatment("Antibiotiques (si H. pylori)");
        gastritis.addAdditionalInfo("facteurs de risque", "Infection à H. pylori, AINS, alcool, stress");
        gastritis.addAdditionalInfo("prevention", "Éviter l'alcool, le tabac, les AINS, gérer le stress");
        diseases.put(gastritis.getId(), gastritis);

        // Hépatite virale
        Disease hepatitis = new Disease("DIG002", "Hépatite virale",
            "Inflammation du foie d'origine virale", "Maladies digestives");
        hepatitis.addCommonSymptom("fatigue");
        hepatitis.addCommonSymptom("jaunisse");
        hepatitis.addCommonSymptom("urines foncées");
        hepatitis.addCommonSymptom("selles décolorées");
        hepatitis.addCommonSymptom("douleurs abdominales");
        hepatitis.addCommonSymptom("nausées");
        hepatitis.addCommonSymptom("perte d'appétit");
        hepatitis.addRecommendedTreatment("Antiviraux (selon le type)");
        hepatitis.addRecommendedTreatment("Traitement symptomatique");
        hepatitis.addAdditionalInfo("transmission", "Voie orale-fécale (A, E), sang (B, C, D), sexuelle (B)");
        hepatitis.addAdditionalInfo("prevention", "Vaccination (A, B), hygiène, précautions standards");
        diseases.put(hepatitis.getId(), hepatitis);

        // 5. MALADIES NEUROLOGIQUES

        // Épilepsie
        Disease epilepsy = new Disease("NEU001", "Épilepsie",
            "Affection neurologique chronique caractérisée par des crises récurrentes", "Maladies neurologiques");
        epilepsy.addCommonSymptom("crises convulsives");
        epilepsy.addCommonSymptom("perte de conscience");
        epilepsy.addCommonSymptom("absences");
        epilepsy.addCommonSymptom("mouvements anormaux");
        epilepsy.addCommonSymptom("confusion post-critique");
        epilepsy.addRecommendedTreatment("Carbamazépine");
        epilepsy.addRecommendedTreatment("Valproate de sodium");
        epilepsy.addRecommendedTreatment("Lamotrigine");
        epilepsy.addRecommendedTreatment("Lévétiracétam");
        epilepsy.addAdditionalInfo("facteurs déclenchants", "Fièvre, manque de sommeil, stress, lumières clignotantes");
        epilepsy.addAdditionalInfo("prevention", "Traitement antiépileptique, éviction des facteurs déclenchants");
        diseases.put(epilepsy.getId(), epilepsy);

        // 6. MALADIES DERMATOLOGIQUES

        // Gale
        Disease scabies = new Disease("DER001", "Gale",
            "Infestation cutanée par un acarien", "Maladies dermatologiques");
        scabies.addCommonSymptom("prurit intense");
        scabies.addCommonSymptom("éruption cutanée");
        scabies.addCommonSymptom("démangeaisons nocturnes");
        scabies.addCommonSymptom("lésions linéaires");
        scabies.addCommonSymptom("vésicules");
        scabies.addRecommendedTreatment("Ivermectine");
        scabies.addRecommendedTreatment("Benzoate de benzyle");
        scabies.addRecommendedTreatment("Perméthrine");
        scabies.addAdditionalInfo("transmission", "Contact cutané prolongé, linge contaminé");
        scabies.addAdditionalInfo("prevention", "Hygiène, traitement de l'entourage, désinfection du linge");
        diseases.put(scabies.getId(), scabies);

        // 7. MALADIES COURANTES

        // Grippe
        Disease influenza = new Disease("GEN001", "Grippe",
            "Infection virale respiratoire aiguë", "Maladies courantes");
        influenza.addCommonSymptom("fièvre élevée");
        influenza.addCommonSymptom("courbatures");
        influenza.addCommonSymptom("fatigue intense");
        influenza.addCommonSymptom("céphalées");
        influenza.addCommonSymptom("toux sèche");
        influenza.addCommonSymptom("congestion nasale");
        influenza.addCommonSymptom("maux de gorge");
        influenza.addRecommendedTreatment("Paracétamol");
        influenza.addRecommendedTreatment("Antiviraux (oseltamivir si indiqué)");
        influenza.addRecommendedTreatment("Repos");
        influenza.addRecommendedTreatment("Hydratation");
        influenza.addAdditionalInfo("transmission", "Voie aérienne, contact direct");
        influenza.addAdditionalInfo("prevention", "Vaccination annuelle, hygiène des mains, éviter les contacts rapprochés");
        diseases.put(influenza.getId(), influenza);

        // Rhume commun
        Disease commonCold = new Disease("GEN002", "Rhume commun",
            "Infection virale des voies respiratoires supérieures", "Maladies courantes");
        commonCold.addCommonSymptom("congestion nasale");
        commonCold.addCommonSymptom("rhinorrhée");
        commonCold.addCommonSymptom("éternuements");
        commonCold.addCommonSymptom("maux de gorge");
        commonCold.addCommonSymptom("toux légère");
        commonCold.addCommonSymptom("fatigue");
        commonCold.addRecommendedTreatment("Repos");
        commonCold.addRecommendedTreatment("Hydratation");
        commonCold.addRecommendedTreatment("Paracétamol");
        commonCold.addRecommendedTreatment("Décongestionnants");
        commonCold.addAdditionalInfo("transmission", "Voie aérienne, contact direct");
        commonCold.addAdditionalInfo("prevention", "Hygiène des mains, éviter les contacts rapprochés");
        diseases.put(commonCold.getId(), commonCold);

        // Infection urinaire
        Disease uti = new Disease("GEN003", "Infection urinaire",
            "Infection bactérienne des voies urinaires", "Maladies courantes");
        uti.addCommonSymptom("brûlures mictionnelles");
        uti.addCommonSymptom("pollakiurie");
        uti.addCommonSymptom("mictions impérieuses");
        uti.addCommonSymptom("douleurs sus-pubiennes");
        uti.addCommonSymptom("urines troubles ou malodorantes");
        uti.addCommonSymptom("hématurie");
        uti.addRecommendedTreatment("Antibiotiques (quinolones, bêta-lactamines)");
        uti.addRecommendedTreatment("Hydratation");
        uti.addAdditionalInfo("facteurs de risque", "Sexe féminin, activité sexuelle, anomalies des voies urinaires");
        uti.addAdditionalInfo("prevention", "Hydratation, hygiène, mictions après rapports sexuels");
        diseases.put(uti.getId(), uti);
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

    /**
     * Trouve les maladies qui correspondent aux symptômes donnés.
     * Retourne une liste triée par ordre décroissant de pourcentage de correspondance.
     */
    public List<Disease> findDiseasesBySymptoms(HashMap<String, String> symptoms) {
        List<Disease> potentialDiseases = new ArrayList<>();

        // Pour chaque maladie dans la base de données
        for (Disease disease : diseases.values()) {
            int matchPercentage = disease.matchSymptoms(symptoms);
            if (matchPercentage > 30) { // Seuil de correspondance abaissé à 30% pour plus de sensibilité
                disease.addAdditionalInfo("matchScore", String.valueOf(matchPercentage));
                potentialDiseases.add(disease);
            }
        }

        // Trier les maladies par pourcentage de correspondance décroissant
        potentialDiseases.sort((d1, d2) -> {
            int match1 = Integer.parseInt(d1.getAdditionalInfo().getOrDefault("matchScore", "0"));
            int match2 = Integer.parseInt(d2.getAdditionalInfo().getOrDefault("matchScore", "0"));
            return Integer.compare(match2, match1);
        });

        return potentialDiseases;
    }

    /**
     * Génère des questions spécifiques à poser pour une maladie donnée.
     * Ces questions aideront à confirmer ou infirmer le diagnostic.
     */
    public List<String> generateDiagnosticQuestionsForDisease(Disease disease) {
        List<String> questions = new ArrayList<>();

        if (disease == null) {
            return questions;
        }

        // Questions générales sur les symptômes courants de la maladie
        for (String symptom : disease.getCommonSymptoms()) {
            questions.add("Présentez-vous " + symptom + " ? Si oui, depuis quand et avec quelle intensité ?");
        }

        // Questions spécifiques par catégorie de maladie
        String category = disease.getCategory();

        if ("Maladies infectieuses".equals(category)) {
            questions.add("Avez-vous été en contact avec des personnes présentant des symptômes similaires ?");
            questions.add("Avez-vous voyagé récemment dans des zones où cette maladie est endémique ?");
            questions.add("Avez-vous été piqué par des insectes (moustiques, mouches) récemment ?");
        }
        else if ("Maladies respiratoires".equals(category)) {
            questions.add("Ressentez-vous une gêne respiratoire en montant les escaliers ou en marchant rapidement ?");
            questions.add("Votre toux s'aggrave-t-elle la nuit ou tôt le matin ?");
            questions.add("Avez-vous déjà eu des crises d'asthme ou des allergies respiratoires ?");
        }
        else if ("Maladies cardiovasculaires".equals(category)) {
            questions.add("Ressentez-vous des douleurs thoraciques à l'effort ou au repos ?");
            questions.add("Avez-vous des antécédents familiaux d'hypertension ou de maladies cardiaques ?");
            questions.add("Avez-vous remarqué des œdèmes (gonflements) au niveau des chevilles ?");
        }
        else if ("Maladies digestives".equals(category)) {
            questions.add("Avez-vous observé des changements dans vos selles (couleur, consistance) ?");
            questions.add("Ressentez-vous des douleurs abdominales après les repas ?");
            questions.add("Avez-vous perdu l'appétit ou perdu du poids récemment ?");
        }
        else if ("Maladies neurologiques".equals(category)) {
            questions.add("Avez-vous des troubles de l'équilibre ou des vertiges ?");
            questions.add("Avez-vous des tremblements ou des mouvements involontaires ?");
            questions.add("Avez-vous des troubles de la mémoire ou des difficultés de concentration ?");
        }
        else if ("Maladies métaboliques".equals(category)) {
            questions.add("Avez-vous une sensation de soif intense et fréquente ?");
            questions.add("Urinez-vous plus fréquemment qu'avant, surtout la nuit ?");
            questions.add("Y a-t-il des cas de diabète dans votre famille ?");
        }

        return questions;
    }
}
