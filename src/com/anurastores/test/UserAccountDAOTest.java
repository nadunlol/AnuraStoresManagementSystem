package com.anurastores.test;

import com.anurastores.dao.UserAccountDAO;
import com.anurastores.model.UserAccount;
import com.anurastores.dao.UserDAO;
import com.anurastores.dao.EmployeeDAO;
import com.anurastores.model.Employee;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountDAOTest {

    private final UserAccountDAO userAccountDAO =
            new UserAccountDAO();

    @Test
    void testGetAllUserAccounts() {

        List<UserAccount> accounts =
                userAccountDAO.getAllUserAccounts();

        assertNotNull(accounts);
    }
    
    @Test
    void testSearchUserAccounts() {

        List<UserAccount> accounts =
                userAccountDAO.searchUserAccounts("admin");

        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
    }
    
    @Test
    void testUsernameExists() {

        boolean exists =
                userAccountDAO.usernameExists(
                        "admin",
                        0
                );

        assertTrue(exists);
    }
    
    @Test
    void testEmployeeHasAccount() {

        List<UserAccount> accounts =
                userAccountDAO.searchUserAccounts("admin");

        assertFalse(accounts.isEmpty());

        UserAccount account =
                accounts.get(0);

        boolean hasAccount =
                userAccountDAO.employeeHasAccount(
                        account.getEmployeeId(),
                        0
                );

        assertTrue(hasAccount);
    }
    
    @Test
    void testAddUserAccount() {

        long timestamp = System.currentTimeMillis();

        String nic =
                String.valueOf(timestamp).substring(2);

        String phone =
                "07" + String.valueOf(timestamp).substring(2);

        Employee employee = new Employee(
                0,
                "JUnit",
                "UserAccount",
                nic,
                phone,
                "useraccount" + timestamp + "@test.com",
                "JUnit User Account Address",
                "Tester",
                java.sql.Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO =
                new EmployeeDAO();

        assertTrue(
                employeeDAO.addEmployee(employee)
        );

        List<Employee> employees =
                employeeDAO.searchEmployees(nic);

        assertFalse(employees.isEmpty());

        int employeeId =
                employees.get(0).getEmployeeId();

        String username =
                "junituser" + timestamp;

        assertTrue(
                userAccountDAO.addUserAccount(
                        employeeId,
                        username,
                        "test123",
                        "EMPLOYEE"
                )
        );

        assertTrue(
                userAccountDAO.usernameExists(
                        username,
                        0
                )
        );
    }
    
    @Test
    void testUpdateUserAccount() {

        long timestamp = System.currentTimeMillis();

        String nic =
                String.valueOf(timestamp).substring(2);

        String phone =
                "07" + String.valueOf(timestamp).substring(2);

        Employee employee = new Employee(
                0,
                "JUnit",
                "UpdateUser",
                nic,
                phone,
                "updateuser" + timestamp + "@test.com",
                "JUnit Update User Address",
                "Tester",
                java.sql.Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO =
                new EmployeeDAO();

        assertTrue(
                employeeDAO.addEmployee(employee)
        );

        List<Employee> employees =
                employeeDAO.searchEmployees(nic);

        assertFalse(employees.isEmpty());

        int employeeId =
                employees.get(0).getEmployeeId();

        String username =
                "junitupdate" + timestamp;

        assertTrue(
                userAccountDAO.addUserAccount(
                        employeeId,
                        username,
                        "test123",
                        "EMPLOYEE"
                )
        );

        List<UserAccount> accounts =
                userAccountDAO.searchUserAccounts(
                        username
                );

        assertFalse(accounts.isEmpty());

        UserAccount account =
                accounts.get(0);

        String updatedUsername =
                "junitupdated" + timestamp;

        assertTrue(
                userAccountDAO.updateUserAccount(
                        account.getUserId(),
                        employeeId,
                        updatedUsername,
                        "ADMIN",
                        "ACTIVE"
                )
        );

        List<UserAccount> updatedAccounts =
                userAccountDAO.searchUserAccounts(
                        updatedUsername
                );

        assertFalse(updatedAccounts.isEmpty());

        UserAccount updatedAccount =
                updatedAccounts.get(0);

        assertEquals(
                updatedUsername,
                updatedAccount.getUsername()
        );

        assertEquals(
                "ADMIN",
                updatedAccount.getRole()
        );
    }
    
    @Test
    void testResetPassword() {

        List<UserAccount> accounts =
                userAccountDAO.searchUserAccounts("admin");

        assertFalse(accounts.isEmpty());

        UserAccount account =
                accounts.get(0);

        String newPassword =
                "JUnitReset123";

        assertTrue(
                userAccountDAO.resetPassword(
                        account.getUserId(),
                        newPassword
                )
        );

        UserDAO userDAO =
                new UserDAO();

        UserAccount accountCheck =
                account;

        assertNotNull(accountCheck);

        assertNotNull(
                userDAO.authenticate(
                        "admin",
                        newPassword
                )
        );

        assertNull(
                userDAO.authenticate(
                        "admin",
                        "admin123"
                )
        );

        assertTrue(
                userAccountDAO.resetPassword(
                        account.getUserId(),
                        "admin123"
                )
        );
    }
    
    
}