package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import application.StaffHomePage;

public class DiscussionBoardPage{
	
	private Questions questionsMain = new Questions();
	
	private boolean searchingForQuestions = false;
	private boolean searchingForAnswers = false;
	
	private DatabaseHelper databaseHelper;
	private User currentUser;
	
	public DiscussionBoardPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
		
	public void show(Stage primaryStage){
		
		/*
		 * Sets up the JavaFX panes
		 * 
		 */
		System.out.println(currentUser.getRole());
		
		BorderPane root = new BorderPane();
		GridPane grid = new GridPane();
		
		// Makes each square in the 2x2 grid (about) equal in size
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);
		grid.getColumnConstraints().addAll(col1, col2);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(50);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(50);
		grid.getRowConstraints().addAll(row1, row2);

	
		// This sets up the content inside the grid, the tables are added into the BorderPanes where the tables are made
		VBox questionDisplay = new VBox();
			questionDisplay.setStyle("-fx-padding: 20;");
			questionDisplay.setAlignment(Pos.TOP_LEFT);
		VBox answerDisplay = new VBox();
			answerDisplay.setStyle("-fx-padding: 20;");
			answerDisplay.setAlignment(Pos.TOP_LEFT);
		BorderPane questionsTableLayout = new BorderPane();
		BorderPane answersTableLayout = new BorderPane();
		HBox buttonsLayout = new HBox(5);
		buttonsLayout.setStyle("-fx-padding: 4;");
		HBox searchLayout = new HBox(5);
		buttonsLayout.setStyle("-fx-padding: 4;");
		
		// Displays the question and answer selected
		Label questionUser = new Label();
		questionUser.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: left;");
    	Label questionTopic = new Label();
    	questionTopic.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-alignment: center;");
    	Label questionBody = new Label();
    	questionBody.setStyle("-fx-font-size: 16px;; -fx-text-alignment: left;");
    	questionDisplay.getChildren().addAll(questionUser,questionTopic,questionBody);	
    	
    	Label answerUser = new Label();
    	answerUser.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: left;");
    	Label answerBody = new Label();
    	answerBody.setStyle("-fx-font-size: 16px; -fx-text-alignment: left;");
		answerDisplay.getChildren().addAll(answerUser,answerBody);
		
		grid.add(questionsTableLayout, 0, 0);
		grid.add(questionDisplay, 1, 0);
		grid.add(answersTableLayout, 0, 1);
		grid.add(answerDisplay, 1, 1);
		
		root.setCenter(grid);
		root.setTop(buttonsLayout);
		root.setBottom(searchLayout);
		
		
		/*
		//This section populates the initial questions from the database
		*/
		try {
			int maxID = databaseHelper.maxQuestionIds();
			for(int i = 1; i <= maxID; i++) {
				Question tempQuest = new Question(LocalDateTime.now(), "", "", "", false);
				if(databaseHelper.readQuestion(i) != null) {
					tempQuest = databaseHelper.readQuestion(i);
					tempQuest.setAnswers(databaseHelper.readAnswer(tempQuest.getTopic()));
					
					// debugged section
					//System.out.println(tempQuest.getAnswers().getAnswer(0));
					//for (int j = 0; j < tempQuest.getAnswers().length(); j++) {
						//tempQuest.addAnswer(databaseHelper.readAnswer(tempQuest.getTopic()).getAnswerList().get(j));
					//}
					
					//for (int j = 0; j < databaseHelper.readAnswer(tempQuest.getTopic()).getAnswerList(); j++) {
						
					
					//if(!databaseHelper.readAnswer(tempQuest.getTopic()).getAnswerList().isEmpty()) {
						//tempQuest.addAnswer(databaseHelper.readAnswer(tempQuest.getTopic()).getAnswerList().get(j));
						//tempQuest.setAnswers(databaseHelper.readAnswer(tempQuest.getTopic()));
					//}
					//}
					// debugged section
					
					questionsMain.addQuestion(tempQuest);
				}									
			}
		}	catch (SQLException e) {
			e.printStackTrace();
		}		
		
