package coursework_manager.controllers.groups;

import coursework_manager.models.CourseworkRecord;
import coursework_manager.models.Mark;
import coursework_manager.repos.MarkRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartsWindowController {
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label titleLabel;

    private Stage stage;
    private CourseworkRecord record;

    public void setCourseworkRecord(CourseworkRecord record) {
        this.record = record;
        updateCharts();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateCharts() {
        titleLabel.setText("Группа: " + record.getGroup().getName() +
                ", Курсовая: " + record.getCoursework().getName());

        // Получаем оценки студентов по этой курсовой работе
        List<Mark> marks = MarkRepo.getMarksByCourseworkRecordId(
                record.getCoursework().getId(),
                record.getGroup().getId()
        );

        // Считаем количество каждой оценки
        Map<Integer, Integer> markCounts = new HashMap<>();
        markCounts.put(2, 0);
        markCounts.put(3, 0);
        markCounts.put(4, 0);
        markCounts.put(5, 0);

        for (Mark mark : marks) {
            Integer markValue = mark.getMark();

            if (markValue == null) {
                continue;
            }
            markCounts.put(markValue, markCounts.get(markValue) + 1);
        }

        // Обновляем столбчатую диаграмму
        updateBarChart(markCounts);

        // Обновляем круговую диаграмму
        updatePieChart(markCounts);
    }

    private void updateBarChart(Map<Integer, Integer> markCounts) {
        barChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<Integer, Integer> entry : markCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(
                    String.valueOf(entry.getKey()),
                    entry.getValue()
            ));
        }

        barChart.getData().add(series);
    }

    private void updatePieChart(Map<Integer, Integer> markCounts) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<Integer, Integer> entry : markCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(
                    "Оценка " + entry.getKey(),
                    entry.getValue()
            ));
        }

        pieChart.setData(pieChartData);
    }

    @FXML
    private void closeWindow() {
        stage.close();
    }

    public static void show(CourseworkRecord record) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ChartsWindowController.class.getResource("charts_window.fxml")
            );
            Parent root = loader.load();

            ChartsWindowController controller = loader.getController();
            controller.setCourseworkRecord(record);

            Stage stage = new Stage();
            controller.setStage(stage);

            stage.setTitle("Статистика оценок");
            stage.setScene(new Scene(root, 600, 500));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}