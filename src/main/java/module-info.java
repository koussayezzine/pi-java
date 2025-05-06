module com.example.event_match_crud {
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
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;
    requires java.desktop;


    opens com.example.event_match_crud to javafx.fxml;
    exports com.example.event_match_crud;
    opens com.example.event_match_crud.controller to javafx.fxml;
//

    opens com.example.event_match_crud.models to javafx.base;

}