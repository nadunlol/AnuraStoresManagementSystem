package com.anurastores.test;

import com.anurastores.dao.EmployeeDAO;
import com.anurastores.model.Employee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class EmployeeDAOTest {

    private final EmployeeDAO employeeDAO =
            new EmployeeDAO();

    @Test
    void testGetAllEmployees() {

        List<Employee> employees =
                employeeDAO.getAllEmployees();

        assertNotNull(employees);
    }
    
    @Test
    void testSearchEmployees() {

        List<Employee> employees =
                employeeDAO.searchEmployees("a");

        assertNotNull(employees);
    }
    
    @Test
    void testAddEmployee() {

        com.anurastores.model.Employee employee =
                new com.anurastores.model.Employee();

        long timestamp =
                System.currentTimeMillis();

        employee.setFirstName("JUnit");
        employee.setLastName("TestEmployee");
        employee.setNic("JUNIT" + timestamp);
        employee.setPhone("07" + String.valueOf(timestamp).substring(2));
        employee.setEmail("junit" + timestamp + "@test.com");
        employee.setAddress("JUnit Test Address");
        employee.setPosition("TESTER");
        employee.setHireDate(
                java.sql.Date.valueOf("2026-01-01"));
        employee.setStatus("ACTIVE");

        boolean result =
                employeeDAO.addEmployee(employee);

        assertTrue(result);
    }
    
    @Test
    void testUpdateEmployee() {

        com.anurastores.model.Employee employee =
                new com.anurastores.model.Employee();

        long timestamp =
                System.currentTimeMillis();

        String nic =
                String.valueOf(timestamp)
                     .substring(2);

        employee.setFirstName("JUnit");
        employee.setLastName("UpdateTest");
        employee.setNic(nic);
        employee.setPhone(
                "08" +
                String.valueOf(timestamp)
                     .substring(2));

        employee.setEmail(
                "junitupdate" +
                timestamp +
                "@test.com");

        employee.setAddress("Original Address");
        employee.setPosition("TESTER");

        employee.setHireDate(
                java.sql.Date.valueOf("2026-01-01"));

        employee.setStatus("ACTIVE");

        boolean added =
                employeeDAO.addEmployee(employee);

        assertTrue(added);

        List<Employee> employees =
                employeeDAO.searchEmployees(nic);

        assertFalse(employees.isEmpty());

        com.anurastores.model.Employee addedEmployee =
                employees.get(0);

        addedEmployee.setFirstName("Updated");
        addedEmployee.setLastName("Employee");
        addedEmployee.setAddress("Updated Address");
        addedEmployee.setPosition("MANAGER");

        boolean updated =
                employeeDAO.updateEmployee(
                        addedEmployee);

        assertTrue(updated);

        List<Employee> updatedEmployees =
                employeeDAO.searchEmployees(nic);

        assertFalse(updatedEmployees.isEmpty());

        com.anurastores.model.Employee result =
                updatedEmployees.get(0);

        assertEquals(
                "Updated",
                result.getFirstName());

        assertEquals(
                "Employee",
                result.getLastName());

        assertEquals(
                "Updated Address",
                result.getAddress());

        assertEquals(
                "MANAGER",
                result.getPosition());
    }
    
    @Test
    void testDeactivateEmployee() {

        com.anurastores.model.Employee employee =
                new com.anurastores.model.Employee();

        long timestamp =
                System.currentTimeMillis();

        String nic =
                String.valueOf(timestamp)
                        .substring(2);

        employee.setFirstName("JUnit");
        employee.setLastName("Deactivate");
        employee.setNic(nic);

        employee.setPhone(
                "09" +
                String.valueOf(timestamp)
                        .substring(2));

        employee.setEmail(
                "junitdeactivate" +
                timestamp +
                "@test.com");

        employee.setAddress("Deactivate Test Address");
        employee.setPosition("TESTER");

        employee.setHireDate(
                java.sql.Date.valueOf("2026-01-01"));

        employee.setStatus("ACTIVE");

        boolean added =
                employeeDAO.addEmployee(employee);

        assertTrue(added);

        List<Employee> employees =
                employeeDAO.searchEmployees(nic);

        assertFalse(employees.isEmpty());

        com.anurastores.model.Employee addedEmployee =
                employees.get(0);

        int employeeId =
                addedEmployee.getEmployeeId();

        boolean deactivated =
                employeeDAO.deactivateEmployee(
                        employeeId);

        assertTrue(deactivated);

        List<Employee> deactivatedEmployees =
                employeeDAO.searchEmployees(nic);

        assertFalse(deactivatedEmployees.isEmpty());

        com.anurastores.model.Employee result =
                deactivatedEmployees.get(0);

        assertEquals(
                "INACTIVE",
                result.getStatus());
    }
}