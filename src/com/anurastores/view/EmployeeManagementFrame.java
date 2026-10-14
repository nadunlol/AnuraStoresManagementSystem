package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Pattern;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.EmployeeDAO;
import com.anurastores.model.Employee;
import com.anurastores.util.UITheme;

public class EmployeeManagementFrame extends JFrame {

    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField nicField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField positionField;
    private JTextField hireDateField;
    private JButton calendarButton;
    
    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JComboBox<String> statusCombo;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private EmployeeDAO employeeDAO;

    // Standard Regex Patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s-]{7,15}$"
    );

    public EmployeeManagementFrame() {

        employeeDAO = new EmployeeDAO();

        setTitle("Employee Management - Anura Stores");
        setSize(1150, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadEmployees();
    }

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

        idField = new JTextField(15);
        idField.setEditable(false);
        UITheme.styleTextField(idField);

        firstNameField = new JTextField(15);
        UITheme.styleTextField(firstNameField);

        lastNameField = new JTextField(15);
        UITheme.styleTextField(lastNameField);

        nicField = new JTextField(15);
        UITheme.styleTextField(nicField);

        phoneField = new JTextField(15);
        UITheme.styleTextField(phoneField);

        emailField = new JTextField(15);
        UITheme.styleTextField(emailField);

        addressField = new JTextField(15);
        UITheme.styleTextField(addressField);

        positionField = new JTextField(15);
        UITheme.styleTextField(positionField);

        hireDateField = new JTextField(10);
        hireDateField.setEditable(false);
        UITheme.styleTextField(hireDateField);

        calendarButton = new JButton("Pick");
        UITheme.stylePrimaryButton(calendarButton);
        calendarButton.addActionListener(e -> openCalendarPopup());

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(datePanel);
        datePanel.add(hireDateField, BorderLayout.CENTER);
        datePanel.add(calendarButton, BorderLayout.EAST);

        statusCombo = new JComboBox<String>(new String[] {"ACTIVE", "INACTIVE"});
        UITheme.styleComboBox(statusCombo);

        addFormRow(formPanel, gbc, 0, "Employee ID:", idField);
        addFormRow(formPanel, gbc, 1, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, 2, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, 3, "NIC:", nicField);
        addFormRow(formPanel, gbc, 4, "Phone:", phoneField);
        addFormRow(formPanel, gbc, 5, "Email:", emailField);
        addFormRow(formPanel, gbc, 6, "Address:", addressField);
        addFormRow(formPanel, gbc, 7, "Position:", positionField);
        
        // Add date panel row
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel dateLbl = new JLabel("Hire Date:");
        UITheme.styleNormalLabel(dateLbl);
        formPanel.add(dateLbl, gbc);

        gbc.gridx = 1;
        formPanel.add(datePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        UITheme.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(clearButton);


        // =================================================
        // SEARCH (Centered)
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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
                "ID",
                "First Name",
                "Last Name",
                "NIC",
                "Phone",
                "Email",
                "Address",
                "Position",
                "Hire Date",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        UITheme.styleTable(employeeTable);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
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

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deactivateButton.addActionListener(e -> deactivateEmployee());
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
                searchEmployees();
            }
        });

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadEmployees();
        });

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }


    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field) {

        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel lbl = new JLabel(label);
        UITheme.styleNormalLabel(lbl);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }


    // =====================================================
    // UPDATE EMPLOYEE SEARCH SUGGESTIONS
    // =====================================================

    private void updateSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            loadEmployees();
            return;
        }

        List<Employee> employees = employeeDAO.searchEmployees(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<String>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Employee emp : employees) {
            String firstName = emp.getFirstName();

            if (firstName == null || firstName.trim().isEmpty()) {
                continue;
            }

            if (!firstName.toLowerCase().startsWith(lowerKeyword)) {
                continue;
            }

            boolean duplicate = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.getElementAt(i).equalsIgnoreCase(firstName)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                continue;
            }

            listModel.addElement(firstName);
            count++;

            if (count >= 5) {
                break;
            }
        }

        if (count == 0) {
            suggestionPopup.setVisible(false);
            searchEmployees();
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
        searchEmployees();
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
        searchEmployees();
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
        if (!hireDateField.getText().trim().isEmpty()) {
            try {
                initialDate = LocalDate.parse(hireDateField.getText().trim());
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
                    hireDateField.setText(chosen.toString());
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
    // ADD EMPLOYEE
    // =====================================================

    private void addEmployee() {
        if (!validateFields()) {
            return;
        }

        try {
            Employee employee = new Employee();
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setNic(nicField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setAddress(addressField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            employee.setHireDate(Date.valueOf(hireDateField.getText().trim()));
            employee.setStatus(statusCombo.getSelectedItem().toString());

            boolean success = employeeDAO.addEmployee(employee);

            if (success) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                clearFields();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not add employee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error adding employee: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // UPDATE EMPLOYEE
    // =====================================================

    private void updateEmployee() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            Employee employee = new Employee();
            employee.setEmployeeId(Integer.parseInt(idField.getText()));
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setNic(nicField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setAddress(addressField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            employee.setHireDate(Date.valueOf(hireDateField.getText().trim()));
            employee.setStatus(statusCombo.getSelectedItem().toString());

            boolean success = employeeDAO.updateEmployee(employee);

            if (success) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                clearFields();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Employee update failed.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error updating employee: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // DEACTIVATE
    // =====================================================

    private void deactivateEmployee() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to deactivate this employee?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            int employeeId = Integer.parseInt(idField.getText());

            if (employeeDAO.deactivateEmployee(employeeId)) {
                JOptionPane.showMessageDialog(this, "Employee deactivated successfully!");
                clearFields();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Could not deactivate employee.");
            }
        }
    }


    // =====================================================
    // LOAD ALL
    // =====================================================

    private void loadEmployees() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        displayEmployees(employees);
    }


    // =====================================================
    // SEARCH
    // =====================================================

    private void searchEmployees() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadEmployees();
            return;
        }

        List<Employee> employees = employeeDAO.searchEmployees(keyword);
        displayEmployees(employees);
    }


    // =====================================================
    // DISPLAY TABLE
    // =====================================================

    private void displayEmployees(List<Employee> employees) {
        tableModel.setRowCount(0);

        for (Employee employee : employees) {
            tableModel.addRow(new Object[] {
                    employee.getEmployeeId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getNic(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.getAddress(),
                    employee.getPosition(),
                    employee.getHireDate(),
                    employee.getStatus()
            });
        }
    }


    // =====================================================
    // CLICK TABLE ROW
    // =====================================================

    private void fillFormFromTable() {
        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        firstNameField.setText(getTableValue(selectedRow, 1));
        lastNameField.setText(getTableValue(selectedRow, 2));
        nicField.setText(getTableValue(selectedRow, 3));
        phoneField.setText(getTableValue(selectedRow, 4));
        emailField.setText(getTableValue(selectedRow, 5));
        addressField.setText(getTableValue(selectedRow, 6));
        positionField.setText(getTableValue(selectedRow, 7));
        hireDateField.setText(getTableValue(selectedRow, 8));
        statusCombo.setSelectedItem(getTableValue(selectedRow, 9));
    }


    private String getTableValue(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }


    // =====================================================
    // VALIDATION
    // =====================================================

    private boolean validateFields() {
        if (firstNameField.getText().trim().isEmpty()
                || lastNameField.getText().trim().isEmpty()
                || positionField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "First Name, Last Name, and Position are required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

        if (hireDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hire Date is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
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
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        nicField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        positionField.setText("");
        hireDateField.setText("");

        statusCombo.setSelectedItem("ACTIVE");

        employeeTable.clearSelection();
        searchField.setText("");
        suggestionPopup.setVisible(false);
    }


    // =====================================================
    // STANDALONE TEST
    // =====================================================

    public static void main(String[] args) {
        new EmployeeManagementFrame().setVisible(true);
    }
}