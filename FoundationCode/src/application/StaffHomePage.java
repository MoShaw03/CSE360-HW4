package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Staff Home Page for Moderation View.
 */
public class StaffHomePage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    private final TableView<ModerationObj> table = new TableView<>();
    private final ObservableList<ModerationObj> data = FXCollections.observableArrayList();

    /**
     * @param databaseHelper DB helper
     * @param currentUser    logged-in
     */
    public StaffHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    /**
     * Shows the staff UI.
     * @param primaryStage stage
     */
    public void show(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("Staff Moderation");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList("ALL","QUESTION","ANSWER","REVIEW"));
        filter.getSelectionModel().selectFirst();

        Button refreshBtn = new Button("Refresh");
        Button editBtn = new Button("Edit Text");
        Button deleteBtn = new Button("Delete (Soft)");
        TextField noteField = new TextField();
        noteField.setPromptText("Private note...");
        Button addNoteBtn = new Button("Add Note");

        HBox controls = new HBox(8, new Label("Filter:"), filter, refreshBtn, new Separator(),
                editBtn, deleteBtn, new Separator(), noteField, addNoteBtn);
        controls.setPadding(new Insets(8));

        TableColumn<ModerationObj, String> cType = new TableColumn<>("Type");
        cType.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().getContentType().name()));
        TableColumn<ModerationObj, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ModerationObj, String> cAuthor = new TableColumn<>("Author");
        cAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<ModerationObj, String> cText = new TableColumn<>("Text");
        cText.setPrefWidth(520);
        cText.setCellValueFactory(new PropertyValueFactory<>("text"));
        TableColumn<ModerationObj, Boolean> cDel = new TableColumn<>("Deleted");
        cDel.setCellValueFactory(new PropertyValueFactory<>("deleted"));

        table.getColumns().setAll(cType, cId, cAuthor, cText, cDel);
        table.setItems(data);

        VBox top = new VBox(6, title, controls);
        root.setTop(top);
        root.setCenter(table);

        refreshBtn.setOnAction(e -> load(filter.getValue()));
        filter.setOnAction(e -> load(filter.getValue()));

        editBtn.setOnAction(e -> {
            ModerationObj sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            TextInputDialog dlg = new TextInputDialog(sel.getText());
            dlg.setHeaderText("Edit text: " + sel.getContentType() + " #" + sel.getId());
            dlg.setContentText("New text:");
            dlg.showAndWait().ifPresent(newText -> {
                try {
                    if (databaseHelper.editContentText(sel.getContentType(), sel.getId(), newText)) {
                        sel.setText(newText);
                        table.refresh();
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }
            });
        });

        deleteBtn.setOnAction(e -> {
            ModerationObj sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            try {
                if (databaseHelper.softDeleteContent(sel.getContentType(), sel.getId())) {
                    sel.setDeleted(true);
                    table.refresh();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        addNoteBtn.setOnAction(e -> {
            ModerationObj sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            String note = noteField.getText();
            if (note == null || note.isBlank()) return;
            try {
                databaseHelper.addModerationNote(sel.getContentType(), sel.getId(), currentUser.getUserName(), note);
                noteField.clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        load("ALL");
        Scene scene = new Scene(root, 980, 540);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Home");
        primaryStage.show();
    }

    /**
     * Load all of or a type of moderation items.
     * @param filter "ALL" or one of "QUESTION","ANSWER","REVIEW"
     */
    private void load(String filter) {
        data.clear();
        try {
            if ("ALL".equals(filter)) data.addAll(databaseHelper.loadAllModerationItems());
            else data.addAll(databaseHelper.loadModerationItemsByType(filter));
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

