package tn.esprit.demo.util;

import javafx.event.Event;
import javafx.event.EventHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Gestionnaire de rafraîchissement des données
 * Utilise le pattern Observer pour notifier les contrôleurs quand les données changent
 */
public class DataRefreshManager {
    
    /**
     * Interface pour les écouteurs de changements de données
     */
    public interface DataRefreshListener {
        /**
         * Méthode appelée lorsque des données sont modifiées
         * @param dataType Le type de données modifiées
         */
        void onDataChanged(String dataType);
    }
    
    // Instance singleton
    private static DataRefreshManager instance;
    
    // Liste des listeners
    private final Map<String, List<EventHandler<Event>>> listeners = new ConcurrentHashMap<>();
    private final Map<String, List<DataRefreshListener>> dataListeners = new ConcurrentHashMap<>();
    
    // Constructeur privé (singleton)
    private DataRefreshManager() {
        // Constructeur privé pour le singleton
    }
    
    // Méthode pour obtenir l'instance
    public static synchronized DataRefreshManager getInstance() {
        if (instance == null) {
            instance = new DataRefreshManager();
        }
        return instance;
    }
    
    // Ajouter un listener
    public void addListener(String dataType, EventHandler<Event> listener) {
        listeners.computeIfAbsent(dataType, k -> new CopyOnWriteArrayList<>()).add(listener);
        System.out.println("Nombre d'écouteurs enregistrés: " + listeners.get(dataType).size());
    }
    
    // Ajouter un DataRefreshListener
    public void addListener(DataRefreshListener listener) {
        for (String dataType : dataListeners.keySet()) {
            dataListeners.get(dataType).add(listener);
        }
        // Si aucune liste n'existe encore, en créer une pour "itineraire"
        dataListeners.computeIfAbsent("itineraire", k -> new CopyOnWriteArrayList<>()).add(listener);
        System.out.println("DataRefreshListener ajouté");
    }
    
    // Supprimer un listener
    public void removeListener(String dataType, EventHandler<Event> listener) {
        if (listeners.containsKey(dataType)) {
            listeners.get(dataType).remove(listener);
        }
    }
    
    // Supprimer un DataRefreshListener
    public void removeListener(DataRefreshListener listener) {
        for (List<DataRefreshListener> listenerList : dataListeners.values()) {
            listenerList.remove(listener);
        }
        System.out.println("DataRefreshListener supprimé");
    }
    
    // Notifier les écouteurs d'un changement
    public void notifyChange(String dataType, Event event) {
        if (listeners.containsKey(dataType)) {
            for (EventHandler<Event> listener : listeners.get(dataType)) {
                listener.handle(event != null ? event : new Event(Event.ANY));
            }
        }
        
        // Notifier également les DataRefreshListeners
        if (dataListeners.containsKey(dataType)) {
            for (DataRefreshListener listener : dataListeners.get(dataType)) {
                listener.onDataChanged(dataType);
            }
        }
    }
    
    // Méthode alternative pour la compatibilité avec le code existant
    public void notifyDataChanged(String dataType) {
        notifyChange(dataType, null);
    }
}