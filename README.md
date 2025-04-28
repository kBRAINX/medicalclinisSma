# Système Multi-Agent de Cabinet Médical

Ce projet implémente un système multi-agent de cabinet médical complet, permettant la gestion du parcours de soins des patients, depuis leur arrivée jusqu'à leur diagnostic et prescription de traitement.

## Fonctionnalités principales

- Accueil et enregistrement des patients
- Collecte d'informations sur les symptômes par un infirmier
- Affectation intelligente des patients aux médecins spécialistes appropriés
- Gestion de liste d'attente avec priorités pour les cas urgents
- Diagnostic médical basé sur un système d'évaluation des maladies
- Prescription de traitements adaptés au profil du patient
- Interfaces graphiques pour tous les acteurs du système

## Architecture du système

Le système est composé des agents suivants :

### 1. Agent Réceptionniste
- Accueille les patients et recueille leurs informations personnelles
- Gère l'affectation des patients aux médecins
- Coordonne la communication entre les différents agents
- Gère la liste d'attente avec système de priorité

### 2. Agent Infirmier
- Pose des questions détaillées sur les symptômes des patients
- Détecte les cas urgents
- Transmet les informations à la réceptionniste

### 3. Agent Médecin
- Analyse les symptômes et propose un diagnostic
- Pose des questions spécifiques pour affiner le diagnostic
- Prescrit des traitements adaptés au patient
- Peut être généraliste ou spécialiste (cardiologue, pneumologue, etc.)

### 4. Agent Patient
- Remplit des formulaires d'informations personnelles
- Répond aux questions de l'infirmier et du médecin
- Se déplace dans le cabinet virtuel (salle d'attente, consultation)

## Base de données

Le système intègre deux bases de données principales :

### 1. Base de données des maladies
- Maladies courantes en Afrique (infectieuses, parasitaires, non transmissibles, etc.)
- Système de correspondance symptômes-maladies basé sur des pourcentages
- Génération de questions spécifiques pour chaque maladie

### 2. Base de données des traitements
- Traitements adaptés à chaque maladie
- Ajustement des prescriptions en fonction de l'âge et du poids du patient
- Recommandations et conseils personnalisés

## Prérequis

- Java JDK 8 ou supérieur
- Framework JADE (Java Agent DEvelopment Framework)
- Bibliothèque Gson pour la gestion du JSON
- Bibliothèques Swing pour l'interface graphique

## Démarrage du système

### Utilisation du lanceur graphique

1. Compilez et exécutez la classe `Main`
2. Utilisez l'interface graphique pour :
   - Lancer le système complet (réceptionniste, infirmier, médecins)
   - Lancer un ou plusieurs patients

### Utilisation des lanceurs individuels

Pour démarrer le système :
```bash
java -cp .:jade.jar:gson-2.8.9.jar medicalclinic.utils.StartSystem
```

Pour démarrer un patient :
```bash
java -cp .:jade.jar:gson-2.8.9.jar medicalclinic.utils.StartPatient
```

## Scénario d'utilisation

1. **Accueil du patient**
   - Le patient se connecte et est accueilli par la réceptionniste
   - Il remplit un formulaire d'informations personnelles

2. **Consultation avec l'infirmier**
   - Le patient est dirigé vers la salle d'attente
   - L'infirmier l'accueille et pose des questions sur ses symptômes
   - Les réponses sont envoyées à la réceptionniste

3. **Affectation à un médecin**
   - La réceptionniste analyse les symptômes
   - Elle affecte le patient au médecin le plus approprié
   - Si aucun médecin n'est disponible, le patient est mis en liste d'attente

4. **Consultation médicale**
   - Le médecin reçoit le dossier du patient
   - Il pose des questions complémentaires pour affiner son diagnostic
   - Il établit un diagnostic et prescrit un traitement
   - Le diagnostic et la prescription sont transmis au patient

5. **Conclusion**
   - Le dossier du patient est mis à jour
   - Le patient quitte le cabinet

## Structure des packages

```
medicalclinic/
├── agents/         # Agents du système (Patient, Infirmier, Médecin, Réceptionniste)
├── database/       # Bases de données (maladies, traitements)
├── gui/            # Interfaces graphiques
├── models/         # Modèles de données (Patient, Maladie, Consultation, etc.)
└── utils/          # Classes utilitaires et lanceurs
```

## Personnalisation

Le système peut être facilement étendu en :
- Ajoutant de nouvelles maladies dans `DiseaseDatabase`
- Ajoutant de nouveaux traitements dans `TreatmentDatabase`
- Ajoutant de nouveaux médecins spécialistes dans `StartSystem`

## Amélioration et perspectives

- Ajout d'un système de rendez-vous
- Intégration d'un système d'historique médical plus complet
- Implémentation d'un système de prise en charge des urgences
- Intégration d'une intelligence artificielle pour l'aide au diagnostic
- Ajout d'un système de téléconsultation

