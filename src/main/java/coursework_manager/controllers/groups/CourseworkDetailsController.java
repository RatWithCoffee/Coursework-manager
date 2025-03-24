package coursework_manager.controllers.groups;

import coursework_manager.models.CourseworkRecord;
import coursework_manager.models.Mark;
import coursework_manager.repos.MarkRepo;
import coursework_manager.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;
import java.util.Objects;

public class CourseworkDetailsController {

    @FXML
    private TableView<Mark> marksTable;

    @FXML
    private TableColumn<Mark, String> studentNameColumn;

    @FXML
    private TableColumn<Mark, Integer> markColumn;


    private CourseworkRecord courseworkRecord;

    public void setCourseworkRecord(CourseworkRecord record) {
        this.courseworkRecord = record;
        loadMarks();
    }

    private void loadMarks() {
        // Получаем данные из базы данных через репозитории
        List<Mark> marks = MarkRepo.getMarksByCourseworkRecordId(courseworkRecord.getCoursework().getId(), courseworkRecord.getGroup().getId());

        // Заполняем таблицу данными
        ObservableList<Mark> data = FXCollections.observableArrayList(marks);
        marksTable.setItems(data);

        // Настраиваем редактирование оценок
        marksTable.setEditable(true);
        markColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter() {
            @Override
            public Integer fromString(String value) {
                try {
                    if (value == null || Objects.equals(value.trim(), "")) {
                        return 0;
                    }
                    int mark = super.fromString(value);
                    if (mark < 2 || mark > 5) {
                        AlertUtils.showAlert("Ошибка", "Недопустимая оценка");
                        return null;
                    }
                    return mark;
                } catch (NumberFormatException e) {
                    AlertUtils.showAlert("Ошибка", "Введите число от 2 до 5");
                    return null; // Возвращаем null, чтобы отменить редактирование
                }
            }
        }));

        markColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Mark, Integer>>() {
            @Override
            public void handle(CellEditEvent<Mark, Integer> event) {
                Mark mark = event.getRowValue();
                Integer newMark = event.getNewValue();
                if (newMark == null) {
                    return;
                }

                if (newMark == 0) {
                    mark.setMark(null);
                } else {
                    mark.setMark(newMark);
                }

                updateMarkInDatabase(mark);
            }
        });
    }

    private void updateMarkInDatabase(Mark mark) {
        MarkRepo.updateMark(mark);
    }
}