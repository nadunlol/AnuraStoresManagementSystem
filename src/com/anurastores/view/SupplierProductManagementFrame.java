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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.ProductDAO;
import com.anurastores.dao.SupplierDAO;
import com.anurastores.dao.SupplierProductDAO;
import com.anurastores.model.Product;
import com.anurastores.model.Supplier;
import com.anurastores.model.SupplierProduct;
import com.anurastores.util.UITheme;

public class SupplierProductManagementFrame extends JFrame {

    private SupplierDAO supplierDAO;
    private ProductDAO productDAO;
    private SupplierProductDAO supplierProductDAO;

    private JTextField supplierSearchField;
    private Integer selectedSupplierId = null;
    private JPopupMenu supplierSuggestionPopup;
    private JList<SupplierItem> supplierSuggestionList;
    private JScrollPane supplierSuggestionScrollPane;

    private JTextField productSearchField;
    private Integer selectedProductId = null;
    private JPopupMenu productSuggestionPopup;
    private JList<ProductItem> productSuggestionList;
    private JScrollPane productSuggestionScrollPane;

    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JTable relationshipTable;
    private DefaultTableModel tableModel;


    public SupplierProductManagementFrame() {

        supplierDAO = new SupplierDAO();
        productDAO = new ProductDAO();
        supplierProductDAO = new SupplierProductDAO();

        setTitle("Supplier Product Management - Anura Stores");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadRelationships();
    }


