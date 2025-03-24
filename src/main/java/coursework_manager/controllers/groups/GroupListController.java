package coursework_manager.controllers.groups;

import coursework_manager.models.Group;
import coursework_manager.repos.GroupRepo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GroupListController {

    @FXML
    private ListView<Group> groupListView;

    private List<Group> groups;

    @FXML
    public void initialize() {
        // Получаем список групп из репозитория
        groups = GroupRepo.getAllGroups();

        // Отображаем группы в ListView
        groupListView.getItems().addAll(groups);

        // Обработка нажатия на элемент списка
        groupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openGroupDetailsWindow(newValue);
            }
        });
    }

    private void openGroupDetailsWindow(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("group_details.fxml"));
            Parent root = loader.load();

            GroupDetailsController controller = loader.getController();
            controller.setGroup(group);

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
            // Загрузка FXML-файла для списка учителей
            FXMLLoader loader = new FXMLLoader(getClass().getResource("list_teachers.fxml"));
            Parent root = loader.load();

            // Получение текущего Stage
            Stage stage = (Stage) groupListView.getScene().getWindow();

            // Установка новой сцены
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Управление учителями");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}