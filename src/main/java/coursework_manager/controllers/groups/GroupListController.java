package coursework_manager.controllers.groups;

import coursework_manager.controllers.LoginController;
import coursework_manager.controllers.MainWindow;
import coursework_manager.controllers.teachers.TeacherListController;
import coursework_manager.models.Group;
import coursework_manager.models.users.Role;
import coursework_manager.models.users.Teacher;
import coursework_manager.models.users.User;
import coursework_manager.repos.ReposManager;
import coursework_manager.storage.UserStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class GroupListController {

    @FXML
    private ListView<Group> groupListView;

    @FXML
    private Label userInfoLabel;

    private List<Group> groups;

    @FXML
    private Button teachersButton;
    private User user;


    @FXML
    public void initialize() throws RemoteException {
        user = UserStorage.getUser();

        // Проверяем, что пользователь установлен
        if (user == null) {
            throw new IllegalStateException("User must be set before initialization");
        }

        if (user.getRole() == Role.TEACHER) {
            teachersButton.setVisible(false);
        }

        setUserInfo();

        // Получаем список групп из репозитория
        groups = ReposManager.getGroupRepo().getAllGroups();

        // Отображаем группы в ListView
        groupListView.getItems().addAll(groups);

        // Обработка нажатия на элемент списка
        groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openGroupDetailsWindow(newValue);
            }
        });
    }

    private void setUserInfo() {
        String roleName = user.getRole() == null ? "Не определена" :
                user.isAdmin() ? "Администратор" : "Преподаватель";
        if (user.isAdmin()) {
            userInfoLabel.setText(String.format("Пользователь: %s (%s)", user.getLogin(), roleName));
        } else {
            Teacher teacher = (Teacher) user;
            userInfoLabel.setText(String.format("Преподаватель: %s", teacher.getName()));

        }
    }

    private void openGroupDetailsWindow(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("group_details.fxml"));
            Parent root = loader.load();

            // Получаем контроллер после вызова load()
            GroupDetailsController controller = loader.getController();

            // Устанавливаем группу и пользователя
            controller.init(group);

            Stage stage = new Stage();
            stage.setTitle("Информация о группе: " + group.getName());
            stage.setScene(new Scene(root, 700, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToTeachers() {
        try {
            FXMLLoader loader = new FXMLLoader(TeacherListController.class.getResource("list_teachers.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) groupListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Управление преподавателями");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleExit(ActionEvent actionEvent) {
        UserStorage.deleteUser();

        // Получаем текущую Stage из любого компонента (например, из groupListView)
        Stage currentStage = (Stage) groupListView.getScene().getWindow();

        // Закрываем текущее окно
        currentStage.close();

        // Создаём и показываем окно логина
        LoginController loginController = new LoginController(currentStage);
        loginController.showLoginPage();
    }
}