package coursework_manager;

import coursework_manager.controllers.LoginController;
import coursework_manager.controllers.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LoginController loginController = new LoginController(stage);
        loginController.showLoginPage();
    }

    public static void main(String[] args) {
        launch();
    }
}