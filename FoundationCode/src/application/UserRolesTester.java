package application;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tester for User Roles which include Staff. Quick tester without DB and/or UI.
 */
public class UserRolesTester {

    @Test
    public void testInitialSingleRole() {
        User u = new User("sam", "pw", "user", "s@x.com");
        assertEquals("user", u.getRole());
        assertTrue(u.getRoleList().contains("user"));
    }

    @Test
    public void testAddRole_singleAndCommaSeparated() {
        User u = new User("sam", "pw", "user", "s@x.com");

        u.setRole("admin");                // Singular role
        assertTrue(u.getRoleList().contains("admin"));

        u.setRole("instructor, staff");   // Multiple roles
        assertTrue(u.getRoleList().contains("instructor"));
        assertTrue(u.getRoleList().contains("staff"));

        // Summary of the roles
        String summary = u.getRole();
        assertTrue(summary.contains("user"));
        assertTrue(summary.contains("admin"));
        assertTrue(summary.contains("instructor"));
        assertTrue(summary.contains("staff"));
    }

    @Test
    public void testRemoveRole() {
        User u = new User("sam", "pw", "user", "s@x.com");
        u.setRole("staff");
        assertTrue(u.getRoleList().contains("staff"));

        u.removeRole("staff");
        assertFalse(u.getRoleList().contains("staff"));
    }
}