package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import com.anurastores.dao.SupplierDAO;
import com.anurastores.model.Supplier;
import com.anurastores.util.UITheme;

public class SupplierManagementFrame extends JFrame {

    private JTextField supplierIdField;
    private JTextField supplierNameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    
    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JComboBox<String> statusCombo;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private SupplierDAO supplierDAO;

    // Standard Regex Patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s-]{7,15}$"
    );


    public SupplierManagementFrame() {

        supplierDAO = new SupplierDAO();

        setTitle("Supplier Management - Anura Stores");
        setSize(1300, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadSuppliers();
    }


    // =====================================================
    // UI
    // =====================================================

    private void initComponents() {

        // =================================================
        // FORM
        // =================================================

        JPanel formPanel = new JPanel(new GridBagLayout());
        UITheme.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        supplierIdField = new JTextField(15);
        supplierIdField.setEditable(false);
        UITheme.styleTextField(supplierIdField);


        supplierNameField = new JTextField(15);
        UITheme.styleTextField(supplierNameField);


        contactPersonField = new JTextField(15);
        UITheme.styleTextField(contactPersonField);


        phoneField = new JTextField(15);
        UITheme.styleTextField(phoneField);


        emailField = new JTextField(15);
        UITheme.styleTextField(emailField);


        addressField = new JTextField(15);
        UITheme.styleTextField(addressField);


        statusCombo = new JComboBox<String>(new String[] { "ACTIVE", "INACTIVE" });
        UITheme.styleComboBox(statusCombo);


        addRow(formPanel, gbc, 0, "Supplier ID:", supplierIdField);
        addRow(formPanel, gbc, 1, "Supplier Name:", supplierNameField);
        addRow(formPanel, gbc, 2, "Contact Person:", contactPersonField);
        addRow(formPanel, gbc, 3, "Phone:", phoneField);
        addRow(formPanel, gbc, 4, "Email:", emailField);
        addRow(formPanel, gbc, 5, "Address:", addressField);


        gbc.gridx = 0;
        gbc.gridy = 6;

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


        JButton deactivateButton = new JButton("Deactivate");
        UITheme.styleDangerButton(deactivateButton);


        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);


        JButton manageProductsButton = new JButton("Manage Supplier Products");
        UITheme.styleSecondaryButton(manageProductsButton);


        JPanel topRowButtons = new JPanel(new GridLayout(1, 4, 8, 0));
        UITheme.stylePanel(topRowButtons);
        topRowButtons.add(addButton);
        topRowButtons.add(updateButton);
        topRowButtons.add(deactivateButton);
        topRowButtons.add(clearButton);

        JPanel bottomRowButtons = new JPanel(new GridLayout(1, 1, 0, 0));
        UITheme.stylePanel(bottomRowButtons);
        bottomRowButtons.add(manageProductsButton);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        UITheme.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        buttonPanel.add(topRowButtons);
        buttonPanel.add(bottomRowButtons);


        // =================================================
        // SEARCH
        // =================================================

        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);

        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


        // Suggestion Popup setup
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


        JPanel searchPanel = new JPanel();
        UITheme.stylePanel(searchPanel);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Search:");
        UITheme.styleNormalLabel(searchLabel);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(showAllButton);


        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "Supplier ID",
                "Supplier Name",
                "Contact Person",
                "Phone",
                "Email",
                "Address",
                "Status"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        supplierTable = new JTable(tableModel);
        UITheme.styleTable(supplierTable);


        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        // =================================================
        // LAYOUT
        // =================================================

        JPanel leftPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(leftPanel);
        leftPanel.setPreferredSize(new Dimension(550, 0));

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);


        add(searchPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);


        // =================================================
        // EVENTS
        // =================================================

        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deactivateButton.addActionListener(e -> deactivateSupplier());
        clearButton.addActionListener(e -> clearFields());

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
                searchSuppliers();
            }
        });

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadSuppliers();
        });

        manageProductsButton.addActionListener(e -> openSupplierProductManagement());

        supplierTable.getSelectionModel().addListSelectionListener(e -> {
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
    // UPDATE SUPPLIER SEARCH SUGGESTIONS
    // =====================================================

    private void updateSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            loadSuppliers();
            return;
        }

        List<Supplier> suppliers = supplierDAO.searchSuppliers(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<String>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Supplier supplier : suppliers) {
            String supplierName = supplier.getSupplierName();

            if (supplierName == null || supplierName.trim().isEmpty()) {
                continue;
            }

            if (!supplierName.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(supplierName)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(supplierName);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            suggestionPopup.setVisible(false);
            searchSuppliers();
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
        searchSuppliers();
    }


    // =====================================================
    // SELECT SUGGESTION
    // =====================================================

    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();

        if (selected == null || selected.trim().isEmpty()) {
            return;
        }

        searchField.setText(selected);
        suggestionPopup.setVisible(false);
        searchSuppliers();
    }


    // =====================================================
    // OPEN SUPPLIER PRODUCT MANAGEMENT
    // =====================================================

    private void openSupplierProductManagement() {
        SupplierProductManagementFrame frame = new SupplierProductManagementFrame();
        frame.setVisible(true);
    }


    // =====================================================
    // ADD SUPPLIER
    // =====================================================

    private void addSupplier() {
        if (!validateFields()) {
            return;
        }

        String supplierName = supplierNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (supplierDAO.supplierExists(supplierName, phone, 0)) {
            JOptionPane.showMessageDialog(
                    this,
                    "This supplier already exists.",
                    "Duplicate Supplier",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Supplier supplier = getSupplierFromForm();
        boolean success = supplierDAO.addSupplier(supplier);

        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully!");
            clearFields();
            loadSuppliers();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not add supplier.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // UPDATE SUPPLIER
    // =====================================================

    private void updateSupplier() {
        if (supplierIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a supplier first.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        int supplierId = Integer.parseInt(supplierIdField.getText());
        String supplierName = supplierNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (supplierDAO.supplierExists(supplierName, phone, supplierId)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Another supplier with these details already exists.",
                    "Duplicate Supplier",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Supplier supplier = getSupplierFromForm();
        supplier.setSupplierId(supplierId);

        boolean success = supplierDAO.updateSupplier(supplier);

        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
            clearFields();
            loadSuppliers();
        } else {
            JOptionPane.showMessageDialog(this, "Supplier update failed.");
        }
    }


    // =====================================================
    // DEACTIVATE SUPPLIER
    // =====================================================

    private void deactivateSupplier() {
        if (supplierIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a supplier first.");
            return;
        }

        String currentStatus = statusCombo.getSelectedItem().toString();

        if ("INACTIVE".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This supplier is already inactive.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to deactivate this supplier?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        int supplierId = Integer.parseInt(supplierIdField.getText());
        boolean success = supplierDAO.deactivateSupplier(supplierId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier deactivated successfully!");
            clearFields();
            loadSuppliers();
        } else {
            JOptionPane.showMessageDialog(this, "Could not deactivate supplier.");
        }
    }


    // =====================================================
    // FORM -> SUPPLIER
    // =====================================================

    private Supplier getSupplierFromForm() {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierNameField.getText().trim());
        supplier.setContactPerson(contactPersonField.getText().trim());
        supplier.setPhone(phoneField.getText().trim());
        supplier.setEmail(emailField.getText().trim());
        supplier.setAddress(addressField.getText().trim());
        supplier.setStatus(statusCombo.getSelectedItem().toString());

        return supplier;
    }


    // =====================================================
    // LOAD SUPPLIERS
    // =====================================================

    private void loadSuppliers() {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        displaySuppliers(suppliers);
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void searchSuppliers() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadSuppliers();
            return;
        }

        List<Supplier> suppliers = supplierDAO.searchSuppliers(keyword);
        displaySuppliers(suppliers);
    }


    // =====================================================
    // DISPLAY TABLE
    // =====================================================

    private void displaySuppliers(List<Supplier> suppliers) {
        tableModel.setRowCount(0);

        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[] {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getEmail(),
                    supplier.getAddress(),
                    supplier.getStatus()
            });
        }
    }


    // =====================================================
    // TABLE -> FORM
    // =====================================================

    private void fillFormFromTable() {
        int row = supplierTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        supplierIdField.setText(getTableValue(row, 0));
        supplierNameField.setText(getTableValue(row, 1));
        contactPersonField.setText(getTableValue(row, 2));
        phoneField.setText(getTableValue(row, 3));
        emailField.setText(getTableValue(row, 4));
        addressField.setText(getTableValue(row, 5));
        statusCombo.setSelectedItem(getTableValue(row, 6));
    }


    // =====================================================
    // TABLE VALUE
    // =====================================================

    private String getTableValue(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }


    // =====================================================
    // VALIDATION
    // =====================================================

    private boolean validateFields() {
        if (supplierNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Supplier name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Supplier phone number is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid phone number format.\nExample: 0771234567 or +94771234567",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email address format.\nExample: name@domain.com",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {
        supplierIdField.setText("");
        supplierNameField.setText("");
        contactPersonField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");

        statusCombo.setSelectedItem("ACTIVE");

        supplierTable.clearSelection();
        searchField.setText("");
        suggestionPopup.setVisible(false);
    }


    // =====================================================
    // STANDALONE TEST
    // =====================================================

    public static void main(String[] args) {
        new SupplierManagementFrame().setVisible(true);
    }
}