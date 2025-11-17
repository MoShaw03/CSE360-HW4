package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import java.sql.SQLException;

import databasePart1.*;

public class PasswordResetPage {
	public void show(DatabaseHelper databaseHelper, Stage primaryStage, String userName) {
		// implement page for creating a new password 
		TextField passwordField = new TextField();
		passwordField.setPromptText("Enter your new password");
		passwordField.setMaxWidth(250);
		
		TextField confirmField = new TextField();
		confirmField.setPromptText("Re-Enter your new password");
		confirmField.setMaxWidth(250);
		
		Button enterButton = new Button("Set");
		enterButton.setOnAction(a -> {
			boolean equal = false;
			if (passwordField.getText().equals(confirmField.getText())) {
				equal = true;
			}
			
			if (equal == true) {
				// update new user with new password
				databaseHelper.setNewPassword(passwordField.getText(), userName);
				new UserLoginPage(databaseHelper).show(primaryStage);
			} else {
				System.out.println("New passwords do not match.");
				new PasswordResetPage();
			}
			// once password is updated for user, force re-login
	});
		
		VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(passwordField, confirmField, enterButton);
		
		primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Password Reset");
        primaryStage.show();
		
	}
}