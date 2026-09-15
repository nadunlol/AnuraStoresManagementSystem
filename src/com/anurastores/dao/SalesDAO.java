package com.anurastores.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.anurastores.model.Sale;
import com.anurastores.model.SaleItem;
import com.anurastores.util.DBConnection;

public class SalesDAO {

    // =====================================================
    // 1. CREATE SALE DIRECTLY AS COMPLETED
    // =====================================================
    public int createSale(
            int employeeId,
            List<SaleItem> items,
            String paymentMethod) {

        if (items == null || items.isEmpty()) {
            return -1;
        }

        if (!"CASH".equalsIgnoreCase(paymentMethod)
                && !"CARD".equalsIgnoreCase(paymentMethod)) {
            return -1;
        }

        Connection connection = null;

        try {

            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // Create sale header
            String saleSql =
                    "INSERT INTO sale "
                    + "(employee_id, total_amount, sale_status) "
                    + "VALUES (?, ?, 'COMPLETED')";

            int saleId;

            try (
                PreparedStatement saleStatement =
                        connection.prepareStatement(
                                saleSql,
                                Statement.RETURN_GENERATED_KEYS)
            ) {

                saleStatement.setInt(1, employeeId);
                saleStatement.setBigDecimal(
                        2,
                        BigDecimal.ZERO);

                saleStatement.executeUpdate();

                try (
                    ResultSet generatedKeys =
                            saleStatement.getGeneratedKeys()
                ) {

                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return -1;
                    }

                    saleId = generatedKeys.getInt(1);
                }
            }

            BigDecimal totalAmount =
                    BigDecimal.ZERO;

            // Process sale items
            for (SaleItem item : items) {

                if (item.getQuantity() <= 0) {
                    connection.rollback();
                    return -1;
                }

                String stockSql =
                        "SELECT i.quantity, "
                        + "p.selling_price, "
                        + "p.status "
                        + "FROM inventory i "
                        + "INNER JOIN product p "
                        + "ON i.product_id = p.product_id "
                        + "WHERE i.product_id = ? "
                        + "FOR UPDATE";

                int availableStock;
                BigDecimal unitPrice;
                String productStatus;

                try (
                    PreparedStatement stockStatement =
                            connection.prepareStatement(
                                    stockSql)
                ) {

                    stockStatement.setInt(
                            1,
                            item.getProductId());

                    try (
                        ResultSet result =
                                stockStatement.executeQuery()
                    ) {

                        if (!result.next()) {
                            connection.rollback();
                            return -1;
                        }

                        availableStock =
                                result.getInt("quantity");

                        unitPrice =
                                result.getBigDecimal(
                                        "selling_price");

                        productStatus =
                                result.getString("status");
                    }
                }

                if (!"ACTIVE".equalsIgnoreCase(
                        productStatus)) {

                    connection.rollback();
                    return -1;
                }

                if (item.getQuantity()
                        > availableStock) {

                    connection.rollback();
                    return -1;
                }

                BigDecimal subtotal =
                        unitPrice.multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()));

                totalAmount =
                        totalAmount.add(subtotal);

