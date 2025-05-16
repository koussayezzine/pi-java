package tn.esprit.demo.util;

import java.util.*;

/**
 * Classe utilitaire pour récupérer des statistiques via API
 */
public class StatistiquesAPI {
    private static final Random random = new Random();

    /**
     * Récupère les statistiques en fonction des filtres
     * @param periode La période sélectionnée (Aujourd'hui, Cette semaine, Ce mois, etc.)
     * @param typeBus Le type de bus (ou "Tous")
     * @param itineraire L'itinéraire (ou "Tous")
     * @return Map contenant toutes les données statistiques
     */
    public static Map<String, Object> getStatistiques(String periode, String typeBus, String itineraire) {
        // Simuler un délai réseau
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> data = new HashMap<>();
        
        // Facteur de variation basé sur les filtres
        double facteurType = typeBus.equals("Tous") ? 1.0 : getFacteurType(typeBus);
        double facteurItineraire = itineraire.equals("Tous") ? 1.0 : getFacteurItineraire(itineraire);
        double facteurPeriode = getFacteurPeriode(periode);
        
        // KPIs principaux
        data.put("occupationRate", 65.0 + (random.nextDouble() * 20) * facteurType * facteurItineraire);
        data.put("occupationEvolution", 2.0 + (random.nextDouble() * 5 - 2) * facteurPeriode);
        data.put("punctualityRate", 85.0 + (random.nextDouble() * 15) * facteurType);
        data.put("punctualityEvolution", 1.0 + (random.nextDouble() * 4 - 2) * facteurPeriode);
        data.put("consumptionRate", 25.0 + (random.nextDouble() * 10) * facteurType);
        data.put("consumptionEvolution", -1.0 + (random.nextDouble() * 3 - 1) * facteurPeriode);
        data.put("satisfactionRate", 3.0 + (random.nextDouble() * 2) * facteurType * facteurItineraire);
        data.put("satisfactionEvolution", 0.5 + (random.nextDouble() - 0.5) * facteurPeriode);
        
        // Données pour les graphiques
        data.put("monthlyData", generateMonthlyData(facteurPeriode, facteurType, facteurItineraire));
        data.put("busTypeData", generateBusTypeData(typeBus, facteurType));
        data.put("itineraireData", generateItineraireData(itineraire, facteurItineraire));
        data.put("incidentsData", generateIncidentsData(facteurType, facteurItineraire));
        
        // Ajouter des métadonnées
        data.put("timestamp", System.currentTimeMillis());
        data.put("source", "StatistiquesAPI v1.0");
        data.put("filtres", Map.of(
            "periode", periode,
            "typeBus", typeBus,
            "itineraire", itineraire
        ));
        
        return data;
    }
    
    private static double getFacteurType(String typeBus) {
        switch (typeBus) {
            case "Standard": return 1.0;
            case "Confort": return 1.2;
            case "Premium": return 1.4;
            case "VIP": return 1.6;
            default: return 1.0;
        }
    }

    private static double getFacteurItineraire(String itineraire) {
        if (itineraire.contains("Tunis")) return 1.3;
        if (itineraire.contains("Sousse")) return 1.2;
        if (itineraire.contains("Sfax")) return 1.1;
        return 1.0;
    }

    private static double getFacteurPeriode(String periode) {
        switch (periode) {
            case "Aujourd'hui": return 1.0;
            case "Cette semaine": return 1.1;
            case "Ce mois": return 1.2;
            case "Ce trimestre": return 1.3;
            case "Cette année": return 1.5;
            default: return 1.0;
        }
    }

    private static List<Map<String, Object>> generateMonthlyData(double facteurPeriode, double facteurType, double facteurItineraire) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        
        // Données pour 2023
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("year", 2023);
            
            // Base + variation saisonnière
            double baseValue = 1000 * facteurType * facteurItineraire;
            double seasonalFactor = 1.0;
            
            // Été: plus de passagers
            if (i >= 5 && i <= 7) seasonalFactor = 1.3;
            // Hiver: moins de passagers
            if (i == 11 || i == 0 || i == 1) seasonalFactor = 0.8;
            
