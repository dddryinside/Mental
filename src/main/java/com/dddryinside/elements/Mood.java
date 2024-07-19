package com.dddryinside.elements;

import com.dddryinside.DTO.MoodDTO;
import com.dddryinside.contracts.Widget;
import com.dddryinside.service.DataBaseAccess;
import com.dddryinside.service.PageManager;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class Mood extends VBox implements Widget {
    private SuperLabel moodInputLabel;
    private final Spinner<Integer> moodInputSpinner = new Spinner<>();
    private final Hyperlink saveButton = new Hyperlink("Сохранить");
    private final VBox moodChart = new VBox();
    private final VBox avgValuesBox = new VBox();

    public Mood() {
        getAvgValuesBox();

        if (!DataBaseAccess.isCurrentMoodExist() && isEveningTime()) {
            moodInputLabel = new SuperLabel("Оцените ваше настроение сегодня");
            moodInputSpinner.setDisable(false);
            saveButton.setDisable(false);
        } else {
            moodInputLabel = new SuperLabel("Оценка настроения будет доступна позже");
            moodInputSpinner.setDisable(true);
            saveButton.setDisable(true);
        }

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        moodInputSpinner.setValueFactory(valueFactory);
        moodInputSpinner.setEditable(true);

        saveButton.setOnAction(event -> saveResult(moodInputSpinner.getValueFactory().getValue()));
        VBox assessmentBox = new VBox(moodInputLabel, moodInputSpinner, saveButton);
        assessmentBox.setSpacing(10);

        getMoodChart();

        this.getChildren().addAll(avgValuesBox, moodChart, assessmentBox);
        VBox.setMargin(this, new Insets(3, 0, 0, 0));
        this.setSpacing(20);
        this.setMinWidth(330);
    }

    private void getAvgValuesBox() {
        avgValuesBox.getChildren().clear();

        SuperLabel title = new SuperLabel("Среднее настроение");
        title.makeTitle();

        double avgMood = DataBaseAccess.getAverageMood();
        SuperLabel avgMoodLabel = new SuperLabel(String.valueOf(avgMood));
        avgMoodLabel.makeTitle();
        defineColor(avgMoodLabel, avgMood);

        double avgMonthMood = DataBaseAccess.getAverageMood(30);
        SuperLabel avgMonthMoodLabel = new SuperLabel(String.valueOf(avgMonthMood));
        avgMonthMoodLabel.makeTitle();
        defineColor(avgMonthMoodLabel, avgMonthMood);

        GridPane moodTable = new GridPane();
        moodTable.setHgap(10);
        moodTable.add(new Label("За всё время:"), 0, 0);
        moodTable.add(avgMoodLabel , 1, 0);
        moodTable.add(new Label("За месяц:") , 0, 1);
        moodTable.add(avgMonthMoodLabel , 1, 1);

        Hyperlink detailsButton = new Hyperlink("Подробнее");
        VBox.setMargin(detailsButton, new Insets(10, 0, 0, 0));

        avgValuesBox.getChildren().addAll(title, moodTable, detailsButton);
    }

    public void getMoodChart() {
        moodChart.getChildren().clear();
        List<MoodDTO> moodHistory = DataBaseAccess.getMoodHistory(7);
        Collections.reverse(moodHistory);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(11);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                int value = object.intValue();
                return value <= 10 ? String.valueOf(value) : "";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("График настроения за 7 дней");

        for (int i = 0; i < moodHistory.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + moodHistory.get(i).getShortStringDate(),
                    moodHistory.get(i).getMood()));
        }

        lineChart.getData().add(series);
        lineChart.setMaxWidth(330);
        lineChart.setMaxHeight(280);
        lineChart.setPadding(new Insets(0, 0, 0, -5));

        moodChart.getChildren().add(lineChart);
    }

    private void saveResult(Integer value) {
        if (value == null || value < 0 || value > 10) {
            PageManager.showNotification("Оцените настроение от 0 до 10!");
        } else {
            DataBaseAccess.saveMood(value);

            getMoodChart();
            getAvgValuesBox();
        }
    }

    private void defineColor(SuperLabel label, double value) {
        if (value != 0) {
            if (value > 7) {
                label.makeGreen();
            } else if (value > 5) {
                label.makeYellow();
            } else if (value != 0){
                label.makeRed();
            }
        }
    }

    @Override
    public void onEveningReached() {
        moodInputLabel = new SuperLabel("Оцените ваше настроение сегодня");
        moodInputSpinner.setDisable(false);
        saveButton.setDisable(false);
    }

    @Override
    public void onMidnightReached() {
        moodInputLabel = new SuperLabel("Оценка настроения будет доступна позже");
        moodInputSpinner.setDisable(true);
        saveButton.setDisable(true);
    }

    private static boolean isEveningTime() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(19, 59);
        LocalTime endTime = LocalTime.of(23, 59);

        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
