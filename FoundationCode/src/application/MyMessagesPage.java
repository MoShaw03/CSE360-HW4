package application;

import java.sql.SQLException;
import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Controller and/or Class page that displays a user's messages and allows composing/replying.
 * 
 * The page queries the provided {@link DatabaseHelper} for messages belonging to the provided {@link User} (via {@code readUserMessages}). 
 * Messages are presented in a {@link TableView} on the right side, while the left side shows the conversation the selected message. 
 * The page provides buttons for composing a new message, replying to a selected message, and returning to the homepage based on the user's role.
 *
 */

public class MyMessagesPage {
	private Messages myMessages = new Messages();
	
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	public MyMessagesPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
	public void show(Stage primaryStage){
		
		try {
			myMessages = databaseHelper.readUserMessages(currentUser);
		}
		catch(Exception e) {
			System.out.println("No messages for you ):");
		}
		
			
		/*
		 * Sets up the JavaFX panes
		 * 
		 */
		
		/*
		 * The right side of the screen shows the list of messages, while the left side shows
		 * each individual messages chain
		 */
		
		
		BorderPane root = new BorderPane();
		VBox messageDisplay = new VBox();
		messageDisplay.setStyle("-fx-alignment: center;");
		HBox leftandright;
		Label messageText = new Label();
		messageText.setStyle("-fx-font-size: 18px; -fx-text-alignment: center;");
		messageText.setText("");
		messageDisplay.getChildren().addAll(messageText);
		
		TableView<Message> messageTable = new TableView<>();
		if (myMessages != null) {
		    messageTable.setItems(myMessages.getMessageList());
		}

		
    	TableColumn<Message, LocalDateTime> messageDateColumn = new TableColumn<Message, LocalDateTime>("Date");
    	messageDateColumn.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("date"));
    	messageTable.getColumns().add(messageDateColumn);  
    	TableColumn<Message, String> messageSenderColumn = new TableColumn<Message, String>("From");
    	messageSenderColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("sender"));
    	messageTable.getColumns().add(messageSenderColumn);  
    	TableColumn<Message, String> messageReceiverColumn = new TableColumn<Message, String>("To");
    	messageReceiverColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("receiver"));
    	messageTable.getColumns().add(messageReceiverColumn);  
    	TableColumn<Message, String> messageBodyColumn = new TableColumn<Message, String>("Message");
    	messageBodyColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("body"));
    	messageTable.getColumns().add(messageBodyColumn);  
    	messageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    	
    	leftandright = new HBox(messageDisplay, messageTable);
    	root.setCenter(leftandright);
    	
    	//Listener for question table
    	messageTable.getSelectionModel().selectedItemProperty().addListener(
    		    (obs, oldSelection, newSelection) -> {
		    		if (newSelection != null) {	
		    			messageText.setText(newSelection.getBody());
		    			
		    			messageDisplay.getChildren().clear();
		    			ObservableList<Message> messies = databaseHelper.readMessageChain(newSelection).getMessageList();
		    			if (messies.isEmpty()) {
		    				System.out.println("TEMP FIX: go back to the homepage then return to see the updated message chain");
		    			}
		    			
		    			while (!messies.isEmpty()) {
		    				Message mess = messies.removeFirst();
		    				Label messageTextWhile = new Label(mess.getDateFormatted("casual") + " " + mess.getSender() + ": " + mess.getBody());
		    				messageDisplay.getChildren().addAll(messageTextWhile);
		    			} 
		    						
		    		}         
    		    }
    		);
    	
    	Button newMessageButton = new Button("New Message");
		newMessageButton.setOnAction(a -> {
 	    	newMessageDialogue();
 	    });
		
		Button replyMessageButton = new Button("Reply To Message");
		replyMessageButton.setOnAction(a -> {
			Message selected_message = messageTable.getSelectionModel().getSelectedItem();
			if(selected_message != null) {
				replyToMessageDialogue(selected_message);
			}
			else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Selection Required");
		        alert.setHeaderText(null);
		        alert.setContentText("Please select a message you want to reply to!");
		        alert.showAndWait();
			}
		});
    	
    	Button backButton = new Button("Back");
 	    backButton.setOnAction(a -> {
 	    	switch(currentUser.getRole()) {
 	    	case "admin":
 	    		new AdminHomePage(databaseHelper, currentUser).show(primaryStage);
 	    		break;
 	    	//TODO: add access to instructor and reviewerhomepags here
 	    	case "instructor":
 	    	case "reveiwer":
 	    	case "student":
 	    	case "user":
 	    		new UserHomePage(databaseHelper, currentUser).show(primaryStage);
 	    	}
 	    });
    	
 	    HBox buttonBar = new HBox(5);
 	    buttonBar.getChildren().addAll(newMessageButton, replyMessageButton, backButton);
 	    root.setTop(buttonBar);
 	    
		
        messageDisplay.setMaxWidth(Double.MAX_VALUE);
        messageTable.setMaxWidth(Double.MAX_VALUE);
        messageDisplay.prefWidthProperty().bind(leftandright.widthProperty().divide(2));
        messageTable.prefWidthProperty().bind(leftandright.widthProperty().divide(2));
	
		
		
		
    	Scene scene = new Scene(root, 1200, 800);
    	
    	
    	primaryStage.setScene(scene);
	    primaryStage.setTitle("Questions Page");
    	primaryStage.show();  
    	
	
	}
	
	   /**
     * Open a small dialog to compose a new message.
     * The dialog collects a receiver username and message then creates a {@link Message} with the current user's username as sender.
     */
	private void newMessageDialogue() {
        Stage dialog = new Stage();
        dialog.setTitle("Compose a message");

        TextField receiverField = new TextField();
        receiverField.setPromptText("Enter the username of the person you'd like to message");
        
        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter your message here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	String username = currentUser.getUserName();
            String receiver = receiverField.getText();
            String body = bodyField.getText();

            if (!username.isEmpty() && !receiver.isEmpty() && !body.isEmpty()) {
            	Message mess = new Message(LocalDateTime.now(), currentUser.getUserName(), receiver, body, null);
                myMessages.addMessage(mess);        
            	databaseHelper.createMessage(mess);        
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, receiverField, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");
        

        Scene scene = new Scene(layout, 250, 250);
        dialog.setScene(scene);
        dialog.show();
    }
	
	  /**
     * Open a dialog to reply to an existing message. The reply dialog lets the current user enter text. 
     * Once done, a reply {@link Message} is created with the original message's sender as receiver
     *
     * @param originalMessage the message being replied to.
     */
	private void replyToMessageDialogue(Message originalMessage) {
        Stage dialog = new Stage();
        dialog.setTitle("Reply to " + originalMessage.getSender());

        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter your message here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	String username = currentUser.getUserName();
            String receiver = originalMessage.getSender();
            String body = bodyField.getText();

            if (!body.isEmpty()) {
            	Message mess = new Message(LocalDateTime.now(), username, receiver, body, originalMessage);
                myMessages.addMessage(mess);
                myMessages.removeMessage(originalMessage);
                databaseHelper.createMessage(mess);
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");
        

        Scene scene = new Scene(layout, 250, 250);
        dialog.setScene(scene);
        dialog.show();
    }
}
