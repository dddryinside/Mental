package com.dddryinside.elements;

import com.dddryinside.models.User;
import com.dddryinside.service.SecurityManager;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.*;

public class Profile extends VBox {
    public Profile(User user) {
        SuperLabel fioLabel = new SuperLabel(user.getFio());
        fioLabel.setWrapText(true);
        fioLabel.makeBold();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(3);

        SuperLabel usernameTitle = new SuperLabel("Имя пользователя:");
        usernameTitle.makeInterfaceText();
        SuperLabel usernameValue = new SuperLabel(user.getUsername());
        usernameValue.makeInterfaceText();
        usernameValue.makePurple();
        gridPane.add(usernameTitle, 0, 0);
        gridPane.add(usernameValue , 1, 0);

        SuperLabel birthDayTitle = new SuperLabel("Дата рождения:");
        birthDayTitle.makeInterfaceText();
        SuperLabel birthDayValue = new SuperLabel(user.getStringBirthDate());
        birthDayValue.makeInterfaceText();
        gridPane.add(birthDayTitle, 0, 1);
        gridPane.add(birthDayValue , 1, 1);

        SuperLabel genderTitle = new SuperLabel("Пол:");
        genderTitle.makeInterfaceText();
        SuperLabel genderValue = new SuperLabel(user.getStringGender());
        genderValue.makeInterfaceText();
        gridPane.add(genderTitle, 0, 2);
        gridPane.add(genderValue , 1, 2);


        Hyperlink editProfileButton = new Hyperlink("Редактировать профиль");
        //editProfileButton.setOnAction(event -> PageManager.loadPage(new UpdateUserPage()));
        Hyperlink logOutButton = new Hyperlink("Выйти");
        logOutButton.setOnAction(event -> SecurityManager.logOut());

        HBox buttonsBox = new HBox(editProfileButton, logOutButton);
        buttonsBox.setSpacing(10);

        this.getChildren().addAll(fioLabel, gridPane, buttonsBox);
        this.setSpacing(5);
        this.setMaxWidth(350);
        this.setMinWidth(350);
        this.setWidth(350);

/*        Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
        this.setBackground(DEFAULT_BACKGROUND);*/
    }
}
