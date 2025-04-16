package coursework_manager.controllers.teachers;

import coursework_manager.controllers.groups.GroupListController;
import coursework_manager.models.users.Teacher;
import coursework_manager.repos.ReposManager;
import coursework_manager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherListController {

    @FXML
    private TableView<Teacher> teacherTable;

    private ObservableList<Teacher> teacherList;

    @FXML
    public void initialize() throws RemoteException {
        // Инициализация таблицы данными из базы
        teacherList = FXCollections.observableArrayList(ReposManager.getTeacherRepo().getAllTeachers());
        teacherTable.setItems(teacherList);
    }

    @FXML
    private void handleAddTeacher() {
        // Создаем диалоговое окно с расширенной формой
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Добавить нового преподавателя");
        dialog.setHeaderText("Введите данные преподавателя");

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
        TextField loginField = new TextField();
        loginField.setPromptText("Логин");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Должность:"), 0, 1);
        grid.add(jobTitleField, 1, 1);
        grid.add(new Label("Логин:"), 0, 2);
        grid.add(loginField, 1, 2);
        grid.add(new Label("Пароль:"), 0, 3);
        grid.add(passwordField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Преобразуем результат в объект Teacher
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Teacher(
                        0,
                        nameField.getText(),
                        jobTitleField.getText(),
                        loginField.getText(),
                        passwordField.getText()
                );
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        Optional<Teacher> result = dialog.showAndWait();
        result.ifPresent(teacher -> {
            if (teacher.getName() != null && !teacher.getName().trim().isEmpty() &&
                    teacher.getLogin() != null && !teacher.getLogin().trim().isEmpty() &&
                    teacher.getPassword() != null && !teacher.getPassword().trim().isEmpty()) {

                try {
                    if (ReposManager.getTeacherRepo().addTeacher(teacher)) {
                        teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers());
                    } else {
                        AlertUtils.showAlert("Ошибка", "Не удалось добавить преподавателя. Возможно, логин уже занят.");
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertUtils.showAlert("Ошибка", "Все поля обязательны для заполнения.");
            }
        });
    }

    @FXML
    private void handleUpdateTeacher() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            AlertUtils.showAlert("Ошибка", "Выберите преподавателя для редактирования.");
            return;
        }

        // Создаем диалоговое окно
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Редактировать преподавателя");
        dialog.setHeaderText("Измените данные преподавателя");

        // Устанавливаем кнопки
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Создаем поля для ввода с текущими значениями
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selectedTeacher.getName());
        TextField jobTitleField = new TextField(selectedTeacher.getJobTitle());
        TextField loginField = new TextField(selectedTeacher.getLogin());
        TextField passwordField = new TextField();
        passwordField.setPromptText("Новый пароль (оставьте пустым, чтобы не менять)");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Должность:"), 0, 1);
        grid.add(jobTitleField, 1, 1);
        grid.add(new Label("Логин:"), 0, 2);
        grid.add(loginField, 1, 2);
        grid.add(new Label("Пароль:"), 0, 3);
        grid.add(passwordField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Преобразуем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Teacher updatedTeacher = new Teacher(
                        selectedTeacher.getUserId(),
                        nameField.getText(),
                        jobTitleField.getText(),
                        loginField.getText(),
                        passwordField.getText().isEmpty() ? selectedTeacher.getPassword() : passwordField.getText()
                );
                updatedTeacher.setUserId(selectedTeacher.getUserId());
                return updatedTeacher;
            }
            return null;
        });

        // Показываем диалог и обрабатываем результат
        dialog.showAndWait().ifPresent(updatedTeacher -> {
            if (updatedTeacher.getName() != null && !updatedTeacher.getName().trim().isEmpty() &&
                    updatedTeacher.getLogin() != null && !updatedTeacher.getLogin().trim().isEmpty()) {

                try {
                    if (ReposManager.getTeacherRepo().updateTeacher(updatedTeacher)) {
                        teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers());
                    } else {
                        AlertUtils.showAlert("Ошибка", "Не удалось обновить данные преподавателя.");
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertUtils.showAlert("Ошибка", "Имя и логин не могут быть пустыми.");
            }
        });
    }

    @FXML
    private void handleDeleteTeacher() throws RemoteException {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            AlertUtils.showAlert("Ошибка", "Выберите преподавателя для удаления.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText("Вы уверены, что хотите удалить этого преподавателя?");
        confirmation.setContentText(selectedTeacher.getName());

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (ReposManager.getTeacherRepo().deleteTeacher(selectedTeacher.getUserId())) {
                teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers());
            } else {
                AlertUtils.showAlert("Ошибка", "Не удалось удалить преподавателя.");
            }
        }
    }

    // Остальные методы остаются без изменений
    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(GroupListController.class.getResource("group_list.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) teacherTable.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleImportFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите CSV файл");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(teacherTable.getScene().getWindow());

        if (selectedFile != null) {
            try {
                List<Teacher> importedTeachers = parseCSVFile(selectedFile);
                int successCount = 0;

                for (Teacher teacher : importedTeachers) {
                    System.out.println(teacher);
                    if (ReposManager.getTeacherRepo().addTeacher(teacher)) {
                        successCount++;
                        teacherList.add(teacher);
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Импорт завершен");
                alert.setHeaderText(null);
                alert.setContentText(String.format("Успешно импортировано %d из %d преподавателей",
                        successCount, importedTeachers.size()));
                alert.showAndWait();

            } catch (IOException e) {
                AlertUtils.showAlert("Ошибка при чтении файла", e.getMessage());
            }
        }
    }

    private List<Teacher> parseCSVFile(File csvFile) throws IOException {
        List<Teacher> teachers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String name = values[0].trim();
                    String jobTitle = values[1].trim();
                    String login = values[2].trim();
                    String password = values[3].trim();
                    teachers.add(new Teacher(name, jobTitle, login, password));
                }
            }
        }

        return teachers;
    }
}