    // =====================================================
    // UI
    // =====================================================

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));


        // =================================================
        // ASSIGNMENT AREA
        // =================================================

        JPanel assignmentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        UITheme.stylePanel(assignmentPanel);
        assignmentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Assign Product to Supplier"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );


        // Supplier Search Field & Popup
        supplierSearchField = new JTextField(15);
        UITheme.styleTextField(supplierSearchField);

        supplierSuggestionList = new JList<>();
        supplierSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierSuggestionList.setFocusable(false);
        supplierSuggestionList.setVisibleRowCount(5);

        supplierSuggestionPopup = new JPopupMenu();
        supplierSuggestionPopup.setFocusable(false);
        supplierSuggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        supplierSuggestionScrollPane = new JScrollPane(supplierSuggestionList);
        supplierSuggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        supplierSuggestionPopup.add(supplierSuggestionScrollPane);


        // Product Search Field & Popup
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


        JButton assignButton = new JButton("Assign Product");
        UITheme.stylePrimaryButton(assignButton);


        JButton showSupplierButton = new JButton("Show Selected Supplier Products");
        UITheme.stylePrimaryButton(showSupplierButton);


        JLabel supplierLabel = new JLabel("Supplier:");
        UITheme.styleNormalLabel(supplierLabel);
        assignmentPanel.add(supplierLabel);
        assignmentPanel.add(supplierSearchField);


        JLabel productLabel = new JLabel("Product:");
        UITheme.styleNormalLabel(productLabel);
        assignmentPanel.add(productLabel);
        assignmentPanel.add(productSearchField);


        assignmentPanel.add(assignButton);
        assignmentPanel.add(showSupplierButton);


        // =================================================
        // SEARCH AREA (Centered)
        // =================================================

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(searchPanel);


        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);


        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


        // Main Search Suggestion Popup
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


        JLabel searchLabel = new JLabel("Search:");
        UITheme.styleNormalLabel(searchLabel);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(showAllButton);


        JPanel northPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(northPanel);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        northPanel.add(assignmentPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);


        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "Supplier ID",
                "Supplier Name",
                "Product ID",
                "Product Name"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        relationshipTable = new JTable(tableModel);
        UITheme.styleTable(relationshipTable);


        JScrollPane scrollPane = new JScrollPane(relationshipTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // =================================================
        // BOTTOM BUTTONS
        // =================================================

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(bottomPanel);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));


        JButton removeButton = new JButton("Remove Selected Association");
        UITheme.styleDangerButton(removeButton);


        JButton refreshButton = new JButton("Refresh");
        UITheme.styleSecondaryButton(refreshButton);


        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);


        bottomPanel.add(removeButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);


        // =================================================
        // ADD TO FRAME
        // =================================================

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);


        // =================================================
        // EVENTS
        // =================================================

        assignButton.addActionListener(e -> assignProduct());

        // Live Search Listeners
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
                searchRelationships();
            }
        });

        // Supplier Search Suggestions
        supplierSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSupplierSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSupplierSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSupplierSuggestions(); }
        });

        supplierSuggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSupplierSuggestion();
                }
            }
        });

        // Product Search Suggestions
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

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadRelationships();
        });

        showSupplierButton.addActionListener(e -> showSelectedSupplierProducts());
        removeButton.addActionListener(e -> removeSelectedAssociation());
        refreshButton.addActionListener(e -> refreshAll());
        closeButton.addActionListener(e -> dispose());


        relationshipTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectRelationshipFromTable();
            }
        });
    }


    // =====================================================
    // SUPPLIER SEARCH SUGGESTIONS
    // =====================================================

    private void updateSupplierSuggestions() {
        String keyword = supplierSearchField.getText().trim();

        if (keyword.isEmpty()) {
            selectedSupplierId = null;
            supplierSuggestionPopup.setVisible(false);
            return;
        }

        List<Supplier> suppliers = supplierDAO.searchSuppliers(keyword);
        javax.swing.DefaultListModel<SupplierItem> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Supplier supplier : suppliers) {
            if (!"ACTIVE".equalsIgnoreCase(supplier.getStatus())) {
                continue;
            }
            String name = supplier.getSupplierName();
            if (name == null || !name.toLowerCase().contains(lowerKeyword)) {
                continue;
            }

            listModel.addElement(new SupplierItem(supplier.getSupplierId(), name));
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            supplierSuggestionPopup.setVisible(false);
            selectedSupplierId = null;
            return;
        }

        supplierSuggestionList.setModel(listModel);
        supplierSuggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        supplierSuggestionScrollPane.setPreferredSize(new Dimension(supplierSearchField.getWidth(), height));
        supplierSuggestionPopup.pack();
        supplierSuggestionPopup.show(supplierSearchField, 0, supplierSearchField.getHeight());
        supplierSearchField.requestFocusInWindow();
    }

    private void selectSupplierSuggestion() {
        SupplierItem selected = supplierSuggestionList.getSelectedValue();
        if (selected != null) {
            selectedSupplierId = selected.getSupplierId();
            supplierSearchField.setText(selected.getSupplierName());
            supplierSuggestionPopup.setVisible(false);
        }
    }


    // =====================================================
    // PRODUCT SEARCH SUGGESTIONS
    // =====================================================

    private void updateProductSuggestions() {
        String keyword = productSearchField.getText().trim();

        if (keyword.isEmpty()) {
            selectedProductId = null;
            productSuggestionPopup.setVisible(false);
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
            selectedProductId = selected.getProductId();
            productSearchField.setText(selected.getProductName());
            productSuggestionPopup.setVisible(false);
        }
    }


    // =====================================================
    // MAIN SEARCH SUGGESTIONS
    // =====================================================

    private void updateMainSearchSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            loadRelationships();
            return;
        }

        List<SupplierProduct> list = supplierProductDAO.searchSupplierProducts(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (SupplierProduct item : list) {
            String name = item.getSupplierName();
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
            searchRelationships();
            return;
        }

        suggestionList.setModel(listModel);
        suggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        suggestionScrollPane.setPreferredSize(new Dimension(searchField.getWidth(), height));
        suggestionPopup.pack();
        suggestionPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow();
        searchRelationships();
    }

    private void selectMainSearchSuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            searchField.setText(selected);
            suggestionPopup.setVisible(false);
            searchRelationships();
        }
    }


    // =====================================================
    // LOAD ALL RELATIONSHIPS
    // =====================================================

    private void loadRelationships() {
        displayRelationships(supplierProductDAO.getAllSupplierProducts());
    }


    // =====================================================
    // DISPLAY TABLE
    // =====================================================

    private void displayRelationships(List<SupplierProduct> list) {
        tableModel.setRowCount(0);

        for (SupplierProduct item : list) {
            tableModel.addRow(new Object[] {
                    item.getSupplierId(),
                    item.getSupplierName(),
                    item.getProductId(),
                    item.getProductName()
            });
        }
    }


    // =====================================================
    // ASSIGN PRODUCT
    // =====================================================

    private void assignProduct() {
        if (selectedSupplierId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid supplier using the search field.");
            return;
        }

        if (selectedProductId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid product using the search field.");
            return;
        }

        if (supplierProductDAO.associationExists(selectedSupplierId, selectedProductId)) {
            JOptionPane.showMessageDialog(
                    this,
                    "This product is already assigned to the selected supplier.",
                    "Duplicate Assignment",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = supplierProductDAO.assignProductToSupplier(selectedSupplierId, selectedProductId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Product assigned to supplier successfully!");
            loadRelationships();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not assign product.\nMake sure both supplier and product are ACTIVE.",
                    "Assignment Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void searchRelationships() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadRelationships();
            return;
        }

        displayRelationships(supplierProductDAO.searchSupplierProducts(keyword));
    }


    // =====================================================
    // SHOW PRODUCTS FOR SELECTED SUPPLIER
    // =====================================================

    private void showSelectedSupplierProducts() {
        if (selectedSupplierId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid supplier using the search field.");
            return;
        }

        displayRelationships(supplierProductDAO.getProductsBySupplier(selectedSupplierId));
    }


    // =====================================================
    // REMOVE RELATIONSHIP
    // =====================================================

    private void removeSelectedAssociation() {
        int row = relationshipTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier-product relationship first.");
            return;
        }

        int supplierId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int productId = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
        String supplierName = tableModel.getValueAt(row, 1).toString();
        String productName = tableModel.getValueAt(row, 3).toString();

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Remove the relationship between:\n\n"
                + "Supplier: " + supplierName
                + "\nProduct: " + productName
                + "\n\n"
                + "The supplier and product records will NOT be deleted.",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = supplierProductDAO.removeSupplierProduct(supplierId, productId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier-product association removed successfully!");
            loadRelationships();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not remove association.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // TABLE SELECTION -> FIELDS
    // =====================================================

    private void selectRelationshipFromTable() {
        int row = relationshipTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedSupplierId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        supplierSearchField.setText(tableModel.getValueAt(row, 1).toString());

        selectedProductId = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
        productSearchField.setText(tableModel.getValueAt(row, 3).toString());
    }


    // =====================================================
    // REFRESH ALL
    // =====================================================

    private void refreshAll() {
        loadRelationships();
        searchField.setText("");
        supplierSearchField.setText("");
        productSearchField.setText("");
        selectedSupplierId = null;
        selectedProductId = null;
        suggestionPopup.setVisible(false);
        supplierSuggestionPopup.setVisible(false);
        productSuggestionPopup.setVisible(false);
    }


    // =====================================================
    // SUPPLIER ITEM HELPER
    // =====================================================

    private static class SupplierItem {
        private int supplierId;
        private String supplierName;

        public SupplierItem(int supplierId, String supplierName) {
            this.supplierId = supplierId;
            this.supplierName = supplierName;
        }

        public int getSupplierId() { return supplierId; }
        public String getSupplierName() { return supplierName; }

        @Override
        public String toString() {
            return supplierId + " - " + supplierName;
        }
    }


    // =====================================================
    // PRODUCT ITEM HELPER
    // =====================================================

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