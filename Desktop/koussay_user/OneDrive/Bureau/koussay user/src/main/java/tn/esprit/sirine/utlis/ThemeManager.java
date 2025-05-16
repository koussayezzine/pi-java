package tn.esprit.sirine.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.prefs.Preferences;

/**
 * Manages application themes and provides methods for switching between light and dark modes.
 */
public class ThemeManager {
    private static ThemeManager instance;
    private static final String THEME_PREF_KEY = "app_theme";
    private static final String LIGHT_THEME = "light-theme.css";
    private static final String DARK_THEME = "dark-theme.css";
    private static final String COMPONENTS_CSS = "components.css";
    
    private boolean isDarkMode = false;
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    private ThemeManager() {
        // Load saved theme preference
        isDarkMode = prefs.getBoolean(THEME_PREF_KEY, false);
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Applies the current theme to a scene
     * @param scene The scene to apply the theme to
     */
    public void applyTheme(Scene scene) {
        String themeFile = isDarkMode ? DARK_THEME : LIGHT_THEME;
        
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Add components CSS first
        scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/" + COMPONENTS_CSS).toExternalForm());
        
        // Add theme CSS
        scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/" + themeFile).toExternalForm());
    }
    
    /**
     * Toggles between light and dark mode
     * @param scene The scene to apply the theme change to
     * @return The new dark mode state
     */
    public boolean toggleTheme(Scene scene) {
        isDarkMode = !isDarkMode;
        applyTheme(scene);
        
        // Save preference
        prefs.putBoolean(THEME_PREF_KEY, isDarkMode);
        
        return isDarkMode;
    }
    
    /**
     * Sets a specific theme
     * @param scene The scene to apply the theme to
     * @param darkMode True for dark mode, false for light mode
     */
    public void setTheme(Scene scene, boolean darkMode) {
        if (isDarkMode != darkMode) {
            isDarkMode = darkMode;
            applyTheme(scene);
            prefs.putBoolean(THEME_PREF_KEY, isDarkMode);
        } else {
            applyTheme(scene);
        }
    }
    
    /**
     * Applies the current theme to all open stages
     */
    public void applyThemeToAllStages() {
        for (Stage stage : javafx.stage.Window.getWindows().filtered(window -> window instanceof Stage)
                .stream().map(window -> (Stage) window).toList()) {
            if (stage.getScene() != null) {
                applyTheme(stage.getScene());
            }
        }
    }
    
    /**
     * @return True if dark mode is enabled, false otherwise
     */
    public boolean isDarkMode() {
        return isDarkMode;
    }
}
