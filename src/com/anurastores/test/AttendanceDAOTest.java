package com.anurastores.test;

import com.anurastores.dao.AttendanceDAO;
import com.anurastores.model.Attendance;
import com.anurastores.dao.EmployeeDAO;
import com.anurastores.model.Employee;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceDAOTest {

    private final AttendanceDAO attendanceDAO =
            new AttendanceDAO();

    @Test
    void testGetAllAttendance() {

        List<Attendance> attendanceList =
                attendanceDAO.getAllAttendance();

        assertNotNull(attendanceList);
    }
    
    @Test
    void testSearchAttendance() {

        List<Attendance> attendanceList =
                attendanceDAO.searchAttendance("1");

        assertNotNull(attendanceList);
    }
    
    @Test
    void testSearchByDateRange() {

        Date fromDate = Date.valueOf("2000-01-01");
        Date toDate = Date.valueOf("2100-12-31");

        List<Attendance> attendanceList =
                attendanceDAO.searchByDateRange(fromDate, toDate);

        assertNotNull(attendanceList);
    }
    
    @Test
    void testAddAttendance() {

        long timestamp = System.currentTimeMillis();

        Employee employee = new Employee(
                0,
                "JUnit",
                "Attendance",
                String.valueOf(timestamp).substring(2),
                "07" + String.valueOf(timestamp).substring(2),
                "junit" + timestamp + "@test.com",
                "JUnit Test Address",
                "Tester",
                Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertTrue(employeeDAO.addEmployee(employee));

        List<Employee> employees =
                employeeDAO.searchEmployees(employee.getNic());

        assertFalse(employees.isEmpty());

        int employeeId = employees.get(0).getEmployeeId();

        Attendance attendance = new Attendance();

        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(
                Date.valueOf("2026-09-29")
        );
        attendance.setCheckIn(
                Time.valueOf("09:00:00")
        );
        attendance.setCheckOut(
                Time.valueOf("17:00:00")
        );
        attendance.setAttendanceStatus("PRESENT");
        attendance.setRemarks("JUnit test attendance");

        assertTrue(attendanceDAO.addAttendance(attendance));
    }
    
    @Test
    void testUpdateAttendance() {

        long timestamp = System.currentTimeMillis();

        Employee employee = new Employee(
                0,
                "JUnit",
                "UpdateAttendance",
                String.valueOf(timestamp).substring(2),
                "07" + String.valueOf(timestamp).substring(2),
                "update" + timestamp + "@test.com",
                "JUnit Update Address",
                "Tester",
                Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertTrue(employeeDAO.addEmployee(employee));

        List<Employee> employees =
                employeeDAO.searchEmployees(employee.getNic());

        assertFalse(employees.isEmpty());

        int employeeId = employees.get(0).getEmployeeId();

        Date attendanceDate =
                Date.valueOf("2026-09-28");

        Attendance attendance = new Attendance();

        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(attendanceDate);
        attendance.setCheckIn(Time.valueOf("09:00:00"));
        attendance.setCheckOut(Time.valueOf("17:00:00"));
        attendance.setAttendanceStatus("PRESENT");
        attendance.setRemarks("Before update");

        assertTrue(attendanceDAO.addAttendance(attendance));

        List<Attendance> attendanceList =
                attendanceDAO.searchByDateRange(
                        attendanceDate,
                        attendanceDate
                );

        Attendance addedAttendance = null;

        for (Attendance item : attendanceList) {
            if (item.getEmployeeId() == employeeId) {
                addedAttendance = item;
                break;
            }
        }

        assertNotNull(addedAttendance);

        addedAttendance.setCheckIn(Time.valueOf("08:30:00"));
        addedAttendance.setCheckOut(Time.valueOf("17:30:00"));
        addedAttendance.setAttendanceStatus("LATE");
        addedAttendance.setRemarks("After update");

        assertTrue(attendanceDAO.updateAttendance(addedAttendance));
    }
    
    @Test
    void testDeleteAttendance() {

        long timestamp = System.currentTimeMillis();

        Employee employee = new Employee(
                0,
                "JUnit",
                "DeleteAttendance",
                String.valueOf(timestamp).substring(2),
                "07" + String.valueOf(timestamp).substring(2),
                "delete" + timestamp + "@test.com",
                "JUnit Delete Address",
                "Tester",
                Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertTrue(employeeDAO.addEmployee(employee));

        List<Employee> employees =
                employeeDAO.searchEmployees(employee.getNic());

        assertFalse(employees.isEmpty());

        int employeeId = employees.get(0).getEmployeeId();

        Date attendanceDate =
                Date.valueOf("2026-09-27");

        Attendance attendance = new Attendance();

        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(attendanceDate);
        attendance.setCheckIn(Time.valueOf("09:00:00"));
        attendance.setCheckOut(Time.valueOf("17:00:00"));
        attendance.setAttendanceStatus("PRESENT");
        attendance.setRemarks("Delete test");

        assertTrue(attendanceDAO.addAttendance(attendance));

        List<Attendance> attendanceList =
                attendanceDAO.searchByDateRange(
                        attendanceDate,
                        attendanceDate
                );

        Attendance addedAttendance = null;

        for (Attendance item : attendanceList) {
            if (item.getEmployeeId() == employeeId) {
                addedAttendance = item;
                break;
            }
        }

        assertNotNull(addedAttendance);

        int attendanceId =
                addedAttendance.getAttendanceId();

        assertTrue(
                attendanceDAO.deleteAttendance(attendanceId)
        );

        List<Attendance> afterDelete =
                attendanceDAO.searchByDateRange(
                        attendanceDate,
                        attendanceDate
                );

        boolean found = false;

        for (Attendance item : afterDelete) {
            if (item.getAttendanceId() == attendanceId) {
                found = true;
                break;
            }
        }

        assertFalse(found);
    }
    
    @Test
    void testAttendanceExists() {

        long timestamp = System.currentTimeMillis();

        Employee employee = new Employee(
                0,
                "JUnit",
                "ExistsAttendance",
                String.valueOf(timestamp).substring(2),
                "07" + String.valueOf(timestamp).substring(2),
                "exists" + timestamp + "@test.com",
                "JUnit Exists Address",
                "Tester",
                Date.valueOf("2026-09-29"),
                "ACTIVE"
        );

        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertTrue(employeeDAO.addEmployee(employee));

        List<Employee> employees =
                employeeDAO.searchEmployees(employee.getNic());

        assertFalse(employees.isEmpty());

        int employeeId = employees.get(0).getEmployeeId();

        Date attendanceDate =
                Date.valueOf("2026-09-26");

        Attendance attendance = new Attendance();

        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(attendanceDate);
        attendance.setCheckIn(Time.valueOf("09:00:00"));
        attendance.setCheckOut(Time.valueOf("17:00:00"));
        attendance.setAttendanceStatus("PRESENT");
        attendance.setRemarks("Exists test");

        assertTrue(attendanceDAO.addAttendance(attendance));

        List<Attendance> attendanceList =
                attendanceDAO.searchByDateRange(
                        attendanceDate,
                        attendanceDate
                );

        Attendance addedAttendance = null;

        for (Attendance item : attendanceList) {
            if (item.getEmployeeId() == employeeId) {
                addedAttendance = item;
                break;
            }
        }

        assertNotNull(addedAttendance);

        int attendanceId =
                addedAttendance.getAttendanceId();

        assertTrue(
                attendanceDAO.attendanceExists(
                        employeeId,
                        attendanceDate,
                        0
                )
        );

        assertFalse(
                attendanceDAO.attendanceExists(
                        employeeId,
                        attendanceDate,
                        attendanceId
                )
        );
    }
    
    
}