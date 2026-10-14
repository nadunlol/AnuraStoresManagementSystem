package com.anurastores.test;

import com.anurastores.dao.ReportDAO;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class ReportDAOTest {

    private final ReportDAO reportDAO =
            new ReportDAO();

    @Test
    void testGetSalesTotal() {

        Date fromDate =
                Date.valueOf("2000-01-01");

        Date toDate =
                Date.valueOf("2100-12-31");

        BigDecimal total =
                reportDAO.getSalesTotal(
                        fromDate,
                        toDate
                );

        assertNotNull(total);
        assertTrue(
                total.compareTo(BigDecimal.ZERO) >= 0
        );
    }
    
    @Test
    void testGetCompletedSalesCount() {

        Date fromDate =
                Date.valueOf("2000-01-01");

        Date toDate =
                Date.valueOf("2100-12-31");

        int count =
                reportDAO.getCompletedSalesCount(
                        fromDate,
                        toDate
                );

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetCancelledSalesCount() {

        Date fromDate =
                Date.valueOf("2000-01-01");

        Date toDate =
                Date.valueOf("2100-12-31");

        int count =
                reportDAO.getCancelledSalesCount(
                        fromDate,
                        toDate
                );

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetAttendanceCount() {

        Date fromDate =
                Date.valueOf("2000-01-01");

        Date toDate =
                Date.valueOf("2100-12-31");

        int count =
                reportDAO.getAttendanceCount(
                        "PRESENT",
                        fromDate,
                        toDate
                );

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetLowStockCount() {

        int count =
                reportDAO.getLowStockCount();

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetOutOfStockCount() {

        int count =
                reportDAO.getOutOfStockCount();

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetActiveProductCount() {

        int count =
                reportDAO.getActiveProductCount();

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetActiveEmployeeCount() {

        int count =
                reportDAO.getActiveEmployeeCount();

        assertTrue(count >= 0);
    }
    
    @Test
    void testGetActiveSupplierCount() {

        int count =
                reportDAO.getActiveSupplierCount();

        assertTrue(count >= 0);
    }
    
}