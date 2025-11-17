package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import java.sql.SQLException;
import databasePart1.*;



public class AdminSetOTPPage {
	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	
	public AdminSetOTPPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
	public void show(Stage primaryStage) {
		TextField userField = new TextField();
		userField.setPromptText("Enter user to use OTP");
		userField.setMaxWidth(250);
		
		TextField otpField = new TextField();
		otpField.setPromptText("Set One-Time Password");
		otpField.setMaxWidth(250);
		
		Button setOTPButton = new Button("Set");
		setOTPButton.setOnAction(a -> {
			String userName = userField.getText();
			String otp = otpField.getText();
			
			if (otp.length() > 20) {
				System.out.println("One-time password must be 20 characters or less.");
				new AdminSetOTPPage(databaseHelper,currentUser).show(primaryStage);
			}
			
			databaseHelper.setOTP(otp, userName);
			
			new AdminHomePage(databaseHelper,currentUser).show(primaryStage);
		});
		Button backButton = new Button("Back");
 	    backButton.setOnAction(a -> {
 	    	new AdminHomePage(databaseHelper, currentUser).show(primaryStage);
 	    });
		
		VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userField, otpField, setOTPButton, backButton);
		
		primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Admin Set OTP");
        primaryStage.show();
	}
}