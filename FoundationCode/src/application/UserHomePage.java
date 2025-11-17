package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	
	public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }


    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, " + currentUser.getUserName());
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// alert when a reviewer request is sent
	    Alert goodAlert = new Alert(AlertType.INFORMATION);	
        goodAlert.setTitle("Reviewer Request");
        goodAlert.setHeaderText("Successful Reviewer Request Sent!");
        goodAlert.setContentText("Please wait for your instructor to approve or deny your request.");
        
        // alert when a review request fails
        Alert failAlert = new Alert(AlertType.INFORMATION);	
        failAlert.setTitle("Reviewer Request");
        failAlert.setHeaderText("Un-successful Request!");
        failAlert.setContentText("Please re-load and try again.");

		// button to request to become a reviewer
	    Button reviewerRequestButton = new Button("Request to be a Reviewer");
	    reviewerRequestButton.setOnAction(a -> {
	    	databaseHelper.requestingToReviewer(currentUser.getUserName());
	    	
	    	if (databaseHelper.getRequesting(currentUser.getUserName()) == true) {
	    		goodAlert.show();
	    	}
	    	else {
	    		failAlert.show();
	    	}
 	    });
		
	    Button discussionBoardButton = new Button("View Discussion Board");
	    discussionBoardButton.setOnAction(a -> {
 	    	new DiscussionBoardPage(databaseHelper,currentUser).show(primaryStage);
 	    });
	    
	    Button myMessagesButton = new Button("View My Messages");
	    myMessagesButton.setOnAction(a -> {
 	    	new MyMessagesPage(databaseHelper,currentUser).show(primaryStage);
 	    });
	    
	    Button logoutButton = new Button("Logout");
 	    logoutButton.setOnAction(a -> {
 	    	new SetupLoginSelectionPage(databaseHelper,"Signed out successfully!").show(primaryStage);
 	    });

	    layout.getChildren().addAll(userLabel, discussionBoardButton, myMessagesButton, reviewerRequestButton, logoutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}
