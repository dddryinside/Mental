package com.dddryinside.controllers;

import com.dddryinside.service.PatientDTO;
import com.dddryinside.PageLoader;
import com.dddryinside.service.DataBaseAccess;
import com.dddryinside.service.AllTests;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class PatientPageController extends PageLoader {
    @FXML Label fio;
    @FXML Label birthDate;
    @FXML Label sex;
    @FXML VBox allResearches;

    private PatientDTO patient;

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
        updateUI();
    }

    private void updateUI() {
        if (patient != null) {

            fio.setText(patient.getFio());
            birthDate.setText("Дата рождения: " + patient.getStringBirthDate());
            sex.setText("Пол: " + patient.getStringSex());

            List<AllTests> tests = DataBaseAccess.getAllTestsOfPatient(patient);
            for (AllTests test : tests) {
                Hyperlink hyperlink = new Hyperlink(test.getFullName());
                hyperlink.setOnAction(event -> {
                    loadTestResultsPage(test, patient);
                });
                allResearches.getChildren().add(hyperlink);
            }
        }
    }
}
