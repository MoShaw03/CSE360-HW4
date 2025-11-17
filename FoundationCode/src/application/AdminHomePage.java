package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    public AdminHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
		Button setOTPButton = new Button("Set One-Time Password");
        setOTPButton.setOnAction(a -> {
            new AdminSetOTPPage(databaseHelper,currentUser).show(primaryStage);
        });
		
        Button discussionBoardButton = new Button("View Discussion Board");
	    discussionBoardButton.setOnAction(a -> {
 	    	new DiscussionBoardPage(databaseHelper,currentUser).show(primaryStage);
 	    });
        
	    Label adminLabel = new Label("Hello, " + currentUser.getUserName());
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");	   
	    
	    // "Invite" button for admin to generate invitation codes
	    
        Button inviteButton = new Button("Invite");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper,currentUser, primaryStage);
        });
        
        Button userListButton = new Button("List Of Users");
        userListButton.setOnAction(a -> {
        	AdminSetupPage setup = new AdminSetupPage(databaseHelper); // new helper instance
            setup.showAdminUserManagement(primaryStage, currentUser);                    // open manager UI
        });
        
        Button myMessagesButton = new Button("View My Messages");
	    myMessagesButton.setOnAction(a -> {
 	    	new MyMessagesPage(databaseHelper,currentUser).show(primaryStage);
 	    });
              	    
	    Button logoutButton = new Button("Logout");
 	    logoutButton.setOnAction(a -> {
 	    	new SetupLoginSelectionPage(databaseHelper,"Signed out successfully!").show(primaryStage);
 	    });

	    layout.getChildren().addAll(adminLabel,discussionBoardButton,myMessagesButton,userListButton,inviteButton,setOTPButton,logoutButton);

	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}
