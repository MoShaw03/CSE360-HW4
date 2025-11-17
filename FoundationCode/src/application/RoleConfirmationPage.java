package application;

import databasePart1.*;


/**
 * Confirmation page for an instructor to confirm or reject a student's request to become a reviewer.
 * 
 * This page displays the selected student's discussion contributions and provides three
 * actions such as "Approve Request", "Reject Request" or "Back".
 * Each action navigates back to {@code InstructorHomePage} after performing the database operations.
 */

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class RoleConfirmationPage {
	private DatabaseHelper databaseHelper;
	private Questions questions;
	private User currentUser;
	private String studentUser;
	
	public RoleConfirmationPage(DatabaseHelper databaseHelper, User currentUser, String studentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        this.studentUser = studentUser;
    }
	
	public void show(Stage primaryStage) {
		VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		
	    Label title = new Label(studentUser + "'s Discussion Contributions:");
	    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // adds every contribution of a user to a listView
	    ListView<String> list = new ListView<>();
	    list.getItems().setAll(databaseHelper.getContributions(studentUser));
	    
	    Button approveButton = new Button("Approve Request");
	    approveButton.setOnAction(e -> {
	    	// update students role to reviewer
	    	databaseHelper.updateRole(studentUser, "reviewer");
	    	// databaseHelper function to remove request ie set "requesting=FALSE"
	    	databaseHelper.removeRequest(studentUser);
	    	new InstructorHomePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    Button rejectButton = new Button("Reject Request");
	    rejectButton.setOnAction(e -> {
	    	// databaseHelper function to remove request ie set "requesting=FALSE"
	    	databaseHelper.removeRequest(studentUser);
	    	new InstructorHomePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    // to be able to return to the Instructor Homepage
	    Button backButton = new Button("Back");
	    backButton.setOnAction(e -> {
	    	new InstructorHomePage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
		layout.getChildren().addAll(title, list, approveButton, rejectButton, backButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Reviewer Confirmation Page");
	}
}