module tn.esprit.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires java.sql;
    requires mysql.connector.j;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires itextpdf; // Using the automatic module name derived from JAR filename

    opens tn.esprit.demo to javafx.fxml;
    opens tn.esprit.demo.controllers to javafx.fxml;
    opens tn.esprit.demo.models to javafx.base;
    opens tn.esprit.demo.services to javafx.base;
    opens tn.esprit.demo.util to javafx.base;

    exports tn.esprit.demo;
    exports tn.esprit.demo.controllers;
    exports tn.esprit.demo.models;
    exports tn.esprit.demo.services;
    exports tn.esprit.demo.util;
}
