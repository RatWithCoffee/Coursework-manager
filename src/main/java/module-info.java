module coursework_manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;
    requires static lombok;
    requires java.rmi;

    exports coursework_manager.controllers;
    exports coursework_manager.models;
    exports coursework_manager.repos;
    opens coursework_manager.controllers to javafx.fxml;
    opens coursework_manager.controllers.teachers to javafx.fxml;
    exports coursework_manager.controllers.groups;
    opens coursework_manager.controllers.groups to javafx.fxml;
    exports coursework_manager.controllers.teachers;
    exports coursework_manager;
    opens coursework_manager to javafx.fxml;
}