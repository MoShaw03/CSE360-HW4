package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import java.sql.SQLException;

import databasePart1.*;

public class PasswordResetConfirmationPage {
	private final DatabaseHelper databaseHelper;
	
	public PasswordResetConfirmationPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	public void show(Stage primaryStage) {
		TextField userField = new TextField();
		userField.setPromptText("Enter your username");
		userField.setMaxWidth(250);
		
		TextField otpField = new TextField();
		otpField.setPromptText("Enter your one time passowrd");
		otpField.setMaxWidth(250);
		
		Button loginButton = new Button("Login");
		
		loginButton.setOnAction(a -> {
				String userOTP = otpField.getText();
				String adminOTP = databaseHelper.getOTP(userField.getText());
			
				if (userOTP.equals(adminOTP)) {
					databaseHelper.removeOTP(userField.getText());
					
					new PasswordResetPage().show(databaseHelper, primaryStage, userField.getText());
				} 
		});
		
		VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userField, otpField, loginButton);
		
		primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("One Time Password Confirmation");
        primaryStage.show();
	}
}