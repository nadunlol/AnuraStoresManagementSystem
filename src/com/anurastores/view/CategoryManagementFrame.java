package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.CategoryDAO;
import com.anurastores.model.Category;
import com.anurastores.util.UITheme;

public class CategoryManagementFrame extends JFrame {

    private CategoryDAO categoryDAO;

    private JTextField categoryIdField;
    private JTextField categoryNameField;
    private JTextField descriptionField;
    private JComboBox<String> statusCombo;

    private JTextField searchField;

    private JTable categoryTable;
    private DefaultTableModel tableModel;


    public CategoryManagementFrame() {

        categoryDAO = new CategoryDAO();

        setTitle("Category Management - Anura Stores");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadCategories();
    }


    // =====================================================
    // UI
    // =====================================================

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));


        // =================================================
        // FORM
        // =================================================

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        UITheme.stylePanel(formPanel);
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Category Details"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );


        categoryIdField = new JTextField();
        categoryIdField.setEditable(false);
        UITheme.styleTextField(categoryIdField);

        categoryNameField = new JTextField();
        UITheme.styleTextField(categoryNameField);

        descriptionField = new JTextField();
        UITheme.styleTextField(descriptionField);

        statusCombo = new JComboBox<String>(new String[] { "ACTIVE", "INACTIVE" });
        UITheme.styleComboBox(statusCombo);


        JLabel idLabel = new JLabel("Category ID:");
        UITheme.styleNormalLabel(idLabel);
        formPanel.add(idLabel);
        formPanel.add(categoryIdField);


        JLabel nameLabel = new JLabel("Category Name:");
        UITheme.styleNormalLabel(nameLabel);
        formPanel.add(nameLabel);
        formPanel.add(categoryNameField);


        JLabel descLabel = new JLabel("Description:");
        UITheme.styleNormalLabel(descLabel);
        formPanel.add(descLabel);
        formPanel.add(descriptionField);


        JLabel statusLabel = new JLabel("Status:");
        UITheme.styleNormalLabel(statusLabel);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);


        // =================================================
        // BUTTONS
        // =================================================

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(buttonPanel);


        JButton addButton = new JButton("Add Category");
        UITheme.stylePrimaryButton(addButton);


        JButton updateButton = new JButton("Update Category");
        UITheme.stylePrimaryButton(updateButton);


        JButton deactivateButton = new JButton("Deactivate");
        UITheme.styleDangerButton(deactivateButton);


        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);


        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(clearButton);


        JPanel northPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(northPanel);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);


        // =================================================
        // SEARCH
        // =================================================

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        UITheme.stylePanel(searchPanel);


        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);


        JButton searchButton = new JButton("Search");
        UITheme.styleSecondaryButton(searchButton);


        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


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
                "Category ID",
                "Category Name",
                "Description",
                "Status"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        categoryTable = new JTable(tableModel);
        UITheme.styleTable(categoryTable);


        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        JPanel centerPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);


        // =================================================
        // ADD TO FRAME
        // =================================================

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);


        // =================================================
        // EVENTS
        // =================================================

        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateCategory());
        deactivateButton.addActionListener(e -> deactivateCategory());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchCategories());

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadCategories();
        });


        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }


    // =====================================================
    // LOAD ALL
    // =====================================================

    private void loadCategories() {
        displayCategories(categoryDAO.getAllCategories());
    }


    // =====================================================
    // DISPLAY
    // =====================================================

    private void displayCategories(List<Category> categories) {
        tableModel.setRowCount(0);

        for (Category category : categories) {
            tableModel.addRow(new Object[] {
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getDescription(),
                    category.getStatus()
            });
        }
    }


    // =====================================================
    // ADD
    // =====================================================

    private void addCategory() {
        String categoryName = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required.");
            return;
        }

        boolean success = categoryDAO.addCategory(categoryName, description);

        if (success) {
            JOptionPane.showMessageDialog(this, "Category added successfully!");
            clearForm();
            loadCategories();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not add category.\nCategory name may already exist.",
                    "Add Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // UPDATE
    // =====================================================

    private void updateCategory() {
        if (categoryIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category first.");
            return;
        }

        String categoryName = categoryNameField.getText().trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required.");
            return;
        }

        int categoryId = Integer.parseInt(categoryIdField.getText());
        String description = descriptionField.getText().trim();
        String status = statusCombo.getSelectedItem().toString();

        boolean success = categoryDAO.updateCategory(categoryId, categoryName, description, status);

        if (success) {
            JOptionPane.showMessageDialog(this, "Category updated successfully!");
            clearForm();
            loadCategories();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update category.\nCheck duplicate category name.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    private void deactivateCategory() {
        if (categoryIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category first.");
            return;
        }

        String currentStatus = statusCombo.getSelectedItem().toString();

        if ("INACTIVE".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This category is already inactive.");
            return;
        }

        int categoryId = Integer.parseInt(categoryIdField.getText());

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Deactivate this category?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = categoryDAO.deactivateCategory(categoryId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Category deactivated successfully!");
            clearForm();
            loadCategories();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not deactivate category.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void searchCategories() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadCategories();
            return;
        }

        displayCategories(categoryDAO.searchCategories(keyword));
    }


    // =====================================================
    // TABLE -> FORM
    // =====================================================

    private void fillFormFromTable() {
        int row = categoryTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        categoryIdField.setText(tableModel.getValueAt(row, 0).toString());
        categoryNameField.setText(tableModel.getValueAt(row, 1).toString());

        Object description = tableModel.getValueAt(row, 2);
        descriptionField.setText(description == null ? "" : description.toString());

        statusCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearForm() {
        categoryIdField.setText("");
        categoryNameField.setText("");
        descriptionField.setText("");
        statusCombo.setSelectedItem("ACTIVE");

        categoryTable.clearSelection();
        categoryNameField.requestFocus();
    }
}