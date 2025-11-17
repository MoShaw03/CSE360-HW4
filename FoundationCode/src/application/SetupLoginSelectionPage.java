package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
	
    private final DatabaseHelper databaseHelper;
    private final String message;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.message = "";
    }
    
    public SetupLoginSelectionPage(DatabaseHelper databaseHelper, String message) {
        this.databaseHelper = databaseHelper;
        this.message = message;
    }

    public void show(Stage primaryStage) {
        
    	// This message appears when creating a new account or logging out
    	Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
        messageLabel.setText(message);
    	
    	// Buttons to select Login / Setup options that redirect to respective pages    	
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");
        
        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        loginButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(messageLabel, setupButton, loginButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
