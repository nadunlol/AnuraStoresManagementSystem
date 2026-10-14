package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.anurastores.dao.ReportDAO;
import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class ReportsFrame extends JFrame {

    private User loggedInUser;

    private ReportDAO reportDAO;

    private JTextField fromDateField;
    private JButton fromDateButton;
    private JTextField toDateField;
    private JButton toDateButton;

    private JLabel salesTotalLabel;
    private JLabel completedSalesLabel;
    private JLabel cancelledSalesLabel;

    private JLabel presentLabel;
    private JLabel absentLabel;
    private JLabel lateLabel;
    private JLabel leaveLabel;

    private JLabel lowStockLabel;
    private JLabel outOfStockLabel;

    private JLabel activeProductsLabel;
    private JLabel activeEmployeesLabel;
    private JLabel activeSuppliersLabel;

    private JPanel cardsPanel;


    public ReportsFrame(User user) {

        this.loggedInUser = user;

        reportDAO = new ReportDAO();

        setTitle("Management Reports - Anura Stores");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        setDefaultDates();

        generateReport();
    }


    // ==================================================
    // UI
    // ==================================================

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));


        // ----------------------------------------------
        // TOP
        // ----------------------------------------------

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        UITheme.stylePanel(topPanel);
        topPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        UITheme.createSectionBorder("Management Reports Filter"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );


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


        JButton generateButton = new JButton("Generate Report");
        UITheme.stylePrimaryButton(generateButton);


        JButton todayButton = new JButton("Today");
        UITheme.styleSecondaryButton(todayButton);


        JButton monthButton = new JButton("This Month");
        UITheme.styleSecondaryButton(monthButton);


        JLabel fromLabel = new JLabel("From:");
        UITheme.styleNormalLabel(fromLabel);
        JLabel toLabel = new JLabel("To:");
        UITheme.styleNormalLabel(toLabel);


        topPanel.add(fromLabel);
        topPanel.add(fromDatePanel);
        topPanel.add(toLabel);
        topPanel.add(toDatePanel);
        topPanel.add(generateButton);
        topPanel.add(todayButton);
        topPanel.add(monthButton);


        // ----------------------------------------------
        // REPORT CARDS
        // ----------------------------------------------

        cardsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        UITheme.stylePanel(cardsPanel);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        salesTotalLabel = createValueLabel();
        completedSalesLabel = createValueLabel();
        cancelledSalesLabel = createValueLabel();
        activeProductsLabel = createValueLabel();

        presentLabel = createValueLabel();
        absentLabel = createValueLabel();
        lateLabel = createValueLabel();
        leaveLabel = createValueLabel();

        lowStockLabel = createValueLabel();
        outOfStockLabel = createValueLabel();
        activeEmployeesLabel = createValueLabel();
        activeSuppliersLabel = createValueLabel();


        cardsPanel.add(createCard("Sales Total", salesTotalLabel));
        cardsPanel.add(createCard("Completed Sales", completedSalesLabel));
        cardsPanel.add(createCard("Cancelled Sales", cancelledSalesLabel));
        cardsPanel.add(createCard("Active Products", activeProductsLabel));

        cardsPanel.add(createCard("Present", presentLabel));
        cardsPanel.add(createCard("Absent", absentLabel));
        cardsPanel.add(createCard("Late", lateLabel));
        cardsPanel.add(createCard("Leave", leaveLabel));

        cardsPanel.add(createCard("Low Stock", lowStockLabel));
        cardsPanel.add(createCard("Out of Stock", outOfStockLabel));
        cardsPanel.add(createCard("Active Employees", activeEmployeesLabel));
        cardsPanel.add(createCard("Active Suppliers", activeSuppliersLabel));


        // ----------------------------------------------
        // BOTTOM
        // ----------------------------------------------

        JPanel bottomPanel = new JPanel(new BorderLayout());
        UITheme.stylePanel(bottomPanel);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));


        String username = loggedInUser == null ? "-" : loggedInUser.getUsername();
        String role = loggedInUser == null ? "-" : loggedInUser.getRole();

        JLabel userLabel = new JLabel("Logged in as: " + username + " | Role: " + role);
        UITheme.styleSubtitleLabel(userLabel);


        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        UITheme.stylePanel(actionButtonsPanel);

        JButton saveButton = new JButton("Save to Device");
        UITheme.styleSecondaryButton(saveButton);

        JButton printButton = new JButton("Print Report");
        UITheme.stylePrimaryButton(printButton);

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);

        actionButtonsPanel.add(saveButton);
        actionButtonsPanel.add(printButton);
        actionButtonsPanel.add(closeButton);


        bottomPanel.add(userLabel, BorderLayout.WEST);
        bottomPanel.add(actionButtonsPanel, BorderLayout.EAST);


        JPanel topContainer = new JPanel(new BorderLayout());
        UITheme.stylePanel(topContainer);
        topContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topContainer.add(topPanel, BorderLayout.CENTER);


        add(topContainer, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);


        // ----------------------------------------------
        // EVENTS
        // ----------------------------------------------

        generateButton.addActionListener(e -> generateReport());
        todayButton.addActionListener(e -> showToday());
        monthButton.addActionListener(e -> showThisMonth());
        saveButton.addActionListener(e -> saveReportToDevice());
        printButton.addActionListener(e -> printReport());
        closeButton.addActionListener(e -> dispose());
    }


 // ==================================================
    // SAVE REPORT TO DEVICE (TXT or PDF)
    // ==================================================

    private void saveReportToDevice() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new File("Management_Report_" + LocalDate.now() + ".txt"));
        
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf");
        
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(pdfFilter);
        fileChooser.setFileFilter(txtFilter);

        // Update default file extension dynamically when user changes file filter drop-down
        fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, e -> {
            javax.swing.filechooser.FileFilter selectedFilter = fileChooser.getFileFilter();
            String dateStr = LocalDate.now().toString();
            if (selectedFilter == pdfFilter) {
                fileChooser.setSelectedFile(new File("Management_Report_" + dateStr + ".pdf"));
            } else if (selectedFilter == txtFilter) {
                fileChooser.setSelectedFile(new File("Management_Report_" + dateStr + ".txt"));
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getName().toLowerCase();

            if (fileName.endsWith(".pdf") || fileChooser.getFileFilter() == pdfFilter) {
                // Trigger native OS Print-to-PDF to generate a valid, openable PDF file
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Management Report PDF Export");
                job.setPrintable(getReportPrintable());

                JOptionPane.showMessageDialog(this, 
                    "To generate your PDF file:\nIn the print dialog that opens, choose 'Microsoft Print to PDF' (or 'Save as PDF') and select your destination file.", 
                    "Save as PDF", 
                    JOptionPane.INFORMATION_MESSAGE);

                boolean doPrint = job.printDialog();
                if (doPrint) {
                    try {
                        job.print();
                        JOptionPane.showMessageDialog(this, "PDF document generated and saved successfully!");
                    } catch (PrinterException ex) {
                        JOptionPane.showMessageDialog(this, "PDF generation failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                if (!fileName.endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                }
                saveAsTextFile(fileToSave);
            }
        }
    }

    private void saveAsTextFile(File fileToSave) {
        try (FileWriter writer = new FileWriter(fileToSave)) {
            writer.write(getReportContentString());
            JOptionPane.showMessageDialog(this, "Report saved successfully to:\n" + fileToSave.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ==================================================
    // PRINT REPORT
    // ==================================================

    private void printReport() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Management Report - Anura Stores");
        job.setPrintable(getReportPrintable());

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Report sent to printer successfully!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Printable getReportPrintable() {
        return new Printable() {
            @Override
            public int print(java.awt.Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.translate(pf.getImageableX() + 50, pf.getImageableY() + 50);

                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2d.drawString("ANURA STORES - MANAGEMENT REPORT", 0, 20);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.drawString("Period: " + fromDateField.getText() + " to " + toDateField.getText(), 0, 45);
                g2d.drawString("Generated On: " + LocalDate.now(), 0, 65);

                int y = 100;
                String[] lines = getReportContentString().split("\n");

                for (String line : lines) {
                    if (line.startsWith("[") && line.endsWith("]")) {
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        y += 10;
                    } else {
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                    g2d.drawString(line, 0, y);
                    y += 20;
                }

                return PAGE_EXISTS;
            }
        };
    }

    private String getReportContentString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       ANURA STORES - MANAGEMENT REPORT\n");
        sb.append("========================================\n");
        sb.append("Period: ").append(fromDateField.getText()).append(" to ").append(toDateField.getText()).append("\n");
        sb.append("Generated On: ").append(LocalDate.now()).append("\n");
        sb.append("----------------------------------------\n\n");

        sb.append("[SALES PERFORMANCE]\n");
        sb.append("- Sales Total: ").append(salesTotalLabel.getText()).append("\n");
        sb.append("- Completed Sales: ").append(completedSalesLabel.getText()).append("\n");
        sb.append("- Cancelled Sales: ").append(cancelledSalesLabel.getText()).append("\n\n");

        sb.append("[ATTENDANCE SUMMARY]\n");
        sb.append("- Present: ").append(presentLabel.getText()).append("\n");
        sb.append("- Absent: ").append(absentLabel.getText()).append("\n");
        sb.append("- Late: ").append(lateLabel.getText()).append("\n");
        sb.append("- Leave: ").append(leaveLabel.getText()).append("\n\n");

        sb.append("[INVENTORY STATUS]\n");
        sb.append("- Low Stock Items: ").append(lowStockLabel.getText()).append("\n");
        sb.append("- Out of Stock Items: ").append(outOfStockLabel.getText()).append("\n\n");

        sb.append("[MASTER DATA METRICS]\n");
        sb.append("- Active Products: ").append(activeProductsLabel.getText()).append("\n");
        sb.append("- Active Employees: ").append(activeEmployeesLabel.getText()).append("\n");
        sb.append("- Active Suppliers: ").append(activeSuppliersLabel.getText()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
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
    // CREATE CARD
    // ==================================================

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        UITheme.styleCardPanel(panel);
        panel.setBorder(UITheme.createSectionBorder(title));
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }


    private JLabel createValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(UITheme.PRIMARY);
        return label;
    }


    // ==================================================
    // DEFAULT DATE = THIS MONTH
    // ==================================================

    private void setDefaultDates() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);

        fromDateField.setText(firstDay.toString());
        toDateField.setText(today.toString());
    }


    // ==================================================
    // TODAY
    // ==================================================

    private void showToday() {
        LocalDate today = LocalDate.now();

        fromDateField.setText(today.toString());
        toDateField.setText(today.toString());

        generateReport();
    }


    // ==================================================
    // THIS MONTH
    // ==================================================

    private void showThisMonth() {
        LocalDate today = LocalDate.now();

        fromDateField.setText(today.withDayOfMonth(1).toString());
        toDateField.setText(today.toString());

        generateReport();
    }


    // ==================================================
    // GENERATE REPORT
    // ==================================================

    private void generateReport() {
        try {
            String fromText = fromDateField.getText().trim();
            String toText = toDateField.getText().trim();

            if (fromText.isEmpty() || toText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both dates.");
                return;
            }

            Date fromDate = Date.valueOf(fromText);
            Date toDate = Date.valueOf(toText);

            if (toDate.before(fromDate)) {
                JOptionPane.showMessageDialog(this, "To date cannot be before From date.");
                return;
            }


            // ------------------------------------------
            // SALES
            // ------------------------------------------

            BigDecimal salesTotal = reportDAO.getSalesTotal(fromDate, toDate);
            int completedSales = reportDAO.getCompletedSalesCount(fromDate, toDate);
            int cancelledSales = reportDAO.getCancelledSalesCount(fromDate, toDate);

            salesTotalLabel.setText("Rs. " + salesTotal.toPlainString());
            completedSalesLabel.setText(String.valueOf(completedSales));
            cancelledSalesLabel.setText(String.valueOf(cancelledSales));


            // ------------------------------------------
            // ATTENDANCE
            // ------------------------------------------

            presentLabel.setText(String.valueOf(
                    reportDAO.getAttendanceCount("PRESENT", fromDate, toDate)));

            absentLabel.setText(String.valueOf(
                    reportDAO.getAttendanceCount("ABSENT", fromDate, toDate)));

            lateLabel.setText(String.valueOf(
                    reportDAO.getAttendanceCount("LATE", fromDate, toDate)));

            leaveLabel.setText(String.valueOf(
                    reportDAO.getAttendanceCount("LEAVE", fromDate, toDate)));


            // ------------------------------------------
            // INVENTORY
            // ------------------------------------------

            lowStockLabel.setText(String.valueOf(reportDAO.getLowStockCount()));
            outOfStockLabel.setText(String.valueOf(reportDAO.getOutOfStockCount()));


            // ------------------------------------------
            // MASTER DATA
            // ------------------------------------------

            activeProductsLabel.setText(String.valueOf(reportDAO.getActiveProductCount()));
            activeEmployeesLabel.setText(String.valueOf(reportDAO.getActiveEmployeeCount()));
            activeSuppliersLabel.setText(String.valueOf(reportDAO.getActiveSupplierCount()));

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Date format must be YYYY-MM-DD.",
                    "Invalid Date",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Report could not be generated.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}