                // Insert sale item
                String itemSql =
                        "INSERT INTO sale_item "
                        + "(sale_id, product_id, quantity, "
                        + "unit_price, subtotal) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (
                    PreparedStatement itemStatement =
                            connection.prepareStatement(
                                    itemSql)
                ) {

                    itemStatement.setInt(
                            1,
                            saleId);

                    itemStatement.setInt(
                            2,
                            item.getProductId());

                    itemStatement.setInt(
                            3,
                            item.getQuantity());

                    itemStatement.setBigDecimal(
                            4,
                            unitPrice);

                    itemStatement.setBigDecimal(
                            5,
                            subtotal);

                    itemStatement.executeUpdate();
                }

                // Reduce inventory
                String inventorySql =
                        "UPDATE inventory "
                        + "SET quantity = quantity - ? "
                        + "WHERE product_id = ?";

                try (
                    PreparedStatement inventoryStatement =
                            connection.prepareStatement(
                                    inventorySql)
                ) {

                    inventoryStatement.setInt(
                            1,
                            item.getQuantity());

                    inventoryStatement.setInt(
                            2,
                            item.getProductId());

                    inventoryStatement.executeUpdate();
                }

                // Record stock movement
                String movementSql =
                        "INSERT INTO stock_movement "
                        + "(product_id, movement_type, "
                        + "quantity, remarks) "
                        + "VALUES (?, 'SALE', ?, ?)";

                try (
                    PreparedStatement movementStatement =
                            connection.prepareStatement(
                                    movementSql)
                ) {

                    movementStatement.setInt(
                            1,
                            item.getProductId());

                    movementStatement.setInt(
                            2,
                            -item.getQuantity());

                    movementStatement.setString(
                            3,
                            "Sale ID: " + saleId);

                    movementStatement.executeUpdate();
                }
            }

            // Update total
            String updateTotalSql =
                    "UPDATE sale "
                    + "SET total_amount = ? "
                    + "WHERE sale_id = ?";

            try (
                PreparedStatement updateStatement =
                        connection.prepareStatement(
                                updateTotalSql)
            ) {

                updateStatement.setBigDecimal(
                        1,
                        totalAmount);

                updateStatement.setInt(
                        2,
                        saleId);

                updateStatement.executeUpdate();
            }

            // Create payment
            String paymentSql =
                    "INSERT INTO payment "
                    + "(sale_id, payment_method, amount) "
                    + "VALUES (?, ?, ?)";

            try (
                PreparedStatement paymentStatement =
                        connection.prepareStatement(
                                paymentSql)
            ) {

                paymentStatement.setInt(
                        1,
                        saleId);

                paymentStatement.setString(
                        2,
                        paymentMethod.toUpperCase());

                paymentStatement.setBigDecimal(
                        3,
                        totalAmount);

                paymentStatement.executeUpdate();
            }

            connection.commit();

            return saleId;

        } catch (Exception e) {

            rollback(connection);
            e.printStackTrace();

            return -1;

        } finally {

            closeConnection(connection);
        }
    }


    // =====================================================
    // 2. VIEW ALL SALES
    // =====================================================
    public List<Sale> getAllSales() {

        List<Sale> sales =
                new ArrayList<Sale>();

        String sql =
                "SELECT s.sale_id, "
                + "s.employee_id, "
                + "CONCAT(e.first_name, ' ', e.last_name) "
                + "AS employee_name, "
                + "s.sale_date, "
                + "s.total_amount, "
                + "s.sale_status, "
                + "p.payment_method "
                + "FROM sale s "
                + "INNER JOIN employee e "
                + "ON s.employee_id = e.employee_id "
                + "LEFT JOIN payment p "
                + "ON s.sale_id = p.sale_id "
                + "ORDER BY s.sale_date DESC, "
                + "s.sale_id DESC";

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery()
        ) {

            while (result.next()) {

                sales.add(
                        createSaleFromResult(result));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;
    }


    // =====================================================
    // 3. SEARCH SALES
    // =====================================================
    public List<Sale> searchSales(
            String keyword) {

        List<Sale> sales =
                new ArrayList<Sale>();

        String sql =
                "SELECT s.sale_id, "
                + "s.employee_id, "
                + "CONCAT(e.first_name, ' ', e.last_name) "
                + "AS employee_name, "
                + "s.sale_date, "
                + "s.total_amount, "
                + "s.sale_status, "
                + "p.payment_method "
                + "FROM sale s "
                + "INNER JOIN employee e "
                + "ON s.employee_id = e.employee_id "
                + "LEFT JOIN payment p "
                + "ON s.sale_id = p.sale_id "
                + "WHERE CAST(s.sale_id AS CHAR) LIKE ? "
                + "OR CAST(s.employee_id AS CHAR) LIKE ? "
                + "OR e.first_name LIKE ? "
                + "OR e.last_name LIKE ? "
                + "OR CONCAT(e.first_name, ' ', e.last_name) LIKE ? "
                + "OR s.sale_status LIKE ? "
                + "OR p.payment_method LIKE ? "
                + "ORDER BY s.sale_date DESC, "
                + "s.sale_id DESC";

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            String value =
                    "%" + keyword + "%";

            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            statement.setString(4, value);
            statement.setString(5, value);
            statement.setString(6, value);
            statement.setString(7, value);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                while (result.next()) {

                    sales.add(
                            createSaleFromResult(result));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;
    }


    // =====================================================
    // 4. SEARCH SALES BY DATE RANGE
    // =====================================================
    public List<Sale> searchSalesByDateRange(
            Date fromDate,
            Date toDate) {

        List<Sale> sales =
                new ArrayList<Sale>();

        String sql =
                "SELECT s.sale_id, "
                + "s.employee_id, "
                + "CONCAT(e.first_name, ' ', e.last_name) "
                + "AS employee_name, "
                + "s.sale_date, "
                + "s.total_amount, "
                + "s.sale_status, "
                + "p.payment_method "
                + "FROM sale s "
                + "INNER JOIN employee e "
                + "ON s.employee_id = e.employee_id "
                + "LEFT JOIN payment p "
                + "ON s.sale_id = p.sale_id "
                + "WHERE DATE(s.sale_date) BETWEEN ? AND ? "
                + "ORDER BY s.sale_date DESC, "
                + "s.sale_id DESC";

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

                while (result.next()) {

                    sales.add(
                            createSaleFromResult(result));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;
    }


    // =====================================================
    // 5. GET ITEMS FOR ONE SALE
    // =====================================================
    public List<SaleItem> getSaleItems(
            int saleId) {

        List<SaleItem> items =
                new ArrayList<SaleItem>();

        String sql =
                "SELECT si.sale_item_id, "
                + "si.sale_id, "
                + "si.product_id, "
                + "p.product_name, "
                + "si.quantity, "
                + "si.unit_price, "
                + "si.subtotal "
                + "FROM sale_item si "
                + "INNER JOIN product p "
                + "ON si.product_id = p.product_id "
                + "WHERE si.sale_id = ? "
                + "ORDER BY si.sale_item_id";

        try (
            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(1, saleId);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                while (result.next()) {

                    SaleItem item =
                            new SaleItem(
                                    result.getInt(
                                            "sale_item_id"),

                                    result.getInt(
                                            "sale_id"),

                                    result.getInt(
                                            "product_id"),

                                    result.getString(
                                            "product_name"),

                                    result.getInt(
                                            "quantity"),

                                    result.getBigDecimal(
                                            "unit_price"),

                                    result.getBigDecimal(
                                            "subtotal")
                            );

                    items.add(item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }


    // =====================================================
    // 6. CANCEL COMPLETED SALE
    // Only COMPLETED sales can be cancelled.
    // Inventory is restored.
    // =====================================================
    public boolean cancelSale(
            int saleId) {

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Lock sale
            String saleCheckSql =
                    "SELECT sale_status "
                    + "FROM sale "
                    + "WHERE sale_id = ? "
                    + "FOR UPDATE";

            String saleStatus;

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                saleCheckSql)
            ) {

                statement.setInt(1, saleId);

                try (
                    ResultSet result =
                            statement.executeQuery()
                ) {

                    if (!result.next()) {

                        connection.rollback();
                        return false;
                    }

                    saleStatus =
                            result.getString(
                                    "sale_status");
                }
            }

            // IMPORTANT FIX:
            // PENDING sales have not reduced inventory.
            // Therefore only COMPLETED sales can be cancelled.
            if (!"COMPLETED".equalsIgnoreCase(
                    saleStatus)) {

                connection.rollback();
                return false;
            }

            String itemsSql =
                    "SELECT product_id, quantity "
                    + "FROM sale_item "
                    + "WHERE sale_id = ?";

            try (
                PreparedStatement itemStatement =
                        connection.prepareStatement(
                                itemsSql)
            ) {

                itemStatement.setInt(
                        1,
                        saleId);

                try (
                    ResultSet itemResult =
                            itemStatement.executeQuery()
                ) {

                    while (itemResult.next()) {

                        int productId =
                                itemResult.getInt(
                                        "product_id");

                        int quantity =
                                itemResult.getInt(
                                        "quantity");

                        // Lock inventory row
                        String lockInventorySql =
                                "SELECT quantity "
                                + "FROM inventory "
                                + "WHERE product_id = ? "
                                + "FOR UPDATE";

                        try (
                            PreparedStatement lockStatement =
                                    connection.prepareStatement(
                                            lockInventorySql)
                        ) {

                            lockStatement.setInt(
                                    1,
                                    productId);

                            try (
                                ResultSet lockResult =
                                        lockStatement.executeQuery()
                            ) {

                                if (!lockResult.next()) {

                                    connection.rollback();
                                    return false;
                                }
                            }
                        }

                        // Restore stock
                        String restoreSql =
                                "UPDATE inventory "
                                + "SET quantity = quantity + ? "
                                + "WHERE product_id = ?";

                        try (
                            PreparedStatement restoreStatement =
                                    connection.prepareStatement(
                                            restoreSql)
                        ) {

                            restoreStatement.setInt(
                                    1,
                                    quantity);

                            restoreStatement.setInt(
                                    2,
                                    productId);

                            restoreStatement.executeUpdate();
                        }

                        // Record SALE_CANCEL movement
                        String movementSql =
                                "INSERT INTO stock_movement "
                                + "(product_id, movement_type, "
                                + "quantity, remarks) "
                                + "VALUES (?, 'SALE_CANCEL', ?, ?)";

                        try (
                            PreparedStatement movementStatement =
                                    connection.prepareStatement(
                                            movementSql)
                        ) {

                            movementStatement.setInt(
                                    1,
                                    productId);

                            movementStatement.setInt(
                                    2,
                                    quantity);

                            movementStatement.setString(
                                    3,
                                    "Cancelled Sale ID: "
                                    + saleId);

                            movementStatement.executeUpdate();
                        }
                    }
                }
            }

            // Mark sale cancelled
            String cancelSql =
                    "UPDATE sale "
                    + "SET sale_status = 'CANCELLED' "
                    + "WHERE sale_id = ? "
                    + "AND sale_status = 'COMPLETED'";

            try (
                PreparedStatement cancelStatement =
                        connection.prepareStatement(
                                cancelSql)
            ) {

                cancelStatement.setInt(
                        1,
                        saleId);

                int updated =
                        cancelStatement.executeUpdate();

                if (updated == 0) {

                    connection.rollback();
                    return false;
                }
            }

            connection.commit();

            return true;

        } catch (Exception e) {

            rollback(connection);
            e.printStackTrace();

            return false;

        } finally {

            closeConnection(connection);
        }
    }


    // =====================================================
    // 7. SAVE SALE AS PENDING
    // Inventory is NOT reduced yet.
    // =====================================================
    public int savePendingSale(
            int employeeId,
            List<SaleItem> items) {

        if (items == null || items.isEmpty()) {
            return -1;
        }

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            String saleSql =
                    "INSERT INTO sale "
                    + "(employee_id, total_amount, sale_status) "
                    + "VALUES (?, 0.00, 'PENDING')";

            int saleId;

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                saleSql,
                                Statement.RETURN_GENERATED_KEYS)
            ) {

                statement.setInt(
                        1,
                        employeeId);

                statement.executeUpdate();

                try (
                    ResultSet keys =
                            statement.getGeneratedKeys()
                ) {

                    if (!keys.next()) {

                        connection.rollback();
                        return -1;
                    }

                    saleId =
                            keys.getInt(1);
                }
            }

            BigDecimal total =
                    BigDecimal.ZERO;

            for (SaleItem item : items) {

                if (item.getQuantity() <= 0) {

                    connection.rollback();
                    return -1;
                }

                String productSql =
                        "SELECT selling_price, status "
                        + "FROM product "
                        + "WHERE product_id = ?";

                BigDecimal price;
                String productStatus;

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    productSql)
                ) {

                    statement.setInt(
                            1,
                            item.getProductId());

                    try (
                        ResultSet result =
                                statement.executeQuery()
                    ) {

                        if (!result.next()) {

                            connection.rollback();
                            return -1;
                        }

                        price =
                                result.getBigDecimal(
                                        "selling_price");

                        productStatus =
                                result.getString(
                                        "status");
                    }
                }

                if (!"ACTIVE".equalsIgnoreCase(
                        productStatus)) {

                    connection.rollback();
                    return -1;
                }

                BigDecimal subtotal =
                        price.multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()));

                String itemSql =
                        "INSERT INTO sale_item "
                        + "(sale_id, product_id, quantity, "
                        + "unit_price, subtotal) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    itemSql)
                ) {

                    statement.setInt(
                            1,
                            saleId);

                    statement.setInt(
                            2,
                            item.getProductId());

                    statement.setInt(
                            3,
                            item.getQuantity());

                    statement.setBigDecimal(
                            4,
                            price);

                    statement.setBigDecimal(
                            5,
                            subtotal);

                    statement.executeUpdate();
                }

                total =
                        total.add(subtotal);
            }

            String totalSql =
                    "UPDATE sale "
                    + "SET total_amount = ? "
                    + "WHERE sale_id = ?";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                totalSql)
            ) {

                statement.setBigDecimal(
                        1,
                        total);

                statement.setInt(
                        2,
                        saleId);

                statement.executeUpdate();
            }

            connection.commit();

            return saleId;

        } catch (Exception e) {

            rollback(connection);
            e.printStackTrace();

            return -1;

        } finally {

            closeConnection(connection);
        }
    }


    // =====================================================
    // 8. UPDATE PENDING SALE
    // Only PENDING sales can be modified.
    // =====================================================
    public boolean updatePendingSale(
            int saleId,
            List<SaleItem> items) {

        if (items == null || items.isEmpty()) {
            return false;
        }

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Check status
            String statusSql =
                    "SELECT sale_status "
                    + "FROM sale "
                    + "WHERE sale_id = ? "
                    + "FOR UPDATE";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                statusSql)
            ) {

                statement.setInt(
                        1,
                        saleId);

                try (
                    ResultSet result =
                            statement.executeQuery()
                ) {

                    if (!result.next()) {

                        connection.rollback();
                        return false;
                    }

                    String status =
                            result.getString(
                                    "sale_status");

                    if (!"PENDING".equalsIgnoreCase(
                            status)) {

                        connection.rollback();
                        return false;
                    }
                }
            }

            // Remove old items
            String deleteSql =
                    "DELETE FROM sale_item "
                    + "WHERE sale_id = ?";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                deleteSql)
            ) {

                statement.setInt(
                        1,
                        saleId);

                statement.executeUpdate();
            }

            BigDecimal total =
                    BigDecimal.ZERO;

            // Insert updated items
            for (SaleItem item : items) {

                if (item.getQuantity() <= 0) {

                    connection.rollback();
                    return false;
                }

                String productSql =
                        "SELECT selling_price, status "
                        + "FROM product "
                        + "WHERE product_id = ?";

                BigDecimal price;
                String productStatus;

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    productSql)
                ) {

                    statement.setInt(
                            1,
                            item.getProductId());

                    try (
                        ResultSet result =
                                statement.executeQuery()
                    ) {

                        if (!result.next()) {

                            connection.rollback();
                            return false;
                        }

                        price =
                                result.getBigDecimal(
                                        "selling_price");

                        productStatus =
                                result.getString(
                                        "status");
                    }
                }

                if (!"ACTIVE".equalsIgnoreCase(
                        productStatus)) {

                    connection.rollback();
                    return false;
                }

                BigDecimal subtotal =
                        price.multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()));

                String insertSql =
                        "INSERT INTO sale_item "
                        + "(sale_id, product_id, quantity, "
                        + "unit_price, subtotal) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    insertSql)
                ) {

                    statement.setInt(
                            1,
                            saleId);

                    statement.setInt(
                            2,
                            item.getProductId());

                    statement.setInt(
                            3,
                            item.getQuantity());

                    statement.setBigDecimal(
                            4,
                            price);

                    statement.setBigDecimal(
                            5,
                            subtotal);

                    statement.executeUpdate();
                }

                total =
                        total.add(subtotal);
            }

            String updateSql =
                    "UPDATE sale "
                    + "SET total_amount = ? "
                    + "WHERE sale_id = ? "
                    + "AND sale_status = 'PENDING'";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                updateSql)
            ) {

                statement.setBigDecimal(
                        1,
                        total);

                statement.setInt(
                        2,
                        saleId);

                int updated =
                        statement.executeUpdate();

                if (updated == 0) {

                    connection.rollback();
                    return false;
                }
            }

            connection.commit();

            return true;

        } catch (Exception e) {

            rollback(connection);
            e.printStackTrace();

            return false;

        } finally {

            closeConnection(connection);
        }
    }


    // =====================================================
    // 9. COMPLETE PENDING SALE
    // Inventory reduced + payment created.
    // =====================================================
    public boolean completePendingSale(
            int saleId,
            String paymentMethod) {

        if (!"CASH".equalsIgnoreCase(paymentMethod)
                && !"CARD".equalsIgnoreCase(paymentMethod)) {

            return false;
        }

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Check pending status
            String statusSql =
                    "SELECT sale_status "
                    + "FROM sale "
                    + "WHERE sale_id = ? "
                    + "FOR UPDATE";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                statusSql)
            ) {

                statement.setInt(
                        1,
                        saleId);

                try (
                    ResultSet result =
                            statement.executeQuery()
                ) {

                    if (!result.next()) {

                        connection.rollback();
                        return false;
                    }

                    if (!"PENDING".equalsIgnoreCase(
                            result.getString(
                                    "sale_status"))) {

                        connection.rollback();
                        return false;
                    }
                }
            }

            // Load pending items
            List<SaleItem> items =
                    new ArrayList<SaleItem>();

            String itemSql =
                    "SELECT product_id, quantity, "
                    + "unit_price, subtotal "
                    + "FROM sale_item "
                    + "WHERE sale_id = ?";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                itemSql)
            ) {

                statement.setInt(
                        1,
                        saleId);

                try (
                    ResultSet result =
                            statement.executeQuery()
                ) {

                    while (result.next()) {

                        SaleItem item =
                                new SaleItem();

                        item.setProductId(
                                result.getInt(
                                        "product_id"));

                        item.setQuantity(
                                result.getInt(
                                        "quantity"));

                        item.setUnitPrice(
                                result.getBigDecimal(
                                        "unit_price"));

                        item.setSubtotal(
                                result.getBigDecimal(
                                        "subtotal"));

                        items.add(item);
                    }
                }
            }

            if (items.isEmpty()) {

                connection.rollback();
                return false;
            }

            BigDecimal total =
                    BigDecimal.ZERO;

            for (SaleItem item : items) {

                String inventorySql =
                        "SELECT i.quantity, "
                        + "p.status "
                        + "FROM inventory i "
                        + "INNER JOIN product p "
                        + "ON i.product_id = p.product_id "
                        + "WHERE i.product_id = ? "
                        + "FOR UPDATE";

                int availableStock;
                String productStatus;

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    inventorySql)
                ) {

                    statement.setInt(
                            1,
                            item.getProductId());

                    try (
                        ResultSet result =
                                statement.executeQuery()
                    ) {

                        if (!result.next()) {

                            connection.rollback();
                            return false;
                        }

                        availableStock =
                                result.getInt(
                                        "quantity");

                        productStatus =
                                result.getString(
                                        "status");
                    }
                }

                if (!"ACTIVE".equalsIgnoreCase(
                        productStatus)
                        || item.getQuantity()
                        > availableStock) {

                    connection.rollback();
                    return false;
                }

                // Reduce inventory
                String reduceSql =
                        "UPDATE inventory "
                        + "SET quantity = quantity - ? "
                        + "WHERE product_id = ?";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    reduceSql)
                ) {

                    statement.setInt(
                            1,
                            item.getQuantity());

                    statement.setInt(
                            2,
                            item.getProductId());

                    statement.executeUpdate();
                }

                // Stock movement
                String movementSql =
                        "INSERT INTO stock_movement "
                        + "(product_id, movement_type, "
                        + "quantity, remarks) "
                        + "VALUES (?, 'SALE', ?, ?)";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    movementSql)
                ) {

                    statement.setInt(
                            1,
                            item.getProductId());

                    statement.setInt(
                            2,
                            -item.getQuantity());

                    statement.setString(
                            3,
                            "Sale ID: "
                            + saleId);

                    statement.executeUpdate();
                }

                total =
                        total.add(
                                item.getSubtotal());
            }

            // Create payment
            String paymentSql =
                    "INSERT INTO payment "
                    + "(sale_id, payment_method, amount) "
                    + "VALUES (?, ?, ?)";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                paymentSql)
            ) {

                statement.setInt(
                        1,
                        saleId);

                statement.setString(
                        2,
                        paymentMethod.toUpperCase());

                statement.setBigDecimal(
                        3,
                        total);

                statement.executeUpdate();
            }

            // Complete sale
            String completeSql =
                    "UPDATE sale "
                    + "SET total_amount = ?, "
                    + "sale_status = 'COMPLETED' "
                    + "WHERE sale_id = ? "
                    + "AND sale_status = 'PENDING'";

            try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                completeSql)
            ) {

                statement.setBigDecimal(
                        1,
                        total);

                statement.setInt(
                        2,
                        saleId);

                int updated =
                        statement.executeUpdate();

                if (updated == 0) {

                    connection.rollback();
                    return false;
                }
            }

            connection.commit();

            return true;

        } catch (Exception e) {

            rollback(connection);
            e.printStackTrace();

            return false;

        } finally {

            closeConnection(connection);
        }
    }


    // =====================================================
    // RESULTSET -> SALE OBJECT
    // =====================================================
    private Sale createSaleFromResult(
            ResultSet result) throws Exception {

        return new Sale(
                result.getInt("sale_id"),
                result.getInt("employee_id"),
                result.getString("employee_name"),
                result.getTimestamp("sale_date"),
                result.getBigDecimal("total_amount"),
                result.getString("sale_status"),
                result.getString("payment_method")
        );
    }


    // =====================================================
    // ROLLBACK HELPER
    // =====================================================
    private void rollback(
            Connection connection) {

        try {

            if (connection != null) {
                connection.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =====================================================
    // CLOSE CONNECTION HELPER
    // =====================================================
    private void closeConnection(
            Connection connection) {

        try {

            if (connection != null) {

                connection.setAutoCommit(true);
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}