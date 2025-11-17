package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The AdminSetupPage class oversees the setup process for creating an administrator account.
 * First user (admin) is responsible for initializing and creating an admin account within the system.
 */
public class AdminSetupPage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.currentUser = null;
    }
    public AdminSetupPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        // Input fields for userName and password

        Label errorUserNameLabel = new Label("");
        errorUserNameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-alignment: center;");
        errorUserNameLabel.setTextFill(Color.RED);

        Label errorPasswordLabel = new Label("");
        errorPasswordLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-alignment: center;");
        errorPasswordLabel.setTextFill(Color.RED);

        Label errorEmailLabel = new Label("");
        errorEmailLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-alignment: center;");
        errorEmailLabel.setTextFill(Color.RED);

        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(250);

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String userNameCheck = UserNameRecognizer.checkForValidUserName(userName);		// Tests if the username is valid
            String passwordCheck= PasswordEvaluator.evaluatePassword(password);				// Tests if the password is valid
            String emailCheck = EmailRecognizer.checkForValidEmail(email);					// Tests if the email is valid

            try {
                // If the returned String is not empty, it is an error message
                if (userNameCheck != "" || passwordCheck != "" || emailCheck != "") {
                    // Display the error message
                    errorUserNameLabel.setText(userNameCheck);
                    errorPasswordLabel.setText(passwordCheck);
                    errorEmailLabel.setText(emailCheck);
                    // Fetch the index where the processing of the input stopped
                    if (UserNameRecognizer.userNameRecognizerIndexofError <= -1) return;	// Should never happen
                }
                else {
                    // The returned String is empty, it, so there is no error in the input.
                    System.out.println("Success! The UserName is valid.");
                    // Create a new User object with admin role and register in the database
                    User user=new User(userName, password, "admin", email);
                    databaseHelper.register(user);
                    System.out.println("Administrator setup completed.");

                    // Sends the new admin back to the login screen
                    new SetupLoginSelectionPage(databaseHelper, "Admin created successfully!").show(primaryStage);
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, emailField, setupButton, errorUserNameLabel, errorPasswordLabel, errorEmailLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();

    }

    // Open the User Management UI. Lgged-in admin's username.
    public void showAdminUserManagement(javafx.stage.Stage stage, User currentUser) {
        try {
            // Ensure required userRoles, name/email exists.
            databaseHelper.ensureRoleSchema();
        } catch (SQLException ex) {
            showInfo("DB init failed: " + ex.getMessage());
            return;
        }

        // Table to display/edit users.
        javafx.scene.control.TableView<UserRow> table = new javafx.scene.control.TableView<>();
        table.setEditable(true);

        // Username column.
        javafx.scene.control.TableColumn<UserRow,String> colUser =
                new javafx.scene.control.TableColumn<>("Username");
        colUser.setCellValueFactory(v -> v.getValue().usernameProperty());
        colUser.setPrefWidth(160);

        // Name column.
        javafx.scene.control.TableColumn<UserRow,String> colName =
                new javafx.scene.control.TableColumn<>("Name");
        colName.setCellValueFactory(v -> v.getValue().nameProperty());
        colName.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> {
            UserRow r = e.getRowValue();                     // row being edited
            r.setName(e.getNewValue());                      // update local model
            try { databaseHelper.updateName(r.getUsername(), r.getName()); }
            catch (SQLException ex) { showInfo("Update name failed: " + ex.getMessage()); }
        });
        colName.setPrefWidth(180);

        // Email column.
        javafx.scene.control.TableColumn<UserRow,String> colEmail =
                new javafx.scene.control.TableColumn<>("Email");
        colEmail.setCellValueFactory(v -> v.getValue().emailProperty());
        colEmail.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> {
            UserRow r = e.getRowValue();
            r.setEmail(e.getNewValue());
            try { databaseHelper.updateEmail(r.getUsername(), r.getEmail()); }
            catch (SQLException ex) { showInfo("Update email failed: " + ex.getMessage()); }
        });
        colEmail.setPrefWidth(220);

        // Admin Column.
        javafx.scene.control.TableColumn<UserRow,Boolean> colAdmin =
                new javafx.scene.control.TableColumn<>("Admin?");
        colAdmin.setCellValueFactory(v -> v.getValue().adminProperty());
        colAdmin.setCellFactory(col -> {
            javafx.scene.control.cell.CheckBoxTableCell<UserRow,Boolean> cell =
                    new javafx.scene.control.cell.CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });
        colAdmin.setEditable(true);

        // User column.
        javafx.scene.control.TableColumn<UserRow,Boolean> colUserRole =
                new javafx.scene.control.TableColumn<>("User?");
        colUserRole.setCellValueFactory(v -> v.getValue().userRoleProperty());
        colUserRole.setCellFactory(col -> {
            javafx.scene.control.cell.CheckBoxTableCell<UserRow,Boolean> cell =
                    new javafx.scene.control.cell.CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });
        colUserRole.setEditable(true);

        // Instructor Column
        javafx.scene.control.TableColumn<UserRow,Boolean> colInstructor =
                new javafx.scene.control.TableColumn<>("Instructor?");
        colInstructor.setCellValueFactory(v -> v.getValue().instructorRoleProperty());
        colInstructor.setCellFactory(col -> {
            javafx.scene.control.cell.CheckBoxTableCell<UserRow,Boolean> cell =
                    new javafx.scene.control.cell.CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });
        colInstructor.setEditable(true);

        // Staff Column
        javafx.scene.control.TableColumn<UserRow,Boolean> colStaffRole =
                new javafx.scene.control.TableColumn<>("Staff?");
        colStaffRole.setCellValueFactory(v -> v.getValue().staffRoleProperty());
        colStaffRole.setCellFactory(col -> {
            javafx.scene.control.cell.CheckBoxTableCell<UserRow,Boolean> cell =
                    new javafx.scene.control.cell.CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });
        colStaffRole.setEditable(true);

        // Assemble columns.
        table.getColumns().setAll(colUser, colName, colEmail, colAdmin, colUserRole, colInstructor, colStaffRole);

        // Buttons: Add / Remove / Refresh.
        javafx.scene.control.Button addBtn = new javafx.scene.control.Button("Add User");
        addBtn.setOnAction(ev -> openAddUserDialog(stage, table));

        javafx.scene.control.Button delBtn = new javafx.scene.control.Button("Remove Selected");
        delBtn.setOnAction(ev -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();   // pick selection
            if (sel == null) { showInfo("Select a user first."); return; }
            if (!confirm("Delete user '" + sel.getUsername() + "'?")) return;
            try {
                boolean ok = databaseHelper.deleteUser(sel.getUsername(), currentUser.getUserName());
                if (!ok) { showInfo("Guarded: cannot remove another admin or the last admin."); return; }
                table.getItems().remove(sel);                            // update UI list
            } catch (SQLException ex) {
                showInfo("Delete failed: " + ex.getMessage());
            }
        });

        javafx.scene.control.Button refreshBtn = new javafx.scene.control.Button("Refresh");
        refreshBtn.setOnAction(ev -> table.setItems(loadUserRows()));

        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("Back");
        backBtn.setOnAction(ev -> new AdminHomePage(databaseHelper,currentUser).show(stage));

        // Layout with header, table and actions.
        javafx.scene.layout.HBox actions = new javafx.scene.layout.HBox(8, addBtn, delBtn, refreshBtn, backBtn);
        actions.setPadding(new javafx.geometry.Insets(8));

        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setTop(new javafx.scene.control.Label("Admin • User Management"));
        javafx.scene.layout.BorderPane.setMargin(root.getTop(), new javafx.geometry.Insets(8));
        root.setCenter(table);
        root.setBottom(actions);

        // Initial data load.
        table.setItems(loadUserRows());

        // Bind role checkboxes to DB calls for current items.
        table.getItems().forEach(r -> r.bindRoleHandlers(databaseHelper, () -> {
            table.setItems(loadUserRows());                               // reload on block failure
        }, currentUser.getUserName()));

        // Re-bind listeners whenever the table items list changes.
        table.itemsProperty().addListener((obs, oldList, newList) -> {
            if (newList != null) {
                newList.forEach(r -> r.bindRoleHandlers(databaseHelper, () -> {
                    table.setItems(loadUserRows());
                }, currentUser.getUserName()));
            }
        });

        // Show scene.
        stage.setScene(new javafx.scene.Scene(root, 900, 520));
        stage.setTitle("Admin — Manage Users");
        stage.show();
    }

    // Load table rows from DB -> ObservableList<UserRow>.
    private javafx.collections.ObservableList<UserRow> loadUserRows() {
        javafx.collections.ObservableList<UserRow> rows =
                javafx.collections.FXCollections.observableArrayList();
        try {
            databaseHelper.ensureRoleSchema();                             // ensure schema always exists
            for (DatabaseHelper.AdminViewRow r : databaseHelper.listUsersWithRoles()) {
                boolean isAdmin = r.roles.contains("admin");               // derive roles from Set
                boolean isUser  = r.roles.contains("user");
                boolean isInstructor = r.roles.contains("instructor");
                boolean isStaff = r.roles.contains("staff");
                rows.add(new UserRow(r.userName, r.name, r.email, isAdmin, isUser, isInstructor, isStaff));
            }
        } catch (SQLException ex) {
            showInfo("Load failed: " + ex.getMessage());
        }
        return rows;
    }

    // Dialog to add a user with checkboxes for roles.
    private void openAddUserDialog(Stage owner, javafx.scene.control.TableView<UserRow> table) {
        javafx.scene.control.Dialog<UserRow> dlg = new javafx.scene.control.Dialog<>();
        dlg.initOwner(owner);
        dlg.setTitle("Add User");

        // Inputs: username/password/name/email + role checkboxes.
        javafx.scene.control.TextField tfUser  = new javafx.scene.control.TextField();
        javafx.scene.control.PasswordField pfPass = new javafx.scene.control.PasswordField();
        javafx.scene.control.TextField tfName  = new javafx.scene.control.TextField();
        javafx.scene.control.TextField tfEmail = new javafx.scene.control.TextField();
        javafx.scene.control.CheckBox cbAdmin  = new javafx.scene.control.CheckBox("Admin");
        javafx.scene.control.CheckBox cbUser   = new javafx.scene.control.CheckBox("User");
        javafx.scene.control.CheckBox cbInstructor   = new javafx.scene.control.CheckBox("Instructor");
        javafx.scene.control.CheckBox cbStaff = new javafx.scene.control.CheckBox("Staff");
        cbUser.setSelected(true);                                         // default to user

        // Compact grid.
        javafx.scene.layout.GridPane g = new javafx.scene.layout.GridPane();
        g.setHgap(10); g.setVgap(10); g.setPadding(new javafx.geometry.Insets(10));
        g.add(new javafx.scene.control.Label("Username:"), 0, 0); g.add(tfUser,  1, 0);
        g.add(new javafx.scene.control.Label("Password:"), 0, 1); g.add(pfPass, 1, 1);
        g.add(new javafx.scene.control.Label("Name:"),     0, 2); g.add(tfName, 1, 2);
        g.add(new javafx.scene.control.Label("Email:"),    0, 3); g.add(tfEmail,1, 3);
        g.add(cbAdmin, 0, 4); g.add(cbUser, 1, 4); g.add(cbInstructor, 2, 4); g.add(cbStaff, 3, 4); // ADDED

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(
                javafx.scene.control.ButtonType.CANCEL,
                javafx.scene.control.ButtonType.OK
        );

        // On OK -> validate + insert + return a new row for the table.
        dlg.setResultConverter(btn -> {
            if (btn != javafx.scene.control.ButtonType.OK) return null;

            String u  = tfUser.getText().trim();
            String p  = pfPass.getText();
            String n  = tfName.getText().trim();
            String em = tfEmail.getText().trim();
            boolean a = cbAdmin.isSelected();
            boolean b = cbUser.isSelected();
            boolean c = cbInstructor.isSelected();
            boolean d = cbStaff.isSelected();

            if (u.isEmpty() || p.isEmpty()) { showInfo("Username and password are required."); return null; }
            try {
                if (databaseHelper.usernameExists(u)) { showInfo("Username already exists."); return null; }
                java.util.LinkedHashSet<String> roles = new java.util.LinkedHashSet<>();
                if (a) roles.add("admin");
                if (b) roles.add("user");
                if (c) roles.add("instructor");
                if (d) roles.add("staff");
                if (roles.isEmpty()) roles.add("user");                   // ensure at least "user"
                boolean ok = databaseHelper.addUser(u, p, n, em, roles);  // DB insert + roles
                if (!ok) { showInfo("Could not add user."); return null; }
                return new UserRow(u, n, em, roles.contains("admin"), roles.contains("user"), roles.contains("instructor"), roles.contains("staff"));
            } catch (SQLException ex) {
                showInfo("Add failed: " + ex.getMessage());
                return null;
            }
        });

        // Append row on success and bind its role toggles.
        dlg.showAndWait().ifPresent(row -> {
            table.getItems().add(row);
            row.bindRoleHandlers(databaseHelper, () -> table.setItems(loadUserRows()), row.getUsername());
        });
    }

    // Info dialog.
    private void showInfo(String msg) {
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    // Confirm dialog.
    private boolean confirm(String question) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION,
                question,
                javafx.scene.control.ButtonType.OK,
                javafx.scene.control.ButtonType.CANCEL
        );
        return a.showAndWait().filter(javafx.scene.control.ButtonType.OK::equals).isPresent();
    }

    // Row model for the TableView with role bindings.
    public static final class UserRow {
        // Backing properties so TableView can observe updates.
        private final javafx.beans.property.SimpleStringProperty username =
                new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleStringProperty name =
                new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleStringProperty email =
                new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleBooleanProperty admin =
                new javafx.beans.property.SimpleBooleanProperty(false);
        private final javafx.beans.property.SimpleBooleanProperty userRole =
                new javafx.beans.property.SimpleBooleanProperty(false);
        private final javafx.beans.property.SimpleBooleanProperty instructorRole =
                new javafx.beans.property.SimpleBooleanProperty(false);
        private final javafx.beans.property.SimpleBooleanProperty staffRole =
                new javafx.beans.property.SimpleBooleanProperty(false);

        public UserRow(String u, String n, String e, boolean isAdmin, boolean isUser, boolean isInstructor, boolean isStaff) {
            username.set(u); name.set(n); email.set(e); admin.set(isAdmin); userRole.set(isUser); instructorRole.set(isInstructor); staffRole.set(isStaff);
        }

        // Basic getters.
        public String getUsername() { return username.get(); }
        public String getName()     { return name.get(); }
        public String getEmail()    { return email.get(); }

        // Property accessors for TableColumn cell value factories.
        public javafx.beans.property.StringProperty usernameProperty() { return username; }
        public javafx.beans.property.StringProperty nameProperty()     { return name; }
        public javafx.beans.property.StringProperty emailProperty()    { return email; }
        public javafx.beans.property.BooleanProperty adminProperty()   { return admin; }
        public javafx.beans.property.BooleanProperty userRoleProperty(){ return userRole; }
        public javafx.beans.property.BooleanProperty instructorRoleProperty(){ return instructorRole; }
        public javafx.beans.property.BooleanProperty staffRoleProperty(){ return staffRole; }

        // Mutators for inline edits.
        public void setName(String v)  { name.set(v); }
        public void setEmail(String v) { email.set(v); }

        // Attach listeners to role checkboxes that call DB methods.
        public void bindRoleHandlers(DatabaseHelper db, Runnable onGuardFailReload, String actingAdmin) {
            // Admin checkbox toggle handler.
            adminProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal) {
                        db.addUserRole(getUsername(), "admin");          // grant admin
                    } else {
                        boolean ok = db.removeUserRole(getUsername(), "admin", actingAdmin); // prevent remove
                        if (!ok) {                                       // blocked
                            admin.set(true);                              // revert checkbox
                            onGuardFailReload.run();                      // reload truth
                        }
                    }
                } catch (SQLException ex) {
                    admin.set(oldVal);                                    // revert on error
                }
            });

            // User checkbox toggle handler.
            userRoleProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal) {
                        db.addUserRole(getUsername(), "user");           // grant user
                    } else {
                        db.removeUserRole(getUsername(), "user", actingAdmin); // allowed removal
                    }
                } catch (SQLException ex) {
                    userRole.set(oldVal);                                 // revert on error
                }
            });

            // Instructor checkbox toggle handler
            instructorRoleProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal) {
                        db.addUserRole(getUsername(), "instructor");           // grant user
                    } else {
                        db.removeUserRole(getUsername(), "instructor", actingAdmin); // allowed removal
                    }
                } catch (SQLException ex) {
                    userRole.set(oldVal);                                 // revert on error
                }
            });

            // Staff Checkbox toggle handler.
            staffRoleProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal) {
                        db.addUserRole(getUsername(), "staff");
                    } else {
                        db.removeUserRole(getUsername(), "staff", actingAdmin);
                    }
                } catch (SQLException ex) {
                    staffRole.set(oldVal);
                }
            });
        }
    }
}
