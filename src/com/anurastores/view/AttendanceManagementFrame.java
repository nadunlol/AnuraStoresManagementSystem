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
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.anurastores.dao.AttendanceDAO;
import com.anurastores.dao.EmployeeDAO;
import com.anurastores.model.Attendance;
import com.anurastores.model.Employee;
import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class AttendanceManagementFrame extends JFrame {

    private JTextField attendanceIdField;
    private JComboBox<EmployeeItem> employeeCombo;
    private JTextField dateField;
    private JButton calendarButton;
    private JTextField checkInField;
    private JButton checkInTimeButton;
    private JTextField checkOutField;
    private JButton checkOutTimeButton;
    private JComboBox<String> statusCombo;
    private JTextField remarksField;

    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;

    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private AttendanceDAO attendanceDAO;
    private EmployeeDAO employeeDAO;

    private User loggedInUser;
    private JButton deleteButton;

    public AttendanceManagementFrame(User user) {

        this.loggedInUser = user;

        attendanceDAO = new AttendanceDAO();
        employeeDAO = new EmployeeDAO();

        setTitle("Attendance Management - Anura Stores");
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadEmployees();
        loadAttendance();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        // =================================================
        // FORM PANEL
        // =================================================

        JPanel formPanel = new JPanel(new GridBagLayout());
        UITheme.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        attendanceIdField = new JTextField(15);
        attendanceIdField.setEditable(false);
        UITheme.styleTextField(attendanceIdField);

        employeeCombo = new JComboBox<EmployeeItem>();
        UITheme.styleComboBox(employeeCombo);

        dateField = new JTextField(10);
        dateField.setEditable(false);
        UITheme.styleTextField(dateField);

        calendarButton = new JButton("Pick");
        UITheme.stylePrimaryButton(calendarButton);
        calendarButton.addActionListener(e -> openCalendarPopup(dateField));

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(datePanel);
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(calendarButton, BorderLayout.EAST);

        checkInField = new JTextField(10);
        checkInField.setEditable(false);
        UITheme.styleTextField(checkInField);

        checkInTimeButton = new JButton("Time");
        UITheme.stylePrimaryButton(checkInTimeButton);
        checkInTimeButton.addActionListener(e -> openTimePickerPopup(checkInField));

        JPanel checkInPanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(checkInPanel);
        checkInPanel.add(checkInField, BorderLayout.CENTER);
        checkInPanel.add(checkInTimeButton, BorderLayout.EAST);

        checkOutField = new JTextField(10);
        checkOutField.setEditable(false);
        UITheme.styleTextField(checkOutField);

        checkOutTimeButton = new JButton("Time");
        UITheme.stylePrimaryButton(checkOutTimeButton);
        checkOutTimeButton.addActionListener(e -> openTimePickerPopup(checkOutField));

        JPanel checkOutPanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(checkOutPanel);
        checkOutPanel.add(checkOutField, BorderLayout.CENTER);
        checkOutPanel.add(checkOutTimeButton, BorderLayout.EAST);

        statusCombo = new JComboBox<String>(
                new String[] {
                        "PRESENT",
                        "ABSENT",
                        "LATE",
                        "LEAVE"
                });
        UITheme.styleComboBox(statusCombo);

        remarksField = new JTextField(15);
        UITheme.styleTextField(remarksField);

        addRow(formPanel, gbc, 0, "Attendance ID:", attendanceIdField);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel empLabel = new JLabel("Employee:");
        UITheme.styleNormalLabel(empLabel);
        formPanel.add(empLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(employeeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLbl = new JLabel("Date:");
        UITheme.styleNormalLabel(dateLbl);
        formPanel.add(dateLbl, gbc);

        gbc.gridx = 1;
        formPanel.add(datePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel checkInLbl = new JLabel("Check In (HH:MM):");
        UITheme.styleNormalLabel(checkInLbl);
        formPanel.add(checkInLbl, gbc);

        gbc.gridx = 1;
        formPanel.add(checkInPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel checkOutLbl = new JLabel("Check Out (HH:MM):");
        UITheme.styleNormalLabel(checkOutLbl);
        formPanel.add(checkOutLbl, gbc);

        gbc.gridx = 1;
        formPanel.add(checkOutPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel statusLabel = new JLabel("Status:");
        UITheme.styleNormalLabel(statusLabel);
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        addRow(formPanel, gbc, 6, "Remarks:", remarksField);


        // =================================================
        // BUTTONS PANEL
        // =================================================

        JButton addButton = new JButton("Add");
        UITheme.stylePrimaryButton(addButton);

        JButton updateButton = new JButton("Update");
        UITheme.stylePrimaryButton(updateButton);

        deleteButton = new JButton("Delete");
        UITheme.styleDangerButton(deleteButton);

        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        UITheme.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Only ADMIN can delete attendance
        if (loggedInUser == null
                || !"ADMIN".equalsIgnoreCase(
                        loggedInUser.getRole())) {
            deleteButton.setEnabled(false);
        }


        // =================================================
        // SEARCH / FILTER PANEL (Centered)
        // =================================================

        searchField = new JTextField(15);
        UITheme.styleTextField(searchField);

        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);

        JButton dateRangeButton = new JButton("Filter Date Range");
        UITheme.styleSecondaryButton(dateRangeButton);

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
        searchPanel.add(dateRangeButton);
        searchPanel.add(showAllButton);


        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "Attendance ID",
                "Employee ID",
                "Employee Name",
                "Date",
                "Check In",
                "Check Out",
                "Status",
                "Remarks"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        UITheme.styleTable(attendanceTable);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
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

        addButton.addActionListener(e -> addAttendance());
        updateButton.addActionListener(e -> updateAttendance());
        deleteButton.addActionListener(e -> deleteAttendance());
        clearButton.addActionListener(e -> clearFields());
        dateRangeButton.addActionListener(e -> filterDateRange());

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
                searchAttendance();
            }
        });

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            loadAttendance();
        });

        attendanceTable.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        fillFormFromTable();
                    }
                });
    }


    private void addRow(
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
    // UPDATE ATTENDANCE SEARCH SUGGESTIONS
    // =====================================================

    private void updateSuggestions() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            suggestionPopup.setVisible(false);
            loadAttendance();
            return;
        }

        List<Attendance> attendances = attendanceDAO.searchAttendance(keyword);
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<String>();
        int count = 0;
        String lowerKeyword = keyword.toLowerCase();

        for (Attendance att : attendances) {
            String empName = att.getEmployeeName();

            if (empName == null || empName.trim().isEmpty()) {
                continue;
            }

            if (!empName.toLowerCase().startsWith(lowerKeyword)) {
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
            suggestionPopup.setVisible(false);
            searchAttendance();
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
        searchAttendance();
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
        searchAttendance();
    }


    // =====================================================
    // TIME PICKER POPUP DIALOG (Larger Controls)
    // =====================================================

    private void openTimePickerPopup(JTextField targetField) {
        JDialog timeDialog = new JDialog(this, "Select Time", true);
        timeDialog.setSize(360, 220);
        timeDialog.setLocationRelativeTo(this);
        timeDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UITheme.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        LocalTime initialTime = LocalTime.now();
        if (!targetField.getText().trim().isEmpty()) {
            try {
                String val = targetField.getText().trim();
                if (val.length() == 5) {
                    val += ":00";
                }
                initialTime = LocalTime.parse(val);
            } catch (Exception ignored) {}
        }

        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        UITheme.stylePanel(spinnerPanel);

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(initialTime.getHour(), 0, 23, 1));
        JSpinner.NumberEditor hourEditor = new JSpinner.NumberEditor(hourSpinner, "00");
        hourSpinner.setEditor(hourEditor);
        hourSpinner.setPreferredSize(new Dimension(75, 45));
        hourSpinner.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(initialTime.getMinute(), 0, 59, 1));
        JSpinner.NumberEditor minuteEditor = new JSpinner.NumberEditor(minuteSpinner, "00");
        minuteSpinner.setEditor(minuteEditor);
        minuteSpinner.setPreferredSize(new Dimension(75, 45));
        minuteSpinner.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel colonLabel = new JLabel(":");
        UITheme.styleNormalLabel(colonLabel);
        colonLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        spinnerPanel.add(hourSpinner);
        spinnerPanel.add(colonLabel);
        spinnerPanel.add(minuteSpinner);

        JButton okButton = new JButton("OK");
        UITheme.stylePrimaryButton(okButton);
        okButton.setPreferredSize(new Dimension(100, 38));
        okButton.addActionListener(e -> {
            int hour = (Integer) hourSpinner.getValue();
            int minute = (Integer) minuteSpinner.getValue();
            LocalTime chosenTime = LocalTime.of(hour, minute);
            targetField.setText(chosenTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            timeDialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        UITheme.stylePanel(buttonPanel);
        buttonPanel.add(okButton);

        mainPanel.add(spinnerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        timeDialog.add(mainPanel);
        timeDialog.setVisible(true);
    }


    // =====================================================
    // CALENDAR POPUP DIALOG
    // =====================================================

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


    // ===================================================
    // LOAD ACTIVE EMPLOYEES
    // ===================================================

    private void loadEmployees() {
        employeeCombo.removeAllItems();

        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee employee : employees) {
            if ("ACTIVE".equalsIgnoreCase(employee.getStatus())) {
                employeeCombo.addItem(
                        new EmployeeItem(
                                employee.getEmployeeId(),
                                employee.getFirstName()
                                        + " "
                                        + employee.getLastName()
                        )
                );
            }
        }
    }


    // ===================================================
    // ADD ATTENDANCE
    // ===================================================

    private void addAttendance() {
        try {
            Attendance attendance = getAttendanceFromForm();

            if (attendance == null) {
                return;
            }

            boolean duplicate =
                    attendanceDAO.attendanceExists(
                            attendance.getEmployeeId(),
                            attendance.getAttendanceDate(),
                            0);

            if (duplicate) {
                JOptionPane.showMessageDialog(
                        this,
                        "Attendance already exists for this employee on this date.",
                        "Duplicate Attendance",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = attendanceDAO.addAttendance(attendance);

            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance added successfully!");
                clearFields();
                loadAttendance();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not add attendance.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please check the date and time values.",
                    "Invalid Data",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    // ===================================================
    // UPDATE ATTENDANCE
    // ===================================================

    private void updateAttendance() {
        if (attendanceIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attendance record first.");
            return;
        }

        try {
            Attendance attendance = getAttendanceFromForm();

            if (attendance == null) {
                return;
            }

            int attendanceId = Integer.parseInt(attendanceIdField.getText());
            attendance.setAttendanceId(attendanceId);

            boolean duplicate =
                    attendanceDAO.attendanceExists(
                            attendance.getEmployeeId(),
                            attendance.getAttendanceDate(),
                            attendanceId);

            if (duplicate) {
                JOptionPane.showMessageDialog(
                        this,
                        "Another attendance record already exists for this employee on this date.",
                        "Duplicate Attendance",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = attendanceDAO.updateAttendance(attendance);

            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance updated successfully!");
                clearFields();
                loadAttendance();
            } else {
                JOptionPane.showMessageDialog(this, "Attendance update failed.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please check the date and time values.",
                    "Invalid Data",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    // ===================================================
    // DELETE ATTENDANCE
    // ===================================================

    private void deleteAttendance() {
        if (attendanceIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attendance record first.");
            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to delete this attendance record?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            int attendanceId = Integer.parseInt(attendanceIdField.getText());

            if (attendanceDAO.deleteAttendance(attendanceId)) {
                JOptionPane.showMessageDialog(this, "Attendance deleted successfully!");
                clearFields();
                loadAttendance();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete attendance.");
            }
        }
    }


    // ===================================================
    // GET FORM DATA
    // ===================================================

    private Attendance getAttendanceFromForm() {
        EmployeeItem employee = (EmployeeItem) employeeCombo.getSelectedItem();

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.");
            return null;
        }

        if (dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Attendance date is required.");
            return null;
        }

        Date attendanceDate = Date.valueOf(dateField.getText().trim());
        Time checkIn = parseTime(checkInField.getText().trim());
        Time checkOut = parseTime(checkOutField.getText().trim());

        if (checkIn != null
                && checkOut != null
                && checkOut.before(checkIn)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-out time cannot be before check-in time.",
                    "Invalid Time",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employee.getEmployeeId());
        attendance.setAttendanceDate(attendanceDate);
        attendance.setCheckIn(checkIn);
        attendance.setCheckOut(checkOut);
        attendance.setAttendanceStatus(statusCombo.getSelectedItem().toString());
        attendance.setRemarks(remarksField.getText().trim());

        return attendance;
    }


    private Time parseTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        value = value.trim();

        if (value.length() == 5) {
            value = value + ":00";
        }

        return Time.valueOf(value);
    }


    // ===================================================
    // LOAD ALL
    // ===================================================

    private void loadAttendance() {
        displayAttendance(attendanceDAO.getAllAttendance());
    }


    // ===================================================
    // SEARCH
    // ===================================================

    private void searchAttendance() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadAttendance();
            return;
        }

        displayAttendance(attendanceDAO.searchAttendance(keyword));
    }


    // ===================================================
    // DATE RANGE POPUP FILTER
    // ===================================================

    private void filterDateRange() {
        JTextField fromField = new JTextField(10);
        fromField.setEditable(false);
        UITheme.styleTextField(fromField);
        JButton fromBtn = new JButton("Pick");
        UITheme.stylePrimaryButton(fromBtn);
        fromBtn.addActionListener(e -> openCalendarPopup(fromField));

        JPanel fromPanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(fromPanel);
        fromPanel.add(fromField, BorderLayout.CENTER);
        fromPanel.add(fromBtn, BorderLayout.EAST);

        JTextField toField = new JTextField(10);
        toField.setEditable(false);
        UITheme.styleTextField(toField);
        JButton toBtn = new JButton("Pick");
        UITheme.stylePrimaryButton(toBtn);
        toBtn.addActionListener(e -> openCalendarPopup(toField));

        JPanel toPanel = new JPanel(new BorderLayout(5, 0));
        UITheme.stylePanel(toPanel);
        toPanel.add(toField, BorderLayout.CENTER);
        toPanel.add(toBtn, BorderLayout.EAST);

        Object[] message = {
                "From Date:", fromPanel,
                "To Date:", toPanel
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Filter Date Range",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String fromText = fromField.getText().trim();
                String toText = toField.getText().trim();

                if (fromText.isEmpty() || toText.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please select both From and To dates.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Date fromDate = Date.valueOf(fromText);
                Date toDate = Date.valueOf(toText);

                if (toDate.before(fromDate)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "To date cannot be before From date.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                displayAttendance(attendanceDAO.searchByDateRange(fromDate, toDate));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date selection.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ===================================================
    // DISPLAY
    // ===================================================

    private void displayAttendance(List<Attendance> attendanceList) {
        tableModel.setRowCount(0);

        for (Attendance attendance : attendanceList) {
            tableModel.addRow(new Object[] {
                    attendance.getAttendanceId(),
                    attendance.getEmployeeId(),
                    attendance.getEmployeeName(),
                    attendance.getAttendanceDate(),
                    attendance.getCheckIn(),
                    attendance.getCheckOut(),
                    attendance.getAttendanceStatus(),
                    attendance.getRemarks()
            });
        }
    }


    // ===================================================
    // TABLE ROW -> FORM
    // ===================================================

    private void fillFormFromTable() {
        int row = attendanceTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        attendanceIdField.setText(getTableValue(row, 0));

        int employeeId = Integer.parseInt(getTableValue(row, 1));
        selectEmployee(employeeId);

        dateField.setText(getTableValue(row, 3));
        checkInField.setText(getTableValue(row, 4));
        checkOutField.setText(getTableValue(row, 5));
        statusCombo.setSelectedItem(getTableValue(row, 6));
        remarksField.setText(getTableValue(row, 7));
    }


    private void selectEmployee(int employeeId) {
        for (int i = 0; i < employeeCombo.getItemCount(); i++) {
            EmployeeItem item = employeeCombo.getItemAt(i);

            if (item.getEmployeeId() == employeeId) {
                employeeCombo.setSelectedIndex(i);
                return;
            }
        }
    }


    private String getTableValue(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }


    // ===================================================
    // CLEAR
    // ===================================================

    private void clearFields() {
        attendanceIdField.setText("");

        if (employeeCombo.getItemCount() > 0) {
            employeeCombo.setSelectedIndex(0);
        }

        dateField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
        remarksField.setText("");

        statusCombo.setSelectedItem("PRESENT");

        attendanceTable.clearSelection();
        searchField.setText("");
        suggestionPopup.setVisible(false);
    }


    // ===================================================
    // EMPLOYEE COMBO ITEM
    // ===================================================

    private static class EmployeeItem {

        private int employeeId;
        private String employeeName;

        public EmployeeItem(int employeeId, String employeeName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        @Override
        public String toString() {
            return employeeId + " - " + employeeName;
        }
    }
}