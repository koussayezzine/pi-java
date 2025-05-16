package tn.esprit.sirine.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class TransportAPI {
    
    // Classe pour stocker les informations d'itinéraire
    public static class ItineraireInfo {
        private double distance;
        private String duree;
        
        public ItineraireInfo(double distance, String duree) {
            this.distance = distance;
            this.duree = duree;
        }
        
        public double getDistance() {
            return distance;
        }
        
        public String getDuree() {
            return duree;
        }
    }
    
    // Classe pour stocker les informations de trafic
    public static class TrafficInfo {
        private String roadName;
        private int congestion;
        private String trafficStatus;
        private int currentSpeed;
        
        public TrafficInfo(String roadName, int congestion, String trafficStatus, int currentSpeed) {
            this.roadName = roadName;
            this.congestion = congestion;
            this.trafficStatus = trafficStatus;
            this.currentSpeed = currentSpeed;
        }
        
        public String getRoadName() {
            return roadName;
        }
        
        public int getCongestion() {
            return congestion;
        }
        
        public String getTrafficStatus() {
            return trafficStatus;
        }
        
        public int getCurrentSpeed() {
            return currentSpeed;
        }
    }
    
    // Distances prédéfinies entre certaines villes tunisiennes (en km)
    private static final Map<String, Map<String, Double>> DISTANCES = new HashMap<>();
    
    static {
        // Distances depuis Tunis
        Map<String, Double> tunis = new HashMap<>();
        tunis.put("sousse", 140.0);
        tunis.put("sfax", 270.0);
        tunis.put("gabes", 400.0);
        tunis.put("bizerte", 65.0);
        tunis.put("nabeul", 75.0);
        tunis.put("hammamet", 90.0);
        tunis.put("monastir", 160.0);
        tunis.put("kairouan", 150.0);
        DISTANCES.put("tunis", tunis);
        
        // Distances depuis Sousse
        Map<String, Double> sousse = new HashMap<>();
        sousse.put("tunis", 140.0);
        sousse.put("sfax", 130.0);
        sousse.put("gabes", 260.0);
        sousse.put("bizerte", 205.0);
        sousse.put("nabeul", 65.0);
        sousse.put("hammamet", 50.0);
        sousse.put("monastir", 20.0);
        sousse.put("kairouan", 60.0);
        DISTANCES.put("sousse", sousse);
        
        // Distances depuis Sfax
        Map<String, Double> sfax = new HashMap<>();
        sfax.put("tunis", 270.0);
        sfax.put("sousse", 130.0);
        sfax.put("gabes", 130.0);
        sfax.put("bizerte", 335.0);
        sfax.put("nabeul", 195.0);
        sfax.put("hammamet", 180.0);
        sfax.put("monastir", 150.0);
        sfax.put("kairouan", 190.0);
        DISTANCES.put("sfax", sfax);
    }
    
    /**
     * Obtient les informations d'itinéraire entre deux villes
     * @param villeDepart La ville de départ
     * @param villeArrivee La ville d'arrivée
     * @return Les informations d'itinéraire (distance et durée)
     */
    public static ItineraireInfo getRouteInfo(String villeDepart, String villeArrivee) {
        // Simuler un délai réseau
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Normaliser les noms de villes
        String departNorm = villeDepart.toLowerCase().trim();
        String arriveeNorm = villeArrivee.toLowerCase().trim();
        
        // Vérifier si nous avons des données pour ces villes
        if (DISTANCES.containsKey(departNorm) && DISTANCES.get(departNorm).containsKey(arriveeNorm)) {
            double distance = DISTANCES.get(departNorm).get(arriveeNorm);
            // Simuler une durée basée sur la distance
            double dureeMinutes = distance * 1.5 + new Random().nextInt(30); // 1.5 km/min + 0-30 min
            String duree = String.format("%.0f min", dureeMinutes);
            return new ItineraireInfo(distance, duree);
        } else {
            return new ItineraireInfo(0, "Erreur: Aucune route trouvée entre ces villes");
        }
    }
    
    /**
     * Obtient les informations de trafic pour une zone géographique
     * @param latitude La latitude du centre de la zone
     * @param longitude La longitude du centre de la zone
     * @return Une liste d'informations de trafic
     */
    public static List<TrafficInfo> getTrafficInfo(double latitude, double longitude) {
        // Simuler un délai réseau
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simuler des informations de trafic pour les principales routes de Tunis
        List<TrafficInfo> trafficInfos = new ArrayList<>();
        Random random = new Random();
        
        // Ajouter quelques routes principales avec des niveaux de congestion aléatoires
        String[] roads = {
            "Avenue Habib Bourguiba", 
            "Avenue Mohamed V", 
            "Avenue de la Liberté", 
            "Avenue de Carthage", 
            "Avenue Louis Braille",
            "Autoroute A1 (Tunis-Hammamet)",
            "Autoroute A3 (Tunis-Oued Zarga)",
            "Route X20 (La Marsa)"
        };
        
        for (String road : roads) {
            int congestion = random.nextInt(101); // 0-100%
            String status;
            int speed;
            
            if (congestion < 25) {
                status = "Fluide";
                speed = 70 + random.nextInt(30); // 70-100 km/h
            } else if (congestion < 50) {
                status = "Modéré";
                speed = 40 + random.nextInt(30); // 40-70 km/h
            } else if (congestion < 75) {
                status = "Dense";
                speed = 20 + random.nextInt(20); // 20-40 km/h
            } else {
                status = "Congestionné";
                speed = 5 + random.nextInt(15); // 5-20 km/h
            }
            
            trafficInfos.add(new TrafficInfo(road, congestion, status, speed));
        }
        
        return trafficInfos;
    }
}