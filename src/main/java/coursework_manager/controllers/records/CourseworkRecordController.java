package coursework_manager.controllers.records;

import coursework_manager.models.CourseworkRecord;
import coursework_manager.repos.RecordRepo;
import coursework_manager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CourseworkRecordController {

    @FXML
    private TableView<CourseworkRecord> courseworkRecordTable;

    @FXML
    private TableColumn<CourseworkRecord, String> courseworkTitle;

    @FXML
    private TableColumn<CourseworkRecord, String> teacherName;

    @FXML
    private TableColumn<CourseworkRecord, String> groupName;

    @FXML
    private Button addRecordButton;

    @FXML
    private Button deleteRecordButton;

    // Данные для таблицы
    private ObservableList<CourseworkRecord> courseworkRecords = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Привязка колонок к свойствам модели
        courseworkTitle.setCellValueFactory(new PropertyValueFactory<>("courseworkTitle"));
        teacherName.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        // Загрузка данных в таблицу
        loadCourseworkRecords();

        // Привязка данных к таблице
        courseworkRecordTable.setItems(courseworkRecords);
    }

    // Загрузка данных из базы данных
    private void loadCourseworkRecords() {
        // Очистка текущих данных
        courseworkRecords.clear();
//        courseworkRecords = FXCollections.observableArrayList(RecordRepo.getAllByGroup());
    }

    // Обработчик кнопки "Добавить запись"
    @FXML
    private void showAddWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_record.fxml"));
            Parent root = loader.load();

            // Получение контроллера для формы
            AddCourseworkRecordController controller = loader.getController();

            // Загрузка данных в ComboBox (преподаватели и группы)
            controller.loadTeachers();
            controller.loadGroups();
            controller.loadCourseworks();
            controller.setRecordsController(this);

            // Создание нового окна
            Stage stage = new Stage();
            stage.setTitle("Добавление записи");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Блокирует главное окно
            stage.showAndWait(); // Ожидание закрытия окна

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Ошибка", "Не удалось открыть окно добавления записи.");
        }
    }

    public void addRecord(CourseworkRecord record) {
        this.courseworkRecords.add(record);
    }



    // Обработчик кнопки "Удалить запись"
    @FXML
    private void handleDeleteRecord() {
        // Получение выбранной записи
        CourseworkRecord selectedRecord = courseworkRecordTable.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            AlertUtils.showAlert("Ошибка", "Выберите запись для удаления!");
            return;
        }

        // Логика удаления записи
        courseworkRecords.remove(selectedRecord);
        RecordRepo.remove(selectedRecord.getId());
    }

}
