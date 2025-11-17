package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.List;
import java.util.Scanner;

import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;
	private Scanner sc;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome! Please select the role you wish to log in as:");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    layout.getChildren().add(welcomeLabel);
	    
	    List<String> roles = user.getRoleList();
	    if(roles.contains("user") || roles.contains("admin"))  {
	    	Button userButton = new Button("User");
	 	    userButton.setOnAction(a -> {
	 	    	new UserHomePage(databaseHelper,user).show(primaryStage);
	 	    });
	 	    layout.getChildren().add(userButton);
	    }
	    if(roles.contains("admin")) {
	    	Button adminButton = new Button("Admin");
	 	    adminButton.setOnAction(a -> {
	 	    	new AdminHomePage(databaseHelper,user).show(primaryStage);
	 	    });
	 	   layout.getChildren().add(adminButton);
	    }
	    /*
	     * These roles don't have a page yet, and are just placeholders for now
	     * 
	    if(roles.contains("student") || roles.contains("admin"))  {
	    	Button userButton = new Button("Student");
	 	    userButton.setOnAction(a -> {
	 	    	new UserHomePage(databaseHelper).show(primaryStage);
	 	    });
	 	    layout.getChildren().add(userButton);
	    }
		*/
	    if(roles.contains("instructor") || roles.contains("admin"))  {
	    	Button userButton = new Button("Instructor");
	 	    userButton.setOnAction(a -> {
	 	    	new InstructorHomePage(databaseHelper,user).show(primaryStage);
	 	    });
	 	    layout.getChildren().add(userButton);
	    }
		/*
	    if(roles.contains("staff") || roles.contains("admin"))  {
	    	Button staffButton = new Button("Staff");
	 	    staffButton.setOnAction(a -> {
	 	    	new UserHomePage(databaseHelper).show(primaryStage);
	 	    });
	 	    layout.getChildren().add(staffButton);
	    }
	    */
	    if(roles.contains("reviewer") || roles.contains("admin")) {
	    	Button reviewerButton = new Button("Reviewer");
	 	    reviewerButton.setOnAction(a -> {
	 	    	new UserHomePage(databaseHelper, user).show(primaryStage);
	 	    });
	 	    layout.getChildren().add(reviewerButton);
	    }
	    
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    
	    
	    
	    
	    Button logoutButton = new Button("Logout");
 	    logoutButton.setOnAction(a -> {
 	    	new SetupLoginSelectionPage(databaseHelper,"Signed out successfully!").show(primaryStage);
 	    });

	    layout.getChildren().addAll(logoutButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}
