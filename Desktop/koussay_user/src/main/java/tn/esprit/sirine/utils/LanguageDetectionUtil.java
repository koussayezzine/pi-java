package tn.esprit.sirine.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class LanguageDetectionUtil {

    private static final String API_KEY = "3c7fac58d78bf075a0e4fd110c8f4f8a"; // Remplace par ta clé

    public static String detectLanguage(String text) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "q=" + text;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ws.detectlanguage.com/0.2/detect"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            JSONArray data = json.getJSONObject("data").getJSONArray("detections");
            if (data.length() > 0) {
                return data.getJSONObject(0).getString("language");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "inconnu";
    }
}