package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

import databasePart1.*;

public class InstructorHomePage {
	private DatabaseHelper databaseHelper;
	private User currentUser;
	private ArrayList<String> users;
	private String selectedUser;
	
	public InstructorHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
	public void show(Stage primaryStage) {
    	VBox mainContent = new VBox();
	    mainContent.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello Instructor
	    Label userLabel = new Label("Hello, Instructor!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
 	    
	    // creates listView for displaying users requesting
	    ListView<String> list = new ListView<>();
	    MultipleSelectionModel<String> selection = list.getSelectionModel();
	    
 	    // side panel setup
        Label panelLabel = new Label("Current Reviewer Requests:");
        VBox sidePanel = new VBox(10, panelLabel, list);
        sidePanel.setStyle("-fx-background-color: lightgray;");
        sidePanel.setPadding(new Insets(10));
        sidePanel.setPrefWidth(300);
        sidePanel.setVisible(false); 
        sidePanel.setManaged(false); 
        
        // will open side panel and populate with requesting students 
	    Button reviewerRequestButton = new Button("Reviewer Requests");
 	    reviewerRequestButton.setOnAction(a -> {
 	    	sidePanel.setVisible(!sidePanel.isVisible());
 	    	sidePanel.setManaged(!sidePanel.isManaged());
 	    	
 	    	users = databaseHelper.getAllRequesting();
 	    	
 	    	if (!users.isEmpty()) {
 	    		list.getItems().setAll(users);
 	    	}
 	    });
        
 	    // creates listener for when a user is selected in the side panel
 	    // will bring you to the list of contributions
 	    selection.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
 	    	if (newSelection != null) {
 	    		selectedUser = newSelection;
 	    		new RoleConfirmationPage(databaseHelper, currentUser, selectedUser).show(primaryStage);
 	    		
 	    	}
 	    });
 	    
 	    // root setup
        BorderPane root = new BorderPane();
        root.setCenter(mainContent);
        root.setRight(sidePanel);
 	    
 	    // Button to logout
 	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper,"Signed out successfully!").show(primaryStage);
	    });

	    mainContent.getChildren().addAll(userLabel, reviewerRequestButton, logoutButton);
	    Scene userScene = new Scene(root, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");
    	
    }
}