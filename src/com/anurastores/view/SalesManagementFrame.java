package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.InventoryDAO;
import com.anurastores.dao.ProductDAO;
import com.anurastores.dao.SalesDAO;
import com.anurastores.model.Inventory;
import com.anurastores.model.Product;
import com.anurastores.model.Sale;
import com.anurastores.model.SaleItem;
import com.anurastores.model.User;

public class SalesManagementFrame extends JFrame {

    private User loggedInUser;

    private SalesDAO salesDAO;
    private ProductDAO productDAO;
    private InventoryDAO inventoryDAO;

    private JTabbedPane tabs;

    // ==========================
    // NEW SALE
    // ==========================

    private JComboBox<ProductItem> productCombo;
    private JTextField quantityField;

    private JLabel stockLabel;
    private JLabel totalLabel;
    private JLabel pendingEditLabel;

    private JComboBox<String> paymentCombo;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private List<SaleItem> cartItems =
            new ArrayList<SaleItem>();

    private int editingPendingSaleId = -1;


    // ==========================
    // SALES HISTORY
    // ==========================

    private JTable salesTable;
    private DefaultTableModel salesTableModel;

    private JTextField searchField;
    private JTextField fromDateField;
    private JTextField toDateField;

    private JLabel summaryLabel;


    public SalesManagementFrame(User user) {

        this.loggedInUser = user;

        salesDAO = new SalesDAO();
        productDAO = new ProductDAO();
        inventoryDAO = new InventoryDAO();

        setTitle(
                "Sales Management - Anura Stores");

        setSize(1250, 720);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadProducts();
        loadSalesHistory();
    }


    // ==================================================
    // MAIN TABS
    // ==================================================

    private void initComponents() {

        tabs = new JTabbedPane();

        tabs.addTab(
                "New Sale",
                createSalesPanel());

        tabs.addTab(
                "Sales History",
                createHistoryPanel());

        add(tabs);
    }


    // ==================================================
    // NEW SALE PANEL
    // ==================================================

