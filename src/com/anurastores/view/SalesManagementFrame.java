package com.anurastores.view;

import java.awt.print.PrinterException;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.InventoryDAO;
import com.anurastores.dao.ProductDAO;
import com.anurastores.dao.SalesDAO;
import com.anurastores.model.Inventory;
import com.anurastores.model.Product;
import com.anurastores.model.Sale;
import com.anurastores.model.SaleItem;
import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class SalesManagementFrame extends JFrame {

    private User loggedInUser;

    private SalesDAO salesDAO;
    private ProductDAO productDAO;
    private InventoryDAO inventoryDAO;

    private JTabbedPane tabs;

    // ==========================
    // NEW SALE
    // ==========================

    private JTextField productSearchField;
    private Integer selectedProductId = null;
    private BigDecimal selectedProductPrice = BigDecimal.ZERO;
    private JPopupMenu productSuggestionPopup;
    private JList<ProductItem> productSuggestionList;
    private JScrollPane productSuggestionScrollPane;

    private JTextField quantityField;

    private JLabel stockLabel;
    private JLabel totalLabel;
    private JLabel pendingEditLabel;

    private JComboBox<String> paymentCombo;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private List<SaleItem> cartItems = new ArrayList<SaleItem>();

    private int editingPendingSaleId = -1;
    private JPanel editControlsPanel;
    private JButton savePendingButton;


    // ==========================
    // SALES HISTORY
    // ==========================

    private JTable salesTable;
    private DefaultTableModel salesTableModel;

    private JTextField searchField;
    private JPopupMenu searchSuggestionPopup;
    private JList<String> searchSuggestionList;
    private JScrollPane searchSuggestionScrollPane;

    private JTextField fromDateField;
    private JButton fromDateButton;
    private JTextField toDateField;
    private JButton toDateButton;

    private JLabel summaryLabel;


    public SalesManagementFrame(User user) {

        this.loggedInUser = user;

        salesDAO = new SalesDAO();
        productDAO = new ProductDAO();
        inventoryDAO = new InventoryDAO();

        setTitle("Sales Management - Anura Stores");
        setSize(1250, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadSalesHistory();
    }


    // ==================================================
    // MAIN TABS
    // ==================================================

    private void initComponents() {

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.NORMAL_FONT);

        tabs.addTab("New Sale", createSalesPanel());
        tabs.addTab("Sales History", createHistoryPanel());

        add(tabs);
    }


    // ==================================================
    // NEW SALE PANEL
    // ==================================================

    private JPanel createSalesPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UITheme.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // ----------------------------------
        // TOP PRODUCT AREA
        // ----------------------------------

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(topPanel);
        topPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Create Sale / Add Items"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );


        productSearchField = new JTextField(15);
        UITheme.styleTextField(productSearchField);

        productSuggestionList = new JList<>();
        productSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productSuggestionList.setFocusable(false);
        productSuggestionList.setVisibleRowCount(5);

        productSuggestionPopup = new JPopupMenu();
        productSuggestionPopup.setFocusable(false);
        productSuggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        productSuggestionScrollPane = new JScrollPane(productSuggestionList);
        productSuggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        productSuggestionPopup.add(productSuggestionScrollPane);


        quantityField = new JTextField(6);
        UITheme.styleTextField(quantityField);


        stockLabel = new JLabel("Available Stock: -");
        UITheme.styleNormalLabel(stockLabel);


        JButton addCartButton = new JButton("Add to Cart");
        UITheme.stylePrimaryButton(addCartButton);


        JButton removeButton = new JButton("Remove Selected");
        UITheme.styleDangerButton(removeButton);


        JButton clearCartButton = new JButton("Clear Cart");
        UITheme.styleSecondaryButton(clearCartButton);


        JLabel prodLabel = new JLabel("Product:");
        UITheme.styleNormalLabel(prodLabel);
        JLabel qtyLabel = new JLabel("Quantity:");
        UITheme.styleNormalLabel(qtyLabel);

        topPanel.add(prodLabel);
        topPanel.add(productSearchField);
        topPanel.add(stockLabel);
        topPanel.add(qtyLabel);
        topPanel.add(quantityField);
        topPanel.add(addCartButton);
        topPanel.add(removeButton);
        topPanel.add(clearCartButton);


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


        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        cartTable = new JTable(cartTableModel);
        UITheme.styleTable(cartTable);


        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // ----------------------------------
        // BOTTOM PAYMENT / PENDING AREA
        // ----------------------------------

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        UITheme.stylePanel(bottomPanel);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        paymentCombo = new JComboBox<String>(new String[] { "CASH", "CARD" });
        UITheme.styleComboBox(paymentCombo);


        pendingEditLabel = new JLabel("Editing Pending Sale ID: -");
        UITheme.styleSubtitleLabel(pendingEditLabel);


        savePendingButton = new JButton("Save Pending");
        UITheme.styleSecondaryButton(savePendingButton);


        JButton updatePendingButton = new JButton("Update Pending");
        UITheme.styleSecondaryButton(updatePendingButton);


        JButton cancelEditButton = new JButton("Cancel Edit");
        UITheme.styleDangerButton(cancelEditButton);



        // Edit Controls Panel wrapper
        // Hidden by default until a pending sale is selected for editing.
        editControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        UITheme.stylePanel(editControlsPanel);

        editControlsPanel.add(pendingEditLabel);
        editControlsPanel.add(updatePendingButton);
        editControlsPanel.add(cancelEditButton);

        editControlsPanel.setVisible(false);



        totalLabel = new JLabel("Total: Rs. 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(UITheme.TEXT_DARK);


        JButton confirmButton = new JButton("Confirm Sale");
        UITheme.stylePrimaryButton(confirmButton);


        JLabel payLabel = new JLabel("Payment Method:");
        UITheme.styleNormalLabel(payLabel);


        bottomPanel.add(editControlsPanel);
        bottomPanel.add(payLabel);
        bottomPanel.add(paymentCombo);
        bottomPanel.add(totalLabel);
        bottomPanel.add(savePendingButton);
        bottomPanel.add(confirmButton);



        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(cartScroll, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);


        // ----------------------------------
        // EVENTS
        // ----------------------------------

        // Product Search Suggestions Listener
        productSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateProductSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateProductSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateProductSuggestions(); }
        });

        productSuggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectProductSuggestion();
                }
            }
        });

        addCartButton.addActionListener(e -> addToCart());
        removeButton.addActionListener(e -> removeCartItem());
        clearCartButton.addActionListener(e -> clearCart());
        confirmButton.addActionListener(e -> confirmSale());
        savePendingButton.addActionListener(e -> savePendingSale());
        updatePendingButton.addActionListener(e -> updatePendingSale());
        cancelEditButton.addActionListener(e -> cancelPendingEdit());


        return mainPanel;
    }


    // ==================================================
    // SALES HISTORY PANEL
    // ==================================================

    private JPanel createHistoryPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        UITheme.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // ----------------------------------
        // SEARCH AREA (Centered)
        // ----------------------------------

        searchField = new JTextField(12);
        UITheme.styleTextField(searchField);

        // Main Search Suggestions Popup
        searchSuggestionList = new JList<>();
        searchSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchSuggestionList.setFocusable(false);
        searchSuggestionList.setVisibleRowCount(5);

        searchSuggestionPopup = new JPopupMenu();
        searchSuggestionPopup.setFocusable(false);
        searchSuggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        searchSuggestionScrollPane = new JScrollPane(searchSuggestionList);
        searchSuggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        searchSuggestionPopup.add(searchSuggestionScrollPane);


        fromDateField = new JTextField(10);
        fromDateField.setEditable(false);
        UITheme.styleTextField(fromDateField);
        fromDateButton = new JButton("Pick");
        UITheme.styleSecondaryButton(fromDateButton);
        fromDateButton.addActionListener(e -> openCalendarPopup(fromDateField));

        JPanel fromDatePanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(fromDatePanel);
        fromDatePanel.add(fromDateField, BorderLayout.CENTER);
        fromDatePanel.add(fromDateButton, BorderLayout.EAST);


        toDateField = new JTextField(10);
        toDateField.setEditable(false);
        UITheme.styleTextField(toDateField);
        toDateButton = new JButton("Pick");
        UITheme.styleSecondaryButton(toDateButton);
        toDateButton.addActionListener(e -> openCalendarPopup(toDateField));

        JPanel toDatePanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(toDatePanel);
        toDatePanel.add(toDateField, BorderLayout.CENTER);
        toDatePanel.add(toDateButton, BorderLayout.EAST);


        JButton dateButton = new JButton("Filter Date Range");
        UITheme.styleSecondaryButton(dateButton);

        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        UITheme.stylePanel(searchPanel);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Sales History & Filter"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        JLabel searchLabel = new JLabel("Search:");
        UITheme.styleNormalLabel(searchLabel);
        JLabel fromLabel = new JLabel("From:");
        UITheme.styleNormalLabel(fromLabel);
        JLabel toLabel = new JLabel("To:");
        UITheme.styleNormalLabel(toLabel);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(fromLabel);
        searchPanel.add(fromDatePanel);
        searchPanel.add(toLabel);
        searchPanel.add(toDatePanel);
        searchPanel.add(dateButton);
        searchPanel.add(showAllButton);


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


        salesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        salesTable = new JTable(salesTableModel);
        UITheme.styleTable(salesTable);


        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // ----------------------------------
        // HISTORY BUTTONS
        // ----------------------------------

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(bottomPanel);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        JButton invoiceButton = new JButton("View Invoice");
        UITheme.stylePrimaryButton(invoiceButton);


        JButton editPendingButton = new JButton("Edit Pending");
        UITheme.styleSecondaryButton(editPendingButton);


        JButton completePendingButton = new JButton("Complete Pending");
        UITheme.stylePrimaryButton(completePendingButton);


        JButton cancelButton = new JButton("Cancel Selected Sale");
        UITheme.styleDangerButton(cancelButton);


        summaryLabel = new JLabel("Completed Sales: 0 | Total: Rs. 0.00");
        UITheme.styleSubtitleLabel(summaryLabel);


        // Cancel completed sale = ADMIN only
        if (loggedInUser == null
                || !"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            cancelButton.setEnabled(false);
        }


        bottomPanel.add(invoiceButton);
        bottomPanel.add(editPendingButton);
        bottomPanel.add(completePendingButton);
        bottomPanel.add(cancelButton);
        bottomPanel.add(summaryLabel);


        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);


        // ----------------------------------
        // EVENTS
        // ----------------------------------

        // Live Search History Listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSearchSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSearchSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSearchSuggestions(); }
        });

        searchSuggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSearchSuggestion();
                }
            }
        });

        searchField.addActionListener(e -> {
            if (searchSuggestionPopup.isVisible() && searchSuggestionList.getSelectedIndex() >= 0) {
                selectSearchSuggestion();
            } else {
                searchSales();
            }
        });

        dateButton.addActionListener(e -> filterSalesByDate());

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            fromDateField.setText("");
            toDateField.setText("");
            searchSuggestionPopup.setVisible(false);

            loadSalesHistory();
        });


        invoiceButton.addActionListener(e -> viewSelectedInvoice());
        editPendingButton.addActionListener(e -> editSelectedPendingSale());
        completePendingButton.addActionListener(e -> completeSelectedPendingSale());
        cancelButton.addActionListener(e -> cancelSelectedSale());


        return panel;
    }


    // ==================================================
    // PRODUCT SEARCH SUGGESTIONS (NEW SALE)
    // ==================================================

    private void updateProductSuggestions() {
        String keyword = productSearchField.getText().trim();

        if (keyword.isEmpty()) {
            selectedProductId = null;
            selectedProductPrice = BigDecimal.ZERO;
            productSuggestionPopup.setVisible(false);
            stockLabel.setText("Available Stock: -");
            return;
        }

        List<Product> products = productDAO.searchProducts(keyword);
        javax.swing.DefaultListModel<ProductItem> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Product product : products) {
            if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                continue;
            }
            String name = product.getProductName();
            if (name == null || !name.toLowerCase().contains(lowerKeyword)) {
                continue;
            }

            listModel.addElement(new ProductItem(product.getProductId(), name, product.getSellingPrice()));
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            productSuggestionPopup.setVisible(false);
            selectedProductId = null;
            selectedProductPrice = BigDecimal.ZERO;
            stockLabel.setText("Available Stock: -");
            return;
        }

        productSuggestionList.setModel(listModel);
        productSuggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        productSuggestionScrollPane.setPreferredSize(new Dimension(productSearchField.getWidth(), height));
        productSuggestionPopup.pack();
        productSuggestionPopup.show(productSearchField, 0, productSearchField.getHeight());
        productSearchField.requestFocusInWindow();
    }


