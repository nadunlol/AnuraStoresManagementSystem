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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.EmployeeDAO;
import com.anurastores.dao.UserAccountDAO;
import com.anurastores.model.Employee;
import com.anurastores.model.User;
import com.anurastores.model.UserAccount;
import com.anurastores.util.UITheme;

public class UserAccountManagementFrame extends JFrame {

    private User loggedInUser;

    private UserAccountDAO userAccountDAO;
    private EmployeeDAO employeeDAO;

    private JTextField userIdField;
    
    // Searchable Employee Field and Popup
    private JTextField employeeSearchField;
    private Integer selectedEmployeeId = null;
    private String selectedEmployeeStatus = "ACTIVE";
    private JPopupMenu employeeSuggestionPopup;
    private JList<EmployeeItem> employeeSuggestionList;
    private JScrollPane employeeSuggestionScrollPane;

    private JTextField usernameField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JComboBox<String> roleCombo;
    private JComboBox<String> statusCombo;

    private JTextField searchField;
    private JPopupMenu searchSuggestionPopup;
    private JList<String> searchSuggestionList;
    private JScrollPane searchSuggestionScrollPane;

    private JTable userTable;
    private DefaultTableModel tableModel;


    public UserAccountManagementFrame(User user) {

        this.loggedInUser = user;

        userAccountDAO = new UserAccountDAO();
        employeeDAO = new EmployeeDAO();

        setTitle("User Account Management - Anura Stores");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        loadUsers();
    }


