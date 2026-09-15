package com.anurastores.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.anurastores.util.DBConnection;

public class ReportDAO {

    public BigDecimal getSalesTotal(
            Date fromDate,
            Date toDate) {

        String sql =
                "SELECT COALESCE(SUM(total_amount), 0) AS total "
                + "FROM sale "
                + "WHERE sale_status = 'COMPLETED' "
                + "AND DATE(sale_date) BETWEEN ? AND ?";

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(1, fromDate);
            statement.setDate(2, toDate);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {
                    return result.getBigDecimal("total");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }


    public int getCompletedSalesCount(
            Date fromDate,
            Date toDate) {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM sale "
                + "WHERE sale_status = 'COMPLETED' "
                + "AND DATE(sale_date) BETWEEN ? AND ?";

        return getDateRangeCount(
                sql,
                fromDate,
                toDate);
    }


    public int getCancelledSalesCount(
            Date fromDate,
            Date toDate) {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM sale "
                + "WHERE sale_status = 'CANCELLED' "
                + "AND DATE(sale_date) BETWEEN ? AND ?";

        return getDateRangeCount(
                sql,
                fromDate,
                toDate);
    }


    public int getAttendanceCount(
            String status,
            Date fromDate,
            Date toDate) {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM attendance "
                + "WHERE attendance_status = ? "
                + "AND attendance_date BETWEEN ? AND ?";

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setDate(2, fromDate);
            statement.setDate(3, toDate);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {
                    return result.getInt("count_value");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public int getLowStockCount() {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM inventory i "
                + "INNER JOIN product p "
                + "ON i.product_id = p.product_id "
                + "WHERE p.status = 'ACTIVE' "
                + "AND i.quantity > 0 "
                + "AND i.quantity <= p.reorder_level";

        return getSimpleCount(sql);
    }


    public int getOutOfStockCount() {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM inventory i "
                + "INNER JOIN product p "
                + "ON i.product_id = p.product_id "
                + "WHERE p.status = 'ACTIVE' "
                + "AND i.quantity = 0";

        return getSimpleCount(sql);
    }


    public int getActiveProductCount() {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM product "
                + "WHERE status = 'ACTIVE'";

        return getSimpleCount(sql);
    }


    public int getActiveEmployeeCount() {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM employee "
                + "WHERE status = 'ACTIVE'";

        return getSimpleCount(sql);
    }


    public int getActiveSupplierCount() {

        String sql =
                "SELECT COUNT(*) AS count_value "
                + "FROM supplier "
                + "WHERE status = 'ACTIVE'";

        return getSimpleCount(sql);
    }


    private int getSimpleCount(String sql) {

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery()
        ) {

            if (result.next()) {
                return result.getInt("count_value");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    private int getDateRangeCount(
            String sql,
            Date fromDate,
            Date toDate) {

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(1, fromDate);
            statement.setDate(2, toDate);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {
                    return result.getInt("count_value");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}