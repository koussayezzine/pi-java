@echo off
echo Running the application...
echo Make sure you have Java and JavaFX installed.

REM Set the path to your JavaFX SDK
set PATH_TO_FX=lib

REM Run the application
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp target/classes tn.esprit.sirine.HelloApplication

echo Done.
pause
