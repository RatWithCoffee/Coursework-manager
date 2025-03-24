package coursework_manager.controllers;

import coursework_manager.controllers.groups.GroupListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {


    public static void showMainWindow(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GroupListController.class.getResource("group_list.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Менеджер курсовых работ");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
