package coursework_manager.controllers.groups;

import coursework_manager.models.Teacher;
import coursework_manager.repos.TeacherRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class TeacherListController {

    @FXML
    private TableView<Teacher> teacherTable;

    private ObservableList<Teacher> teacherList;

    @FXML
    public void initialize() {
        // Инициализация таблицы данными из базы
        teacherList = FXCollections.observableArrayList(TeacherRepo.getAllTeachers());
        teacherTable.setItems(teacherList);
    }

    @FXML
    private void handleAddTeacher() {
        // Создаем диалоговое окно
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Добавить нового учителя");
        dialog.setHeaderText("Введите данные учителя");

        // Устанавливаем кнопки (OK и Cancel)
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Создаем поля для ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Имя");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Должность");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Должность:"), 0, 1);
        grid.add(jobTitleField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Преобразуем результат в пару значений
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nameField.getText(), jobTitleField.getText());
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        dialog.showAndWait().ifPresent(result -> {
            String name = result.getKey();
            String jobTitle = result.getValue();

            if (name != null && !name.trim().isEmpty()) {
                Teacher newTeacher = new Teacher(0, name, jobTitle);
                if (TeacherRepo.addTeacher(newTeacher)) {
                    teacherList.setAll(TeacherRepo.getAllTeachers()); // Обновляем список
                } else {
                    showAlert("Ошибка", "Не удалось добавить учителя.");
                }
            } else {
                showAlert("Ошибка", "Имя учителя не может быть пустым.");
            }
        });
    }

    @FXML
    private void handleUpdateTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showAlert("Ошибка", "Выберите учителя для редактирования.");
            return;
        }

        // Создаем диалоговое окно
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Редактировать учителя");
        dialog.setHeaderText("Измените данные учителя");

        // Устанавливаем кнопки
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Создаем поля для ввода с текущими значениями
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selectedTeacher.getName());
        TextField jobTitleField = new TextField(selectedTeacher.getJobTitle());

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Должность:"), 0, 1);
        grid.add(jobTitleField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Преобразуем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nameField.getText(), jobTitleField.getText());
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        dialog.showAndWait().ifPresent(result -> {
            String name = result.getKey();
            String jobTitle = result.getValue();

            if (name != null && !name.trim().isEmpty()) {
                selectedTeacher.setName(name);
                selectedTeacher.setJobTitle(jobTitle);
                if (TeacherRepo.updateTeacher(selectedTeacher)) {
                    teacherList.setAll(TeacherRepo.getAllTeachers()); // Обновляем список
                } else {
                    showAlert("Ошибка", "Не удалось обновить данные учителя.");
                }
            } else {
                showAlert("Ошибка", "Имя учителя не может быть пустым.");
            }
        });
    }

    @FXML
    private void handleDeleteTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showAlert("Ошибка", "Выберите учителя для удаления.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText("Вы уверены, что хотите удалить этого учителя?");
        confirmation.setContentText(selectedTeacher.getName());

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (TeacherRepo.deleteTeacher(selectedTeacher.getId())) {
                teacherList.setAll(TeacherRepo.getAllTeachers()); // Обновляем список
            } else {
                showAlert("Ошибка", "Не удалось удалить учителя.");
            }
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("group_list.fxml"));
        Parent root = loader.load();

        // Получение текущего Stage
        Stage stage = (Stage) teacherTable.getScene().getWindow();

        // Установка новой сцены
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}