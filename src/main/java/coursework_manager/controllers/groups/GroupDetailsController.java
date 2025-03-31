package coursework_manager.controllers.groups;

import coursework_manager.controllers.groups.CourseworkDetailsController;
import coursework_manager.models.CourseworkRecord;
import coursework_manager.models.Group;
import coursework_manager.repos.RecordRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import coursework_manager.models.Teacher;
import coursework_manager.repos.TeacherRepo;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupDetailsController {

    @FXML
    private TableView<CourseworkRecord> courseworkTable;

    private Group group;

    public void setGroup(Group group) {
        this.group = group;
        loadCourseworkRecords();
    }

    private void loadCourseworkRecords() {
        // Получаем данные из базы данных через репозитории
        List<CourseworkRecord> records = RecordRepo.getAllByGroup(group.getId());

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
        List<Teacher> teachers = TeacherRepo.getAllTeachers();
        List<String> teacherNames = teachers.stream()
                .map(Teacher::getName)
                .collect(Collectors.toList());

        // Создаем диалог выбора преподавателя
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedRecord.getTeacherName(), teacherNames);
        dialog.setTitle("Изменить преподавателя");
        dialog.setHeaderText("Выберите нового преподавателя");
        dialog.setContentText("Преподаватель:");

        // Показываем диалог и ждем выбора пользователя
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(teacherName -> {
            // Находим выбранного преподавателя
            Teacher selectedTeacher = teachers.stream()
                    .filter(teacher -> teacher.getName().equals(teacherName))
                    .findFirst()
                    .orElse(null);

            if (selectedTeacher != null) {
                // Обновляем преподавателя в записи
                selectedRecord.setTeacher(selectedTeacher);
                // Сохраняем изменения в базе данных
                RecordRepo.edit(selectedRecord.getId(), selectedRecord.getCoursework().getId(), selectedTeacher.getId(), selectedRecord.getGroup().getId());
                // Обновляем таблицу
                courseworkTable.refresh();
            }
        });
    }

//    private void openCourseworkDetailsWindow(CourseworkRecord record) {
//        try {
//            var loader = new FXMLLoader(getClass().getResource("coursework_details.fxml"));
//            Parent root = loader.load();
//
//            CourseworkDetailsController controller = loader.getController();
//            controller.setCourseworkRecord(record);
//
//            Stage stage = new Stage();
//            stage.setTitle("Курсовая работа: " + record.getCoursework().getName());
//            stage.setScene(new Scene(root, 700, 600));
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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


}