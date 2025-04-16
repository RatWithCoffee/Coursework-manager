package coursework_manager.controllers.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import coursework_manager.models.*;
import coursework_manager.models.users.Role;
import coursework_manager.models.users.Teacher;
import coursework_manager.models.users.User;
import coursework_manager.repos.ReposManager;
import coursework_manager.storage.UserStorage;
import coursework_manager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.net.URI;

public class GroupDetailsController {

    public Button changeTeacherButton;
    public Button handleAddCw;
    @FXML
    private TableView<CourseworkRecord> courseworkTable;

    private Group group;

    private User user;
    List<CourseworkRecord> records;

    public void setGroup(Group group) {
        this.group = group;
    }


    public void init(Group group) {
        user = UserStorage.getUser();

        setGroup(group);
        loadCourseworkRecords();
    }


    private void loadCourseworkRecords() {
        if (user.getRole() == Role.TEACHER) {
            changeTeacherButton.setVisible(false);
        }
        if (user.isAdmin()) {
            handleAddCw.setVisible(false);
        }
        // Получаем данные из базы данных через репозитории
        try {
            if (user.getRole() == Role.TEACHER) {
                records = ReposManager.getRecordRepo().getAllByGroupAndTeacher(group.getId(), user.getUserId());
                System.out.println(records.size());
            } else {
                records = ReposManager.getRecordRepo().getAllByGroup(group.getId());
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Заполняем таблицу данными
        ObservableList<CourseworkRecord> data = FXCollections.observableArrayList(records);
        courseworkTable.setItems(data);

        // Добавляем обработчик событий для строк таблицы
        courseworkTable.setRowFactory(tv -> {
            TableRow<CourseworkRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    CourseworkRecord record = row.getItem();
                    openCourseworkDetailsWindow(record);
                }
            });
            return row;
        });
    }

    @FXML
    private void changeTeacher() {
        // Получаем выбранную запись из таблицы
        CourseworkRecord selectedRecord = courseworkTable.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            // Если запись не выбрана, показываем сообщение об ошибке
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Запись не выбрана");
            alert.setContentText("Пожалуйста, выберите запись из таблицы.");
            alert.showAndWait();
            return;
        }

        // Получаем список всех преподавателей
        List<Teacher> teachers = null;
        try {
            teachers = ReposManager.getTeacherRepo().getAllTeachers();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        List<String> teacherNames = teachers.stream()
                .map(Teacher::getName)
                .collect(Collectors.toList());

        // Создаем диалог выбора преподавателя
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedRecord.getTeacher().getName(), teacherNames);
        dialog.setTitle("Изменить преподавателя");
        dialog.setHeaderText("Выберите нового преподавателя");
        dialog.setContentText("Преподаватель:");

        // Показываем диалог и ждем выбора пользователя
        Optional<String> result = dialog.showAndWait();
        List<Teacher> finalTeachers = teachers;
        result.ifPresent(teacherName -> {
            // Находим выбранного преподавателя
            Teacher selectedTeacher = finalTeachers.stream()
                    .filter(teacher -> teacher.getName().equals(teacherName))
                    .findFirst()
                    .orElse(null);

            if (selectedTeacher != null) {
                // Обновляем преподавателя в записи
                selectedRecord.setTeacher(selectedTeacher);
                // Сохраняем изменения в базе данных
                try {
                    ReposManager.getRecordRepo().edit(selectedRecord.getId(), selectedRecord.getCoursework().getId(), selectedTeacher.getUserId(), selectedRecord.getGroup().getId());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                // Обновляем таблицу
                courseworkTable.refresh();
            }
        });
    }


    private void openCourseworkDetailsWindow(CourseworkRecord record) {
        try {
            var loader = new FXMLLoader(getClass().getResource("coursework_details.fxml"));
            Parent root = loader.load();

            CourseworkDetailsController controller = loader.getController();
            controller.setCourseworkRecord(record);

            Stage stage = new Stage();
            stage.setTitle("Курсовая работа: " + record.getCoursework().getName());
            stage.setScene(new Scene(root, 700, 600));
            stage.show();

            // Добавляем кнопку для просмотра графиков в окно деталей курсовой
//            controller.addChartsButton(() -> ChartsWindowController.show(record));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleAddCw(ActionEvent actionEvent) {
        User teacher = UserStorage.getUser();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с курсовыми работами");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (selectedFile == null) {
            return; // Пользователь отменил выбор файла
        }

        try {
            // Читаем JSON из файла
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(selectedFile);

            // Проверяем структуру JSON
            if (!rootNode.has("courseworks")) {
                AlertUtils.showAlert("Ошибка", "Неверный формат файла: отсутствует поле 'courseworks'");
                return;
            }

            // Создаем новый JSON объект для отправки
            ObjectNode requestNode = mapper.createObjectNode();
            requestNode.set("courseworks", rootNode.get("courseworks"));
            requestNode.put("group_id", group.getId());
            requestNode.put("teacher_id", teacher.getUserId());

            // Конвертируем в JSON строку
            String json = mapper.writeValueAsString(requestNode);
            System.out.println(json);

            // Отправляем на сервер
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/courseworks/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AlertUtils.showAlert("Ура", "Курсовые работы успешно добавлены из файла");
                 mapper = new ObjectMapper();
                CourseworkRecordDto resp = mapper.readValue(response.body(), CourseworkRecordDto.class);
                try {
                    // Получаем только новые записи (например, по timestamp или другим критериям)
                    // Это более эффективно, но требует поддержки на стороне сервера/репозитория
                    List<CourseworkRecord> newRecords = new ArrayList<>();
                    for (CourseworkDTO record : resp.getCourseworks()) {
                        CourseworkRecord cw = ReposManager.getRecordRepo().getById(record.getId());
                        newRecords.add(cw);
                    }

                    // Добавляем новые записи к существующим
                    courseworkTable.getItems().addAll(newRecords);
                    courseworkTable.refresh();

                } catch (RemoteException e) {
                    AlertUtils.showAlert("Ошибка", "Не удалось обновить список курсовых работ: " + e.getMessage());
                }
            } else {
                AlertUtils.showAlert("Не ура", "Ошибка при добавлении курсовых работ: " + response.body());
            }
        } catch (JsonProcessingException e) {
            AlertUtils.showAlert("Не ура","Ошибка обработки JSON: " + e.getMessage());
        } catch (IOException e) {
            AlertUtils.showAlert("Не ура", "Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            AlertUtils.showAlert("Не ура","Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Data
    public static class CourseworkRecordDto {
        private List<CourseworkDTO> courseworks;


        @JsonProperty("group_id")
        private int groupId;

        @JsonProperty("teacher_id")
        private int teacherId;

    }

    @Data
    public static class CourseworkDTO {
        private int id;
        private String title;

    }

}