    private JPanel createSalesPanel() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10));


        // ----------------------------------
        // TOP PRODUCT AREA
        // ----------------------------------

        JPanel topPanel =
                new JPanel(
                        new FlowLayout());


        productCombo =
                new JComboBox<ProductItem>();


        quantityField =
                new JTextField(6);


        stockLabel =
                new JLabel(
                        "Available Stock: -");


        JButton addCartButton =
                new JButton(
                        "Add to Cart");


        JButton removeButton =
                new JButton(
                        "Remove Selected");


        JButton clearCartButton =
                new JButton(
                        "Clear Cart");


        topPanel.add(
                new JLabel(
                        "Product:"));

        topPanel.add(
                productCombo);

        topPanel.add(
                stockLabel);

        topPanel.add(
                new JLabel(
                        "Quantity:"));

        topPanel.add(
                quantityField);

        topPanel.add(
                addCartButton);

        topPanel.add(
                removeButton);

        topPanel.add(
                clearCartButton);


        // ----------------------------------
        // CART TABLE
        // ----------------------------------

        String[] cartColumns = {

                "Product ID",
                "Product Name",
                "Quantity",
                "Unit Price",
                "Subtotal"
        };


        cartTableModel =
                new DefaultTableModel(
                        cartColumns,
                        0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        cartTable =
                new JTable(
                        cartTableModel);


        JScrollPane cartScroll =
                new JScrollPane(
                        cartTable);


        // ----------------------------------
        // BOTTOM PAYMENT / PENDING AREA
        // ----------------------------------

        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT));


        paymentCombo =
                new JComboBox<String>(
                        new String[] {
                                "CASH",
                                "CARD"
                        });


        pendingEditLabel =
                new JLabel(
                        "Editing Pending Sale ID: -");


        JButton savePendingButton =
                new JButton(
                        "Save Pending");


        JButton updatePendingButton =
                new JButton(
                        "Update Pending");


        JButton cancelEditButton =
                new JButton(
                        "Cancel Edit");


        totalLabel =
                new JLabel(
                        "Total: Rs. 0.00");


        totalLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22));


        JButton confirmButton =
                new JButton(
                        "Confirm Sale");


        bottomPanel.add(
                pendingEditLabel);

        bottomPanel.add(
                savePendingButton);

        bottomPanel.add(
                updatePendingButton);

        bottomPanel.add(
                cancelEditButton);

        bottomPanel.add(
                new JLabel(
                        "Payment Method:"));

        bottomPanel.add(
                paymentCombo);

        bottomPanel.add(
                totalLabel);

        bottomPanel.add(
                confirmButton);


        mainPanel.add(
                topPanel,
                BorderLayout.NORTH);

        mainPanel.add(
                cartScroll,
                BorderLayout.CENTER);

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH);


        // ----------------------------------
        // EVENTS
        // ----------------------------------

        productCombo.addActionListener(
                e -> updateStockLabel());


        addCartButton.addActionListener(
                e -> addToCart());


        removeButton.addActionListener(
                e -> removeCartItem());


        clearCartButton.addActionListener(
                e -> clearCart());


        confirmButton.addActionListener(
                e -> confirmSale());


        savePendingButton.addActionListener(
                e -> savePendingSale());


        updatePendingButton.addActionListener(
                e -> updatePendingSale());


        cancelEditButton.addActionListener(
                e -> cancelPendingEdit());


        return mainPanel;
    }


    // ==================================================
    // SALES HISTORY PANEL
    // ==================================================

    private JPanel createHistoryPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout());


        // ----------------------------------
        // SEARCH AREA
        // ----------------------------------

        JPanel searchPanel =
                new JPanel();


        searchField =
                new JTextField(12);


        fromDateField =
                new JTextField(10);


        toDateField =
                new JTextField(10);


        JButton searchButton =
                new JButton(
                        "Search");


        JButton dateButton =
                new JButton(
                        "Filter Date Range");


        JButton showAllButton =
                new JButton(
                        "Show All");


        searchPanel.add(
                new JLabel(
                        "Search:"));

        searchPanel.add(
                searchField);

        searchPanel.add(
                searchButton);

        searchPanel.add(
                new JLabel(
                        "From:"));

        searchPanel.add(
                fromDateField);

        searchPanel.add(
                new JLabel(
                        "To:"));

        searchPanel.add(
                toDateField);

        searchPanel.add(
                dateButton);

        searchPanel.add(
                showAllButton);


        // ----------------------------------
        // SALES TABLE
        // ----------------------------------

        String[] columns = {

                "Sale ID",
                "Employee ID",
                "Employee Name",
                "Date",
                "Total",
                "Payment",
                "Status"
        };


        salesTableModel =
                new DefaultTableModel(
                        columns,
                        0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        salesTable =
                new JTable(
                        salesTableModel);


        JScrollPane scrollPane =
                new JScrollPane(
                        salesTable);


        // ----------------------------------
        // HISTORY BUTTONS
        // ----------------------------------

        JPanel bottomPanel =
                new JPanel();


        JButton invoiceButton =
                new JButton(
                        "View Invoice");


        JButton editPendingButton =
                new JButton(
                        "Edit Pending");


        JButton completePendingButton =
                new JButton(
                        "Complete Pending");


        JButton cancelButton =
                new JButton(
                        "Cancel Selected Sale");


        summaryLabel =
                new JLabel(
                        "Completed Sales: 0 | Total: Rs. 0.00");


        // Cancel completed sale = ADMIN only
        if (loggedInUser == null
                || !"ADMIN".equalsIgnoreCase(
                        loggedInUser.getRole())) {

            cancelButton.setEnabled(false);
        }


        bottomPanel.add(
                invoiceButton);

        bottomPanel.add(
                editPendingButton);

        bottomPanel.add(
                completePendingButton);

        bottomPanel.add(
                cancelButton);

        bottomPanel.add(
                summaryLabel);


        panel.add(
                searchPanel,
                BorderLayout.NORTH);

        panel.add(
                scrollPane,
                BorderLayout.CENTER);

        panel.add(
                bottomPanel,
                BorderLayout.SOUTH);


        // ----------------------------------
        // EVENTS
        // ----------------------------------

        searchButton.addActionListener(
                e -> searchSales());


        dateButton.addActionListener(
                e -> filterSalesByDate());


        showAllButton.addActionListener(
                e -> {

                    searchField.setText("");
                    fromDateField.setText("");
                    toDateField.setText("");

                    loadSalesHistory();
                });


        invoiceButton.addActionListener(
                e -> viewSelectedInvoice());


        editPendingButton.addActionListener(
                e -> editSelectedPendingSale());


        completePendingButton.addActionListener(
                e -> completeSelectedPendingSale());


        cancelButton.addActionListener(
                e -> cancelSelectedSale());


        return panel;
    }


    // ==================================================
    // LOAD PRODUCTS
    // ==================================================

    private void loadProducts() {

        productCombo.removeAllItems();


        List<Product> products =
                productDAO.getAllProducts();


        for (Product product : products) {

            if ("ACTIVE".equalsIgnoreCase(
                    product.getStatus())) {

                productCombo.addItem(
                        new ProductItem(
                                product.getProductId(),
                                product.getProductName(),
                                product.getSellingPrice()
                        ));
            }
        }


        updateStockLabel();
    }


    // ==================================================
    // GET AVAILABLE STOCK
    // ==================================================

    private int getAvailableStock(
            int productId) {

        List<Inventory> inventoryList =
                inventoryDAO
                        .getAllInventory();


        for (Inventory inventory :
                inventoryList) {

            if (inventory.getProductId()
                    == productId) {

                return inventory
                        .getQuantity();
            }
        }


        return 0;
    }


    private void updateStockLabel() {

        ProductItem product =
                (ProductItem)
                        productCombo
                                .getSelectedItem();


        if (product == null) {

            stockLabel.setText(
                    "Available Stock: -");

            return;
        }


        int stock =
                getAvailableStock(
                        product.getProductId());


        stockLabel.setText(
                "Available Stock: "
                + stock);
    }


    // ==================================================
    // ADD TO CART
    // ==================================================

    private void addToCart() {

        ProductItem product =
                (ProductItem)
                        productCombo
                                .getSelectedItem();


        if (product == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product.");

            return;
        }


        int quantity;


        try {

            quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be a whole number.");

            return;
        }


        if (quantity <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be greater than zero.");

            return;
        }


        int availableStock =
                getAvailableStock(
                        product.getProductId());


        int alreadyInCart = 0;

        SaleItem existingItem = null;


        for (SaleItem item :
                cartItems) {

            if (item.getProductId()
                    == product.getProductId()) {

                alreadyInCart =
                        item.getQuantity();

                existingItem =
                        item;

                break;
            }
        }


        if (alreadyInCart + quantity
                > availableStock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Insufficient stock.\n"
                    + "Available: "
                    + availableStock,
                    "Stock Warning",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }


        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity()
                    + quantity;


            existingItem.setQuantity(
                    newQuantity);


            existingItem.setSubtotal(
                    existingItem
                            .getUnitPrice()
                            .multiply(
                                    BigDecimal
                                            .valueOf(
                                                    newQuantity)));

        } else {

            SaleItem item =
                    new SaleItem();


            item.setProductId(
                    product.getProductId());


            item.setProductName(
                    product.getProductName());


            item.setQuantity(
                    quantity);


            item.setUnitPrice(
                    product.getPrice());


            item.setSubtotal(
                    product.getPrice()
                            .multiply(
                                    BigDecimal
                                            .valueOf(
                                                    quantity)));


            cartItems.add(item);
        }


        quantityField.setText("");

        refreshCartTable();
    }


    // ==================================================
    // REMOVE CART ITEM
    // ==================================================

    private void removeCartItem() {

        int selectedRow =
                cartTable
                        .getSelectedRow();


        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item from the cart.");

            return;
        }


        cartItems.remove(
                selectedRow);


        refreshCartTable();
    }


    // ==================================================
    // CLEAR CART
    // ==================================================

    private void clearCart() {

        cartItems.clear();

        refreshCartTable();
    }


    // ==================================================
    // REFRESH CART
    // ==================================================

    private void refreshCartTable() {

        cartTableModel.setRowCount(0);


        BigDecimal total =
                BigDecimal.ZERO;


        for (SaleItem item :
                cartItems) {

            cartTableModel.addRow(
                    new Object[] {

                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal()
                    });


            total =
                    total.add(
                            item.getSubtotal());
        }


        totalLabel.setText(
                "Total: Rs. "
                + total.toPlainString());
    }


    // ==================================================
    // DIRECT CONFIRM SALE
    // ==================================================

    private void confirmSale() {

        // Prevent duplicate new sale
        // while editing a pending sale.
        if (editingPendingSaleId != -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "You are editing a pending sale.\n"
                    + "Use Update Pending first.");

            return;
        }


        if (cartItems.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one product to the cart.");

            return;
        }


        if (loggedInUser == null
                || loggedInUser.getEmployeeId()
                <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Logged-in user is not linked to an employee.");

            return;
        }


        String paymentMethod =
                paymentCombo
                        .getSelectedItem()
                        .toString();


        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Confirm this sale?",
                        "Confirm Sale",
                        JOptionPane.YES_NO_OPTION);


        if (answer
                != JOptionPane.YES_OPTION) {

            return;
        }


        int saleId =
                salesDAO.createSale(
                        loggedInUser
                                .getEmployeeId(),
                        cartItems,
                        paymentMethod);


        if (saleId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale could not be completed.\n"
                    + "Please check available stock.",
                    "Sale Failed",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }


        JOptionPane.showMessageDialog(
                this,
                "Sale completed successfully!\n"
                + "Sale ID: "
                + saleId);


        showInvoice(
                saleId,
                paymentMethod);


        cartItems.clear();

        refreshCartTable();

        loadProducts();
        loadSalesHistory();
        updateStockLabel();
    }


    // ==================================================
    // SAVE PENDING SALE
    // ==================================================

    private void savePendingSale() {

        if (editingPendingSaleId != -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "You are already editing a pending sale.\n"
                    + "Use Update Pending.");

            return;
        }


        if (cartItems.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one product.");

            return;
        }


        if (loggedInUser == null
                || loggedInUser
                        .getEmployeeId()
                <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Logged-in user is not linked to an employee.");

            return;
        }


        int saleId =
                salesDAO
                        .savePendingSale(
                                loggedInUser
                                        .getEmployeeId(),
                                cartItems);


        if (saleId != -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale saved as PENDING.\n"
                    + "Sale ID: "
                    + saleId);


            cartItems.clear();

            refreshCartTable();

            loadSalesHistory();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not save pending sale.");
        }
    }


    // ==================================================
    // EDIT SELECTED PENDING SALE
    // ==================================================

    private void editSelectedPendingSale() {

        int row =
                salesTable
                        .getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale first.");

            return;
        }


        String status =
                salesTableModel
                        .getValueAt(
                                row,
                                6)
                        .toString();


        if (!"PENDING".equalsIgnoreCase(
                status)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only PENDING sales can be edited.");

            return;
        }


        int saleId =
                Integer.parseInt(
                        salesTableModel
                                .getValueAt(
                                        row,
                                        0)
                                .toString());


        List<SaleItem> savedItems =
                salesDAO
                        .getSaleItems(
                                saleId);


        if (savedItems.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No sale items were found.");

            return;
        }


        cartItems.clear();


        for (SaleItem savedItem :
                savedItems) {

            SaleItem item =
                    new SaleItem();


            item.setSaleItemId(
                    savedItem
                            .getSaleItemId());


            item.setSaleId(
                    savedItem
                            .getSaleId());


            item.setProductId(
                    savedItem
                            .getProductId());


            item.setProductName(
                    savedItem
                            .getProductName());


            item.setQuantity(
                    savedItem
                            .getQuantity());


            item.setUnitPrice(
                    savedItem
                            .getUnitPrice());


            item.setSubtotal(
                    savedItem
                            .getSubtotal());


            cartItems.add(item);
        }


        editingPendingSaleId =
                saleId;


        pendingEditLabel.setText(
                "Editing Pending Sale ID: "
                + saleId);


        refreshCartTable();


        // Go to New Sale tab
        tabs.setSelectedIndex(0);
    }


    // ==================================================
    // UPDATE PENDING SALE
    // ==================================================

    private void updatePendingSale() {

        if (editingPendingSaleId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a PENDING sale from Sales History\n"
                    + "and click Edit Pending first.");

            return;
        }


        if (cartItems.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale must contain at least one item.");

            return;
        }


        boolean success =
                salesDAO
                        .updatePendingSale(
                                editingPendingSaleId,
                                cartItems);


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale updated successfully!");


            loadSalesHistory();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale update failed.\n"
                    + "Only PENDING sales can be modified.");
        }
    }


    // ==================================================
    // CANCEL EDIT MODE
    // ==================================================

    private void cancelPendingEdit() {

        editingPendingSaleId = -1;


        pendingEditLabel.setText(
                "Editing Pending Sale ID: -");


        cartItems.clear();

        refreshCartTable();
    }


    // ==================================================
    // COMPLETE SELECTED PENDING SALE
    // ==================================================

    private void completeSelectedPendingSale() {

        int row =
                salesTable
                        .getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale first.");

            return;
        }


        String status =
                salesTableModel
                        .getValueAt(
                                row,
                                6)
                        .toString();


        if (!"PENDING".equalsIgnoreCase(
                status)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only PENDING sales can be completed.");

            return;
        }


        int saleId =
                Integer.parseInt(
                        salesTableModel
                                .getValueAt(
                                        row,
                                        0)
                                .toString());


        String[] paymentMethods = {
                "CASH",
                "CARD"
        };


        String paymentMethod =
                (String)
                        JOptionPane
                                .showInputDialog(
                                        this,
                                        "Select payment method:",
                                        "Complete Pending Sale",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        paymentMethods,
                                        paymentMethods[0]);


        if (paymentMethod == null) {
            return;
        }


        boolean success =
                salesDAO
                        .completePendingSale(
                                saleId,
                                paymentMethod);


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale completed successfully!\n"
                    + "Inventory has been updated.");


            if (editingPendingSaleId
                    == saleId) {

                editingPendingSaleId = -1;

                pendingEditLabel.setText(
                        "Editing Pending Sale ID: -");

                cartItems.clear();

                refreshCartTable();
            }


            loadProducts();
            loadSalesHistory();
            updateStockLabel();


            showInvoice(
                    saleId,
                    paymentMethod);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale could not be completed.\n"
                    + "Check product status and available stock.",
                    "Sale Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // ==================================================
    // INVOICE
    // ==================================================

    private void showInvoice(
            int saleId,
            String paymentMethod) {

        List<SaleItem> items =
                salesDAO
                        .getSaleItems(
                                saleId);


        BigDecimal total =
                BigDecimal.ZERO;


        StringBuilder invoice =
                new StringBuilder();


        invoice.append(
                "========================================\n");

        invoice.append(
                "              ANURA STORES\n");

        invoice.append(
                "========================================\n");

        invoice.append(
                "Sale ID : ")
                .append(
                        saleId)
                .append("\n");


        invoice.append(
                "Payment : ")
                .append(
                        paymentMethod)
                .append("\n");


        invoice.append(
                "----------------------------------------\n");


        for (SaleItem item :
                items) {

            invoice.append(
                    item.getProductName())
                    .append("\n");


            invoice.append(
                    "   ")
                    .append(
                            item.getQuantity())
                    .append(
                            " x ")
                    .append(
                            item.getUnitPrice())
                    .append(
                            " = Rs. ")
                    .append(
                            item.getSubtotal())
                    .append("\n");


            total =
                    total.add(
                            item.getSubtotal());
        }


        invoice.append(
                "----------------------------------------\n");


        invoice.append(
                "TOTAL: Rs. ")
                .append(
                        total.toPlainString())
                .append("\n");


        invoice.append(
                "========================================\n");


        invoice.append(
                "             Thank you!\n");


        JTextArea invoiceArea =
                new JTextArea(
                        invoice.toString());


        invoiceArea.setEditable(
                false);


        invoiceArea.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        14));


        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(
                        invoiceArea),
                "Invoice - Sale "
                + saleId,
                JOptionPane.INFORMATION_MESSAGE);
    }


    // ==================================================
    // LOAD SALES HISTORY
    // ==================================================

    private void loadSalesHistory() {

        displaySales(
                salesDAO
                        .getAllSales());
    }


    // ==================================================
    // SEARCH SALES
    // ==================================================

    private void searchSales() {

        String keyword =
                searchField
                        .getText()
                        .trim();


        if (keyword.isEmpty()) {

            loadSalesHistory();

            return;
        }


        displaySales(
                salesDAO
                        .searchSales(
                                keyword));
    }


    // ==================================================
    // DATE RANGE FILTER
    // ==================================================

    private void filterSalesByDate() {

        try {

            if (fromDateField
                    .getText()
                    .trim()
                    .isEmpty()

                    || toDateField
                            .getText()
                            .trim()
                            .isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter both From and To dates.");

                return;
            }


            Date fromDate =
                    Date.valueOf(
                            fromDateField
                                    .getText()
                                    .trim());


            Date toDate =
                    Date.valueOf(
                            toDateField
                                    .getText()
                                    .trim());


            if (toDate.before(
                    fromDate)) {

                JOptionPane.showMessageDialog(
                        this,
                        "To date cannot be before From date.");

                return;
            }


            displaySales(
                    salesDAO
                            .searchSalesByDateRange(
                                    fromDate,
                                    toDate));

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Dates must be in YYYY-MM-DD format.");
        }
    }


    // ==================================================
    // DISPLAY SALES
    // ==================================================

    private void displaySales(
            List<Sale> sales) {

        salesTableModel
                .setRowCount(0);


        int completedCount = 0;


        BigDecimal completedTotal =
                BigDecimal.ZERO;


        for (Sale sale :
                sales) {

            salesTableModel.addRow(
                    new Object[] {

                            sale.getSaleId(),
                            sale.getEmployeeId(),
                            sale.getEmployeeName(),
                            sale.getSaleDate(),
                            sale.getTotalAmount(),
                            sale.getPaymentMethod(),
                            sale.getSaleStatus()
                    });


            if ("COMPLETED"
                    .equalsIgnoreCase(
                            sale.getSaleStatus())) {

                completedCount++;


                completedTotal =
                        completedTotal.add(
                                sale.getTotalAmount());
            }
        }


        summaryLabel.setText(
                "Completed Sales: "
                + completedCount
                + " | Total: Rs. "
                + completedTotal
                        .toPlainString());
    }


    // ==================================================
    // VIEW SELECTED INVOICE
    // ==================================================

    private void viewSelectedInvoice() {

        int row =
                salesTable
                        .getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale first.");

            return;
        }


        int saleId =
                Integer.parseInt(
                        salesTableModel
                                .getValueAt(
                                        row,
                                        0)
                                .toString());


        Object paymentValue =
                salesTableModel
                        .getValueAt(
                                row,
                                5);


        String paymentMethod =
                paymentValue == null
                        ? "-"
                        : paymentValue
                                .toString();


        showInvoice(
                saleId,
                paymentMethod);
    }


    // ==================================================
    // CANCEL COMPLETED SALE
    // ==================================================

    private void cancelSelectedSale() {

        int row =
                salesTable
                        .getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a sale first.");

            return;
        }


        int saleId =
                Integer.parseInt(
                        salesTableModel
                                .getValueAt(
                                        row,
                                        0)
                                .toString());


        String status =
                salesTableModel
                        .getValueAt(
                                row,
                                6)
                        .toString();


        // IMPORTANT:
        // Pending sales cannot be cancelled
        // using this method because their
        // stock was never reduced.
        if (!"COMPLETED".equalsIgnoreCase(
                status)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only COMPLETED sales can be cancelled.");

            return;
        }


        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Cancel Sale ID "
                        + saleId
                        + "?\n"
                        + "Inventory will be restored.",
                        "Cancel Sale",
                        JOptionPane.YES_NO_OPTION);


        if (answer
                != JOptionPane.YES_OPTION) {

            return;
        }


        boolean success =
                salesDAO
                        .cancelSale(
                                saleId);


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale cancelled successfully.\n"
                    + "Inventory has been restored.");


            loadSalesHistory();
            loadProducts();
            updateStockLabel();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale cancellation failed.");
        }
    }


    // ==================================================
    // PRODUCT COMBO ITEM
    // ==================================================

    private static class ProductItem {

        private int productId;
        private String productName;
        private BigDecimal price;


        public ProductItem(
                int productId,
                String productName,
                BigDecimal price) {

            this.productId =
                    productId;

            this.productName =
                    productName;

            this.price =
                    price;
        }


        public int getProductId() {
            return productId;
        }


        public String getProductName() {
            return productName;
        }


        public BigDecimal getPrice() {
            return price;
        }


        @Override
        public String toString() {

            return productId
                    + " - "
                    + productName
                    + " (Rs. "
                    + price
                    + ")";
        }
    }
}