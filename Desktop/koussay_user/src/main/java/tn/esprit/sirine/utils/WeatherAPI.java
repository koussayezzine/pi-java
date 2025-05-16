package tn.esprit.sirine.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherAPI {
    private static final String API_KEY = "votre_clé_api"; // Remplacez par votre clé API OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getWeatherForCity(String city) {
        try {
            String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric&lang=fr";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Erreur: " + response.code();
                }

                JsonNode rootNode = mapper.readTree(response.body().string());
                String weather = rootNode.path("weather").get(0).path("description").asText();
                double temp = rootNode.path("main").path("temp").asDouble();
                
                return String.format("%s - %.1f°C", weather, temp);
            }
        } catch (IOException e) {
            return "Erreur de connexion: " + e.getMessage();
        }
    }
}
