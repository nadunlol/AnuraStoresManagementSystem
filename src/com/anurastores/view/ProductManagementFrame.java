package com.anurastores.view;

import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.CategoryDAO;
import com.anurastores.dao.ProductDAO;
import com.anurastores.model.Category;
import com.anurastores.model.Product;
import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class ProductManagementFrame extends JFrame {

    private JTextField productIdField;
    private JTextField productNameField;

    private JComboBox<Category> categoryCombo;

    private JTextField barcodeField;
    private JTextField sellingPriceField;
    private JTextField reorderLevelField;
    private JTextField expiryDateField;
    private JButton calendarButton;

    private JComboBox<String> statusCombo;

    private JTextField searchField;
    
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private User loggedInUser;

    private JButton deactivateButton;


    public ProductManagementFrame(User user) {

        this.loggedInUser = user;

        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();

        setTitle("Product Management - Anura Stores");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadCategories();
        loadProducts();
    }


    // =====================================================
    // UI
    // =====================================================

    private void initComponents() {

        // =================================================
        // FORM PANEL
        // =================================================

        JPanel formPanel = new JPanel(new GridBagLayout());
        UITheme.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        productIdField = new JTextField(15);
        productIdField.setEditable(false);
        UITheme.styleTextField(productIdField);

        productNameField = new JTextField(15);
        UITheme.styleTextField(productNameField);

        categoryCombo = new JComboBox<Category>();
        UITheme.styleComboBox(categoryCombo);

        barcodeField = new JTextField(15);
        UITheme.styleTextField(barcodeField);

        sellingPriceField = new JTextField(15);
        UITheme.styleTextField(sellingPriceField);

        reorderLevelField = new JTextField(15);
        UITheme.styleTextField(reorderLevelField);

        expiryDateField = new JTextField(15);
        UITheme.styleTextField(expiryDateField);
        
        calendarButton = new JButton("Pick");
        UITheme.stylePrimaryButton(calendarButton);
        calendarButton.addActionListener(e -> openCalendarPopup());
        
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(datePanel);
        datePanel.add(expiryDateField, BorderLayout.CENTER);
        datePanel.add(calendarButton, BorderLayout.EAST);

        statusCombo = new JComboBox<String>(new String[] { "ACTIVE", "INACTIVE" });
        UITheme.styleComboBox(statusCombo);


        // -------------------------------------------------
        // PRODUCT ID
        // -------------------------------------------------

        addRow(formPanel, gbc, 0, "Product ID:", productIdField);


        // -------------------------------------------------
        // PRODUCT NAME
        // -------------------------------------------------

        addRow(formPanel, gbc, 1, "Product Name:", productNameField);


        // -------------------------------------------------
        // CATEGORY
        // -------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel categoryLabel = new JLabel("Category:");
        UITheme.styleNormalLabel(categoryLabel);
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);


        // -------------------------------------------------
        // BARCODE
        // -------------------------------------------------

        addRow(formPanel, gbc, 3, "Barcode:", barcodeField);


        // -------------------------------------------------
        // SELLING PRICE
        // -------------------------------------------------

        addRow(formPanel, gbc, 4, "Selling Price:", sellingPriceField);


        // -------------------------------------------------
        // REORDER LEVEL
        // -------------------------------------------------

        addRow(formPanel, gbc, 5, "Reorder Level:", reorderLevelField);


        // -------------------------------------------------
        // EXPIRY DATE
        // -------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 6;

        JLabel expiryDateLabel = new JLabel("Expiry Date (YYYY-MM-DD):");
        UITheme.styleNormalLabel(expiryDateLabel);
        formPanel.add(expiryDateLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(datePanel, gbc);



        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel statusLabel = new JLabel("Status:");
        UITheme.styleNormalLabel(statusLabel);
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);


        // =================================================
        // BUTTONS
        // =================================================

        JButton addButton = new JButton("Add");
        UITheme.stylePrimaryButton(addButton);

        JButton updateButton = new JButton("Update");
        UITheme.stylePrimaryButton(updateButton);

        deactivateButton = new JButton("Deactivate");
        UITheme.styleDangerButton(deactivateButton);

        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);

        JButton manageCategoriesButton = new JButton("Manage Categories");
        UITheme.styleSecondaryButton(manageCategoriesButton);


        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        UITheme.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(manageCategoriesButton);


        // =================================================
        // PRODUCT DEACTIVATE = ADMIN ONLY
        // =================================================

        if (loggedInUser == null || !"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            deactivateButton.setEnabled(false);
        }


        // =================================================
        // SEARCH
        // =================================================

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);

        JButton searchButton = new JButton("Search");
        UITheme.styleSecondaryButton(searchButton);

        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


        // -------------------------------------------------
        // PRODUCT SUGGESTION POPUP
        // -------------------------------------------------

        suggestionList = new JList<String>();
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFocusable(false);
        suggestionList.setVisibleRowCount(5);
        suggestionList.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        suggestionPopup = new JPopupMenu();
        suggestionPopup.setFocusable(false);
        suggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());

        suggestionPopup.add(suggestionScrollPane);


        // -------------------------------------------------
        // SEARCH PANEL
        // -------------------------------------------------

        JPanel searchPanel = new JPanel();
        UITheme.stylePanel(searchPanel);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Search:");
        UITheme.styleNormalLabel(searchLabel);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);


        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "Product ID",
                "Product Name",
                "Category",
                "Barcode",
                "Selling Price",
                "Reorder Level",
                "Expiry Date",
                "Status"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        productTable = new JTable(tableModel);
        UITheme.styleTable(productTable);


        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // =================================================
        // LAYOUT
        // =================================================

        JPanel leftPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(leftPanel);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);


        add(searchPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);


        // =================================================
        // EVENTS
        // =================================================

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deactivateButton.addActionListener(e -> deactivateProduct());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchProducts());

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadProducts();
        });


        // =================================================
        // LIVE SEARCH SUGGESTIONS
        // =================================================

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }

            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }

            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });


        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion();
                }
            }
        });


        searchField.addActionListener(e -> {
            if (suggestionPopup.isVisible() && suggestionList.getSelectedIndex() >= 0) {
                selectSuggestion();
            } else {
                searchProducts();
            }
        });


        // =================================================
        // MANAGE CATEGORIES
        // =================================================

        manageCategoriesButton.addActionListener(e -> openCategoryManagement());


        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }


    // =====================================================
    // ADD FORM ROW
    // =====================================================

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel lbl = new JLabel(label);
        UITheme.styleNormalLabel(lbl);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }


    // =====================================================
    // OPEN CATEGORY MANAGEMENT
    // =====================================================

    private void openCategoryManagement() {
        CategoryManagementFrame categoryFrame = new CategoryManagementFrame();

        categoryFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCategories();
                loadProducts();
            }
        });

        categoryFrame.setVisible(true);
    }


    // =====================================================
    // LOAD ACTIVE CATEGORIES ONLY
    // =====================================================

    private void loadCategories() {
        categoryCombo.removeAllItems();

        List<Category> categories = categoryDAO.getActiveCategories();

        for (Category category : categories) {
            categoryCombo.addItem(category);
        }
    }
    
    // =====================================================
    // CALENDAR POPUP DIALOG
    // =====================================================

    private void openCalendarPopup() {
        JDialog calendarDialog = new JDialog(this, "Select Hire Date", true);
        calendarDialog.setSize(460, 380);
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        UITheme.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        LocalDate initialDate = LocalDate.now();
        if (!expiryDateField.getText().trim().isEmpty()) {
            try {
                initialDate = LocalDate.parse(expiryDateField.getText().trim());
            } catch (Exception ignored) {}
        }

        final LocalDate[] viewingDate = { initialDate };

        // Top Navigation Panel (Month/Year dropdowns + Arrows)
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

        // Days Grid Panel
        JPanel daysGridPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        UITheme.stylePanel(daysGridPanel);

        Runnable updateCalendarGrid = () -> {
            daysGridPanel.removeAll();
            int monthIdx = monthCombo.getSelectedIndex() + 1;
            int yearVal = (Integer) yearCombo.getSelectedItem();
            YearMonth yearMonth = YearMonth.of(yearVal, monthIdx);
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            int dayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
            int offset = dayOfWeekValue % 7; // Sunday start format (Su=0)

            String[] dayHeaders = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };
            for (String header : dayHeaders) {
                JLabel lbl = new JLabel(header, SwingConstants.CENTER);
                UITheme.styleNormalLabel(lbl);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                daysGridPanel.add(lbl);
            }

            // Blank spaces for previous month trailing days
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

            // Current month days
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
                    expiryDateField.setText(chosen.toString());
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

    // =====================================================
    // ADD PRODUCT
    // =====================================================

    private void addProduct() {
        try {
            Product product = getProductFromForm();

            if (product == null) {
                return;
            }

            if (productDAO.barcodeExists(product.getBarcode(), 0)) {
                JOptionPane.showMessageDialog(
                        this,
                        "This barcode is already used by another product.",
                        "Duplicate Barcode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = productDAO.addProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                clearFields();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not add product.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please check the entered product information.",
                    "Invalid Data",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    // =====================================================
    // UPDATE PRODUCT
    // =====================================================

    private void updateProduct() {
        if (productIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product first.");
            return;
        }

        try {
            Product product = getProductFromForm();

            if (product == null) {
                return;
            }

            int productId = Integer.parseInt(productIdField.getText());
            product.setProductId(productId);

            if (productDAO.barcodeExists(product.getBarcode(), productId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "This barcode is already used by another product.",
                        "Duplicate Barcode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = productDAO.updateProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                clearFields();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Product update failed.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please check the entered product information.",
                    "Invalid Data",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    // =====================================================
    // DEACTIVATE PRODUCT
    // =====================================================

    private void deactivateProduct() {
        if (productIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to deactivate this product?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        int productId = Integer.parseInt(productIdField.getText());
        boolean success = productDAO.deactivateProduct(productId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Product deactivated successfully!");
            clearFields();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Could not deactivate product.");
        }
    }


    // =====================================================
    // FORM -> PRODUCT OBJECT
    // =====================================================

    private Product getProductFromForm() {
        String productName = productNameField.getText().trim();

        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required.");
            return null;
        }

        Category category = (Category) categoryCombo.getSelectedItem();

        if (category == null) {
            JOptionPane.showMessageDialog(this, "Please select an ACTIVE category.");
            return null;
        }

        String priceText = sellingPriceField.getText().trim();

        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selling price is required.");
            return null;
        }

        BigDecimal sellingPrice;

        try {
            sellingPrice = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Selling price must be a valid number.");
            return null;
        }

        if (sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Selling price must be greater than zero.");
            return null;
        }

        int reorderLevel = 0;
        String reorderText = reorderLevelField.getText().trim();

        if (!reorderText.isEmpty()) {
            try {
                reorderLevel = Integer.parseInt(reorderText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Reorder level must be a whole number.");
                return null;
            }
        }

        if (reorderLevel < 0) {
            JOptionPane.showMessageDialog(this, "Reorder level cannot be negative.");
            return null;
        }

        Date expiryDate = null;
        String expiryText = expiryDateField.getText().trim();

        if (!expiryText.isEmpty()) {
            try {
                expiryDate = Date.valueOf(expiryText);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Expiry date must be in YYYY-MM-DD format.",
                        "Invalid Expiry Date",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            LocalDate expiryLocalDate = expiryDate.toLocalDate();
            LocalDate today = LocalDate.now();

            if (expiryLocalDate.isBefore(today)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Expiry date cannot be in the past.\nPlease enter today or a future date.",
                        "Invalid Expiry Date",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }

        Product product = new Product();
        product.setProductName(productName);
        product.setCategoryId(category.getCategoryId());
        product.setCategoryName(category.getCategoryName());
        product.setBarcode(barcodeField.getText().trim());
        product.setSellingPrice(sellingPrice);
        product.setReorderLevel(reorderLevel);
        product.setExpiryDate(expiryDate);
        product.setStatus(statusCombo.getSelectedItem().toString());

        return product;
    }


    // =====================================================
    // LOAD ALL PRODUCTS
    // =====================================================

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        displayProducts(products);
    }


    // =====================================================
    // SEARCH PRODUCTS
    // =====================================================

    private void searchProducts() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        List<Product> products = productDAO.searchProducts(keyword);
        displayProducts(products);
    }


    // =====================================================
    // DISPLAY PRODUCTS
    // =====================================================

    private void displayProducts(List<Product> products) {
        tableModel.setRowCount(0);

        for (Product product : products) {
            tableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategoryName(),
                    product.getBarcode(),
                    product.getSellingPrice(),
                    product.getReorderLevel(),
                    product.getExpiryDate(),
                    product.getStatus()
            });
        }
    }


    // =====================================================
    // TABLE -> FORM
    // =====================================================

    private void fillFormFromTable() {
        int row = productTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        productIdField.setText(getTableValue(row, 0));
        productNameField.setText(getTableValue(row, 1));
        selectCategory(getTableValue(row, 2));
        barcodeField.setText(getTableValue(row, 3));
        sellingPriceField.setText(getTableValue(row, 4));
        reorderLevelField.setText(getTableValue(row, 5));
        expiryDateField.setText(getTableValue(row, 6));
        statusCombo.setSelectedItem(getTableValue(row, 7));
    }


    // =====================================================
    // SELECT CATEGORY
    // =====================================================

    private void selectCategory(String categoryName) {
        categoryCombo.setSelectedItem(null);

        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            Category category = categoryCombo.getItemAt(i);

            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                categoryCombo.setSelectedIndex(i);
                return;
            }
        }
    }


    // =====================================================
    // GET TABLE VALUE
    // =====================================================

    private String getTableValue(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }


    // =====================================================
    // UPDATE PRODUCT SEARCH SUGGESTIONS
    // =====================================================

    private void updateSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }

        List<Product> products = productDAO.searchProducts(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<String>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Product product : products) {
            String productName = product.getProductName();

            if (productName == null || productName.trim().isEmpty()) {
                continue;
            }

            if (!productName.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(productName)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(productName);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            suggestionPopup.setVisible(false);
            return;
        }

        suggestionList.setModel(listModel);
        suggestionList.setSelectedIndex(0);

        int rowHeight = suggestionList.getFixedCellHeight();
        if (rowHeight <= 0) {
            rowHeight = 25;
        }

        int height = Math.min(count, 5) * rowHeight + 4;

        suggestionScrollPane.setPreferredSize(new Dimension(searchField.getWidth(), height));
        suggestionPopup.pack();

        suggestionPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow();
    }


    // =====================================================
    // SELECT PRODUCT SUGGESTION
    // =====================================================

    private void selectSuggestion() {
        String selectedProduct = suggestionList.getSelectedValue();

        if (selectedProduct == null || selectedProduct.trim().isEmpty()) {
            return;
        }

        searchField.setText(selectedProduct);
        suggestionPopup.setVisible(false);
        searchProducts();
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {
        productIdField.setText("");
        productNameField.setText("");
        barcodeField.setText("");
        sellingPriceField.setText("");
        reorderLevelField.setText("");
        expiryDateField.setText("");

        statusCombo.setSelectedItem("ACTIVE");

        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }

        productTable.clearSelection();
        searchField.setText("");
        suggestionPopup.setVisible(false);
    }
}