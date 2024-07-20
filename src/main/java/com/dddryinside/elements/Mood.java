package com.dddryinside.elements;

import com.dddryinside.contracts.Page;
import com.dddryinside.contracts.Widget;
import com.dddryinside.pages.MoodStatPage;
import com.dddryinside.service.DataBaseAccess;
import com.dddryinside.service.PageManager;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class Mood extends VBox implements Widget {
    private SuperLabel moodInputLabel;
    private final Spinner<Integer> moodInputSpinner = new Spinner<>();
    private final Hyperlink saveButton = new Hyperlink(Page.localeRes.getString("save"));
    private final VBox moodChart = new VBox();
    private final VBox avgValuesBox = new VBox();

    public Mood() {
        getAvgValuesBox();

        if (!DataBaseAccess.isCurrentMoodExist() && isEveningTime()) {
            moodInputLabel = new SuperLabel(Page.localeRes.getString("rate_your_mood_today"));
            moodInputSpinner.setVisible(true);
            saveButton.setVisible(true);
        } else {
            moodInputLabel = new SuperLabel(Page.localeRes.getString("assessment_is_not_available"));
            moodInputSpinner.setVisible(false);
            saveButton.setVisible(false);
        }

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        moodInputSpinner.setValueFactory(valueFactory);
        moodInputSpinner.setEditable(true);

        saveButton.setOnAction(event -> saveResult(moodInputSpinner.getValueFactory().getValue()));
        VBox assessmentBox = new VBox(moodInputLabel, moodInputSpinner, saveButton);
        assessmentBox.setSpacing(10);

        getMoodChart();

        this.getChildren().addAll(avgValuesBox, moodChart, assessmentBox);
        this.setSpacing(20);
        this.setMinWidth(330);
    }

    private void getAvgValuesBox() {
        avgValuesBox.getChildren().clear();

        SuperLabel title = new SuperLabel(Page.localeRes.getString("avg_mood"));
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

        SuperLabel allTime = new SuperLabel(Page.localeRes.getString("for_30_days"));
        allTime.makeGrey();
        moodTable.add(allTime, 0, 0);
        moodTable.add(avgMoodLabel , 1, 0);

        SuperLabel month = new SuperLabel(Page.localeRes.getString("for_7_days"));
        month.makeGrey();
        moodTable.add(month, 0, 1);
        moodTable.add(avgMonthMoodLabel , 1, 1);

        Hyperlink detailsButton = new Hyperlink(Page.localeRes.getString("more"));
        detailsButton.setOnAction(event -> PageManager.loadPage(new MoodStatPage()));
        VBox.setMargin(detailsButton, new Insets(5, 0, 0, 0));

        avgValuesBox.getChildren().addAll(title, moodTable, detailsButton);
    }

    public void getMoodChart() {
        moodChart.getChildren().clear();
        List<com.dddryinside.models.Mood> moodHistory = DataBaseAccess.getMoodHistory(7);
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
        series.setName(Page.localeRes.getString("mood_chart_for_7_days"));


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
            PageManager.showNotification(Page.localeRes.getString("rate_your_mood_from_1_to_10"));
        } else {
            DataBaseAccess.saveMood(value);

            getMoodChart();
            getAvgValuesBox();

            moodInputLabel.setText(Page.localeRes.getString("assessment_is_not_available"));
            moodInputSpinner.setVisible(false);
            saveButton.setVisible(false);
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
        moodInputLabel = new SuperLabel(Page.localeRes.getString("rate_your_mood_today"));
        moodInputSpinner.setVisible(true);
        saveButton.setVisible(true);
    }

    @Override
    public void onMidnightReached() {
        moodInputLabel = new SuperLabel(Page.localeRes.getString("assessment_is_not_available"));
        moodInputSpinner.setVisible(false);
        saveButton.setVisible(false);
    }

    private static boolean isEveningTime() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(19, 59);
        LocalTime endTime = LocalTime.of(23, 59);

        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