		/*
		 * This section sets up the different tables
		*/		
	    Background defaultBG = new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5),new Insets(5)));
 	    Background highlightBG = new Background(new BackgroundFill(Color.web("#a8e6a1"), new CornerRadii(5),new Insets(5)));
		    	
    	TableView<Question> questionTable = new TableView<Question>(questionsMain.getQuestionList());
    	TableColumn<Question, LocalDateTime> questionDateColumn = new TableColumn<Question, LocalDateTime>("Date");
    	questionDateColumn.setCellValueFactory(new PropertyValueFactory<Question, LocalDateTime>("date"));
    	TableColumn<Question, String> questionUserColumn = new TableColumn<Question, String>("User");
    	questionUserColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("user"));
    	TableColumn<Question, String> questionTopicColumn = new TableColumn<Question, String>("Topic");
    	questionTopicColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("topic"));
    	questionTable.getColumns().addAll(questionDateColumn,questionUserColumn,questionTopicColumn);
    	questionsTableLayout.setCenter(questionTable);
    	
    	
    	TableView<Answer> answerTable = new TableView<Answer>();   	
    	answerTable.setEditable(true);
    	
    	TableColumn<Answer, LocalDate> answerDateColumn = new TableColumn<Answer, LocalDate>("Date");
    	answerDateColumn.setCellValueFactory(new PropertyValueFactory<Answer, LocalDate>("date"));
    	answerTable.getColumns().add(answerDateColumn);   	
    	TableColumn<Answer, User> answerUserColumn = new TableColumn<Answer, User>("User");
    	answerUserColumn.setCellValueFactory(new PropertyValueFactory<Answer, User>("user"));
    	answerTable.getColumns().add(answerUserColumn);   	
    	TableColumn<Answer, String> answerTopicColumn = new TableColumn<Answer, String>("Answer");
    	answerTopicColumn.setCellValueFactory(new PropertyValueFactory<Answer, String>("body"));
    	answerTable.getColumns().add(answerTopicColumn);
    	//This is for the helpful column, will implement later
    	
    	//TableColumn<Answer, Boolean> answerHelpfulColumn = new TableColumn<>("Helpful?");
    	//answerHelpfulColumn.setCellValueFactory(new PropertyValueFactory<>("helpful"));
    	//answerHelpfulColumn.setCellFactory(CheckBoxTableCell.forTableColumn(answerHelpfulColumn));
    	//answerHelpfulColumn.setPrefWidth(80);
    	//answerHelpfulColumn.setEditable(true);
    	//answerTable.getColumns().add(answerHelpfulColumn);
  	
    	answersTableLayout.setCenter(answerTable);
    	questionTable.setBackground(defaultBG);
    	answerTable.setBackground(defaultBG);
    	
    	/*
    	 *  These listeners are here so that the tables and questionDisplay and answerDisplay can update
    	 */
    	
    	//Listener for question table
    	questionTable.getSelectionModel().selectedItemProperty().addListener(
    		    (obs, oldSelection, newSelection) -> {
    		    		if (newSelection != null && !searchingForAnswers) {  		         
        		            ObservableList<Answer> relevantAnswers = newSelection.getAnswers().getAnswerList();  
        		            answerTable.setItems(relevantAnswers);      		            
        		        }   
    		    		if (newSelection != null) {
    		    			questionUser.setText("on " + newSelection.getDate("casual") + ", " + newSelection.getUser() + " asked: ");
        		            questionTopic.setText(newSelection.getTopic());
        		            questionBody.setText(newSelection.getBody());
        		            answerTable.getSelectionModel().clearSelection();
    		    		}
        		        if (oldSelection == null && newSelection == null) {
        		        	questionTopic.setText("");
        		        	questionBody.setText("");    		     
        		        	answerTable.setItems(FXCollections.observableArrayList());
        		        }  	      
    		    }
    		);
    	//Listener for answer table
    	answerTable.getSelectionModel().selectedItemProperty().addListener(
    		    (obs, oldSelection, newSelection) -> {
		    		if (newSelection != null) { 
		    			Question ansParent = newSelection.getQuestion();
    		        	answerUser.setText("and on " + newSelection.getDate("casual") + ", " + newSelection.getUser() + " replied: ");
    		        	answerBody.setText(newSelection.getBody());
    		        	questionTable.getSelectionModel().clearSelection();
    		        	
    		        	questionUser.setText("on " + ansParent.getDate("casual") + ", " + ansParent.getUser() + " asked: ");
    		            questionTopic.setText(ansParent.getTopic());
    		            questionBody.setText(ansParent.getBody());    		        	
    		        }
    		        else {
    		        	answerBody.setText("");
    		        } 	       
    		    }
    		);
    	   	
    	questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    	answerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);   	
    	
		/*
		 * This section sets up the buttons on the top of the screen
		 * 
		*/
		
		Button newQuestionButton = new Button("New Question");
		newQuestionButton.setOnAction(a -> newQuestionDialogue());
        
		Button newAnswerButton = new Button("New Answer");
		newAnswerButton.setOnAction(a ->{
			Question selected_question = questionTable.getSelectionModel().getSelectedItem();
		    if (selected_question == null) {
		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Selection Required");
		        alert.setHeaderText(null);
		        alert.setContentText("Please select a question you want to answer first!");
		        alert.showAndWait();
		    } 
		    else {   	
		        newAnswerDialogue(selected_question);
		    }	
		});
		// Added input validation so that only the user who made the post can edit it
		// Allows staff members to edit user's post. 
		Button updateButton = new Button("Edit");
		updateButton.setOnAction(a ->{
			Question selected_question = questionTable.getSelectionModel().getSelectedItem();
			Answer selected_answer = answerTable.getSelectionModel().getSelectedItem();
		    if (selected_question == null && selected_answer == null) {
		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Selection Required");
		        alert.setHeaderText(null);
		        alert.setContentText("Please select a question or answer you want to edit first!");
		        alert.showAndWait();
		    } 
		    else if (selected_question != null){
				if(selected_question.getUser().equals(currentUser.getUserName()) || currentUser.hasRole("staff")) {
					 editQuestionDialogue(selected_question);
				}
				else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
			        alert.setTitle("Incorrect User");
			        alert.setHeaderText(null);
			        alert.setContentText("Users are only allowed to edit their own posts!");
			        alert.showAndWait();
				} 
		    }
		    else if (selected_answer != null) {
		    	if(selected_answer.getUser().equals(currentUser.getUserName())) {
		    		editAnswerDialogue(selected_answer);
				}
				else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
			        alert.setTitle("Incorrect User");
			        alert.setHeaderText(null);
			        alert.setContentText("Users are only allowed to edit their own posts!");
			        alert.showAndWait();
				}
		    }
		});
		// Also made it so only the user who made the post can delete it
		// Staff members are allowed to delete and edit posts
		Button deleteButton = new Button ("Delete");
		deleteButton.setOnAction(a ->{
			Question selected_question = questionTable.getSelectionModel().getSelectedItem();
			Answer selected_answer = answerTable.getSelectionModel().getSelectedItem();
		    if (selected_question == null && selected_answer == null) {
		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Selection Required");
		        alert.setHeaderText(null);
		        alert.setContentText("Please select a question or answer you want to delete first!");
		        alert.showAndWait();
		    } 
		    else if (selected_question != null){
		    	if(selected_question.getUser().equals(currentUser.getUserName()) || currentUser.hasRole("staff")) {
		    		deleteDialogue(selected_question, null);
				}
				else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
			        alert.setTitle("Incorrect User");
			        alert.setHeaderText(null);
			        alert.setContentText("Users are only allowed to delete their own posts!");
			        alert.showAndWait();
				}
		    }
		    else if (selected_answer != null) {
		    	if(selected_answer.getUser().equals(currentUser.getUserName())) {
		    		deleteDialogue(null, selected_answer);
				}
				else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
			        alert.setTitle("Incorrect User");
			        alert.setHeaderText(null);
			        alert.setContentText("Users are only allowed to delete their own posts!");
			        alert.showAndWait();
				}
		    }
		});			
		Button backButton = new Button("Back");
 	    backButton.setOnAction(a -> {
 	    	switch(currentUser.getRole()) {
 	    	case "admin":
 	    		new AdminHomePage(databaseHelper, currentUser).show(primaryStage);
 	    		break;
 	    	case "student":
 	    	case "user":
 	    		new UserHomePage(databaseHelper, currentUser).show(primaryStage);
 	    	}
 	    });
		Button addReviewButton = new Button("New Review");
        addReviewButton.setOnAction(a -> {
            Answer selAns = answerTable.getSelectionModel().getSelectedItem();
            Question selQ = questionTable.getSelectionModel().getSelectedItem();
            if (selAns == null && selQ == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Selection Required");
                alert.setHeaderText(null);
                alert.setContentText("Please select a question or answer first!");
                alert.showAndWait();
                return;
            }
            TextInputDialog td = new TextInputDialog();
            td.setTitle("Write Review");
            td.setHeaderText("Enter your review text");
            td.setContentText("Body:");
            td.showAndWait().ifPresent(body -> {
                if (body == null || body.isBlank()) return;
                if (selAns != null) {
                    Review r = new Review(currentUser.getUserName(), body, selAns.getBody(), true);
                    databaseHelper.postReview(r);
                    openReviewsDialog(selAns.getBody(), true);
                } else {
                    Review r = new Review(currentUser.getUserName(), body, selQ.getTopic(), false);
                    databaseHelper.postReview(r);
                    openReviewsDialog(selQ.getTopic(), false);
                }
            });
        });
 	    
 	    Button trustedReviewsButton = new Button("Trusted Reviewers");
 	    trustedReviewsButton.setOnAction(a -> {
 	    	showTrustedReviewersList();
 	    });
 	    
 	    Button rateReviewerButton = new Button("Rate Reviewer");
 	    rateReviewerButton.setOnAction(a -> {
 	    	trustReviewerPopUpPage();
	    });
 	    
 	    /*
 	     * 	These are for the search bar
 	     * 
 	        questionTable.setRowFactory(_ -> {
	            TableRow<Question> row = new TableRow<>();
	            row.setStyle("-fx-background-color: #a8e6a1;");  // Set the background color for each row
	            return row;
	        });
	        
	        this code can be used if you just want to highlight the rows rather than delete them
 	     */
 	
 	    
 	    
 	    TextField searchBar = new TextField();
 	    searchBar.setPromptText("Enter text in your question");
 	    Button searchQuestions = new Button("Search In Questions");
 	    searchQuestions.setOnAction(a -> {
 	    	searchingForQuestions = true;
 	    	searchingForAnswers = false;
	    	questionTable.setItems(databaseHelper.searchQuestions(searchBar.getText()).getQuestionList());
	    	//questionTable.setBackground(highlightBG);	
	    	answerTable.setBackground(defaultBG);
	    });
	    Button searchAnswers = new Button("Search In Answers");
	    searchAnswers.setOnAction(a -> {
	    	searchingForQuestions = false;
	    	searchingForAnswers = true;
	    	answerTable.setItems(databaseHelper.searchAnswers(searchBar.getText()).getAnswerList());
	    	questionTable.setBackground(defaultBG);
	    	//answerTable.setBackground(highlightBG);
	    });
	    Button cancelSearch = new Button("Cancel");
	    cancelSearch.setOnAction(a -> {
	    	searchingForQuestions = false;
	    	searchingForAnswers = false;
	    	questionTable.setItems(questionsMain.getQuestionList());
	    	answerTable.setItems(null);
	    	questionTable.setBackground(defaultBG);
	    	answerTable.setBackground(defaultBG);
	    });
    	
    	buttonsLayout.getChildren().addAll(newQuestionButton, newAnswerButton, addReviewButton,updateButton, deleteButton, trustedReviewsButton, rateReviewerButton, backButton);       

        // Entry point for Staff moderation (hasRole("staff"))
        if (currentUser != null && currentUser.hasRole("staff")) {
            Button staffBtn = new Button("Staff Moderation");
            staffBtn.setTooltip(new javafx.scene.control.Tooltip("Moderate questions, answers, and reviews"));
            staffBtn.setOnAction(e -> new StaffHomePage(databaseHelper, currentUser).show(primaryStage));
            buttonsLayout.getChildren().add(staffBtn);
        }

    	searchLayout.getChildren().addAll(searchBar, searchQuestions, searchAnswers, cancelSearch);
        
	    Scene scene = new Scene(root, 1200, 800);
	    
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Questions Page");
    	primaryStage.show();  	      
    }
	
	/*
	 * Methods that are called by the buttons
	 */
	
	private void newQuestionDialogue() {
        Stage dialog = new Stage();
        dialog.setTitle("Ask a new question");

        TextField topicField = new TextField();
        topicField.setPromptText("Enter the topic of your question here");

        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter the body of your question here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	String username = currentUser.getUserName();
            String topic = topicField.getText();
            String body = bodyField.getText();

            if (!username.isEmpty() && !topic.isEmpty() && !body.isEmpty()) {
            	Question quest = new Question(LocalDateTime.now(),username,topic, body, false);
                questionsMain.addQuestion(quest);
                try {
            		databaseHelper.postQuestion(quest);
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, topicField, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");
        

        Scene scene = new Scene(layout, 250, 250);
        dialog.setScene(scene);
        dialog.show();
    }
	private void newAnswerDialogue(Question question) {
        Stage dialog = new Stage();
        dialog.setTitle("Answer a question");
        
        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter the body of your question here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	String username = currentUser.getUserName();
            String body = bodyField.getText();
            if (!username.isEmpty() && !body.isEmpty()) {
            	try {
            		databaseHelper.postReply(new Answer(LocalDateTime.now(),username, body, question, false));
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 250, 150);
        dialog.setScene(scene);
        dialog.show();
    }
	private void editQuestionDialogue(Question question) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit a post");
             
        TextField topicField = new TextField();
        topicField.setPromptText("Enter the new topic of your question here");
        topicField.setText(question.getTopic());

        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter the body of your question here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");
        bodyField.setText(question.getBody());
              
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	
            String topic = topicField.getText();
            String body = bodyField.getText();

            if (!topic.isEmpty() && !body.isEmpty()) {              	
            	Question editted_question = question;
            	editted_question.setDate(LocalDateTime.now());
            	editted_question.setTopic(topic);
            	editted_question.setBody(body);
            	
            	try {
             		databaseHelper.updateQuestion(topic, body, question.getTopic());
             	} catch (SQLException e) {
             		e.printStackTrace();
             	}
            	
            	questionsMain.removeQuestion(question);
            	questionsMain.addQuestion(editted_question);
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, topicField, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 250, 150);
        dialog.setScene(scene);
        dialog.show();
    }
	private void editAnswerDialogue(Answer answer) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit a post");
                   
        TextField bodyField = new TextField();
        bodyField.setPromptText("Enter the body of your question here");
        bodyField.setPrefHeight(200);
        bodyField.setStyle("-fx-alignment: top-left;");
        bodyField.setText(answer.getBody());
              
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {
        	
            String body = bodyField.getText();

            if (!body.isEmpty()) {
            	Answer edited_answer = answer;
            	edited_answer.setDate(LocalDateTime.now());
            	edited_answer.setBody(body);           
            	try {
            		databaseHelper.updateAnswer(edited_answer.getBody(), answer.getBody(), answer.getQuestion().getTopic());
             	} catch (SQLException e) {
             		e.printStackTrace();
             	}
            	
            	answer.getQuestion().getAnswers().removeAnswer(answer);
            	answer.getQuestion().getAnswers().addAnswer(edited_answer);
            	
            	
                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled!");
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, bodyField, confirmButton);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 250, 150);
        dialog.setScene(scene);
        dialog.show();
    }
	/*
	try {
 		
 	} catch (SQLException e) {
 		e.printStackTrace();
 	}
	*/
	private void deleteDialogue(Question question, Answer answer) {
		Stage dialog = new Stage();
        dialog.setTitle("Deletion Confirmation");

        Label deleteLabel = new Label();
        deleteLabel.setText("Are you sure you want to delete this post? This action cannot be undone!");
        deleteLabel.setWrapText(true);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(a -> {       	
            if(question != null) {
            	try {
            		databaseHelper.deleteQuestion(question.getTopic());
             	} catch (SQLException e) {
             		e.printStackTrace();
             	}
            	questionsMain.removeQuestion(question);
            }
            if(answer != null){
            	answer.getQuestion().getAnswers().removeAnswer(answer);
            	try {
            		databaseHelper.deleteAnswer(answer.getBody());
             	} catch (SQLException e) {
             		e.printStackTrace();
             	}
            }
            dialog.close();
        });
        Button denyButton = new Button("Cancel");
        denyButton.setOnAction(a -> {
        	dialog.close();
        });

        BorderPane root = new BorderPane();
        VBox labellayout = new VBox(10, deleteLabel);
        HBox yesandnobuttons = new HBox(10, confirmButton, denyButton);
        labellayout.setStyle("-fx-padding: 10; -fx-text-alignment: center;");
        yesandnobuttons.setStyle("-fx-padding: 10; -fx-text-alignment: center;");
        
        root.setCenter(labellayout);
        root.setBottom(yesandnobuttons);

        Scene scene = new Scene(root, 250, 150);
        dialog.setScene(scene);
        dialog.show();
	}

 /** openReviewsDialog.
   * @param replyId Question topic or answer body.
   * @param isAnswer True when target is an answer.
   */
	    private void openReviewsDialog(String replyId, boolean isAnswer) {
        Stage dialog = new Stage();
        dialog.setTitle("Reviews");

        TableView<Review> table = new TableView<>();
        TableColumn<Review, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Review, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        TableColumn<Review, String> bodyCol = new TableColumn<>("Body");
        bodyCol.setCellValueFactory(new PropertyValueFactory<>("body"));
        bodyCol.setPrefWidth(500);
        table.getColumns().addAll(idCol, userCol, bodyCol);

        table.setItems(FXCollections.observableArrayList(
                databaseHelper.readReviewsFor(replyId, isAnswer)
        ));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(a -> {
            Review r = table.getSelectionModel().getSelectedItem();
            if (r == null) return;
            if (!currentUser.getUserName().equals(r.getUserName())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You can only edit your own reviews.");
                alert.showAndWait();
                return;
            }
            TextInputDialog td = new TextInputDialog(r.getBody());
            td.setTitle("Edit Review");
            td.setHeaderText("Update your review");
            td.setContentText("Body:");
            td.showAndWait().ifPresent(newBody -> {
                if (newBody == null || newBody.isBlank()) return;
                databaseHelper.updateReview(r.getId(), newBody);
                table.setItems(FXCollections.observableArrayList(
                        databaseHelper.readReviewsFor(replyId, isAnswer)
                ));
            });
        });

        Button delBtn = new Button("Delete");
        delBtn.setOnAction(a -> {
            Review r = table.getSelectionModel().getSelectedItem();
            if (r == null) return;
            if (!currentUser.getUserName().equals(r.getUserName())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You can only delete your own reviews.");
                alert.showAndWait();
                return;
            }
            databaseHelper.deleteReview(r.getId());
            table.setItems(FXCollections.observableArrayList(
                    databaseHelper.readReviewsFor(replyId, isAnswer)
            ));
        });

        HBox actions = new HBox(10, editBtn, delBtn);
        actions.setStyle("-fx-padding: 10;");

        VBox layout = new VBox(10, table, actions);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout, 800, 480);
        dialog.setScene(scene);
        dialog.show();
    }
	
	// Pop-Up Window for a list of trusted Reviewer it is called when the trusted reviewer
	// button is pressed and can be closed within the pop-up
	private void showTrustedReviewersList() {
		Stage popUp = new Stage();
		VBox layout = new VBox();
		HBox top = new HBox();
		Region spacer = new Region();
		Button closeBttn = new Button("Close");
		ArrayList<TrustedReview> list = databaseHelper.readTrustedReviews(currentUser.getUserName());
		
		layout.setStyle("-fx-padding: 20;");
		layout.setAlignment(Pos.TOP_CENTER);
		
		// Label displaying the user
		Label userLabel = new Label(currentUser.getUserName() + "'s list of trusted Reviewers");
		userLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		top.getChildren().addAll(userLabel, spacer, closeBttn);
		layout.getChildren().add(top);
		
		closeBttn.setOnAction(a -> {
			popUp.close();
		});
		
		if(list != null) {
			for(int i = 0; i < list.size(); ++i) {
				Region space = new Region();
				TrustedReview review = list.get(i);
				Button deleteBttn = new Button("Delete");
				TextField rating = new TextField(String.valueOf(review.getRating()));
				rating.setPrefWidth(25);
				Label reviewer = new Label(review.getReviewer());
				Label ratingPt2 = new Label("/5");
				
				HBox.setHgrow(space, Priority.ALWAYS);
				HBox trustedReview = new HBox();
				trustedReview.getChildren().addAll(reviewer, space, rating, ratingPt2, deleteBttn);
				layout.getChildren().add(trustedReview);
				
				reviewer.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
				ratingPt2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
				
				deleteBttn.setOnAction(a -> {
					databaseHelper.deleteTrustedReviewer(currentUser.getUserName(),review.getReviewer());
					layout.getChildren().remove(trustedReview);
				});
				
				rating.setOnAction(event -> {
					databaseHelper.editTrustedReviewer(currentUser.getUserName(), review.getReviewer(), Integer.parseInt(rating.getText()));
				});
			}	
		}
		
		else {
			Label noReviews = new Label("No Reviews yet!");
			noReviews.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
			
			layout.getChildren().add(noReviews);
		}
		
		
		
		Scene popUpScene = new Scene(layout, 400, 400);
		
		// Set the scene for the popUp stage
		popUp.show();
		popUp.setScene(popUpScene);
		popUp.setTitle("Trusted Reviews");
	}
	
	// Trust a reviewer another popup window allowing you to rate a reviewer and
    // add them to your trustedReviewers list
    private void trustReviewerPopUpPage() {
        Stage popUp = new Stage();
        VBox structure = new VBox();
        HBox layout = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField enterReviewer = new TextField("Enter your reviewer");
        
        layout.setStyle("-fx-padding: 20;");
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getChildren().add(enterReviewer);
        
        enterReviewer.setOnAction(event -> {
        	String reviewer = enterReviewer.getText();
            layout.getChildren().remove(enterReviewer);

	        if(databaseHelper.trustReviewer(reviewer, currentUser.getUserName(), 1) != "") {
	            Label rateReviewer = new Label("Rate " + reviewer);
	            TextField rating = new TextField("0");
	            rating.setPrefWidth(25);
	            Label ratingPt2 = new Label("/5");
	
	            rateReviewer.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	            ratingPt2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	
	            layout.getChildren().addAll(rateReviewer, spacer, rating, ratingPt2);
	
	            rating.setOnAction(e -> {
	            	int input = Integer.parseInt(rating.getText());
	            	if(input <= 5 && input > 0) {
	            		databaseHelper.trustReviewer(currentUser.getUserName(), reviewer, input);
		                popUp.close();
	            	}
	            	else {
	            		Label errLabel = new Label("Enter valid number between 1-5");
	            		errLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	            		structure.getChildren().add(errLabel);
	            	}
	            });
	        }
	        else {
	            Label alreadyReviewed = new Label(reviewer + " has already been reviewed");
	            layout.getChildren().add(alreadyReviewed);
	        }
        });
        
        structure.getChildren().add(layout);
        Scene popUpScene = new Scene(structure, 400, 100);

        // Set the scene for the popUp stage
        popUp.show();
        popUp.setScene(popUpScene);
        popUp.setTitle("Trust Reviewer");
    }
	
}
