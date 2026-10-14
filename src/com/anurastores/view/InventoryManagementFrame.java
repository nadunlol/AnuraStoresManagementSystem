package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.InventoryDAO;
import com.anurastores.dao.ProductDAO;
import com.anurastores.model.Inventory;
import com.anurastores.model.Product;
import com.anurastores.model.StockMovement;
import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class InventoryManagementFrame extends JFrame {

	private JTextField productSearchField;
	private Integer selectedProductId = null;

	// Prevent autocomplete when the product field is changed by code.
	private boolean suppressProductSuggestions = false;
    private JPopupMenu productSuggestionPopup;
    private JList<ProductItem> productSuggestionList;
    private JScrollPane productSuggestionScrollPane;

    private JTextField quantityField;
    private JTextField remarksField;
    
    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JLabel currentStockLabel;
    private JLabel lowStockLabel;

    private JTable inventoryTable;
    private JTable movementTable;

    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel movementTableModel;

    private InventoryDAO inventoryDAO;
    private ProductDAO productDAO;

    private User loggedInUser;

    private JButton deleteMovementButton;

    public InventoryManagementFrame(User user) {

        this.loggedInUser = user;

        inventoryDAO = new InventoryDAO();
        productDAO = new ProductDAO();

        setTitle("Inventory Management - Anura Stores");
        setSize(1250, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadInventory();
        loadStockMovements();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        // ==========================
        // STOCK FORM
        // ==========================

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

        quantityField = new JTextField(8);
        UITheme.styleTextField(quantityField);

        remarksField = new JTextField(18);
        UITheme.styleTextField(remarksField);

        currentStockLabel = new JLabel("Current Stock: -");
        UITheme.styleNormalLabel(currentStockLabel);

        lowStockLabel = new JLabel("Low Stock Items: 0");
        UITheme.styleNormalLabel(lowStockLabel);


        JButton addStockButton = new JButton("Add Stock");
        UITheme.stylePrimaryButton(addStockButton);

        JButton adjustStockButton = new JButton("Adjust Stock");
        UITheme.stylePrimaryButton(adjustStockButton);

        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);


        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(stockPanel);
        stockPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Manage Stock"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        JLabel productLabel = new JLabel("Product:");
        UITheme.styleNormalLabel(productLabel);
        JLabel qtyLabel = new JLabel("Quantity:");
        UITheme.styleNormalLabel(qtyLabel);
        JLabel remLabel = new JLabel("Remarks:");
        UITheme.styleNormalLabel(remLabel);

        stockPanel.add(productLabel);
        stockPanel.add(productSearchField);
        stockPanel.add(qtyLabel);
        stockPanel.add(quantityField);
        stockPanel.add(remLabel);
        stockPanel.add(remarksField);
        stockPanel.add(addStockButton);
        stockPanel.add(adjustStockButton);
        stockPanel.add(clearButton);


        // ==========================
        // SEARCH (Centered)
        // ==========================

        searchField = new JTextField(15);
        UITheme.styleTextField(searchField);

        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);

        // Inventory Main Search Suggestions Popup
        suggestionList = new JList<>();
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFocusable(false);
        suggestionList.setVisibleRowCount(5);

        suggestionPopup = new JPopupMenu();
        suggestionPopup.setFocusable(false);
        suggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        suggestionPopup.add(suggestionScrollPane);


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        UITheme.stylePanel(searchPanel);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel searchInvLabel = new JLabel("Search Inventory:");
        UITheme.styleNormalLabel(searchInvLabel);

        searchPanel.add(searchInvLabel);
        searchPanel.add(searchField);
        searchPanel.add(showAllButton);
        searchPanel.add(currentStockLabel);
        searchPanel.add(lowStockLabel);


        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        UITheme.stylePanel(topPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        topPanel.add(stockPanel);
        topPanel.add(searchPanel);


        // ==========================
        // INVENTORY TABLE
        // ==========================

        String[] inventoryColumns = {
                "Inventory ID",
                "Product ID",
                "Product Name",
                "Quantity",
                "Reorder Level",
                "Stock Status",
                "Last Updated"
        };

        inventoryTableModel = new DefaultTableModel(inventoryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(inventoryTableModel);
        UITheme.styleTable(inventoryTable);

        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);
        inventoryScrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // ==========================
        // STOCK MOVEMENT TABLE
        // ==========================

        String[] movementColumns = {
                "Movement ID",
                "Product ID",
                "Product Name",
                "Type",
                "Quantity",
                "Date",
                "Remarks"
        };

        movementTableModel = new DefaultTableModel(movementColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        movementTable = new JTable(movementTableModel);
        UITheme.styleTable(movementTable);

        JScrollPane movementScrollPane = new JScrollPane(movementTable);
        movementScrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // ==========================
        // DELETE MOVEMENT
        // ==========================

        deleteMovementButton = new JButton("Delete / Reverse Selected Movement");
        UITheme.styleDangerButton(deleteMovementButton);

        if (loggedInUser == null
                || !"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            deleteMovementButton.setEnabled(false);
        }


        JPanel movementPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(movementPanel);
        movementPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel movementTitle = new JLabel("Stock Movement History");
        UITheme.styleSectionLabel(movementTitle);
        movementPanel.add(movementTitle, BorderLayout.NORTH);
        movementPanel.add(movementScrollPane, BorderLayout.CENTER);

        JPanel deleteButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        UITheme.stylePanel(deleteButtonWrapper);
        deleteButtonWrapper.add(deleteMovementButton);
        movementPanel.add(deleteButtonWrapper, BorderLayout.SOUTH);


        JPanel inventoryPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(inventoryPanel);
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel inventoryTitle = new JLabel("Current Inventory");
        UITheme.styleSectionLabel(inventoryTitle);
        inventoryPanel.add(inventoryTitle, BorderLayout.NORTH);
        inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                inventoryPanel,
                movementPanel);
        splitPane.setDividerLocation(280);
        splitPane.setBorder(BorderFactory.createEmptyBorder());


        JPanel centerPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerPanel.add(splitPane, BorderLayout.CENTER);


        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);


        // ==========================
        // EVENTS
        // ==========================

        addStockButton.addActionListener(e -> addStock());
        adjustStockButton.addActionListener(e -> adjustStock());

        // Live Main Search Field Listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateMainSearchSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateMainSearchSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateMainSearchSuggestions(); }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectMainSearchSuggestion();
                }
            }
        });

        searchField.addActionListener(e -> {
            if (suggestionPopup.isVisible() && suggestionList.getSelectedIndex() >= 0) {
                selectMainSearchSuggestion();
            } else {
                searchInventory();
            }
        });

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadInventory();
        });

        // Live Product Search Field Listener
        productSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!suppressProductSuggestions) {
                    updateProductSuggestions();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!suppressProductSuggestions) {
                    updateProductSuggestions();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!suppressProductSuggestions) {
                    updateProductSuggestions();
                }
            }
        });

        productSuggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectProductSuggestion();
                }
            }
        });

        clearButton.addActionListener(e -> clearFields());
        deleteMovementButton.addActionListener(e -> deleteMovement());

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectProductFromInventoryTable();
            }
        });
    }


    // =====================================================
    // PRODUCT SEARCH SUGGESTIONS
    // =====================================================

    private void updateProductSuggestions() {
        String keyword = productSearchField.getText().trim();

        if (keyword.isEmpty()) {
            selectedProductId = null;
            productSuggestionPopup.setVisible(false);
            updateCurrentStockLabel();
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

            listModel.addElement(new ProductItem(product.getProductId(), name));
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            productSuggestionPopup.setVisible(false);
            selectedProductId = null;
            updateCurrentStockLabel();
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

        if (selected == null) {
            return;
        }

        suppressProductSuggestions = true;

        try {
            selectedProductId = selected.getProductId();
            productSearchField.setText(selected.getProductName());
            productSuggestionPopup.setVisible(false);
        } finally {
            suppressProductSuggestions = false;
        }

        updateCurrentStockLabel();
    }


    // =====================================================
    // MAIN SEARCH SUGGESTIONS
    // =====================================================

    private void updateMainSearchSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            loadInventory();
            return;
        }

        List<Inventory> inventoryList = inventoryDAO.searchInventory(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Inventory inv : inventoryList) {
            String name = inv.getProductName();
            if (name == null || !name.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(name)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(name);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            suggestionPopup.setVisible(false);
            searchInventory();
            return;
        }

        suggestionList.setModel(listModel);
        suggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        suggestionScrollPane.setPreferredSize(new Dimension(searchField.getWidth(), height));
        suggestionPopup.pack();
        suggestionPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow();
        searchInventory();
    }

    private void selectMainSearchSuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            searchField.setText(selected);
            suggestionPopup.setVisible(false);
            searchInventory();
        }
    }


    // ============================================
    // ADD STOCK
    // ============================================

    private void addStock() {
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
            JOptionPane.showMessageDialog(this, "Stock quantity must be greater than zero.");
            return;
        }

        boolean success = inventoryDAO.addStock(
                selectedProductId,
                quantity,
                remarksField.getText().trim());

        if (success) {
            JOptionPane.showMessageDialog(this, "Stock added successfully!");
            clearFields();
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, "Could not add stock.");
        }
    }


    // ============================================
    // ADJUST STOCK
    // ============================================

    private void adjustStock() {
        if (selectedProductId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid product using the search field.");
            return;
        }

        int adjustment;

        try {
            adjustment = Integer.parseInt(quantityField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter a valid adjustment quantity.");
            return;
        }

        if (adjustment == 0) {
            JOptionPane.showMessageDialog(this, "Adjustment cannot be zero.");
            return;
        }

        boolean success = inventoryDAO.adjustStock(
                selectedProductId,
                adjustment,
                remarksField.getText().trim());

        if (success) {
            JOptionPane.showMessageDialog(this, "Stock adjusted successfully!");
            clearFields();
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Adjustment failed.\nCheck that the stock will not become negative.");
        }
    }


    // ============================================
    // LOAD INVENTORY
    // ============================================

    private void loadInventory() {
        List<Inventory> inventoryList = inventoryDAO.getAllInventory();
        displayInventory(inventoryList);
    }


    // ============================================
    // SEARCH
    // ============================================

    private void searchInventory() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadInventory();
            return;
        }

        List<Inventory> inventoryList = inventoryDAO.searchInventory(keyword);
        displayInventory(inventoryList);
    }


    // ============================================
    // DISPLAY INVENTORY
    // ============================================

    private void displayInventory(List<Inventory> inventoryList) {
        inventoryTableModel.setRowCount(0);
        int lowStockCount = 0;

        for (Inventory inventory : inventoryList) {
            inventoryTableModel.addRow(new Object[] {
                    inventory.getInventoryId(),
                    inventory.getProductId(),
                    inventory.getProductName(),
                    inventory.getQuantity(),
                    inventory.getReorderLevel(),
                    inventory.getStockStatus(),
                    inventory.getLastUpdated()
            });

            if ("LOW STOCK".equalsIgnoreCase(inventory.getStockStatus())
                    || "OUT OF STOCK".equalsIgnoreCase(inventory.getStockStatus())) {
                lowStockCount++;
            }
        }

        lowStockLabel.setText("Low Stock Items: " + lowStockCount);
        updateCurrentStockLabel();
    }


    // ============================================
    // LOAD MOVEMENTS
    // ============================================

    private void loadStockMovements() {
        movementTableModel.setRowCount(0);

        List<StockMovement> movements = inventoryDAO.getAllStockMovements();

        for (StockMovement movement : movements) {
            movementTableModel.addRow(new Object[] {
                    movement.getMovementId(),
                    movement.getProductId(),
                    movement.getProductName(),
                    movement.getMovementType(),
                    movement.getQuantity(),
                    movement.getMovementDate(),
                    movement.getRemarks()
            });
        }
    }


    // ============================================
    // DELETE / REVERSE MOVEMENT
    // ============================================

    private void deleteMovement() {
        int selectedRow = movementTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock movement first.");
            return;
        }

        int movementId = Integer.parseInt(
                movementTableModel.getValueAt(selectedRow, 0).toString());
        String movementType = movementTableModel.getValueAt(selectedRow, 3).toString();

        if ("SALE".equalsIgnoreCase(movementType)
                || "SALE_CANCEL".equalsIgnoreCase(movementType)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sales-related stock movements cannot be manually deleted.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete this stock movement and reverse its effect?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            boolean success = inventoryDAO.deleteStockMovement(movementId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Stock movement reversed successfully!");
                refreshAll();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not reverse this movement.\nThe operation may result in invalid stock.");
            }
        }
    }


    // ============================================
    // SELECT PRODUCT FROM INVENTORY TABLE
    // ============================================

    private void selectProductFromInventoryTable() {
        int row = inventoryTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        // Convert the selected row in case the table is sorted.
        row = inventoryTable.convertRowIndexToModel(row);

        int productId = Integer.parseInt(
                inventoryTableModel.getValueAt(row, 1).toString());

        String productName =
                inventoryTableModel.getValueAt(row, 2).toString();

        // Keep the table selection as the authoritative product selection.
        suppressProductSuggestions = true;

        try {
            selectedProductId = productId;
            productSearchField.setText(productName);
            productSuggestionPopup.setVisible(false);
        } finally {
            suppressProductSuggestions = false;
        }

        updateCurrentStockLabel();
    }


    // ============================================
    // CURRENT STOCK LABEL
    // ============================================

    private void updateCurrentStockLabel() {
        if (selectedProductId == null) {
            currentStockLabel.setText("Current Stock: -");
            return;
        }

        List<Inventory> inventoryList = inventoryDAO.getAllInventory();

        for (Inventory inventory : inventoryList) {
            if (inventory.getProductId() == selectedProductId) {
                currentStockLabel.setText("Current Stock: " + inventory.getQuantity());
                return;
            }
        }

        currentStockLabel.setText("Current Stock: 0");
    }


    // ============================================
    // REFRESH
    // ============================================

    private void refreshAll() {
        loadInventory();
        loadStockMovements();
        updateCurrentStockLabel();
    }


    // ============================================
    // CLEAR
    // ============================================

    private void clearFields() {
        suppressProductSuggestions = true;

        try {
            quantityField.setText("");
            remarksField.setText("");
            productSearchField.setText("");

            selectedProductId = null;

            productSuggestionPopup.setVisible(false);
            suggestionPopup.setVisible(false);

            inventoryTable.clearSelection();
        } finally {
            suppressProductSuggestions = false;
        }

        updateCurrentStockLabel();
    }


    // ============================================
    // PRODUCT ITEM HELPER
    // ============================================

    private static class ProductItem {
        private int productId;
        private String productName;

        public ProductItem(int productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }

        @Override
        public String toString() {
            return productId + " - " + productName;
        }
    }
}