            monthData.put("passengers", (int)(baseValue * seasonalFactor * (0.9 + random.nextDouble() * 0.2)));
            monthlyData.add(monthData);
        }
        
        // Données pour 2024 (avec une légère augmentation)
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("year", 2024);
            
            // Base + variation saisonnière + croissance annuelle
            double baseValue = 1100 * facteurType * facteurItineraire;
            double seasonalFactor = 1.0;
            
            // Été: plus de passagers
            if (i >= 5 && i <= 7) seasonalFactor = 1.3;
            // Hiver: moins de passagers
            if (i == 11 || i == 0 || i == 1) seasonalFactor = 0.8;
            
            monthData.put("passengers", (int)(baseValue * seasonalFactor * (0.9 + random.nextDouble() * 0.2)));
            monthlyData.add(monthData);
        }
        
        return monthlyData;
    }

    private static List<Map<String, Object>> generateBusTypeData(String selectedType, double facteurType) {
        List<Map<String, Object>> busTypeData = new ArrayList<>();
        
        // Types de bus et leurs valeurs de base
        String[] types = {"Standard", "Confort", "Premium", "VIP"};
        int[] baseValues = {15, 8, 12, 5};
        
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", types[i]);
            
            // Ajuster la valeur en fonction du type sélectionné
            double adjustmentFactor = selectedType.equals("Tous") ? 1.0 : 
                                      types[i].equals(selectedType) ? 1.5 : 0.7;
            
            typeData.put("value", (int)(baseValues[i] * adjustmentFactor * facteurType));
            busTypeData.add(typeData);
        }
        
        return busTypeData;
    }

    private static List<Map<String, Object>> generateItineraireData(String selectedItineraire, double facteurItineraire) {
        List<Map<String, Object>> itineraireData = new ArrayList<>();
        
        // Itinéraires et leurs valeurs de base
        String[] itineraires = {"Tunis - Sousse", "Tunis - Sfax", "Sousse - Monastir", "Sfax - Gabès", "Autres"};
        int[] baseValues = {8, 6, 5, 4, 10};
        
        for (int i = 0; i < itineraires.length; i++) {
            Map<String, Object> routeData = new HashMap<>();
            routeData.put("route", itineraires[i]);
            
            // Ajuster la valeur en fonction de l'itinéraire sélectionné
            double adjustmentFactor = selectedItineraire.equals("Tous") ? 1.0 : 
                                      itineraires[i].equals(selectedItineraire) ? 1.5 : 0.7;
            
            routeData.put("value", (int)(baseValues[i] * adjustmentFactor * facteurItineraire));
            itineraireData.add(routeData);
        }
        
        return itineraireData;
    }

    private static List<Map<String, Object>> generateIncidentsData(double facteurType, double facteurItineraire) {
        List<Map<String, Object>> incidentsData = new ArrayList<>();
        
        // Types d'incidents et leurs valeurs de base
        String[] incidents = {"Retards", "Pannes mécaniques", "Problèmes de climatisation", "Incidents voyageurs", "Autres"};
        int[] baseValues = {12, 8, 5, 3, 2};
        
        for (int i = 0; i < incidents.length; i++) {
            Map<String, Object> incidentData = new HashMap<>();
            incidentData.put("type", incidents[i]);
            
            // Ajuster la valeur en fonction des facteurs
            double value = baseValues[i] * (0.8 + random.nextDouble() * 0.4);
            
            // Les pannes mécaniques sont plus fréquentes sur certains types de bus
            if (incidents[i].equals("Pannes mécaniques")) {
                value *= (2.0 - facteurType); // Moins de pannes sur les bus premium
            }
            
            // Les retards sont plus fréquents sur certains itinéraires
            if (incidents[i].equals("Retards")) {
                value *= (2.0 - facteurItineraire); // Moins de retards sur les itinéraires bien desservis
            }
            
            incidentData.put("value", (int)value);
            incidentsData.add(incidentData);
        }
        
        return incidentsData;
    }

    /**
     * Récupère les statistiques comparatives entre bus et itinéraires
     * @return Map contenant les données de comparaison
     */
    public static Map<String, Object> getComparativeStats() {
        // Simuler un délai réseau
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> data = new HashMap<>();
        
        // Générer des données pour les 12 derniers mois
        List<Map<String, Object>> monthlyComparison = new ArrayList<>();
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        
        // Déterminer le mois actuel
        Calendar cal = Calendar.getInstance();
        int currentMonthIndex = cal.get(Calendar.MONTH);
        
        for (int i = 0; i < 12; i++) {
            int monthIndex = (currentMonthIndex - 11 + i) % 12;
            if (monthIndex < 0) monthIndex += 12;
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[monthIndex]);
            
            // Facteur saisonnier (plus d'activité en été)
            double facteurSaisonnier = 1.0;
            if (monthIndex >= 5 && monthIndex <= 7) facteurSaisonnier = 1.3; // Été
            if (monthIndex >= 11 || monthIndex <= 1) facteurSaisonnier = 0.8; // Hiver
            
            // Données pour les bus (taux d'occupation)
            double busOccupationRate = 65.0 + (random.nextDouble() * 15) * facteurSaisonnier;
            monthData.put("busOccupationRate", busOccupationRate);
            
            // Données pour les itinéraires (fréquentation)
            double itineraireUsageRate = 70.0 + (random.nextDouble() * 20) * facteurSaisonnier;
            monthData.put("itineraireUsageRate", itineraireUsageRate);
            
            // Rentabilité (ratio revenus/coûts)
            double profitabilityRate = (busOccupationRate * 0.8 + itineraireUsageRate * 0.6) / 100.0;
            monthData.put("profitabilityRate", profitabilityRate);
            
            monthlyComparison.add(monthData);
        }
        
        data.put("monthlyComparison", monthlyComparison);
        
        // Ajouter des statistiques globales
        data.put("avgBusOccupation", 72.5 + (random.nextDouble() * 5));
        data.put("avgItineraireUsage", 78.3 + (random.nextDouble() * 5));
        data.put("avgProfitability", 1.35 + (random.nextDouble() * 0.3));
        
        return data;
    }

    public static Map<String, Object> getHeatmapData(String periode, String typeBus) {
        Map<String, Object> heatmapData = new HashMap<>();
        String[] heures = {"6h", "8h", "10h", "12h", "14h", "16h", "18h", "20h", "22h"};
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < jours.length; i++) {
            for (int j = 0; j < heures.length; j++) {
                Map<String, Object> point = new HashMap<>();
                point.put("jour", jours[i]);
                point.put("heure", heures[j]);
                point.put("valeur", 20 + random.nextInt(80)); // Valeur entre 20 et 100
                data.add(point);
            }
        }
        
        heatmapData.put("data", data);
        return heatmapData;
    }

    public static List<Map<String, Object>> detectAnomalies(Map<String, Object> data) {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        
        // Détecter les anomalies dans le taux d'occupation
        if (data.containsKey("occupationRate")) {
            double occupationRate = ((Number) data.get("occupationRate")).doubleValue();
            if (occupationRate < 40.0) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("type", "Occupation");
                anomaly.put("message", "Taux d'occupation anormalement bas");
                anomaly.put("value", occupationRate);
                anomaly.put("severity", "warning");
                anomalies.add(anomaly);
            }
        }
        
        // Détecter les anomalies dans la ponctualité
        if (data.containsKey("punctualityRate")) {
            double punctualityRate = ((Number) data.get("punctualityRate")).doubleValue();
            if (punctualityRate < 70.0) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("type", "Ponctualité");
                anomaly.put("message", "Taux de ponctualité critique");
                anomaly.put("value", punctualityRate);
                anomaly.put("severity", "critical");
                anomalies.add(anomaly);
            }
        }
        
        return anomalies;
    }

    public static Map<String, Object> getPredictions(String periode, String typeBus, String itineraire) {
        Map<String, Object> predictions = new HashMap<>();
        Random random = new Random();
        
        // Prédiction d'occupation pour les 3 prochains mois
        List<Map<String, Object>> occupationPredictions = new ArrayList<>();
        String[] nextMonths = {"Janvier", "Février", "Mars"};
        double currentOccupation = 65.0 + (random.nextDouble() * 10);
        
        for (int i = 0; i < nextMonths.length; i++) {
            Map<String, Object> monthPrediction = new HashMap<>();
            monthPrediction.put("month", nextMonths[i]);
            // Tendance légèrement à la hausse avec variations aléatoires
            currentOccupation += (random.nextDouble() * 5) - 2;
            monthPrediction.put("value", currentOccupation);
            occupationPredictions.add(monthPrediction);
        }
        
        predictions.put("occupationPredictions", occupationPredictions);
        
        // Autres prédictions...
        
        return predictions;
    }

    /**
     * Récupère les données comparatives entre deux périodes
     * @param periode La période principale sélectionnée
     * @param periodeDComparaison La période de comparaison
     * @param typeBus Le type de bus (ou "Tous")
     * @param itineraire L'itinéraire (ou "Tous")
     * @return Map contenant les données comparatives
     */
    public static Map<String, Object> getComparativeData(String periode, String periodeComparaison, String typeBus, String itineraire) {
        // Simuler un délai réseau
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> data = new HashMap<>();
        
        // Facteurs de variation basés sur les filtres
        double facteurType = typeBus.equals("Tous") ? 1.0 : getFacteurType(typeBus);
        double facteurItineraire = itineraire.equals("Tous") ? 1.0 : getFacteurItineraire(itineraire);
        double facteurPeriode1 = getFacteurPeriode(periode);
        double facteurPeriode2 = getFacteurPeriode(periodeComparaison);
        
        // Données pour la période principale
        Map<String, Object> donneesPeriode1 = new HashMap<>();
        donneesPeriode1.put("occupationRate", 65.0 + (random.nextDouble() * 20) * facteurType * facteurItineraire * facteurPeriode1);
        donneesPeriode1.put("punctualityRate", 85.0 + (random.nextDouble() * 15) * facteurType * facteurPeriode1);
        donneesPeriode1.put("consumptionRate", 25.0 + (random.nextDouble() * 10) * facteurType * facteurPeriode1);
        donneesPeriode1.put("satisfactionRate", 3.0 + (random.nextDouble() * 2) * facteurType * facteurItineraire * facteurPeriode1);
        
        // Données pour la période de comparaison
        Map<String, Object> donneesPeriode2 = new HashMap<>();
        donneesPeriode2.put("occupationRate", 65.0 + (random.nextDouble() * 20) * facteurType * facteurItineraire * facteurPeriode2);
        donneesPeriode2.put("punctualityRate", 85.0 + (random.nextDouble() * 15) * facteurType * facteurPeriode2);
        donneesPeriode2.put("consumptionRate", 25.0 + (random.nextDouble() * 10) * facteurType * facteurPeriode2);
        donneesPeriode2.put("satisfactionRate", 3.0 + (random.nextDouble() * 2) * facteurType * facteurItineraire * facteurPeriode2);
        
        // Ajouter les données des deux périodes
        data.put("periode1", donneesPeriode1);
        data.put("periode2", donneesPeriode2);
        data.put("nomPeriode1", periode);
        data.put("nomPeriode2", periodeComparaison);
        
        // Générer des données mensuelles comparatives
        List<Map<String, Object>> donneesMensuelles = new ArrayList<>();
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        
        for (int i = 0; i < mois.length; i++) {
            Map<String, Object> donneesMois = new HashMap<>();
            donneesMois.put("mois", mois[i]);
            
            // Facteur saisonnier
            double facteurSaisonnier = 1.0;
            if (i >= 5 && i <= 7) facteurSaisonnier = 1.3; // Été
            if (i == 11 || i == 0 || i == 1) facteurSaisonnier = 0.8; // Hiver
            
            // Données pour la période 1
            donneesMois.put("occupation1", 65.0 + (random.nextDouble() * 20) * facteurType * facteurItineraire * facteurPeriode1 * facteurSaisonnier);
            donneesMois.put("ponctualite1", 85.0 + (random.nextDouble() * 15) * facteurType * facteurPeriode1 * facteurSaisonnier);
            donneesMois.put("consommation1", 25.0 + (random.nextDouble() * 10) * facteurType * facteurPeriode1 * facteurSaisonnier);
            donneesMois.put("satisfaction1", 3.0 + (random.nextDouble() * 2) * facteurType * facteurItineraire * facteurPeriode1 * facteurSaisonnier);
            
            // Données pour la période 2
            donneesMois.put("occupation2", 65.0 + (random.nextDouble() * 20) * facteurType * facteurItineraire * facteurPeriode2 * facteurSaisonnier);
            donneesMois.put("ponctualite2", 85.0 + (random.nextDouble() * 15) * facteurType * facteurPeriode2 * facteurSaisonnier);
            donneesMois.put("consommation2", 25.0 + (random.nextDouble() * 10) * facteurType * facteurPeriode2 * facteurSaisonnier);
            donneesMois.put("satisfaction2", 3.0 + (random.nextDouble() * 2) * facteurType * facteurItineraire * facteurPeriode2 * facteurSaisonnier);
            
            donneesMensuelles.add(donneesMois);
        }
        
        data.put("donneesMensuelles", donneesMensuelles);
        
        return data;
    }
}
