package coursework_manager;

import coursework_manager.controllers.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainWindow.showMainWindow(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}