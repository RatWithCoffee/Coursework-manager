package coursework_manager.controllers;

import coursework_manager.controllers.groups.GroupListController;
import coursework_manager.models.User;
import coursework_manager.repos.ReposManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Label errorLabel;

    private final Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Инициализация контроллера
        loginButton.setOnAction(event -> handleLogin());
        errorLabel.setText("");
    }

    public void showLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Менеджер курсовых работ");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Логин и пароль обязательны для заполнения");
            return;
        }

        try {
            User user = new User(username, password);
           User loginUser = ReposManager.getLoginRepo().login(user);
            if (loginUser != null) {
                errorLabel.setText(""); // Очищаем сообщение об ошибке при успешном входе
                MainWindow.showMainWindow(stage, loginUser);
            } else {
                errorLabel.setText("Неверный логин или пароль");
                // Можно добавить очистку поля пароля для безопасности
                passwordField.clear();
            }
        } catch (RemoteException e) {
            errorLabel.setText("Ошибка соединения с сервером");
            throw new RuntimeException(e);
        }
    }
}