private void selectProductSuggestion() {

    ProductItem selected = productSuggestionList.getSelectedValue();

    if (selected != null) {

        // First, update the search field.
        // This triggers the DocumentListener.
        productSearchField.setText(selected.getProductName());

        // Hide the suggestions popup after updating the text.
        productSuggestionPopup.setVisible(false);

        // Assign the selected product AFTER setText().
        // This prevents the DocumentListener from clearing the ID.
        selectedProductId = selected.getProductId();
        selectedProductPrice = selected.getPrice();

        // Display the available stock for the selected product.
        updateStockLabel();

        // Clear the previous quantity.
        quantityField.setText("");

        // Keep the cursor in the search field.
        productSearchField.requestFocusInWindow();
    }
}



    // ==================================================
    // SALES HISTORY SEARCH SUGGESTIONS
    // ==================================================

    private void updateSearchSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            searchSuggestionPopup.setVisible(false);
            loadSalesHistory();
            return;
        }

        List<Sale> sales = salesDAO.searchSales(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Sale sale : sales) {
            String empName = sale.getEmployeeName();
            if (empName == null || !empName.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(empName)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(empName);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            searchSuggestionPopup.setVisible(false);
            searchSales();
            return;
        }

        searchSuggestionList.setModel(listModel);
        searchSuggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        searchSuggestionScrollPane.setPreferredSize(new Dimension(searchField.getWidth(), height));
        searchSuggestionPopup.pack();
        searchSuggestionPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow();
        searchSales();
    }

    private void selectSearchSuggestion() {
        String selected = searchSuggestionList.getSelectedValue();
        if (selected != null) {
            searchField.setText(selected);
            searchSuggestionPopup.setVisible(false);
            searchSales();
        }
    }


    // ==================================================
    // CALENDAR POPUP DIALOG
    // ==================================================

    private void openCalendarPopup(JTextField targetField) {
        JDialog calendarDialog = new JDialog(this, "Select Date", true);
        calendarDialog.setSize(460, 380);
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        UITheme.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        LocalDate initialDate = LocalDate.now();
        if (!targetField.getText().trim().isEmpty()) {
            try {
                initialDate = LocalDate.parse(targetField.getText().trim());
            } catch (Exception ignored) {}
        }

        final LocalDate[] viewingDate = { initialDate };

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 5));
        UITheme.stylePanel(navPanel);

        JButton prevButton = new JButton("<");
        UITheme.styleSecondaryButton(prevButton);
        prevButton.setPreferredSize(new Dimension(45, 30));

        String[] months = { "January", "February", "March", "April", "May", "June", 
                            "July", "August", "September", "October", "November", "December" };
        JComboBox<String> monthCombo = new JComboBox<>(months);
        UITheme.styleComboBox(monthCombo);
        monthCombo.setPreferredSize(new Dimension(120, 30));
        monthCombo.setSelectedIndex(initialDate.getMonthValue() - 1);

        Integer[] years = new Integer[100];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 100; i++) {
            years[i] = currentYear - 50 + i;
        }
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        UITheme.styleComboBox(yearCombo);
        yearCombo.setPreferredSize(new Dimension(85, 30));
        yearCombo.setSelectedItem(initialDate.getYear());

        JButton nextButton = new JButton(">");
        UITheme.styleSecondaryButton(nextButton);
        nextButton.setPreferredSize(new Dimension(45, 30));

        navPanel.add(prevButton);
        navPanel.add(monthCombo);
        navPanel.add(yearCombo);
        navPanel.add(nextButton);

        JPanel daysGridPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        UITheme.stylePanel(daysGridPanel);

        Runnable updateCalendarGrid = () -> {
            daysGridPanel.removeAll();
            int monthIdx = monthCombo.getSelectedIndex() + 1;
            int yearVal = (Integer) yearCombo.getSelectedItem();
            YearMonth yearMonth = YearMonth.of(yearVal, monthIdx);
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            int dayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue();
            int offset = dayOfWeekValue % 7;

            String[] dayHeaders = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };
            for (String header : dayHeaders) {
                JLabel lbl = new JLabel(header, SwingConstants.CENTER);
                UITheme.styleNormalLabel(lbl);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                daysGridPanel.add(lbl);
            }

            LocalDate prevMonthEnd = yearMonth.minusMonths(1).atEndOfMonth();
            int daysInPrevMonth = prevMonthEnd.getDayOfMonth();
            for (int i = offset - 1; i >= 0; i--) {
                int d = daysInPrevMonth - i;
                JButton dayBtn = new JButton(String.valueOf(d));
                UITheme.styleSecondaryButton(dayBtn);
                dayBtn.setPreferredSize(new Dimension(50, 32));
                dayBtn.setForeground(Color.GRAY);
                dayBtn.setEnabled(false);
                daysGridPanel.add(dayBtn);
            }

            int daysInMonth = yearMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                final int selectedDay = day;
                JButton dayBtn = new JButton(String.valueOf(day));
                UITheme.styleSecondaryButton(dayBtn);
                dayBtn.setPreferredSize(new Dimension(50, 32));

                if (day == viewingDate[0].getDayOfMonth() && 
                    monthIdx == viewingDate[0].getMonthValue() && 
                    yearVal == viewingDate[0].getYear()) {
                    dayBtn.setBackground(UITheme.PRIMARY);
                    dayBtn.setForeground(Color.WHITE);
                }

                dayBtn.addActionListener(e -> {
                    LocalDate chosen = LocalDate.of(yearVal, monthIdx, selectedDay);
                    targetField.setText(chosen.toString());
                    calendarDialog.dispose();
                });

                daysGridPanel.add(dayBtn);
            }

            daysGridPanel.revalidate();
            daysGridPanel.repaint();
        };

        monthCombo.addActionListener(e -> updateCalendarGrid.run());
        yearCombo.addActionListener(e -> updateCalendarGrid.run());

        prevButton.addActionListener(e -> {
            int m = monthCombo.getSelectedIndex() - 1;
            if (m < 0) {
                monthCombo.setSelectedIndex(11);
                yearCombo.setSelectedItem((Integer) yearCombo.getSelectedItem() - 1);
            } else {
                monthCombo.setSelectedIndex(m);
            }
            updateCalendarGrid.run();
        });

        nextButton.addActionListener(e -> {
            int m = monthCombo.getSelectedIndex() + 1;
            if (m > 11) {
                monthCombo.setSelectedIndex(0);
                yearCombo.setSelectedItem((Integer) yearCombo.getSelectedItem() + 1);
            } else {
                monthCombo.setSelectedIndex(m);
            }
            updateCalendarGrid.run();
        });

        updateCalendarGrid.run();

        mainPanel.add(navPanel, BorderLayout.NORTH);
        mainPanel.add(daysGridPanel, BorderLayout.CENTER);

        calendarDialog.add(mainPanel);
        calendarDialog.setVisible(true);
    }


    // ==================================================
    // GET AVAILABLE STOCK
    // ==================================================

    private int getAvailableStock(int productId) {
        List<Inventory> inventoryList = inventoryDAO.getAllInventory();

        for (Inventory inventory : inventoryList) {
            if (inventory.getProductId() == productId) {
                return inventory.getQuantity();
            }
        }

        return 0;
    }


    private void updateStockLabel() {
        if (selectedProductId == null) {
            stockLabel.setText("Available Stock: -");
            return;
        }

        int stock = getAvailableStock(selectedProductId);
        stockLabel.setText("Available Stock: " + stock);
    }


    // ==================================================
    // ADD TO CART
    // ==================================================

    private void addToCart() {
        if (selectedProductId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid product using the search field.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
            return;
        }

        int availableStock = getAvailableStock(selectedProductId);
        int alreadyInCart = 0;
        SaleItem existingItem = null;

        for (SaleItem item : cartItems) {
            if (item.getProductId() == selectedProductId) {
                alreadyInCart = item.getQuantity();
                existingItem = item;
                break;
            }
        }

        if (alreadyInCart + quantity > availableStock) {
            JOptionPane.showMessageDialog(
                    this,
                    "Insufficient stock.\nAvailable: " + availableStock,
                    "Stock Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            existingItem.setQuantity(newQuantity);
            existingItem.setSubtotal(
                    existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity))
            );
        } else {
            SaleItem item = new SaleItem();
            item.setProductId(selectedProductId);
            item.setProductName(productSearchField.getText().trim());
            item.setQuantity(quantity);
            item.setUnitPrice(selectedProductPrice);
            item.setSubtotal(selectedProductPrice.multiply(BigDecimal.valueOf(quantity)));

            cartItems.add(item);
        }

        quantityField.setText("");
        refreshCartTable();
    }


    // ==================================================
    // REMOVE CART ITEM
    // ==================================================

    private void removeCartItem() {
        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart.");
            return;
        }

        cartItems.remove(selectedRow);
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
        BigDecimal total = BigDecimal.ZERO;

        for (SaleItem item : cartItems) {
            cartTableModel.addRow(new Object[] {
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getSubtotal()
            });

            total = total.add(item.getSubtotal());
        }

        totalLabel.setText("Total: Rs. " + total.toPlainString());
    }


    // ==================================================
    // DIRECT CONFIRM SALE
    // ==================================================

    private void confirmSale() {
        if (editingPendingSaleId != -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "You are editing a pending sale.\nUse Update Pending first.");
            return;
        }

        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one product to the cart.");
            return;
        }

        if (loggedInUser == null || loggedInUser.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(this, "Logged-in user is not linked to an employee.");
            return;
        }

        String paymentMethod = paymentCombo.getSelectedItem().toString();

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Confirm this sale?",
                "Confirm Sale",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        int saleId = salesDAO.createSale(
                loggedInUser.getEmployeeId(),
                cartItems,
                paymentMethod);

        if (saleId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale could not be completed.\nPlease check available stock.",
                    "Sale Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Sale completed successfully!\nSale ID: " + saleId);

        showInvoice(saleId, paymentMethod);

        cartItems.clear();
        refreshCartTable();

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
                    "You are already editing a pending sale.\nUse Update Pending.");
            return;
        }

        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one product.");
            return;
        }

        if (loggedInUser == null || loggedInUser.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(this, "Logged-in user is not linked to an employee.");
            return;
        }

        int saleId = salesDAO.savePendingSale(
                loggedInUser.getEmployeeId(),
                cartItems);

        if (saleId != -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale saved as PENDING.\nSale ID: " + saleId);

            cartItems.clear();
            refreshCartTable();
            loadSalesHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Could not save pending sale.");
        }
    }


    // ==================================================
    // EDIT SELECTED PENDING SALE
    // ==================================================

    private void editSelectedPendingSale() {
        int row = salesTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale first.");
            return;
        }

        String status = salesTableModel.getValueAt(row, 6).toString();

        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING sales can be edited.");
            return;
        }

        int saleId = Integer.parseInt(salesTableModel.getValueAt(row, 0).toString());
        List<SaleItem> savedItems = salesDAO.getSaleItems(saleId);

        if (savedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sale items were found.");
            return;
        }

        cartItems.clear();

        for (SaleItem savedItem : savedItems) {
            SaleItem item = new SaleItem();
            item.setSaleItemId(savedItem.getSaleItemId());
            item.setSaleId(savedItem.getSaleId());
            item.setProductId(savedItem.getProductId());
            item.setProductName(savedItem.getProductName());
            item.setQuantity(savedItem.getQuantity());
            item.setUnitPrice(savedItem.getUnitPrice());
            item.setSubtotal(savedItem.getSubtotal());

            cartItems.add(item);
        }

        editingPendingSaleId = saleId;

        pendingEditLabel.setText("Editing Pending Sale ID: " + saleId);
        editControlsPanel.setVisible(true);

        // Hide Save Pending while editing an existing sale.
        savePendingButton.setVisible(false);

        refreshCartTable();
        tabs.setSelectedIndex(0);
    }


    // ==================================================
    // UPDATE PENDING SALE
    // ==================================================

    private void updatePendingSale() {
        if (editingPendingSaleId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a PENDING sale from Sales History\nand click Edit Pending first.");
            return;
        }

        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pending sale must contain at least one item.");
            return;
        }

        boolean success = salesDAO.updatePendingSale(editingPendingSaleId, cartItems);

        if (success) {
            JOptionPane.showMessageDialog(this, "Pending sale updated successfully!");
            loadSalesHistory();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale update failed.\nOnly PENDING sales can be modified.");
        }
    }


    // ==================================================
    // CANCEL EDIT MODE
    // ==================================================

    private void cancelPendingEdit() {

        editingPendingSaleId = -1;

        pendingEditLabel.setText("Editing Pending Sale ID: -");
        editControlsPanel.setVisible(false);

        // Show Save Pending again on the default New Sale screen.
        savePendingButton.setVisible(true);

        cartItems.clear();
        refreshCartTable();

        // Clear the product selection.
        selectedProductId = null;
        selectedProductPrice = BigDecimal.ZERO;
        productSearchField.setText("");
        quantityField.setText("");
        stockLabel.setText("Available Stock: -");
    }


    // ==================================================
    // COMPLETE SELECTED PENDING SALE
    // ==================================================

    private void completeSelectedPendingSale() {
        int row = salesTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale first.");
            return;
        }

        String status = salesTableModel.getValueAt(row, 6).toString();

        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING sales can be completed.");
            return;
        }

        int saleId = Integer.parseInt(salesTableModel.getValueAt(row, 0).toString());

        String[] paymentMethods = { "CASH", "CARD" };
        String paymentMethod = (String) JOptionPane.showInputDialog(
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

        boolean success = salesDAO.completePendingSale(saleId, paymentMethod);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pending sale completed successfully!\nInventory has been updated.");

            if (editingPendingSaleId == saleId) {
                editingPendingSaleId = -1;
                pendingEditLabel.setText("Editing Pending Sale ID: -");
                editControlsPanel.setVisible(false);
                cartItems.clear();
                refreshCartTable();
            }

            loadSalesHistory();
            updateStockLabel();

            showInvoice(saleId, paymentMethod);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale could not be completed.\nCheck product status and available stock.",
                    "Sale Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // ==================================================
    // INVOICE
    // ==================================================

    private void showInvoice(int saleId, String paymentMethod) {
        List<SaleItem> items = salesDAO.getSaleItems(saleId);
        BigDecimal total = BigDecimal.ZERO;

        StringBuilder invoice = new StringBuilder();

        invoice.append("========================================\n");
        invoice.append("             ANURA STORES\n");
        invoice.append("========================================\n");
        invoice.append("Sale ID : ").append(saleId).append("\n");
        invoice.append("Payment : ").append(paymentMethod).append("\n");
        invoice.append("----------------------------------------\n");

        for (SaleItem item : items) {
            invoice.append(item.getProductName()).append("\n");

            invoice.append("   ")
                    .append(item.getQuantity())
                    .append(" x Rs. ")
                    .append(item.getUnitPrice())
                    .append(" = Rs. ")
                    .append(item.getSubtotal())
                    .append("\n");

            total = total.add(item.getSubtotal());
        }

        invoice.append("----------------------------------------\n");
        invoice.append("TOTAL: Rs. ").append(total.toPlainString()).append("\n");
        invoice.append("========================================\n");
        invoice.append("               Thank you!\n");

        // Display the invoice in a text area.
        JTextArea invoiceArea = new JTextArea(invoice.toString());
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        invoiceArea.setCaretPosition(0);

        JScrollPane invoiceScrollPane = new JScrollPane(invoiceArea);
        invoiceScrollPane.setPreferredSize(new Dimension(450, 400));

        // Create a custom invoice window with Print and Close buttons.
        JDialog invoiceDialog = new JDialog(
                this,
                "Invoice - Sale " + saleId,
                true
        );

        invoiceDialog.setLayout(new BorderLayout(10, 10));

        JPanel invoicePanel = new JPanel(new BorderLayout(10, 10));
        invoicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        invoicePanel.add(invoiceScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton printButton = new JButton("Print Invoice");
        UITheme.stylePrimaryButton(printButton);

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        invoicePanel.add(buttonPanel, BorderLayout.SOUTH);
        invoiceDialog.add(invoicePanel);

        // Print the invoice when the user clicks Print Invoice.
        printButton.addActionListener(e -> {
            try {
                boolean printed = invoiceArea.print(
                        null,
                        null,
                        true,
                        null,
                        null,
                        true
                );

                if (printed) {
                    JOptionPane.showMessageDialog(
                            invoiceDialog,
                            "Invoice sent to the printer successfully.",
                            "Print Invoice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(
                        invoiceDialog,
                        "Unable to print the invoice.\n" + ex.getMessage(),
                        "Printing Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        closeButton.addActionListener(e -> invoiceDialog.dispose());

        invoiceDialog.setSize(500, 500);
        invoiceDialog.setLocationRelativeTo(this);
        invoiceDialog.setResizable(true);
        invoiceDialog.setVisible(true);
    }


    // ==================================================
    // LOAD SALES HISTORY
    // ==================================================

    private void loadSalesHistory() {
        displaySales(salesDAO.getAllSales());
    }


    // ==================================================
    // SEARCH SALES
    // ==================================================

    private void searchSales() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadSalesHistory();
            return;
        }

        displaySales(salesDAO.searchSales(keyword));
    }


    // ==================================================
    // DATE RANGE FILTER
    // ==================================================

    private void filterSalesByDate() {
        try {
            if (fromDateField.getText().trim().isEmpty()
                    || toDateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter both From and To dates.");
                return;
            }

            Date fromDate = Date.valueOf(fromDateField.getText().trim());
            Date toDate = Date.valueOf(toDateField.getText().trim());

            if (toDate.before(fromDate)) {
                JOptionPane.showMessageDialog(this, "To date cannot be before From date.");
                return;
            }

            displaySales(salesDAO.searchSalesByDateRange(fromDate, toDate));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dates must be in YYYY-MM-DD format.");
        }
    }


    // ==================================================
    // DISPLAY SALES
    // ==================================================

    private void displaySales(List<Sale> sales) {
        salesTableModel.setRowCount(0);

        int completedCount = 0;
        BigDecimal completedTotal = BigDecimal.ZERO;

        for (Sale sale : sales) {
            salesTableModel.addRow(new Object[] {
                    sale.getSaleId(),
                    sale.getEmployeeId(),
                    sale.getEmployeeName(),
                    sale.getSaleDate(),
                    sale.getTotalAmount(),
                    sale.getPaymentMethod(),
                    sale.getSaleStatus()
            });

            if ("COMPLETED".equalsIgnoreCase(sale.getSaleStatus())) {
                completedCount++;
                completedTotal = completedTotal.add(sale.getTotalAmount());
            }
        }

        summaryLabel.setText(
                "Completed Sales: "
                + completedCount
                + " | Total: Rs. "
                + completedTotal.toPlainString());
    }


    // ==================================================
    // VIEW SELECTED INVOICE
    // ==================================================

    private void viewSelectedInvoice() {
        int row = salesTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale first.");
            return;
        }

        int saleId = Integer.parseInt(salesTableModel.getValueAt(row, 0).toString());
        Object paymentValue = salesTableModel.getValueAt(row, 5);
        String paymentMethod = paymentValue == null ? "-" : paymentValue.toString();

        showInvoice(saleId, paymentMethod);
    }


    // ==================================================
    // CANCEL COMPLETED SALE
    // ==================================================

    private void cancelSelectedSale() {
        int row = salesTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale first.");
            return;
        }

        int saleId = Integer.parseInt(salesTableModel.getValueAt(row, 0).toString());
        String status = salesTableModel.getValueAt(row, 6).toString();

        if (!"COMPLETED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only COMPLETED sales can be cancelled.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Cancel Sale ID " + saleId + "?\nInventory will be restored.",
                "Cancel Sale",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = salesDAO.cancelSale(saleId);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sale cancelled successfully.\nInventory has been restored.");

            loadSalesHistory();
            updateStockLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Sale cancellation failed.");
        }
    }


    // ==================================================
    // PRODUCT ITEM HELPER
    // ==================================================

    private static class ProductItem {

        private int productId;
        private String productName;
        private BigDecimal price;

        public ProductItem(int productId, String productName, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
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
            return productId + " - " + productName + " (Rs. " + price + ")";
        }
    }
}