package com.dddryinside.elements;

import com.dddryinside.contracts.Page;
import com.dddryinside.models.User;
import com.dddryinside.service.SecurityManager;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.*;

public class Profile extends VBox {
    public Profile(User user) {
        SuperLabel fioLabel = new SuperLabel(user.getFio());
        fioLabel.setWrapText(true);
        fioLabel.makeBold();

        SuperLabel username = new SuperLabel("@" + user.getUsername());
        username.makeTitle();
        username.makeGrey();

        Hyperlink editProfileButton = new Hyperlink(Page.localeRes.getString("edit"));
        //editProfileButton.setOnAction(event -> PageManager.loadPage(new UpdateUserPage()));
        Hyperlink logOutButton = new Hyperlink(Page.localeRes.getString("exit"));
        logOutButton.setOnAction(event -> SecurityManager.logOut());

        HBox buttonsBox = new HBox(editProfileButton, logOutButton);
        buttonsBox.setSpacing(10);

        this.getChildren().addAll(fioLabel, username, buttonsBox);
        this.setMaxWidth(350);
        this.setMinWidth(350);
        this.setWidth(350);

/*        Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
        this.setBackground(DEFAULT_BACKGROUND);*/
    }
}
