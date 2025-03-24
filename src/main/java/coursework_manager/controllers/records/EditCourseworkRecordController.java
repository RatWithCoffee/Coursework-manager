package coursework_manager.controllers.records;

import coursework_manager.models.Coursework;
import coursework_manager.models.CourseworkRecord;
import coursework_manager.models.Group;
import coursework_manager.models.Teacher;
import coursework_manager.repos.CwRepo;
import coursework_manager.repos.GroupRepo;
import coursework_manager.repos.RecordRepo;
import coursework_manager.repos.TeacherRepo;
import coursework_manager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class EditCourseworkRecordController {

    // Поля, соответствующие элементам FXML
    @FXML
    private ComboBox<CourseworkRecord> recordComboBox;

    @FXML
    private ComboBox<Coursework> cwComboBox;

    @FXML
    private ComboBox<Teacher> teacherComboBox;

    @FXML
    private ComboBox<Group> groupComboBox;

    // Метод для загрузки списка записей
    public void loadRecords() {
        // Загрузка списка записей из базы данных
        List<CourseworkRecord> records = RecordRepo.getAll();
        System.out.println(records.size());
        recordComboBox.setItems(FXCollections.observableArrayList(records));
        recordComboBox.setConverter(new StringConverter<CourseworkRecord>() {
            @Override
            public String toString(CourseworkRecord record) {
                return String.format("Запись #%d: %s, %s, %s",
                        record.getId(),
                        record.getCoursework().getName(),
                        record.getTeacher().getName(),
                        record.getGroup().getName());
            }

            @Override
            public CourseworkRecord fromString(String string) {
                return null; // Не требуется для ComboBox
            }
        });

        // Обработчик выбора записи
        recordComboBox.setOnAction(event -> {
            CourseworkRecord selectedRecord = recordComboBox.getValue();
            if (selectedRecord != null) {
                // Заполнение полей выбранной записью
                cwComboBox.getSelectionModel().select(selectedRecord.getCoursework());
                teacherComboBox.getSelectionModel().select(selectedRecord.getTeacher());
                groupComboBox.getSelectionModel().select(selectedRecord.getGroup());
            }
        });
    }

    // Метод для загрузки списка курсовых работ
    public void loadCourseworks() {
        // Загрузка списка курсовых работ из базы данных
        List<Coursework> courseworks = CwRepo.getAllCourseworks();
        cwComboBox.setItems(FXCollections.observableArrayList(courseworks));
        cwComboBox.setConverter(new StringConverter<Coursework>() {
            @Override
            public String toString(Coursework coursework) {
                return coursework.getName(); // Предположим, что у Coursework есть метод getName()
            }

            @Override
            public Coursework fromString(String string) {
                return null; // Не требуется для ComboBox
            }
        });
    }

    // Метод для загрузки списка преподавателей
    public void loadTeachers() {
        // Загрузка списка преподавателей из базы данных
        List<Teacher> teachers = TeacherRepo.getAllTeachers();
        teacherComboBox.setItems(FXCollections.observableArrayList(teachers));
        teacherComboBox.setConverter(new StringConverter<Teacher>() {
            @Override
            public String toString(Teacher teacher) {
                return teacher.getName(); // Предположим, что у Teacher есть метод getName()
            }

            @Override
            public Teacher fromString(String string) {
                return null; // Не требуется для ComboBox
            }
        });
    }

    // Метод для загрузки списка групп
    public void loadGroups() {
        // Загрузка списка групп из базы данных
        List<Group> groups = GroupRepo.getAllGroups();
        groupComboBox.setItems(FXCollections.observableArrayList(groups));
        groupComboBox.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group group) {
                return group.getName(); // Предположим, что у Group есть метод getName()
            }

            @Override
            public Group fromString(String string) {
                return null; // Не требуется для ComboBox
            }
        });
    }

    // Обработчик нажатия на кнопку "Сохранить изменения"
    @FXML
    private void handleSaveChanges() {
        try {
            // Получение данных из формы
            CourseworkRecord selectedRecord = recordComboBox.getValue();
            Coursework selectedCoursework = cwComboBox.getValue();
            Teacher selectedTeacher = teacherComboBox.getValue();
            Group selectedGroup = groupComboBox.getValue();

            // Проверка на заполненность полей
            if (selectedRecord == null || selectedCoursework == null || selectedTeacher == null || selectedGroup == null) {
                AlertUtils.showAlert("Ошибка", "Все поля должны быть заполнены.");
                return;
            }

            // Обновление записи в базе данных
            RecordRepo.edit(selectedRecord.getId(), selectedCoursework.getId(), selectedTeacher.getId(), selectedGroup.getId());

            // Закрытие окна
            ((Stage) recordComboBox.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Ошибка", "Не удалось обновить запись.");
        }
    }

    // Обработчик нажатия на кнопку "Отмена"
    @FXML
    private void handleCancel() {
        // Закрытие окна без сохранения
        ((Stage) recordComboBox.getScene().getWindow()).close();
    }
}