package coursework_manager.controllers.teachers;

import coursework_manager.controllers.groups.GroupListController;
import coursework_manager.models.Teacher;
import coursework_manager.repos.ReposManager;
import coursework_manager.utils.AlertUtils;
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
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
        // Создаем диалоговое окно
        Dialog<Pair<String, String>> dialog = new Dialog<>();
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
                try {
                    if (ReposManager.getTeacherRepo().addTeacher(newTeacher)) {
                        teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers()); // Обновляем список
                    } else {
                        AlertUtils.showAlert("Ошибка", "Не удалось добавить преподавателя.");
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertUtils.showAlert("Ошибка", "Имя не может быть пустым.");
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
        Dialog<Pair<String, String>> dialog = new Dialog<>();
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
                try {
                    if (ReposManager.getTeacherRepo().updateTeacher(selectedTeacher)) {
                        teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers()); // Обновляем список
                    } else {
                        AlertUtils.showAlert("Ошибка", "Не удалось обновить данные преподавателя.");
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertUtils.showAlert("Ошибка", "Имя преподавателя не может быть пустым.");
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
            if (ReposManager.getTeacherRepo().deleteTeacher(selectedTeacher.getId())) {
                teacherList.setAll(ReposManager.getTeacherRepo().getAllTeachers()); // Обновляем список
            } else {
                AlertUtils.showAlert("Ошибка", "Не удалось удалить преподавателя.");
            }
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(GroupListController.class.getResource("group_list.fxml"));
        Parent root = loader.load();

        // Получение текущего Stage
        Stage stage = (Stage) teacherTable.getScene().getWindow();

        // Установка новой сцены
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
                if (values.length >= 2) {
                    String name = values[0].trim();
                    String jobTitle = values[1].trim();
                    teachers.add(new Teacher(name, jobTitle));
                }
            }
        }

        return teachers;
    }


}