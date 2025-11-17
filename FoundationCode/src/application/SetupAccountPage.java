package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
    	Label errorUserNameLabel = new Label("");
 	    errorUserNameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight; -fx-text-alignment: center;");
 	    errorUserNameLabel.setTextFill(Color.RED);
 	    
 	    Label errorPasswordLabel = new Label("");
	    errorPasswordLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: -fx-text-alignment: center;");
	    errorPasswordLabel.setTextFill(Color.RED);
    	
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();
            String userNameCheck = UserNameRecognizer.checkForValidUserName(userName);		// Tests if the username is valid
            String passwordCheck = PasswordEvaluator.evaluatePassword(password);				// Tests if the password is valid
            String emailCheck = EmailRecognizer.checkForValidEmail(email);
            
            try {
            	errorUserNameLabel.setText(userNameCheck);
				errorPasswordLabel.setText(passwordCheck);
            	if (userNameCheck != "" || passwordCheck != "" || emailCheck != "") {							// Input validation for username and password
    				// Display the error message
    				System.out.println(userNameCheck);
    				
    				// Fetch the index where the processing of the input stopped
    				if (UserNameRecognizer.userNameRecognizerIndexofError <= -1) return;	// Should never happen
    				// Display the input line so the user can see what was entered		
    				System.out.println(userName);
    				// Display the line up to the error and the display an up arrow
    				System.out.println(userName.substring(0,UserNameRecognizer.userNameRecognizerIndexofError) + "\u21EB");   				
            	}
	            	// Check if the user already exists
            	else if(!databaseHelper.doesUserExist(userName)) {
            		// Validate the invitation code            		
            		if(databaseHelper.validateInvitationCode(code)) {            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, "user", email);
		                databaseHelper.register(user);		                
		             // Navigate to the Welcome Login Page
		                new SetupLoginSelectionPage(databaseHelper,"User registered successfully!").show(primaryStage);
            		}
            		else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		errorLabel.setText("This username is taken!!.. Please use another to setup an account");
            	}            	        			            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, emailField, inviteCodeField, setupButton, errorUserNameLabel, errorPasswordLabel, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
