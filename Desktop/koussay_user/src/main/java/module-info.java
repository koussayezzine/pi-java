module tn.esprit.sirine {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires twilio;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.apache.pdfbox;
    requires mysql.connector.j;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.net.http;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;
    requires okhttp3;
    // java.mail requirement removed to resolve conflict with jakarta.mail


    opens tn.esprit.sirine to javafx.fxml;
    exports tn.esprit.sirine;
    opens tn.esprit.sirine.controller to javafx.fxml;
    exports tn.esprit.sirine.controller;

    opens tn.esprit.sirine.models to javafx.base;

}
