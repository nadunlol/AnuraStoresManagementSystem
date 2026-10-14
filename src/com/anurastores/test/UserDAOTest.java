package com.anurastores.test;

import com.anurastores.dao.UserDAO;
import com.anurastores.model.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private final UserDAO userDAO =
            new UserDAO();

    @Test
    void testAuthenticate() {

        User user =
                userDAO.authenticate("admin", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }
    
    @Test
    void testAuthenticateWithWrongPassword() {

        User user =
                userDAO.authenticate("admin", "wrongpassword");

        assertNull(user);
    }
    
    @Test
    void testAuthenticateWithInvalidUsername() {

        User user =
                userDAO.authenticate(
                        "nonexistent_user",
                        "admin123"
                );

        assertNull(user);
    }
    
    @Test
    void testAuthenticateWithEmptyCredentials() {

        User user =
                userDAO.authenticate("", "");

        assertNull(user);
    }
    
    
}