    // =====================================================
    // UI
    // =====================================================

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));


        // -------------------------------------------------
        // FORM
        // -------------------------------------------------

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        UITheme.stylePanel(formPanel);
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("User Account Details"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );


        userIdField = new JTextField();
        UITheme.styleTextField(userIdField);
        userIdField.setEditable(false);


        employeeSearchField = new JTextField();
        UITheme.styleTextField(employeeSearchField);

        employeeSuggestionList = new JList<>();
        employeeSuggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeSuggestionList.setFocusable(false);
        employeeSuggestionList.setVisibleRowCount(5);

        employeeSuggestionPopup = new JPopupMenu();
        employeeSuggestionPopup.setFocusable(false);
        employeeSuggestionPopup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        employeeSuggestionScrollPane = new JScrollPane(employeeSuggestionList);
        employeeSuggestionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        employeeSuggestionPopup.add(employeeSuggestionScrollPane);


        usernameField = new JTextField();
        UITheme.styleTextField(usernameField);


        passwordField = new JPasswordField();
        UITheme.styleTextField(passwordField);


        confirmPasswordField = new JPasswordField();
        UITheme.styleTextField(confirmPasswordField);


        roleCombo = new JComboBox<String>(new String[] { "ADMIN", "EMPLOYEE" });
        UITheme.styleComboBox(roleCombo);


        statusCombo = new JComboBox<String>(new String[] { "ACTIVE", "INACTIVE" });
        UITheme.styleComboBox(statusCombo);


        JLabel idLabel = new JLabel("User ID:");
        UITheme.styleNormalLabel(idLabel);
        JLabel empLabel = new JLabel("Employee:");
        UITheme.styleNormalLabel(empLabel);
        JLabel userLabel = new JLabel("Username:");
        UITheme.styleNormalLabel(userLabel);
        JLabel passLabel = new JLabel("Password:");
        UITheme.styleNormalLabel(passLabel);
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        UITheme.styleNormalLabel(confirmPassLabel);
        JLabel roleLabel = new JLabel("Role:");
        UITheme.styleNormalLabel(roleLabel);
        JLabel statusLabel = new JLabel("Status:");
        UITheme.styleNormalLabel(statusLabel);


        formPanel.add(idLabel);
        formPanel.add(userIdField);

        formPanel.add(empLabel);
        formPanel.add(employeeSearchField);

        formPanel.add(userLabel);
        formPanel.add(usernameField);

        formPanel.add(passLabel);
        formPanel.add(passwordField);

        formPanel.add(confirmPassLabel);
        formPanel.add(confirmPasswordField);

        formPanel.add(roleLabel);
        formPanel.add(roleCombo);

        formPanel.add(statusLabel);
        formPanel.add(statusCombo);


        // -------------------------------------------------
        // ACTION BUTTONS
        // -------------------------------------------------

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UITheme.stylePanel(buttonPanel);


        JButton addButton = new JButton("Add User");
        UITheme.stylePrimaryButton(addButton);

        JButton updateButton = new JButton("Update User");
        UITheme.stylePrimaryButton(updateButton);

        JButton resetPasswordButton = new JButton("Reset Password");
        UITheme.styleWarningButton(resetPasswordButton);

        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);


        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(clearButton);


        JPanel northPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(northPanel);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);


        // -------------------------------------------------
        // SEARCH
        // -------------------------------------------------

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UITheme.stylePanel(searchPanel);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Search Users"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );


        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);

        // User Search Suggestions Popup
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


        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);


        JLabel searchLbl = new JLabel("Search:");
        UITheme.styleNormalLabel(searchLbl);

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(showAllButton);


        // -------------------------------------------------
        // TABLE
        // -------------------------------------------------

        String[] columns = {
                "User ID",
                "Employee ID",
                "Employee Name",
                "Username",
                "Role",
                "Status"
        };


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        userTable = new JTable(tableModel);
        UITheme.styleTable(userTable);


        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));


        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        UITheme.stylePanel(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));


        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);


        // -------------------------------------------------
        // ADD TO FRAME
        // -------------------------------------------------

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);


        // -------------------------------------------------
        // EVENTS
        // -------------------------------------------------

        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
        clearButton.addActionListener(e -> clearForm());

        // Employee Search Suggestions Listener
        employeeSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateEmployeeSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateEmployeeSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateEmployeeSuggestions(); }
        });

        employeeSuggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectEmployeeSuggestion();
                }
            }
        });

        // User Search Suggestions Listener
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
                searchUsers();
            }
        });

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            searchSuggestionPopup.setVisible(false);
            loadUsers();
        });


        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }


    // =====================================================
    // EMPLOYEE SEARCH SUGGESTIONS
    // =====================================================

    private void updateEmployeeSuggestions() {
        String keyword = employeeSearchField.getText().trim();

        if (keyword.isEmpty()) {
            selectedEmployeeId = null;
            employeeSuggestionPopup.setVisible(false);
            return;
        }

        List<Employee> employees = employeeDAO.searchEmployees(keyword);
        javax.swing.DefaultListModel<EmployeeItem> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Employee employee : employees) {
            String name = employee.getFirstName() + " " + employee.getLastName();
            if (name == null || !name.toLowerCase().contains(lowerKeyword)) {
                continue;
            }

            listModel.addElement(new EmployeeItem(employee.getEmployeeId(), name, employee.getStatus()));
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            employeeSuggestionPopup.setVisible(false);
            selectedEmployeeId = null;
            return;
        }

        employeeSuggestionList.setModel(listModel);
        employeeSuggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        employeeSuggestionScrollPane.setPreferredSize(new Dimension(employeeSearchField.getWidth(), height));
        employeeSuggestionPopup.pack();
        employeeSuggestionPopup.show(employeeSearchField, 0, employeeSearchField.getHeight());
        employeeSearchField.requestFocusInWindow();
    }

    private void selectEmployeeSuggestion() {
        EmployeeItem selected = employeeSuggestionList.getSelectedValue();
        if (selected != null) {
            selectedEmployeeId = selected.getEmployeeId();
            selectedEmployeeStatus = selected.getStatus();
            employeeSearchField.setText(selected.toString());
            employeeSuggestionPopup.setVisible(false);
        }
    }


    // =====================================================
    // USER SEARCH SUGGESTIONS
    // =====================================================

    private void updateSearchSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            searchSuggestionPopup.setVisible(false);
            loadUsers();
            return;
        }

        List<UserAccount> accounts = userAccountDAO.searchUserAccounts(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (UserAccount account : accounts) {
            String username = account.getUsername();
            if (username == null || !username.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(username)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(username);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            searchSuggestionPopup.setVisible(false);
            searchUsers();
            return;
        }

        searchSuggestionList.setModel(listModel);
        searchSuggestionList.setSelectedIndex(0);

        int height = Math.min(count, 5) * 25 + 4;
        searchSuggestionScrollPane.setPreferredSize(new Dimension(searchField.getWidth(), height));
        searchSuggestionPopup.pack();
        searchSuggestionPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow();
        searchUsers();
    }

    private void selectSearchSuggestion() {
        String selected = searchSuggestionList.getSelectedValue();
        if (selected != null) {
            searchField.setText(selected);
            searchSuggestionPopup.setVisible(false);
            searchUsers();
        }
    }


    // =====================================================
    // LOAD USERS
    // =====================================================

    private void loadUsers() {
        displayUsers(userAccountDAO.getAllUserAccounts());
    }


    // =====================================================
    // DISPLAY USERS
    // =====================================================

    private void displayUsers(List<UserAccount> accounts) {
        tableModel.setRowCount(0);

        for (UserAccount account : accounts) {
            tableModel.addRow(new Object[] {
                    account.getUserId(),
                    account.getEmployeeId(),
                    account.getEmployeeName(),
                    account.getUsername(),
                    account.getRole(),
                    account.getStatus()
            });
        }
    }


    // =====================================================
    // ADD USER
    // =====================================================

    private void addUser() {
        if (selectedEmployeeId == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid employee using the search field.");
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(selectedEmployeeStatus)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only ACTIVE employees can receive a new user account.");
            return;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = roleCombo.getSelectedItem().toString();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required.");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must contain at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        boolean success = userAccountDAO.addUserAccount(
                selectedEmployeeId,
                username,
                password,
                role
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "User account created successfully!");
            clearForm();
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not create user account.\n"
                    + "Possible reasons:\n"
                    + "- Username already exists\n"
                    + "- Employee already has an account\n"
                    + "- Employee is inactive.",
                    "Create Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    private void updateUser() {
        if (userIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user account first.");
            return;
        }

        if (selectedEmployeeId == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.");
            return;
        }

        int userId = Integer.parseInt(userIdField.getText());
        String username = usernameField.getText().trim();
        String role = roleCombo.getSelectedItem().toString();
        String status = statusCombo.getSelectedItem().toString();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }

        // Prevent currently logged-in admin
        // from disabling or removing own admin access.
        if (loggedInUser != null && userId == loggedInUser.getUserId()) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                JOptionPane.showMessageDialog(
                        this,
                        "You cannot remove ADMIN role from your currently logged-in account.");
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(
                        this,
                        "You cannot deactivate your currently logged-in account.");
                return;
            }
        }

        boolean success = userAccountDAO.updateUserAccount(
                userId,
                selectedEmployeeId,
                username,
                role,
                status
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "User account updated successfully!");
            clearForm();
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update user account.\nCheck duplicate username or employee account.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

    private void resetPassword() {
        if (userIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user account first.");
            return;
        }

        int userId = Integer.parseInt(userIdField.getText());

        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        Object[] message = {
                "New Password:",
                newPasswordField,
                "Confirm Password:",
                confirmField
        };

        int answer = JOptionPane.showConfirmDialog(
                this,
                message,
                "Reset Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmField.getPassword());

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must contain at least 6 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        boolean success = userAccountDAO.resetPassword(userId, newPassword);

        if (success) {
            JOptionPane.showMessageDialog(this, "Password reset successfully!");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Password reset failed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // SEARCH USERS
    // =====================================================

    private void searchUsers() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadUsers();
            return;
        }

        displayUsers(userAccountDAO.searchUserAccounts(keyword));
    }


    // =====================================================
    // TABLE -> FORM
    // =====================================================

    private void fillFormFromTable() {
        int row = userTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        int userId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int employeeId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
        String employeeName = tableModel.getValueAt(row, 2).toString();
        String username = tableModel.getValueAt(row, 3).toString();
        String role = tableModel.getValueAt(row, 4).toString();
        String status = tableModel.getValueAt(row, 5).toString();

        userIdField.setText(String.valueOf(userId));
        usernameField.setText(username);
        roleCombo.setSelectedItem(role);
        statusCombo.setSelectedItem(status);

        passwordField.setText("");
        confirmPasswordField.setText("");

        selectedEmployeeId = employeeId;
        employeeSearchField.setText(employeeId + " - " + employeeName);
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearForm() {
        userIdField.setText("");
        usernameField.setText("");
        employeeSearchField.setText("");
        selectedEmployeeId = null;
        selectedEmployeeStatus = "ACTIVE";

        passwordField.setText("");
        confirmPasswordField.setText("");

        roleCombo.setSelectedItem("EMPLOYEE");
        statusCombo.setSelectedItem("ACTIVE");

        userTable.clearSelection();
        searchField.setText("");
        searchSuggestionPopup.setVisible(false);
        employeeSuggestionPopup.setVisible(false);
    }


    // =====================================================
    // EMPLOYEE ITEM HELPER
    // =====================================================

    private static class EmployeeItem {

        private int employeeId;
        private String employeeName;
        private String status;

        public EmployeeItem(int employeeId, String employeeName, String status) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.status = status;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return employeeId + " - " + employeeName + " [" + status + "]";
        }
    }
}