package medicalclinic.database;

import medicalclinic.models.Disease;
import medicalclinic.models.Medication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base de données des traitements médicaux adaptée au contexte africain.
 * Contient les médicaments et prescriptions pour les maladies courantes.
 */
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

    /**
     * Initialise tous les traitements pour les maladies de la base de données
     */
    private void initializeTreatments() {
        // 1. TRAITEMENTS POUR MALADIES INFECTIEUSES TROPICALES

        // Paludisme (Malaria) - INF001
        initializeMalariaTreatment();

        // Tuberculose - INF002
        initializeTuberculosisTreatment();

        // VIH/SIDA - INF003
        initializeHIVTreatment();

        // Fièvre typhoïde - INF004
        initializeTyphoidTreatment();

        // Choléra - INF005
        initializeCholeraTreatment();

        // Méningite - INF006
        initializeMeningitisTreatment();

        // Fièvre jaune - INF007
        initializeYellowFeverTreatment();

        // 2. TRAITEMENTS POUR MALADIES PARASITAIRES

        // Schistosomiase (Bilharziose) - PAR001
        initializeSchistosomiasisTreatment();

        // Onchocercose - PAR002
        initializeOnchocerciasisTreatment();

        // Filariose lymphatique - PAR003
        initializeLymphaticFilariasisTreatment();

        // 3. TRAITEMENTS POUR MALADIES NON TRANSMISSIBLES

        // Hypertension artérielle - NCD001
        initializeHypertensionTreatment();

        // Diabète - NCD002
        initializeDiabetesTreatment();

        // Drépanocytose - NCD003
        initializeSickleCellTreatment();

        // 4. TRAITEMENTS POUR MALADIES RESPIRATOIRES

        // Asthme - RES001
        initializeAsthmaTreatment();

        // BPCO - RES002
        initializeCOPDTreatment();

        // 5. TRAITEMENTS POUR MALADIES DIGESTIVES

        // Gastrite - DIG001
        initializeGastritisTreatment();

        // Hépatite virale - DIG002
        initializeHepatitisVTreatment();

        // 6. TRAITEMENTS POUR MALADIES NEUROLOGIQUES

        // Épilepsie - NEU001
        initializeEpilepsyTreatment();

        // 7. TRAITEMENTS POUR MALADIES DERMATOLOGIQUES

        // Gale - DER001
        initializeScabiesTreatment();

        // 8. TRAITEMENTS POUR MALADIES COURANTES

        // Grippe - GEN001
        initializeInfluenzaTreatment();

        // Rhume commun - GEN002
        initializeCommonColdTreatment();

        // Infection urinaire - GEN003
        initializeUTITreatment();
    }

    /**
     * Paludisme (Malaria) - INF001
     */
    private void initializeMalariaTreatment() {
        List<Medication> malariaTreatments = new ArrayList<>();

        // Combinaison thérapeutique à base d'artémisinine (CTA)
        malariaTreatments.add(new Medication(
            "Artéméther-Luméfantrine", "20mg/120mg",
            "4 comprimés deux fois par jour pendant 3 jours",
            3, "Deux fois par jour", true));

        // Alternative pour les cas sévères
        malariaTreatments.add(new Medication(
            "Artésunate", "60mg",
            "Injection intraveineuse ou intramusculaire selon le poids",
            3, "Toutes les 12 heures le premier jour, puis une fois par jour", true));

        // Médicament de soutien pour gérer les symptômes
        malariaTreatments.add(new Medication(
            "Paracétamol", "500mg",
            "1-2 comprimés toutes les 6 heures en cas de fièvre",
            7, "Au besoin", false));

        standardTreatments.put("INF001", malariaTreatments);

        treatmentGuidelines.put("INF001",
            "1. Boire beaucoup d'eau pour rester hydraté\n" +
                "2. Se reposer suffisamment\n" +
                "3. Utiliser une moustiquaire imprégnée d'insecticide pour éviter une réinfection\n" +
                "4. Compléter le traitement entier même si les symptômes s'améliorent\n" +
                "5. Revenir immédiatement en consultation si la fièvre persiste après 48 heures de traitement");
    }

    /**
     * Tuberculose - INF002
     */
    private void initializeTuberculosisTreatment() {
        List<Medication> tbTreatments = new ArrayList<>();

        // Phase intensive (2 mois)
        tbTreatments.add(new Medication(
            "Rifampicine", "150-600mg selon le poids",
            "Prise quotidienne à jeun",
            60, "Une fois par jour", true));

        tbTreatments.add(new Medication(
            "Isoniazide", "75-300mg selon le poids",
            "Prise quotidienne à jeun",
            60, "Une fois par jour", true));

        tbTreatments.add(new Medication(
            "Pyrazinamide", "400-2000mg selon le poids",
            "Prise quotidienne",
            60, "Une fois par jour", true));

        tbTreatments.add(new Medication(
            "Éthambutol", "275-1200mg selon le poids",
            "Prise quotidienne",
            60, "Une fois par jour", true));

        // Prévention des effets secondaires
        tbTreatments.add(new Medication(
            "Pyridoxine (Vitamine B6)", "25mg",
            "Pour prévenir les neuropathies périphériques dues à l'isoniazide",
            180, "Une fois par jour", false));

        standardTreatments.put("INF002", tbTreatments);

        treatmentGuidelines.put("INF002",
            "1. Ce traitement doit être suivi pendant au moins 6 mois (2 mois de phase intensive avec 4 médicaments, puis 4 mois de phase de continuation avec Rifampicine et Isoniazide)\n" +
                "2. Il est crucial de prendre tous les médicaments régulièrement sans interruption\n" +
                "3. Les contrôles médicaux mensuels sont nécessaires pour surveiller l'évolution\n" +
                "4. Éviter l'alcool pendant le traitement\n" +
                "5. Signaler immédiatement tout effet secondaire (jaunisse, troubles visuels, éruptions cutanées)\n" +
                "6. Aérer les pièces de vie et porter un masque les premières semaines du traitement pour éviter la contamination");
    }

    /**
     * VIH/SIDA - INF003
     */
    private void initializeHIVTreatment() {
        List<Medication> hivTreatments = new ArrayList<>();

        // Traitement antirétroviral de première ligne (selon recommandations OMS)
        hivTreatments.add(new Medication(
            "TDF/3TC/DTG (Ténofovir/Lamivudine/Dolutégravir)", "300mg/300mg/50mg",
            "Un comprimé par jour",
            30, "Une fois par jour", true));

        // Prophylaxie des infections opportunistes
        hivTreatments.add(new Medication(
            "Cotrimoxazole", "800mg/160mg",
            "Un comprimé par jour pour prévenir les infections opportunistes",
            30, "Une fois par jour", false));

        standardTreatments.put("INF003", hivTreatments);

        treatmentGuidelines.put("INF003",
            "1. Le traitement antirétroviral (TARV) est à prendre à vie\n" +
                "2. L'adhérence stricte au traitement est essentielle pour éviter les résistances\n" +
                "3. Des contrôles biologiques réguliers (CD4, charge virale) sont nécessaires\n" +
                "4. Maintenir une bonne hygiène de vie (alimentation équilibrée, activité physique adaptée)\n" +
                "5. Protéger vos partenaires en utilisant systématiquement un préservatif\n" +
                "6. Éviter l'automédication qui pourrait interagir avec votre traitement\n" +
                "7. Rejoindre un groupe de soutien peut être bénéfique");
    }

    /**
     * Fièvre typhoïde - INF004
     */
    private void initializeTyphoidTreatment() {
        List<Medication> typhoidTreatments = new ArrayList<>();

        // Antibiotiques de première ligne
        typhoidTreatments.add(new Medication(
            "Ciprofloxacine", "500mg",
            "Un comprimé deux fois par jour",
            7, "Deux fois par jour", true));

        // Alternative (résistance ou contre-indication)
        typhoidTreatments.add(new Medication(
            "Ceftriaxone", "1-2g",
            "Injection intramusculaire ou intraveineuse",
            5, "Une fois par jour", true));

        // Traitement symptomatique
        typhoidTreatments.add(new Medication(
            "Paracétamol", "1000mg",
            "En cas de fièvre élevée",
            7, "Toutes les 6 heures si nécessaire", false));

        standardTreatments.put("INF004", typhoidTreatments);

        treatmentGuidelines.put("INF004",
            "1. Hydratation abondante (au moins 2-3 litres d'eau par jour)\n" +
                "2. Repos strict pendant la phase aiguë\n" +
                "3. Alimentation progressive, légère et équilibrée\n" +
                "4. Éviter l'automédication, surtout les anti-diarrhéiques\n" +
                "5. Mesures d'hygiène strictes pour éviter la contamination des proches\n" +
                "6. Lavage rigoureux des mains après chaque passage aux toilettes\n" +
                "7. Consulter immédiatement en cas de douleurs abdominales importantes ou de diarrhée sanguinolente");
    }

    /**
     * Choléra - INF005
     */
    private void initializeCholeraTreatment() {
        List<Medication> choleraTreatments = new ArrayList<>();

        // Réhydratation (traitement principal)
        choleraTreatments.add(new Medication(
            "Solution de Réhydratation Orale (SRO)", "1 sachet dans 1L d'eau",
            "200-400ml après chaque selle liquide",
            3, "Après chaque selle liquide", true));

        // Antibiotique pour réduire la durée et la sévérité
        choleraTreatments.add(new Medication(
            "Doxycycline", "300mg",
            "Dose unique (adultes seulement)",
            1, "Dose unique", false));

        // Alternative pour enfants et femmes enceintes
        choleraTreatments.add(new Medication(
            "Azithromycine", "20mg/kg",
            "Dose unique pour enfants et femmes enceintes",
            1, "Dose unique", false));

        // Supplémentation en zinc pour les enfants
        choleraTreatments.add(new Medication(
            "Zinc", "20mg",
            "Pour les enfants de moins de 5 ans",
            10, "Une fois par jour", false));

        standardTreatments.put("INF005", choleraTreatments);

        treatmentGuidelines.put("INF005",
            "1. La réhydratation est VITALE et constitue le traitement principal\n" +
                "2. Si vomissements, donner de petites quantités de SRO fréquemment (toutes les 5-10 minutes)\n" +
                "3. Continuer à s'alimenter dès que possible\n" +
                "4. Mesures d'hygiène strictes: lavage des mains au savon, eau potable, désinfection\n" +
                "5. Isoler le patient et désinfecter ses vêtements et literie\n" +
                "6. Consulter immédiatement en cas de signes de déshydratation sévère\n" +
                "7. Informer les autorités sanitaires (maladie à déclaration obligatoire)");
    }

    /**
     * Méningite - INF006
     */
    private void initializeMeningitisTreatment() {
        List<Medication> meningitisTreatments = new ArrayList<>();

        // Antibiotique de première intention
        meningitisTreatments.add(new Medication(
            "Ceftriaxone", "2g",
            "Injection intraveineuse ou intramusculaire",
            7, "Toutes les 12 heures", true));

        // Alternative
        meningitisTreatments.add(new Medication(
            "Chloramphénicol", "1g",
            "Injection intraveineuse",
            7, "Toutes les 6 heures", true));

        // Traitement symptomatique
        meningitisTreatments.add(new Medication(
            "Paracétamol", "1000mg",
            "En cas de fièvre et céphalées",
            7, "Toutes les 6 heures si nécessaire", false));

        standardTreatments.put("INF006", meningitisTreatments);

        treatmentGuidelines.put("INF006",
            "1. Hospitalisation obligatoire en urgence\n" +
                "2. Isolement respiratoire pendant les premières 24 heures du traitement antibiotique\n" +
                "3. Position semi-assise recommandée\n" +
                "4. Éviter le bruit et la lumière forte\n" +
                "5. Surveillance neurologique régulière\n" +
                "6. Chimioprophylaxie des contacts proches selon recommandations\n" +
                "7. Consultation de suivi après guérison pour détecter d'éventuelles séquelles");
    }

    /**
     * Fièvre jaune - INF007
     */
    private void initializeYellowFeverTreatment() {
        List<Medication> yellowFeverTreatments = new ArrayList<>();

        // Traitement uniquement symptomatique (pas d'antiviraux spécifiques)
        yellowFeverTreatments.add(new Medication(
            "Paracétamol", "500-1000mg",
            "En cas de fièvre et douleurs",
            7, "Toutes les 6 heures si nécessaire", false));

        // Réhydratation
        yellowFeverTreatments.add(new Medication(
            "Solution de Réhydratation Orale", "1 sachet dans 1L d'eau",
            "Selon les besoins pour maintenir l'hydratation",
            7, "Répartie sur la journée", true));

        standardTreatments.put("INF007", yellowFeverTreatments);

        treatmentGuidelines.put("INF007",
            "1. Repos strict au lit sous moustiquaire pour éviter la transmission\n" +
                "2. Hydratation abondante\n" +
                "3. Alimentation légère et fractionnée\n" +
                "4. Éviter l'aspirine et les AINS qui augmentent le risque hémorragique\n" +
                "5. Surveillance rapprochée, surtout pendant la phase toxique (après 3-6 jours)\n" +
                "6. Hospitalisation recommandée en cas de forme sévère\n" +
                "7. La vaccination est le meilleur moyen de prévention pour l'avenir (une seule dose protège à vie)");
    }

    /**
     * Schistosomiase (Bilharziose) - PAR001
     */
    private void initializeSchistosomiasisTreatment() {
        List<Medication> schistosomiasisTreatments = new ArrayList<>();

        // Traitement antiparasitaire
        schistosomiasisTreatments.add(new Medication(
            "Praziquantel", "40mg/kg",
            "Dose unique divisée en 2 prises à 4-6 heures d'intervalle",
            1, "Dose unique (divisée en 2 prises)", true));

        // Traitement symptomatique
        schistosomiasisTreatments.add(new Medication(
            "Fer + Acide folique", "200mg/0.25mg",
            "En cas d'anémie associée",
            30, "Une fois par jour", false));

        standardTreatments.put("PAR001", schistosomiasisTreatments);

        treatmentGuidelines.put("PAR001",
            "1. Prendre le traitement pendant ou après un repas\n" +
                "2. Éviter tout contact avec l'eau douce potentiellement contaminée\n" +
                "3. Signaler immédiatement toute réaction allergique au médicament\n" +
                "4. Un traitement des complications peut être nécessaire (fibrose hépatique, hypertension portale, etc.)\n" +
                "5. Surveillance régulière recommandée pour les personnes vivant en zone d'endémie\n" +
                "6. Amélioration des installations sanitaires et accès à l'eau potable dans les communautés touchées");
    }

    /**
     * Onchocercose (Cécité des rivières) - PAR002
     */
    private void initializeOnchocerciasisTreatment() {
        List<Medication> onchocerciasisTreatments = new ArrayList<>();

        // Traitement antiparasitaire
        onchocerciasisTreatments.add(new Medication(
            "Ivermectine", "150μg/kg",
            "Dose unique tous les 3 à 12 mois selon l'endémicité",
            1, "Dose unique (renouvelable)", true));

        // Traitement local des lésions cutanées
        onchocerciasisTreatments.add(new Medication(
            "Crème hydratante", "",
            "Application sur les zones prurigineuses",
            30, "2-3 fois par jour", false));

        // Traitement symptomatique du prurit
        onchocerciasisTreatments.add(new Medication(
            "Antihistaminique", "10mg",
            "En cas de prurit intense",
            7, "Une fois par jour", false));

        standardTreatments.put("PAR002", onchocerciasisTreatments);

        treatmentGuidelines.put("PAR002",
            "1. L'ivermectine tue les microfilaires mais pas les vers adultes\n" +
                "2. Le traitement doit être répété pendant plusieurs années\n" +
                "3. Des réactions cutanées temporaires peuvent apparaître après le traitement\n" +
                "4. Éviter la proximité des rivières à courant rapide où vivent les mouches noires vecteurs\n" +
                "5. Porter des vêtements couvrants dans les zones à risque\n" +
                "6. Consulter un ophtalmologue pour évaluer les complications oculaires éventuelles");
    }

    /**
     * Filariose lymphatique - PAR003
     */
    private void initializeLymphaticFilariasisTreatment() {
        List<Medication> filariasisTreatments = new ArrayList<>();

        // Traitement antiparasitaire
        filariasisTreatments.add(new Medication(
            "Diéthylcarbamazine (DEC) + Albendazole", "6mg/kg + 400mg",
            "DEC en dose quotidienne, Albendazole en dose unique",
            12, "Une fois par jour pour DEC, dose unique pour Albendazole", true));

        // Traitement alternatif (zones à onchocercose)
        filariasisTreatments.add(new Medication(
            "Ivermectine + Albendazole", "200μg/kg + 400mg",
            "Doses uniques",
            1, "Dose unique annuelle", true));

        // Traitement du lymphœdème
        filariasisTreatments.add(new Medication(
            "Antibiotiques locaux", "",
            "En cas de surinfection",
            7, "Selon prescription", false));

        standardTreatments.put("PAR003", filariasisTreatments);

        treatmentGuidelines.put("PAR003",
            "1. Hygiène rigoureuse des membres atteints de lymphœdème\n" +
                "2. Lavage quotidien à l'eau et au savon des zones affectées\n" +
                "3. Élévation des membres atteints pendant la nuit\n" +
                "4. Exercices physiques appropriés pour stimuler le drainage lymphatique\n" +
                "5. Utilisation de bas de contention si disponibles\n" +
                "6. Traiter rapidement toute infection de la peau\n" +
                "7. Participer aux campagnes de traitement de masse si elles existent dans votre région");
    }

    /**
     * Hypertension artérielle - NCD001
     */
    private void initializeHypertensionTreatment() {
        List<Medication> hypertensionTreatments = new ArrayList<>();

        // Traitement de première ligne
        hypertensionTreatments.add(new Medication(
            "Hydrochlorothiazide", "12.5-25mg",
            "À prendre le matin",
            30, "Une fois par jour", true));

        // Traitement de deuxième ligne
        hypertensionTreatments.add(new Medication(
            "Amlodipine", "5-10mg",
            "À prendre de préférence le soir",
            30, "Une fois par jour", true));

        // Traitement de troisième ligne
        hypertensionTreatments.add(new Medication(
            "Énalapril", "5-20mg",
            "À prendre en dehors des repas",
            30, "Une ou deux fois par jour", true));

        standardTreatments.put("NCD001", hypertensionTreatments);

        treatmentGuidelines.put("NCD001",
            "1. Réduire la consommation de sel (< 5g par jour)\n" +
                "2. Maintenir un poids santé\n" +
                "3. Pratiquer une activité physique régulière (30 minutes par jour, 5 fois par semaine)\n" +
                "4. Limiter la consommation d'alcool\n" +
                "5. Consommer davantage de fruits et légumes\n" +
                "6. Arrêter le tabac\n" +
                "7. Contrôler régulièrement votre tension artérielle\n" +
                "8. Prendre les médicaments régulièrement même en l'absence de symptômes");
    }

    /**
     * Diabète - NCD002
     */
    private void initializeDiabetesTreatment() {
        List<Medication> diabetesTreatments = new ArrayList<>();

        // Traitement oral de première ligne
        diabetesTreatments.add(new Medication(
            "Metformine", "500-850mg",
            "À prendre pendant les repas",
            30, "2-3 fois par jour", true));

        // Traitement de deuxième ligne
        diabetesTreatments.add(new Medication(
            "Glibenclamide", "5mg",
            "À prendre avant les repas",
            30, "1-2 fois par jour", false));

        // Insuline (si nécessaire)
        diabetesTreatments.add(new Medication(
            "Insuline NPH", "Selon prescription individuelle",
            "Injection sous-cutanée",
            30, "1-2 fois par jour", false));

        standardTreatments.put("NCD002", diabetesTreatments);

        treatmentGuidelines.put("NCD002",
            "1. Suivre un régime alimentaire équilibré, pauvre en sucres rapides\n" +
                "2. Fractionner les repas (3 repas principaux + 2-3 collations)\n" +
                "3. Pratiquer une activité physique régulière adaptée\n" +
                "4. Contrôler sa glycémie régulièrement\n" +
                "5. Examiner ses pieds tous les jours (recherche de plaies ou lésions)\n" +
                "6. Éviter de marcher pieds nus\n" +
                "7. Porter une identification médicale (carte ou bracelet de diabétique)\n" +
                "8. Consulter régulièrement pour le suivi des complications potentielles");
    }

    /**
     * Drépanocytose - NCD003
     */
    private void initializeSickleCellTreatment() {
        List<Medication> sickleCellTreatments = new ArrayList<>();

        // Traitement préventif
        sickleCellTreatments.add(new Medication(
            "Acide folique", "5mg",
            "Pour prévenir l'anémie",
            30, "Une fois par jour", true));

        // Traitement des crises
        sickleCellTreatments.add(new Medication(
            "Paracétamol", "500-1000mg",
            "En cas de douleur légère à modérée",
            7, "Toutes les 6 heures si nécessaire", false));

        // Traitement de fond
        sickleCellTreatments.add(new Medication(
            "Hydroxyurée", "15-35mg/kg",
            "Pour réduire la fréquence des crises",
            30, "Une fois par jour", false));

        // Antibioprophylaxie
        sickleCellTreatments.add(new Medication(
            "Pénicilline V", "Selon l'âge",
            "Pour prévenir les infections",
            30, "Deux fois par jour", true));

        standardTreatments.put("NCD003", sickleCellTreatments);

        treatmentGuidelines.put("NCD003",
            "1. Boire beaucoup d'eau (au moins 2-3 litres par jour)\n" +
                "2. Éviter les environnements froids, la déshydratation et le surmenage\n" +
                "3. Se faire vacciner contre les infections (pneumocoque, méningocoque, Haemophilus, hépatite B)\n" +
                "4. En cas de fièvre > 38°C, consulter immédiatement\n" +
                "5. Signaler à l'équipe médicale votre drépanocytose avant toute intervention\n" +
                "6. Porter une identification médicale (carte ou bracelet)\n" +
                "7. En cas de grossesse, suivi spécialisé indispensable");
    }

    /**
     * Asthme - RES001
     */
    private void initializeAsthmaTreatment() {
        List<Medication> asthmaTreatments = new ArrayList<>();

        // Traitement de la crise (bronchodilatateur à action rapide)
        asthmaTreatments.add(new Medication(
            "Salbutamol (inhalateur)", "100μg/dose",
            "2 bouffées toutes les 4-6 heures si nécessaire",
            30, "À la demande", true));

        // Traitement de fond (corticoïde inhalé)
        asthmaTreatments.add(new Medication(
            "Béclométasone (inhalateur)", "250μg/dose",
            "2 bouffées matin et soir",
            30, "Deux fois par jour", true));

        standardTreatments.put("RES001", asthmaTreatments);

        treatmentGuidelines.put("RES001",
            "1. Identifiez et évitez vos facteurs déclenchants (allergènes, pollution, effort...)\n" +
                "2. Utilisez d'abord le bronchodilatateur (inhalateur bleu) en cas de crise\n" +
                "3. Prenez le traitement de fond (inhalateur brun) régulièrement, même sans symptômes\n" +
                "4. Technique d'inhalation correcte : expirer, puis inhaler profondément et retenir 10 secondes\n" +
                "5. Rincez-vous la bouche après utilisation des corticoïdes inhalés\n" +
                "6. Ayez toujours votre bronchodilatateur avec vous\n" +
                "7. Consultez en urgence si la crise ne cède pas après plusieurs doses de bronchodilatateur");
    }

    /**
     * BPCO - RES002
     */
    private void initializeCOPDTreatment() {
        List<Medication> copdTreatments = new ArrayList<>();

        // Bronchodilatateur à action courte
        copdTreatments.add(new Medication(
            "Salbutamol (inhalateur)", "100μg/dose",
            "2 bouffées toutes les 4-6 heures si nécessaire",
            30, "À la demande", true));

        // Bronchodilatateur à action longue
        copdTreatments.add(new Medication(
            "Ipratropium (inhalateur)", "20μg/dose",
            "2 bouffées quatre fois par jour",
            30, "Quatre fois par jour", true));

        standardTreatments.put("RES002", copdTreatments);

        treatmentGuidelines.put("RES002",
            "1. Arrêt immédiat et définitif du tabac\n" +
                "2. Éviter l'exposition aux irritants respiratoires (poussière, fumées...)\n" +
                "3. Vaccination antigrippale annuelle et vaccination anti-pneumococcique\n" +
                "4. Activité physique régulière adaptée pour maintenir la capacité respiratoire\n" +
                "5. Réhabilitation respiratoire si possible\n" +
                "6. Oxygénothérapie à domicile si prescrite\n" +
                "7. Consulter rapidement en cas d'aggravation de l'essoufflement ou d'expectoration purulente");
    }

    /**
     * Gastrite - DIG001
     */
    private void initializeGastritisTreatment() {
        List<Medication> gastritisTreatments = new ArrayList<>();

        // Inhibiteur de la pompe à protons
        gastritisTreatments.add(new Medication(
            "Oméprazole", "20mg",
            "À prendre le matin avant le petit-déjeuner",
            14, "Une fois par jour", true));

        // Antiacide
        gastritisTreatments.add(new Medication(
            "Hydroxyde d'aluminium/Hydroxyde de magnésium", "400mg/400mg",
            "À prendre 1 heure après les repas et au coucher",
            14, "3-4 fois par jour", false));

        // Traitement de H. pylori si présent
        gastritisTreatments.add(new Medication(
            "Amoxicilline + Clarithromycine + Oméprazole", "1000mg + 500mg + 20mg",
            "Amoxicilline et Clarithromycine 2 fois/jour, Oméprazole 1 fois/jour",
            7, "Selon prescription", false));

        standardTreatments.put("DIG001", gastritisTreatments);

        treatmentGuidelines.put("DIG001",
            "1. Éviter les aliments épicés, acides, frits et gras\n" +
                "2. Manger lentement et en petites quantités\n" +
                "3. Éviter l'alcool, le tabac et le café\n" +
                "4. Ne pas prendre d'anti-inflammatoires non stéroïdiens (AINS)\n" +
                "5. Élever la tête du lit de 15 cm en cas de reflux nocturne\n" +
                "6. Éviter de se coucher dans les 2-3 heures suivant un repas\n" +
                "7. Consulter si les symptômes persistent malgré le traitement");
    }

    /**
     * Hépatite virale - DIG002
     */
    private void initializeHepatitisVTreatment() {
        List<Medication> hepatitisTreatments = new ArrayList<>();

        // Traitement symptomatique (commun à toutes les hépatites)
        hepatitisTreatments.add(new Medication(
            "Paracétamol", "500mg",
            "En cas de fièvre ou douleurs",
            7, "Toutes les 6 heures si nécessaire (max 4g/jour)", false));

        // Pour l'hépatite B chronique
        hepatitisTreatments.add(new Medication(
            "Ténofovir", "300mg",
            "Pour l'hépatite B chronique active",
            30, "Une fois par jour", false));

        // Pour l'hépatite C
        hepatitisTreatments.add(new Medication(
            "Sofosbuvir + Daclatasvir", "400mg + 60mg",
            "Pour l'hépatite C (selon génotype)",
            84, "Une fois par jour", false));

        standardTreatments.put("DIG002", hepatitisTreatments);

        treatmentGuidelines.put("DIG002",
            "1. Repos pendant la phase aiguë\n" +
                "2. Hydratation abondante\n" +
                "3. Éviter strictement l'alcool\n" +
                "4. Alimentation équilibrée (éviter les aliments gras)\n" +
                "5. Ne pas prendre de médicaments sans avis médical\n" +
                "6. Éviter de partager les objets personnels (rasoir, brosse à dents)\n" +
                "7. Utiliser des préservatifs pour les rapports sexuels\n" +
                "8. Contrôles biologiques réguliers pour surveiller l'évolution");
    }

    /**
     * Épilepsie - NEU001
     */
    private void initializeEpilepsyTreatment() {
        List<Medication> epilepsyTreatments = new ArrayList<>();

        // Traitement de première ligne
        epilepsyTreatments.add(new Medication(
            "Phénobarbital", "50-100mg",
            "Dose unique le soir",
            30, "Une fois par jour", true));

        // Alternative
        epilepsyTreatments.add(new Medication(
            "Carbamazépine", "200mg",
            "Débuter à faible dose puis augmenter progressivement",
            30, "Deux fois par jour", true));

        // Autre alternative
        epilepsyTreatments.add(new Medication(
            "Valproate de sodium", "500mg",
            "À augmenter progressivement selon la tolérance",
            30, "Deux fois par jour", false));

        standardTreatments.put("NEU001", epilepsyTreatments);

        treatmentGuidelines.put("NEU001",
            "1. Prendre le traitement tous les jours sans interruption\n" +
                "2. Ne jamais arrêter brutalement le traitement (risque de crises graves)\n" +
                "3. Éviter l'alcool et les drogues\n" +
                "4. Maintenir un rythme de sommeil régulier\n" +
                "5. Éviter les lumières clignotantes (discothèques, certains jeux vidéo) si épilepsie photosensible\n" +
                "6. Porter une identification médicale (carte ou bracelet)\n" +
                "7. Informer l'entourage sur les gestes à faire en cas de crise\n" +
                "8. Pour les femmes: discuter avec le médecin avant une grossesse (adaptation du traitement)");
    }

    /**
     * Gale - DER001
     */
    private void initializeScabiesTreatment() {
        List<Medication> scabiesTreatments = new ArrayList<>();

        // Traitement topique
        scabiesTreatments.add(new Medication(
            "Benzoate de benzyle 25% (lotion)", "",
            "Appliquer sur tout le corps sauf le visage, laisser 24h puis rincer",
            1, "Application unique (à renouveler après 7 jours)", true));

        // Traitement oral alternatif
        scabiesTreatments.add(new Medication(
            "Ivermectine", "200μg/kg",
            "Dose unique à jeun",
            1, "Dose unique (à renouveler après 7 jours)", false));

        // Traitement du prurit
        scabiesTreatments.add(new Medication(
            "Antihistaminique", "10mg",
            "Pour soulager les démangeaisons",
            7, "Au coucher", false));

        standardTreatments.put("DER001", scabiesTreatments);

        treatmentGuidelines.put("DER001",
            "1. Traiter simultanément tous les membres du foyer, même sans symptômes\n" +
                "2. Laver à 60°C ou isoler pendant 3 jours tous les vêtements et linges utilisés\n" +
                "3. Les démangeaisons peuvent persister jusqu'à 2-3 semaines après traitement efficace\n" +
                "4. Une deuxième application du traitement est souvent nécessaire après 7-10 jours\n" +
                "5. Couper court les ongles et brosser sous les ongles avec une solution savonneuse\n" +
                "6. Éviter les contacts cutanés rapprochés jusqu'à guérison");
    }

    /**
     * Grippe - GEN001
     */
    private void initializeInfluenzaTreatment() {
        List<Medication> influenzaTreatments = new ArrayList<>();

        // Traitement symptomatique
        influenzaTreatments.add(new Medication(
            "Paracétamol", "500-1000mg",
            "En cas de fièvre ou douleurs",
            5, "Toutes les 6 heures si nécessaire", true));

        // Antiviral (cas graves ou complications)
        influenzaTreatments.add(new Medication(
            "Oseltamivir", "75mg",
            "Pour les cas graves ou patients à risque",
            5, "Deux fois par jour", false));

        standardTreatments.put("GEN001", influenzaTreatments);

        treatmentGuidelines.put("GEN001",
            "1. Repos à domicile jusqu'à 24h après la fin de la fièvre\n" +
                "2. Hydratation abondante\n" +
                "3. Éviter de contaminer l'entourage (masque, lavage des mains)\n" +
                "4. Éviter l'aspirine, surtout chez les enfants et adolescents\n" +
                "5. Alimentation légère selon l'appétit\n" +
                "6. Consulter si symptômes inhabituels ou persistants (difficultés respiratoires, douleur thoracique, confusion)");
    }

    /**
     * Rhume commun - GEN002
     */
    private void initializeCommonColdTreatment() {
        List<Medication> coldTreatments = new ArrayList<>();

        // Traitement symptomatique
        coldTreatments.add(new Medication(
            "Paracétamol", "500mg",
            "En cas de fièvre légère ou douleurs",
            5, "Toutes les 6 heures si nécessaire", false));

        // Décongestionnant nasal
        coldTreatments.add(new Medication(
            "Sérum physiologique nasal", "",
            "Pour dégager les fosses nasales",
            7, "Plusieurs fois par jour", false));

        standardTreatments.put("GEN002", coldTreatments);

        treatmentGuidelines.put("GEN002",
            "1. Repos modéré\n" +
                "2. Hydratation abondante (eau, tisanes, bouillons)\n" +
                "3. Humidifier l'air ambiant si possible\n" +
                "4. Éviter le tabagisme actif et passif\n" +
                "5. Se moucher régulièrement pour évacuer les sécrétions\n" +
                "6. Lavage des mains fréquent pour éviter la contamination\n" +
                "7. Consulter si fièvre persistante ou symptômes s'aggravant après 5 jours");
    }

    /**
     * Infection urinaire - GEN003
     */
    private void initializeUTITreatment() {
        List<Medication> utiTreatments = new ArrayList<>();

        // Antibiotique de première intention
        utiTreatments.add(new Medication(
            "Cotrimoxazole", "800mg/160mg",
            "À prendre avec un grand verre d'eau",
            3, "Deux fois par jour", true));

        // Alternative
        utiTreatments.add(new Medication(
            "Ciprofloxacine", "500mg",
            "À prendre avec un grand verre d'eau",
            3, "Deux fois par jour", true));

        // Antalgique urinaire
        utiTreatments.add(new Medication(
            "Phloroglucinol", "80mg",
            "Pour soulager les spasmes",
            3, "Trois fois par jour", false));

        standardTreatments.put("GEN003", utiTreatments);

        treatmentGuidelines.put("GEN003",
            "1. Boire au moins 2 litres d'eau par jour\n" +
                "2. Uriner fréquemment et complètement\n" +
                "3. Uriner après les rapports sexuels\n" +
                "4. Éviter les produits irritants pour la sphère urinaire (bains moussants, déodorants intimes)\n" +
                "5. S'essuyer d'avant en arrière après être allé aux toilettes\n" +
                "6. Consulter si fièvre, douleurs lombaires ou symptômes persistants après 48h de traitement");
    }

    /**
     * Récupère les traitements standard pour une maladie donnée
     * @param diseaseId Identifiant de la maladie
     * @return Liste des médicaments pour cette maladie
     */
    public List<Medication> getStandardTreatment(String diseaseId) {
        List<Medication> treatments = standardTreatments.getOrDefault(diseaseId, new ArrayList<>());

        // Si aucun traitement spécifique n'est trouvé, proposer un traitement symptomatique basique
        if (treatments.isEmpty()) {
            treatments = new ArrayList<>();
            treatments.add(new Medication(
                "Paracétamol", "500mg",
                "En cas de douleur ou fièvre",
                5, "Toutes les 6 heures si nécessaire", false));

            treatments.add(new Medication(
                "Hydratation", "Eau potable",
                "Boire au moins 2 litres par jour",
                7, "Tout au long de la journée", true));
        }

        return treatments;
    }

    /**
     * Récupère les recommandations pour une maladie donnée
     * @param diseaseId Identifiant de la maladie
     * @return Texte des recommandations
     */
    public String getGuidelinesForDisease(String diseaseId) {
        String guidelines = treatmentGuidelines.getOrDefault(diseaseId, "");

        // Si aucune recommandation spécifique n'est trouvée, proposer des recommandations générales
        if (guidelines.isEmpty()) {
            guidelines = "1. Prendre les médicaments selon la prescription\n" +
                "2. Se reposer suffisamment\n" +
                "3. Maintenir une bonne hydratation\n" +
                "4. Consulter si les symptômes s'aggravent\n" +
                "5. Revenir en consultation de suivi si recommandé";
        }

        return guidelines;
    }

    /**
     * Sélectionne un traitement personnalisé pour un patient en fonction de son âge et son poids
     * @param disease Maladie diagnostiquée
     * @param age Âge du patient en années
     * @param weight Poids du patient en kg
     * @return Liste des médicaments adaptés
     */
    public List<Medication> selectTreatmentForPatient(Disease disease, int age, int weight) {
        if (disease == null) {
            return new ArrayList<>();
        }

        List<Medication> standardTreatment = getStandardTreatment(disease.getId());
        List<Medication> personalizedTreatment = new ArrayList<>();

        // Cloner et ajuster chaque médicament
        for (Medication standardMed : standardTreatment) {
            Medication personalizedMed = cloneMedication(standardMed);
            adjustDosage(personalizedMed, age, weight);
            personalizedTreatment.add(personalizedMed);
        }

        // Ajouter des médicaments spécifiques selon l'âge
        addAgeSpecificMedications(personalizedTreatment, disease, age);

        return personalizedTreatment;
    }

    /**
     * Clone un médicament pour éviter de modifier l'original
     * @param original Médicament original
     * @return Nouvelle instance de médicament avec les mêmes propriétés
     */
    private Medication cloneMedication(Medication original) {
        return new Medication(
            original.getName(),
            original.getDosage(),
            original.getInstructions(),
            original.getDuration(),
            original.getFrequency(),
            original.isCritical()
        );
    }

    /**
     * Ajuste le dosage d'un médicament en fonction de l'âge et du poids
     * @param medication Médicament à ajuster
     * @param age Âge du patient
     * @param weight Poids du patient
     */
    private void adjustDosage(Medication medication, int age, int weight) {
        String currentDosage = medication.getDosage();

        try {
            // Personnes âgées (> 65 ans)
            if (age > 65) {
                adjustDosageForElderly(medication, currentDosage);
            }
            // Enfants (< 12 ans)
            else if (age < 12) {
                adjustDosageForChild(medication, currentDosage, age);
            }
            // Ajustement selon le poids si nécessaire (pour certains médicaments)
            adjustDosageByWeight(medication, weight);

            // Ajuster les instructions selon l'âge
            adjustInstructions(medication, age);

        } catch (Exception e) {
            // En cas d'erreur dans les calculs, conserver le dosage original
            System.err.println("Erreur lors de l'ajustement du dosage: " + e.getMessage());
        }
    }

    /**
     * Ajuste le dosage pour les personnes âgées
     */
    private void adjustDosageForElderly(Medication medication, String currentDosage) {
        // Réduction du dosage pour les personnes âgées
        if (currentDosage.contains("mg")) {
            String mgPart = currentDosage.replaceAll("[^0-9]", "");
            if (!mgPart.isEmpty()) {
                int dosage = Integer.parseInt(mgPart);
                int adjustedDosage = (int) (dosage * 0.75); // 75% du dosage standard
                medication.setDosage(adjustedDosage + "mg");

                // Ajouter une instruction spéciale
                String currentInstructions = medication.getInstructions();
                medication.setInstructions(currentInstructions + " (dosage adapté pour personne âgée)");
            }
        }
    }

    /**
     * Ajuste le dosage pour les enfants
     */
    private void adjustDosageForChild(Medication medication, String currentDosage, int age) {
        if (currentDosage.contains("mg")) {
            String mgPart = currentDosage.replaceAll("[^0-9]", "");
            if (!mgPart.isEmpty()) {
                int dosage = Integer.parseInt(mgPart);
                // Formule de Young pour l'ajustement pédiatrique
                double childFactor = age / (age + 12.0);
                int adjustedDosage = (int) (dosage * childFactor);
                medication.setDosage(adjustedDosage + "mg");

                // Ajouter une instruction spéciale
                String currentInstructions = medication.getInstructions();
                medication.setInstructions(currentInstructions + " (dosage pédiatrique)");
            }
        }
    }

    /**
     * Ajuste le dosage en fonction du poids pour certains médicaments
     */
    private void adjustDosageByWeight(Medication medication, int weight) {
        String name = medication.getName().toLowerCase();

        // Certains médicaments sont dosés selon le poids
        if (name.contains("ivermectine") ||
            name.contains("artésunate") ||
            name.contains("albendazole")) {

            // Supposons que le poids normal est de 70kg
            double weightFactor = weight / 70.0;

            // Limiter l'ajustement à ±30% du dosage standard
            weightFactor = Math.max(0.7, Math.min(weightFactor, 1.3));

            String currentDosage = medication.getDosage();
            if (currentDosage.contains("mg")) {
                String mgPart = currentDosage.replaceAll("[^0-9]", "");
                if (!mgPart.isEmpty()) {
                    int dosage = Integer.parseInt(mgPart);
                    int adjustedDosage = (int) (dosage * weightFactor);
                    medication.setDosage(adjustedDosage + "mg");
                }
            }
        }
    }

    /**
     * Ajuste les instructions selon l'âge
     */
    private void adjustInstructions(Medication medication, int age) {
        String currentInstructions = medication.getInstructions();

        if (age < 12) {
            // Pour les enfants
            if (!currentInstructions.contains("pédiatrique")) {
                medication.setInstructions(currentInstructions + " - Surveillance parentale recommandée");
            }
        } else if (age > 65) {
            // Pour les personnes âgées
            if (medication.getName().toLowerCase().contains("diurétique")) {
                medication.setInstructions(currentInstructions + " - Prendre de préférence le matin");
            }
        }
    }

    /**
     * Ajoute des médicaments spécifiques selon l'âge du patient
     */
    private void addAgeSpecificMedications(List<Medication> medications, Disease disease, int age) {
        // Pour les personnes âgées (> 65 ans)
        if (age > 65) {
            // Si maladie cardiovasculaire et pas déjà d'anticoagulant
            if (disease.getCategory().contains("cardiovasculaire") &&
                !containsMedicationType(medications, "anticoagulant")) {
                // Ajouter faible dose d'aspirine
                medications.add(new Medication(
                    "Aspirine (faible dose)", "75mg",
                    "À prendre pendant le repas - Protecteur cardiovasculaire",
                    30, "Une fois par jour", false));
            }
        }

        // Pour les enfants (< 12 ans)
        if (age < 12) {
            // Si maladie infectieuse, ajouter vitamine
            if (disease.getCategory().contains("infectieuse")) {
                medications.add(new Medication(
                    "Multivitamines pédiatriques", "5ml",
                    "Sirop à prendre après le repas",
                    15, "Une fois par jour", false));
            }
        }

        // Pour les femmes en âge de procréer (15-45 ans)
        if (age >= 15 && age <= 45) {
            // Si médicament tératogène
            if (containsTeratogenicMedication(medications)) {
                medications.add(new Medication(
                    "Note importante", "",
                    "Certains médicaments de cette prescription peuvent être contre-indiqués pendant la grossesse. " +
                        "Informez votre médecin si vous êtes enceinte ou prévoyez de l'être.",
                    0, "", true));
            }
        }
    }

    /**
     * Vérifie si la liste des médicaments contient un type spécifique
     */
    private boolean containsMedicationType(List<Medication> medications, String type) {
        for (Medication med : medications) {
            if (med.getName().toLowerCase().contains(type.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si la liste contient des médicaments tératogènes (dangereux pendant la grossesse)
     */
    private boolean containsTeratogenicMedication(List<Medication> medications) {
        String[] teratogenicDrugs = {"valproate", "isotrétinoïne", "warfarine", "méthotrexate",
            "tétracycline", "misoprostol", "hydroxyurée"};

        for (Medication med : medications) {
            String name = med.getName().toLowerCase();
            for (String drug : teratogenicDrugs) {
                if (name.contains(drug.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
