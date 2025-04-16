package coursework_manager.controllers;

import coursework_manager.controllers.groups.GroupListController;
import coursework_manager.models.users.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {


    public static void showMainWindow(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(GroupListController.class.getResource("group_list.fxml"));

            Parent root = loader.load();

            stage.setTitle("Менеджер курсовых работ");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
