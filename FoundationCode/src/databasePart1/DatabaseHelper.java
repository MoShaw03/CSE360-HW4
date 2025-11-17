 package databasePart1;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.*;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(255),"
				+ "otp VARCHAR(20),"
				+ "email VARCHAR(255),"
				+ "requesting BOOLEAN DEFAULT FALSE)";

		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);

		// Create a table holding user questions
	    String questionsTable = "CREATE TABLE IF NOT EXISTS DiscussionQuestions ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "date DATETIME, "
	    		+ "userName VARCHAR(255), "
	    		+ "question VARCHAR(255) UNIQUE, "
	    		+ "body TEXT,"
	    		+ "answered VARCHAR(1))";
	    statement.execute(questionsTable);

		// creates a table for replies using the question they are attached to identify them
	    String answersTable = "CREATE TABLE IF NOT EXISTS DiscussionAnswers ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "date DATETIME, "
	    		+ "userName VARCHAR(255), "
	    		+ "body TEXT, "
	    		+ "question VARCHAR(255), "
	    		+ "helpful VARCHAR(1))";
	    statement.execute(answersTable);
	  // Creates a Messages table to store private messages
	    String messagesTable = "CREATE TABLE IF NOT EXISTS PrivateMessages ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "date DATETIME, "
	    		+ "sender VARCHAR(255), "
	    		+ "receiver VARCHAR(255),"
	    		+ "body TEXT, "
	    		+ "previousmessage INT, "
	    		+ "newestinchain VARCHAR(1))";
	    statement.execute(messagesTable);

		// Creates a Reviews table for all Questions and/or Answers
	    String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews ("
	    		+ "Id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "date DATETIME, "
	    		+ "userName VARCHAR(255), "
	    		+ "body TEXT, "
	    		+ "reply VARCHAR(255), "
	    		+ "isAnswer VARCHAR(1))";
	    statement.execute(reviewsTable);
	    
	    // creates a table for trusted reviewers
	    String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trustedReviewers ("
	    		+ "studentName VARCHAR(255), "
	    		+ "reviewerName VARCHAR(255), "
	    		+ "rating INT, "
	    		+ "CONSTRAINT CHK_Rating CHECK (rating>=1 AND rating<=5), "
	    		+ "CONSTRAINT CHK_Review UNIQUE (studentName, reviewerName))";
	    statement.execute(trustedReviewersTable);
	    
	    // Creates a table for the soft-deleted objects
        String deletedTable = "CREATE TABLE IF NOT EXISTS ModerationDeleted ("
                + "type VARCHAR(20),"
                + "contentId INT,"
                + "PRIMARY KEY(type, contentId))";
        statement.execute(deletedTable); 
        
        // Creates a table for the internal only notes. 
        String notesTable = "CREATE TABLE IF NOT EXISTS ModerationNotes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "type VARCHAR(20),"
                + "contentId INT,"
                + "moderator VARCHAR(255),"
                + "note TEXT,"
                + "date DATETIME)";
        statement.execute(notesTable);   
	}
	
	// Used in JUnit testing
		public void connectToDatabase(Connection testConnection) throws SQLException {
			connection = testConnection;
			System.out.println("Connecting to testing database...");
			statement = connection.createStatement(); 
			createTables();
	    }

	
		
	// sets TRUE when a student requests to become a reviewer
	public void requestingToReviewer(String userName) {
		String query = "UPDATE cse360users SET requesting=TRUE WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	// returns bool of whether a student is requesting to be a reviewer
	public boolean getRequesting(String userName) {
		String query = "SELECT requesting FROM cse360users WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getBoolean("requesting");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// returns a list of all students requesting to be a reviewer
	public ArrayList<String> getAllRequesting() {
		ArrayList<String> usernames = new ArrayList<>();
		String query = "SELECT userName FROM cse360users WHERE requesting=TRUE";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usernames;
	}
	
	// removes student request to become a reviewer
	public void removeRequest(String userName) {
		String query = "UPDATE cse360users SET requesting=FALSE WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	// update existing role of a user
	public void updateRole(String userName, String newRole) {
		String query = "UPDATE cse360users SET role=? WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newRole);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	// returns list of every question and answer posted by a student
	public List<String> getContributions(String userName) {
		List<String> contributions = new ArrayList<>();
		
		String query1 = "SELECT question, body, date FROM DiscussionQuestions WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
	            contributions.add("Question: " + rs.getString("question") + "; " + rs.getString("body") + " Timestamp: " + rs.getString("date"));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		String query2 = "SELECT body, date FROM DiscussionAnswers WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(query2)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
	            contributions.add("Answer: " + rs.getString("body") + " Timestamp: " + rs.getString("date"));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return contributions;
	}

	public void setOTP(String reset, String userName) {
		String query = "UPDATE cse360users SET otp=? WHERE userName=?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, reset);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public String getOTP(String userName) {
		String query = "SELECT otp FROM cse360users WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("otp");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
		return null;
	}
	
	public void removeOTP(String userName) {
		String query = "UPDATE cse360users SET otp=NULL WHERE userName=?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void setNewPassword(String password, String userName) {
		String query = "UPDATE cse360users SET password=? WHERE userName=?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, email) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.setString(4, user.getEmail());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ? AND email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.setString(4, user.getEmail());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	public String getUserEmail(String email) {
	    String query = "SELECT email FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("email"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	// Ensure extra columns/tables exist. 
	public void ensureRoleSchema() throws SQLException {
		// 1) Ensure optional profile columns exist on cse360users.
		boolean createdTemp = false;
		Statement s = this.statement;
		if (s == null) { s = connection.createStatement(); createdTemp = true; }

		// 'name' column
		try { s.execute("ALTER TABLE cse360users ADD COLUMN name VARCHAR(255)"); } catch (SQLException ignore) {}
		// 'email' column
		try { s.execute("ALTER TABLE cse360users ADD COLUMN email VARCHAR(255) UNIQUE"); } catch (SQLException ignore) {}

		// Create normalized roles table if missing.
		//    Primary key(userName, role) prevents duplicates.
		//    FK ties to cse360users(userName) which is UNIQUE in your schema.
		String createUserRoles =
			"CREATE TABLE IF NOT EXISTS UserRoles (" +
			" userName VARCHAR(255) NOT NULL," +
			" role     VARCHAR(50)  NOT NULL," +
			" CONSTRAINT pk_userroles PRIMARY KEY(userName, role)," +
			" CONSTRAINT fk_userroles_user FOREIGN KEY(userName) " +
			"   REFERENCES cse360users(userName) ON DELETE CASCADE" +
			")";
		s.execute(createUserRoles);

		if (createdTemp) s.close();
	}

	// Normalize a role token (trim + lowercase); returns null if blank.
	private static String normalizeRoleToken(String role) {
		if (role == null) return null;
		String r = role.trim().toLowerCase();
		return r.isEmpty() ? null : r;
	}

	// Add a single role to a user. Also sync legacy `role` column if needed.
	public boolean addUserRole(String userName, String role) throws SQLException {
		ensureRoleSchema();                                     // ensure table exists
		String r = normalizeRoleToken(role);                    // normalize input
		if (r == null) return false;                            // ignore blank

		// Insert role; prevents duplicates
		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO UserRoles(userName, role) VALUES(?, ?)")) {
			ps.setString(1, userName);
			ps.setString(2, r);
			ps.executeUpdate();
		} catch (SQLException dup) {
			// Duplicate role for user
		}

		// Keep legacy column in sync: if admin role added, set legacy role to 'admin'.
		if ("admin".equals(r)) {
			try (PreparedStatement ps2 = connection.prepareStatement(
					"UPDATE cse360users SET role='admin' WHERE userName=?")) {
				ps2.setString(1, userName);
				ps2.executeUpdate();
			}
		}
		
		if ("instructor".equals(r)) {
			try (PreparedStatement ps2 = connection.prepareStatement(
					"UPDATE cse360users SET role='instructor' WHERE userName=?")) {
				ps2.setString(1, userName);
				ps2.executeUpdate();
			}
		}
		return true;
	}

	// Remove a single role from a user.
	public boolean removeUserRole(String targetUser, String role, String actingAdmin) throws SQLException {
		ensureRoleSchema();
		String r = normalizeRoleToken(role);
		if (r == null) return false;

		// do not allow removing another user's admin role.
		if ("admin".equals(r) && !targetUser.equals(actingAdmin)) return false;

		// never remove the last admin's admin role.
		if ("admin".equals(r) && countAdminsByRoles() <= 1) return false;

		// Perform deletion
		try (PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM UserRoles WHERE userName=? AND role=?")) {
			ps.setString(1, targetUser);
			ps.setString(2, r);
			int rows = ps.executeUpdate();

			// If admin role was removed, update legacy column if no admin remains for that user.
			if (rows > 0 && "admin".equals(r) && !isAdminByRoles(targetUser)) {
				// Fallback legacy value: keep 'role' as 'user' when no admin role remains.
				try (PreparedStatement ps2 = connection.prepareStatement(
						"UPDATE cse360users SET role='user' WHERE userName=?")) {
					ps2.setString(1, targetUser);
					ps2.executeUpdate();
				}
			}
			
			
			return rows > 0;
		}
	}

	// Replace all roles for a user atomically (for admin removal rules).
	public boolean setUserRoles(String targetUser, java.util.Collection<String> roles, String actingAdmin) throws SQLException {
		ensureRoleSchema();

		// Normalize incoming roles and prevents duplicates.
		java.util.LinkedHashSet<String> next = new java.util.LinkedHashSet<>();
		if (roles != null) {
			for (String r : roles) {
				String t = normalizeRoleToken(r);
				if (t != null) next.add(t);
			}
		}

		// Determine if we're removing admin from someone who currently has it.
		boolean wasAdmin = isAdminByRoles(targetUser);
		boolean willBeAdmin = next.contains("admin");

		// Cannot demote another admin; cannot remove the last admin.
		if (wasAdmin && !willBeAdmin) {
			if (!targetUser.equals(actingAdmin)) return false;
			if (countAdminsByRoles() <= 1) return false;
		}

		// Transaction-like behavior (simple): delete all roles, then re-insert.
		try (PreparedStatement del = connection.prepareStatement(
				 "DELETE FROM UserRoles WHERE userName=?")) {
			del.setString(1, targetUser);
			del.executeUpdate();
		}

		try (PreparedStatement ins = connection.prepareStatement(
				 "INSERT INTO UserRoles(userName, role) VALUES(?, ?)")) {
			for (String r : next) {
				ins.setString(1, targetUser);
				ins.setString(2, r);
				ins.addBatch();
			}
			ins.executeBatch();
		}

		// Keep legacy `role` in sync (for existing login): prefer 'admin' if present, else 'user' or blank.
		String legacy = willBeAdmin ? "admin" : (next.isEmpty() ? "" : next.iterator().next());
		try (PreparedStatement up = connection.prepareStatement(
				"UPDATE cse360users SET role=? WHERE userName=?")) {
			up.setString(1, legacy);
			up.setString(2, targetUser);
			up.executeUpdate();
		}
		return true;
	}

	// Add a new user. You can pass roles like List.of("user") or List.of("admin","user").
	public boolean addUser(String userName, String password, String name, String email, java.util.Collection<String> roles) throws SQLException {
		ensureRoleSchema();

		// Insert into cse360users (legacy role initially mirrors first role or 'user' if empty).
		String legacy = "user";
		if (roles != null) {
			for (String r : roles) { legacy = normalizeRoleToken(r) != null ? normalizeRoleToken(r) : legacy; break; }
		}

		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO cse360users(userName,password,role,name,email) VALUES (?,?,?,?,?)")) {
			ps.setString(1, userName);
			ps.setString(2, password);
			ps.setString(3, legacy == null ? "" : legacy);
			ps.setString(4, name == null ? "" : name.trim());
			ps.setString(5, email == null ? "" : email.trim());
			ps.executeUpdate();
		}

		// Insert roles in UserRoles table.
		if (roles != null) {
			for (String r : roles) addUserRole(userName, r);
		} else {
			// Default to 'user' if no roles provided.
			addUserRole(userName, "user");
		}
		return true;
	}

	// Delete user based on roles table.
	public boolean deleteUser(String targetUser, String actingAdmin) throws SQLException {
		ensureRoleSchema();

		// if target is admin, only allow self-delete and never the last admin.
		if (isAdminByRoles(targetUser)) {
			if (!targetUser.equals(actingAdmin)) return false;
			if (countAdminsByRoles() <= 1) return false;
		}

		// Deleting from parent table cascades to UserRoles via FK ON DELETE CASCADE.
		try (PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM cse360users WHERE userName=?")) {
			ps.setString(1, targetUser);
			return ps.executeUpdate() == 1;
		}
	}

	// Update name/email.
	public boolean updateName(String userName, String newName) throws SQLException {
		ensureRoleSchema();
		try (PreparedStatement ps = connection.prepareStatement(
				"UPDATE cse360users SET name=? WHERE userName=?")) {
			ps.setString(1, newName == null ? "" : newName.trim());
			ps.setString(2, userName);
			return ps.executeUpdate() == 1;
		}
	}
	public boolean updateEmail(String userName, String newEmail) throws SQLException {
		ensureRoleSchema();
		try (PreparedStatement ps = connection.prepareStatement(
				"UPDATE cse360users SET email=? WHERE userName=?")) {
			ps.setString(1, newEmail == null ? "" : newEmail.trim());
			ps.setString(2, userName);
			return ps.executeUpdate() == 1;
		}
	}

	// Does a username exist?
	public boolean usernameExists(String userName) throws SQLException {
		String sql = "SELECT 1 FROM cse360users WHERE userName=?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, userName);
			try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
		}
	}

	// Is user an admin?
	public boolean isAdminByRoles(String userName) throws SQLException {
		ensureRoleSchema();
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT 1 FROM UserRoles WHERE userName=? AND role='admin'")) {
			ps.setString(1, userName);
			try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
		}
	}

	// Count admins (via roles table).
	public int countAdminsByRoles() throws SQLException {
		ensureRoleSchema();
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT COUNT(DISTINCT userName) FROM UserRoles WHERE role='admin'");
		     ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getInt(1);
		}
	}

	// Fetch all roles for a user as a Set.
	public java.util.Set<String> getUserRoles(String userName) throws SQLException {
		ensureRoleSchema();
		java.util.LinkedHashSet<String> roles = new java.util.LinkedHashSet<>();
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT role FROM UserRoles WHERE userName=? ORDER BY role")) {
			ps.setString(1, userName);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) roles.add(rs.getString(1));
			}
		}
		return roles;
	}

	// Returns the max id of all the questions, this is needed to properly iterate through the questions when a question is deleted.
	public int maxQuestionIds() {
		String maxid = "SELECT MAX(ID) FROM DiscussionQuestions";;
		try(PreparedStatement pstmt = connection.prepareStatement(maxid)){
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	// Reads question from database and stores in Questions class
	// Theere are meant to be two readQuestion s!
	public Question readQuestion(int index) throws SQLException {
		String readQuestionText = "SELECT date, userName, question, body, answered FROM DiscussionQuestions WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(readQuestionText)) {
			pstmt.setString(1,  String.valueOf(index)); 
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Timestamp timestamp = rs.getTimestamp("date");
            	LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
            	Boolean answered = rs.getString("answered") != null && rs.getString("answered").equals("1");
				return new Question(date, rs.getString("userName"), rs.getString("question"), rs.getString("body"), answered);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no question exists or an error occurs
	}
	
	public Question readQuestion(String questiontext) throws SQLException {
		String readQuestionText = "SELECT date, userName, body, answered FROM DiscussionQuestions WHERE question = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(readQuestionText)) {
			pstmt.setString(1,  String.valueOf(questiontext)); 
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Timestamp timestamp = rs.getTimestamp("date");
            	LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
            	Boolean answered = rs.getString("answered") != null && rs.getString("answered").equals("1");
				return new Question(date, rs.getString("userName"), questiontext, rs.getString("body"), answered);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no question exists or an error occurs
	}
	// returns a reply list from a specific question
	public Answers readAnswer(String questiontext) throws SQLException{
	String readAnswerText = "SELECT date, userName, body, question, helpful FROM DiscussionAnswers WHERE question = ?";
		Answers answers = new Answers();
		try (PreparedStatement pstmt = connection.prepareStatement(readAnswerText)) {
			pstmt.setString(1, questiontext);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()){
				Timestamp timestamp = rs.getTimestamp("date");
            	LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
            	Boolean helpful = rs.getString("helpful") != null && rs.getString("helpful").equals("1");
				answers.addAnswer(new Answer(date, rs.getString("userName"), rs.getString("body"), readQuestion(questiontext), helpful));
			}
			return answers;
		}
	}
	
	// Creates question into database
	public void postQuestion(Question question) throws SQLException {
		String insertQuestion = "INSERT INTO DiscussionQuestions (date, userName, question, body, answered) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(1, question.getDate());
			pstmt.setString(2, question.getUser());
			pstmt.setString(3, question.getTopic());
			pstmt.setString(4, question.getBody());
			pstmt.setString(5, question.isAnswered() ? "1" : "0");
			pstmt.executeUpdate();
		}
	}

	// reply is posted storing username, question, and the reply
	public void postReply(Answer answer) throws SQLException{
		String insertReply = "INSERT INTO DiscussionAnswers (date, userName, body, question, helpful) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertReply)) {
			pstmt.setString(1, answer.getDate());
			pstmt.setString(2, answer.getUser());
			pstmt.setString(3, answer.getBody());
			pstmt.setString(4, answer.getQuestion().getTopic());
			pstmt.setString(5, answer.isHelpful() ? "1" : "0");
			pstmt.executeUpdate();
		}
	}
	
	public void updateQuestion(String newTopic, String newBody, String oldTopic) throws SQLException {
		String updateQuestion = "UPDATE DiscussionQuestions SET question = ?, body = ? WHERE question = ?";
		String updateReply = "UPDATE DiscussionAnswers SET question = ? WHERE question = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateQuestion)) {
			pstmt.setString(1, newTopic);
			pstmt.setString(2, newBody);
			pstmt.setString(3, oldTopic);
			pstmt.executeUpdate();
		}
		try(PreparedStatement pstmt1 = connection.prepareStatement(updateReply)) {
			pstmt1.setString(1, newTopic);
			pstmt1.setString(2, oldTopic);
			pstmt1.executeUpdate();
		}
	}
	
	public void updateAnswer (String newText, String oldText, String questiontext) throws SQLException{
		String updateAnswer = "UPDATE DiscussionAnswers SET body = ? WHERE question = ? AND body = ? AND helpful = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(updateAnswer)) {
			pstmt.setString(1, newText);
			pstmt.setString(2, questiontext);
			pstmt.setString(3, oldText);
			pstmt.setString(4, "0");
			pstmt.executeUpdate();
		}
	}
	
	public void deleteQuestion(String questiontext) throws SQLException {	
		deleteAnswer(questiontext);	
		String deleteQuestion = "DELETE FROM DiscussionQuestions WHERE question = ?";	
		try(PreparedStatement pstmt = connection.prepareStatement(deleteQuestion)) {
			pstmt.setString(1, questiontext);
			pstmt.executeUpdate();
		}
	}
	public void deleteAnswer (String text) throws SQLException{
		String deleteAnswer = "DELETE FROM DiscussionAnswers WHERE body = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(deleteAnswer)) {
			pstmt.setString(1,  text);
			pstmt.executeUpdate();
		}
	}
	/*
	 * Gets the ID of a message, which is used for getting the reply
	 */
	public int getMessageID(Message replyingTo) {
		String getMessageID = "SELECT id FROM PrivateMessages WHERE sender = ? AND receiver = ? AND body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(getMessageID)) {
			pstmt.setString(1, replyingTo.getSender());
			pstmt.setString(2, replyingTo.getReceiver());
			pstmt.setString(3, replyingTo.getBody());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/*
	 * Creates a new message
	 */
	
	public void createMessage(Message message) {
		String createMessage = "INSERT INTO PrivateMessages (date, sender, receiver, body, previousmessage, newestinchain) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(createMessage)) {
			pstmt.setString(1, message.getDate().toString());
			pstmt.setString(2, message.getSender());
			pstmt.setString(3, message.getReceiver());
			pstmt.setString(4, message.getBody());
			Message replyingTo = message.getReplyingTo();
			// If the message being created is replying to anotherm message...
			if (replyingTo != null) {
				// Sets previousmessage of the new message to the id of the previous message in the chain
			    pstmt.setInt(5, getMessageID(replyingTo));
			    PreparedStatement changeNewest =connection.prepareStatement("UPDATE PrivateMessages set newestinchain = ? WHERE sender = ? AND receiver = ? AND body = ?");
			    // Changes the previous message to be flagged as not the newest
			    changeNewest.setString(1, "0");
			    changeNewest.setString(2, replyingTo.getSender());
			    changeNewest.setString(3, replyingTo.getReceiver());
			    changeNewest.setString(4, replyingTo.getBody());
			    changeNewest.executeUpdate();
			} else {
			    pstmt.setNull(5, java.sql.Types.INTEGER);
			}
			pstmt.setString(6, "1");
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns a message based on its id in the SQL table
	 */
	
	public Message readMessageFromID(int id) {
	    String readMessageFromID = "SELECT date, sender, receiver, body, previousmessage FROM PrivateMessages WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(readMessageFromID)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Timestamp timestamp = rs.getTimestamp("date");
	            LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
	            int previousMessageID = rs.getInt("previousmessage");
	            if(rs.wasNull()) {
	            	return new Message(date, rs.getString("sender"), rs.getString("receiver"), rs.getString("body"));
	            }
	            else {
	            	return new Message(date, rs.getString("sender"), rs.getString("receiver"), rs.getString("body"), readMessageFromID(previousMessageID));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	/*
	 * Returns a list of all of the messages either from or to a user, but only the newest message in its respective chain
	 */
	
	public Messages readUserMessages(User user) {
	    String readUserMessages = "SELECT date, sender, receiver, body, previousmessage FROM PrivateMessages WHERE (receiver = ? OR sender = ?) and newestinchain = 1";
	    Messages messages = new Messages();
	    try (PreparedStatement pstmt = connection.prepareStatement(readUserMessages)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getUserName());
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Timestamp timestamp = rs.getTimestamp("date");
	            LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
	            int prevId = rs.getInt("previousmessage");
	            Message previous = rs.wasNull() ? null : readMessageFromID(prevId);
	            messages.addMessage(new Message(date, rs.getString("sender"), rs.getString("receiver"), rs.getString("body"), previous));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messages;
	}
	
	/*
	 * 	Recursive method to read a chain of messages.
	 */
	
	public Messages readMessageChain(Message mess) {
	    Messages chain = new Messages();
	    if (mess == null) return chain; // base case

	    try {
	        int id = getMessageID(mess);
	        String readMessageChain = "SELECT previousmessage FROM PrivateMessages WHERE id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(readMessageChain)) {
	            pstmt.setInt(1, id);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                int previousMessageID = rs.getInt("previousmessage");
	                boolean hasPrevious = !rs.wasNull();
	                chain.addMessage(mess);
	                if (hasPrevious) {
	                    Message prev = readMessageFromID(previousMessageID);
	                    if (prev != null) {
	                        chain.addAllMessages(readMessageChain(prev).getMessageList());
	                    }
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return chain;
	}
	
	// searches questions table for substrings and returns questions containing input
	public Questions searchQuestions(String substring) {
		String searchQuery = "SELECT * FROM DiscussionQuestions WHERE question LIKE ?";
		substring = "%" + substring + "%";
		Questions questions = new Questions();
		try(PreparedStatement pstmt = connection.prepareStatement(searchQuery)) {
			pstmt.setString(1, substring);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp("date");
	        	LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
	        	Boolean answered = rs.getString("answered") != null && rs.getString("answered").equals("1");
				questions.addQuestion(new Question(date, rs.getString("userName"), rs.getString("question"), rs.getString("body"), answered));
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
		return questions;
	}
	
	// searches answers table for substrings and returns questions containing input
	public Answers searchAnswers(String substring) {
		String searchQuery = "SELECT * FROM DiscussionAnswers WHERE body LIKE ?";
		substring = "%" + substring + "%";
		Answers answers = new Answers();
		try(PreparedStatement pstmt = connection.prepareStatement(searchQuery)) {
			pstmt.setString(1, substring);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp("date");
            	LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
            	Boolean helpful = rs.getString("helpful") != null && rs.getString("helpful").equals("1");
				answers.addAnswer(new Answer(date, rs.getString("username"), rs.getString("body"), readQuestion(rs.getString("question")), helpful));
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
		return answers;
	}
	
	// List users with roles as Sets. Useful for admin UI.
	public static final class AdminViewRow {
		public final String userName;
		public final String name;
		public final String email;
		public final java.util.Set<String> roles;
		public AdminViewRow(String u, String n, String e, java.util.Set<String> r) {
			this.userName = u; this.name = n; this.email = e; this.roles = r;
		}
	}
	public java.util.List<AdminViewRow> listUsersWithRoles() throws SQLException {
		ensureRoleSchema();
		java.util.List<AdminViewRow> out = new java.util.ArrayList<>();
		// Pull basic user profile first
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT userName, COALESCE(name,''), COALESCE(email,'') FROM cse360users ORDER BY userName");
		     ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				String u = rs.getString(1);
				String n = rs.getString(2);
				String e = rs.getString(3);
				// Fetch roles for each user (simple, clear; acceptable for small N)
				java.util.Set<String> roles = getUserRoles(u);
				out.add(new AdminViewRow(u, n, e, roles));
			}
		}
		return out;
	}
	
	/*CRUD OPERATIONS FOR REVIEW (Create Review/Read Review(s)/Update Reviews/Update Reviews/Delete Reviews) */

    /**
     * Inserts a new {@code Review} into the {@code reviews} table and populates the Review's id with the generated primary key.
     * If the review's date is null, the current date/time (LocalDateTime.now()) will be used for the stored timestamp.
     *
     * @param r the Review to insert; must not be null.
     * @return the same Review instance passed in, with its id set to the generated key if insertion succeeded.
     * @throws RuntimeException if a database error occurs while inserting the review.
     */
	public Review postReview(Review r) {
	    final String sql = "INSERT INTO reviews (date, userName, body, reply, isAnswer) VALUES (?,?,?,?,?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        LocalDateTime when = (r.getDate() == null) ? LocalDateTime.now() : r.getDate();
	        ps.setTimestamp(1, Timestamp.valueOf(when));
	        ps.setString(2, r.getUserName());
	        ps.setString(3, r.getBody());
	        ps.setString(4, r.getReply());
	        ps.setString(5, r.isAnswer() ? "1" : "0");
	        ps.executeUpdate();
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) r.setId(keys.getInt(1));
	        }
	        return r;
	    } catch (SQLException e) {
	        throw new RuntimeException("postReview failed", e);
	    }
	}

	  /**
     * Reads a single Review from the {@code reviews} table by its primary key id.
     *
     * @param id the primary key id of the review to read.
     * @return a Review mapped from the result row if found, otherwise {@code null} when no row matches.
     * @throws RuntimeException if a database error occurs while querying the review.
     */
	public Review readReview(int id) {
	    final String sql = "SELECT Id, date, userName, body, reply, isAnswer FROM reviews WHERE Id=?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() ? mapReview(rs) : null;
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("readReview failed", e);
	    }
	}

	 /**
     * Reads reviews that reference a particular reply (question or answer). Results are ordered newest first and then by Id ascending. 
     *
     * @param reply   the reply identifier (or key) that reviews target; used in the {@code reply} column.
     * @param isAnswer true to return reviews that reference an answer, false for reviews that reference a question.
     * @return a List of Review objects matching the criteria; the list will be empty if there are no matches.
     * @throws RuntimeException if a database error occurs while querying reviews.
     */
	public java.util.List<Review> readReviewsFor(String reply, boolean isAnswer) {
	    final String sql = "SELECT Id, date, userName, body, reply, isAnswer "
	            + "FROM reviews WHERE reply=? AND isAnswer=? "
	            + "ORDER BY date DESC, Id ASC";
	    java.util.List<Review> out = new java.util.ArrayList<>();
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, reply);
	        ps.setString(2, isAnswer ? "1" : "0");
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) out.add(mapReview(rs));
	        }
	        return out;
	    } catch (SQLException e) {
	        throw new RuntimeException("readReviewsFor failed", e);
	    }
	}

    /**
     * Updates the contents of the review with the given id and stamps the review date to the current time.
     *
     * @param id the id of the review to update.
     * @param newBody the new body text to set for the review.
     * @return the number of rows affected (should be 1 if the review existed and was updated, otherwise 0).
     * @throws RuntimeException if a database error occurs while updating the review.
     */
	public int updateReview(int id, String newBody) {
	    final String sql = "UPDATE reviews SET body=?, date=? WHERE Id=?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newBody);
	        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
	        ps.setInt(3, id);
	        return ps.executeUpdate();
	    } catch (SQLException e) {
	        throw new RuntimeException("updateReview failed", e);
	    }
	}

    /**
     * Deletes the review with the specified id from the {@code reviews} table.
     *
     * @param id the id of the review to delete.
     * @return the number of rows deleted (1 if a row was deleted, 0 if no row matched).
     * @throws RuntimeException if a database error occurs while deleting the review.
     */
	public int deleteReview(int id) {
	    final String sql = "DELETE FROM reviews WHERE Id=?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, id);
	        return ps.executeUpdate();
	    } catch (SQLException e) {
	        throw new RuntimeException("deleteReview failed", e);
	    }
	}

    /**
     * Maps the current row of the provided ResultSet to a {@code Review} instance.
     *
     * @param rs the ResultSet positioned at a review row containing columns: "Id", "date", "userName", "body", "reply", "isAnswer".
     * @return a newly allocated Review populated with values from the current ResultSet row.
     * @throws SQLException if reading from the ResultSet fails.
     */
	private static Review mapReview(ResultSet rs) throws SQLException {
	    Timestamp ts = rs.getTimestamp("date");
	    LocalDateTime when = ts == null ? null : ts.toLocalDateTime();
	    Review r = new Review();
	    r.setId(rs.getInt("Id"));
	    r.setDate(when);
	    r.setUserName(rs.getString("userName"));
	    r.setBody(rs.getString("body"));
	    r.setReply(rs.getString("reply"));
	    r.setAnswer("1".equals(rs.getString("isAnswer")));
	    return r;
	}
	
	  /**
     * Adds a trusted reviewer rating for a student into the {@code trustedReviewers} table.
     *
     * @param student the name of the student who trusts the reviewer.
     * @param reviewer the name of the reviewer being rated.
     * @param rating the rating value (expected 1..5).
     * @return a status message such as "Success!" or an error message.
     */
	
	public String trustReviewer (String student, String reviewer, int rating) {
		String saveReviewer = "INSERT INTO trustedReviewers (studentName, reviewerName, rating) VALUES (?, ?, ?)";
		try(PreparedStatement pstmt = connection.prepareStatement(saveReviewer)) {
			pstmt.setString(1, student);
			pstmt.setString(2, reviewer);
			pstmt.setInt(3, rating);
			pstmt.executeUpdate();
			return "Success!";
		} catch (SQLException e) {
			e.printStackTrace();
			if(rating > 5 || rating < 1) {
				return "Rating must be between 1-5";
			}
			else { return ""; }
		}
	}
		
    /**
     * Reads all trusted reviewer ratings for a specific student.
     *
     * @param student the student name to look up in the {@code trustedReviewers} table.
     * @return an ArrayList of TrustedReview objects for the student; returns an empty list if none found.
     */
	public ArrayList<TrustedReview> readTrustedReviews (String student) {
		String readReviewer = "SELECT reviewerName, rating FROM trustedReviewers WHERE studentName = ?";
		ArrayList<TrustedReview> trustedReviews = new ArrayList<>();
		try(PreparedStatement pstmt = connection.prepareStatement(readReviewer)) {
			pstmt.setString(1, student);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				TrustedReview trustedReview = new TrustedReview(rs.getString("reviewerName"), rs.getInt("rating"));
				trustedReviews.add(trustedReview);
			}
			return trustedReviews;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
		
    /**
     * Edits (updates) the rating for a particular student's trusted reviewer entry.
     *
     * @param student the name of the student who owns the trusted reviewer record.
     * @param reviewer the reviewer name whose rating should be changed.
     * @param rating the new rating value (expected 1..5).
     * @return status string such as "SUCCESS", "RATING NOT VALID", or "USER NOT FOUND".
     */
	public String editTrustedReviewer (String student, String reviewer, int rating) {
		String editReview = "UPDATE trustedReviewers SET rating = ? WHERE studentName = ? AND reviewerName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(editReview)) {
			pstmt.setString(1, String.valueOf(rating));
			pstmt.setString(2, student);
			pstmt.setString(3, reviewer);
			pstmt.executeUpdate();
			return "SUCCESS";
		} catch(SQLException e) {
			e.printStackTrace();
			if(rating > 5 || rating < 1) {
				return "RATING NOT VALID";
			}
			else {
				return "USER NOT FOUND";
			}
		}
	}
		
    /**
     * Deletes a trusted reviewer record for the given student and reviewer.
     *
     * @param student the student who recorded the trusted reviewer.
     * @param reviewer the reviewer to delete from the student's trusted list.
     * @return "SUCCESS" if deletion completed, otherwise "USER NOT FOUND" (based on current exception handling).
     */
	public String deleteTrustedReviewer(String student, String reviewer) {
		String deleteReviewer = "DELETE FROM trustedReviewers WHERE studentName = ? AND reviewerName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(deleteReviewer)) {
			pstmt.setString(1, student);
			pstmt.setString(2, reviewer);
			pstmt.executeUpdate();
			return "SUCCESS";
		} catch(SQLException e) {
			return "USER NOT FOUND";
		}
	}
	
	/**
	 * Loads all questions, answers, and reviews for staff to moderate.
	 * Items are merged and sorted newest-first by date.
	 *
	 * @return list of ModerationObj entries across all supported types
	 * @throws SQLException if a database error occurs
	 */
	public java.util.List<ModerationObj> loadAllModerationItems() throws SQLException {
		java.util.List<ModerationObj> items = new java.util.ArrayList<ModerationObj>();
		
		// Questions
		String qSql = "SELECT q.id, q.date, q.userName, q.body, "
				+ "d.contentId IS NOT NULL AS deleted "
				+ "FROM DiscussionQuestions q "
				+ "LEFT JOIN ModerationDeleted d "
				+ "ON d.type='QUESTION' AND d.contentId=q.id";
		try (PreparedStatement ps = connection.prepareStatement(qSql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("date");
				LocalDateTime dt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
				boolean deleted = rs.getBoolean("deleted");
				ModerationObj obj = new ModerationObj(
						ModerationObj.ContentType.QUESTION,
						rs.getInt("id"),
						dt,
						rs.getString("userName"),
						rs.getString("body"),
						deleted);
				items.add(obj);
			}
		}
		
		// Answers
		String aSql = "SELECT a.id, a.date, a.userName, a.body, "
				+ "d.contentId IS NOT NULL AS deleted "
				+ "FROM DiscussionAnswers a "
				+ "LEFT JOIN ModerationDeleted d "
				+ "ON d.type='ANSWER' AND d.contentId=a.id";
		try (PreparedStatement ps = connection.prepareStatement(aSql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("date");
				LocalDateTime dt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
				boolean deleted = rs.getBoolean("deleted");
				ModerationObj obj = new ModerationObj(
						ModerationObj.ContentType.ANSWER,
						rs.getInt("id"),
						dt,
						rs.getString("userName"),
						rs.getString("body"),
						deleted);
				items.add(obj);
			}
		}
		
		// Reviews
		String rSql = "SELECT r.Id AS id, r.date, r.userName, r.body, "
				+ "d.contentId IS NOT NULL AS deleted "
				+ "FROM reviews r "
				+ "LEFT JOIN ModerationDeleted d "
				+ "ON d.type='REVIEW' AND d.contentId=r.Id";
		try (PreparedStatement ps = connection.prepareStatement(rSql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("date");
				LocalDateTime dt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
				boolean deleted = rs.getBoolean("deleted");
				ModerationObj obj = new ModerationObj(
						ModerationObj.ContentType.REVIEW,
						rs.getInt("id"),
						dt,
						rs.getString("userName"),
						rs.getString("body"),
						deleted);
				items.add(obj);
			}
		}
		
		// newest-first
		items.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
		return items;
	}
	
	/**
	 * Loads moderation items filtered by it's type.
	 *
	 * @param type one of "QUESTION", "ANSWER", or "REVIEW".
	 * @return filtered list of ModerationObj items.
	 * @throws SQLException if a database error occurs.
	 */
	public java.util.List<ModerationObj> loadModerationItemsByType(String type) throws SQLException {
		ModerationObj.ContentType ct = ModerationObj.ContentType.valueOf(type);
		java.util.List<ModerationObj> all = loadAllModerationItems();
		java.util.List<ModerationObj> out = new java.util.ArrayList<ModerationObj>();
		for (ModerationObj m : all) {
			if (m.getContentType() == ct) {
				out.add(m);
			}
		}
		return out;
	}
	
	/**
	 * Updates the text of a question, answer, or review.
	 *
	 * @param ct the content type
	 * @param id the primary key id of the record.
	 * @param newText new body text to set.
	 * @return true if a row was updated but will be false if null. 
	 * @throws SQLException if a database error occurs
	 */
	public boolean editContentText(ModerationObj.ContentType ct, int id, String newText) throws SQLException {
		if (newText == null || newText.trim().isEmpty()) {
			return false;
		}
		String sql;
		switch (ct) {
		case QUESTION:
			sql = "UPDATE DiscussionQuestions SET body=? WHERE id=?";
			break;
		case ANSWER:
			sql = "UPDATE DiscussionAnswers SET body=? WHERE id=?";
			break;
		case REVIEW:
			sql = "UPDATE reviews SET body=? WHERE Id=?";
			break;
		default:
			return false;
		}
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, newText.trim());
			ps.setInt(2, id);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}
	
	/**
	 * Signifies an item as deleted by inserting a row into ModerationDeleted.
	 * The original content remains in this table for auditing purposes.
	 *
	 * @param ct the content type
	 * @param id the primary key id of the record
	 * @return true if a marker row exists or was inserted
	 * @throws SQLException if a database error occurs
	 */
	public boolean softDeleteContent(ModerationObj.ContentType ct, int id) throws SQLException {
		// 1) check if the row already exists
		String checkSql = "SELECT 1 FROM ModerationDeleted WHERE type=? AND contentId=?";
		try (PreparedStatement check = connection.prepareStatement(checkSql)) {
			check.setString(1, ct.name());
			check.setInt(2, id);
			try (ResultSet rs = check.executeQuery()) {
				if (rs.next()) {
					return true; // already marked as deleted
				}
			}
		}
		
		// 2) insert the marker
		String insertSql = "INSERT INTO ModerationDeleted(type, contentId) VALUES(?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
			ps.setString(1, ct.name());
			ps.setInt(2, id);
			int inserted = ps.executeUpdate();
			return inserted > 0;
		}
	}
	
	/**
	 * Adds a private moderation note to a question, answer, or review.
	 *
	 * @param ct the content type
	 * @param id the primary key id of the record
	 * @param moderator the staff username adding the note
	 * @param noteText the note contents (must be non-empty)
	 * @return true if the note was inserted; false otherwise
	 * @throws SQLException if a database error occurs
	 */
	public boolean addModerationNote(ModerationObj.ContentType ct, int id, String moderator, String noteText) throws SQLException {
		if (moderator == null || moderator.trim().isEmpty() || noteText == null || noteText.trim().isEmpty()) {
			return false;
		}
		String sql = "INSERT INTO ModerationNotes(type, contentId, moderator, note, date) VALUES(?,?,?,?,CURRENT_TIMESTAMP())";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, ct.name());
			ps.setInt(2, id);
			ps.setString(3, moderator.trim());
			ps.setString(4, noteText.trim());
			int inserted = ps.executeUpdate();
			return inserted > 0;
		}
	}
	
	/**
	 * Reads all private moderation notes for a content record, newest-first.
	 *
	 * @param ct the content type
	 * @param id the primary key id of the record
	 * @return list of formatted notes like "[2025-11-16T12:00] staff: note"
	 * @throws SQLException if a database error occurs
	 */
	public java.util.List<String> getModerationNotes(ModerationObj.ContentType ct, int id) throws SQLException {
		java.util.List<String> notes = new java.util.ArrayList<String>();
		String sql = "SELECT date, moderator, note FROM ModerationNotes WHERE type=? AND contentId=? ORDER BY date DESC";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, ct.name());
			ps.setInt(2, id);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Timestamp t = rs.getTimestamp("date");
					LocalDateTime at = (t != null) ? t.toLocalDateTime() : LocalDateTime.now();
					String entry = "[" + at + "] " + rs.getString("moderator") + ": " + rs.getString("note");
					notes.add(entry);
				}
			}
		}
		return notes;
	}
	
	public void clearDatabase() {
        try(PreparedStatement pstmt = connection.prepareStatement("DROP ALL OBJECTS")) {
            pstmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
	

}


