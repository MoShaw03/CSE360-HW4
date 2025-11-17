package application;

import java.sql.SQLException;
import javafx.application.Application;

import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

public class TrustedReviewerTester extends Application {
	
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		try {
			databaseHelper.connectToDatabase(); // Connect to the database
			System.out.println(databaseHelper.trustReviewer("admin", "Amber", 5));
			System.out.println(databaseHelper.trustReviewer("admin", "Mark", 3));
			System.out.println(databaseHelper.trustReviewer("admin", "Ricky", 2));
			System.out.println(databaseHelper.trustReviewer("admin", "Jesus", 1));
			System.out.println(databaseHelper.trustReviewer("admin", "Angel", 4));
			if (databaseHelper.isDatabaseEmpty()) {
				new FirstPage(databaseHelper).show(primaryStage);
			} else {
				new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
