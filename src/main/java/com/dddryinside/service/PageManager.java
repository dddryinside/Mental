package com.dddryinside.service;

import com.dddryinside.contracts.Page;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PageManager {
    private static Stage stage;
    public static void setStage(Stage stage) {
        PageManager.stage = stage;
    }

    public static void loadPage(Page page) {
        Scene scene = page.getInterface();
        scene.getStylesheets().add(ResourceManager.loadStyle("/styles/styles.css"));
        scene.getStylesheets().add(ResourceManager.loadStyle("/styles/fonts.css"));
        stage.setScene(scene);
        stage.show();
    }

    public static void setWindowSize(int height, int width) {
        stage.setMinHeight(height);
        stage.setMinWidth(width);
        stage.setHeight(height);
        stage.setWidth(width);
    }

    public static void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Page.localeRes.getString("message"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(ResourceManager.loadImage("/img/icon.png")));
        alert.showAndWait();
    }
}
