package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.anurastores.dao.ReportDAO;
import com.anurastores.model.User;

public class ReportsFrame extends JFrame {

    private User loggedInUser;

    private ReportDAO reportDAO;

    private JTextField fromDateField;
    private JTextField toDateField;

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


    public ReportsFrame(User user) {

        this.loggedInUser = user;

        reportDAO = new ReportDAO();

        setTitle(
                "Management Reports - Anura Stores");

        setSize(
                1000,
                600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        setDefaultDates();

        generateReport();
    }


    // ==================================================
    // UI
    // ==================================================

    private void initComponents() {

        setLayout(
                new BorderLayout(
                        10,
                        10));


        // ----------------------------------------------
        // TOP
        // ----------------------------------------------

        JPanel topPanel =
                new JPanel();


        JLabel titleLabel =
                new JLabel(
                        "ANURA STORES - MANAGEMENT REPORTS");


        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22));


        fromDateField =
                new JTextField(10);


        toDateField =
                new JTextField(10);


        JButton generateButton =
                new JButton(
                        "Generate Report");


        JButton todayButton =
                new JButton(
                        "Today");


        JButton monthButton =
                new JButton(
                        "This Month");


        topPanel.add(
                titleLabel);

        topPanel.add(
                new JLabel(
                        "   From:"));

        topPanel.add(
                fromDateField);

        topPanel.add(
                new JLabel(
                        "To:"));

        topPanel.add(
                toDateField);

        topPanel.add(
                generateButton);

        topPanel.add(
                todayButton);

        topPanel.add(
                monthButton);


        // ----------------------------------------------
        // REPORT CARDS
        // ----------------------------------------------

        JPanel cardsPanel =
                new JPanel(
                        new GridLayout(
                                3,
                                4,
                                10,
                                10));


        salesTotalLabel =
                createValueLabel();

        completedSalesLabel =
                createValueLabel();

        cancelledSalesLabel =
                createValueLabel();

        activeProductsLabel =
                createValueLabel();


        presentLabel =
                createValueLabel();

        absentLabel =
                createValueLabel();

        lateLabel =
                createValueLabel();

        leaveLabel =
                createValueLabel();


        lowStockLabel =
                createValueLabel();

        outOfStockLabel =
                createValueLabel();

        activeEmployeesLabel =
                createValueLabel();

        activeSuppliersLabel =
                createValueLabel();


        cardsPanel.add(
                createCard(
                        "Sales Total",
                        salesTotalLabel));

        cardsPanel.add(
                createCard(
                        "Completed Sales",
                        completedSalesLabel));

        cardsPanel.add(
                createCard(
                        "Cancelled Sales",
                        cancelledSalesLabel));

        cardsPanel.add(
                createCard(
                        "Active Products",
                        activeProductsLabel));


        cardsPanel.add(
                createCard(
                        "Present",
                        presentLabel));

        cardsPanel.add(
                createCard(
                        "Absent",
                        absentLabel));

        cardsPanel.add(
                createCard(
                        "Late",
                        lateLabel));

        cardsPanel.add(
                createCard(
                        "Leave",
                        leaveLabel));


        cardsPanel.add(
                createCard(
                        "Low Stock",
                        lowStockLabel));

        cardsPanel.add(
                createCard(
                        "Out of Stock",
                        outOfStockLabel));

        cardsPanel.add(
                createCard(
                        "Active Employees",
                        activeEmployeesLabel));

        cardsPanel.add(
                createCard(
                        "Active Suppliers",
                        activeSuppliersLabel));


        // ----------------------------------------------
        // BOTTOM
        // ----------------------------------------------

        JPanel bottomPanel =
                new JPanel();


        String username =
                loggedInUser == null
                        ? "-"
                        : loggedInUser.getUsername();


        String role =
                loggedInUser == null
                        ? "-"
                        : loggedInUser.getRole();


        JLabel userLabel =
                new JLabel(
                        "Logged in as: "
                        + username
                        + " | Role: "
                        + role);


        JButton closeButton =
                new JButton(
                        "Close");


        bottomPanel.add(
                userLabel);

        bottomPanel.add(
                closeButton);


        add(
                topPanel,
                BorderLayout.NORTH);

        add(
                cardsPanel,
                BorderLayout.CENTER);

        add(
                bottomPanel,
                BorderLayout.SOUTH);


        // ----------------------------------------------
        // EVENTS
        // ----------------------------------------------

        generateButton.addActionListener(
                e -> generateReport());


        todayButton.addActionListener(
                e -> showToday());


        monthButton.addActionListener(
                e -> showThisMonth());


        closeButton.addActionListener(
                e -> dispose());
    }


    // ==================================================
    // CREATE CARD
    // ==================================================

    private JPanel createCard(
            String title,
            JLabel valueLabel) {

        JPanel panel =
                new JPanel(
                        new BorderLayout());


        panel.setBorder(
                BorderFactory.createTitledBorder(
                        title));


        panel.add(
                valueLabel,
                BorderLayout.CENTER);


        return panel;
    }


    private JLabel createValueLabel() {

        JLabel label =
                new JLabel(
                        "0",
                        SwingConstants.CENTER);


        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25));


        return label;
    }


    // ==================================================
    // DEFAULT DATE = THIS MONTH
    // ==================================================

    private void setDefaultDates() {

        LocalDate today =
                LocalDate.now();


        LocalDate firstDay =
                today.withDayOfMonth(1);


        fromDateField.setText(
                firstDay.toString());


        toDateField.setText(
                today.toString());
    }


    // ==================================================
    // TODAY
    // ==================================================

    private void showToday() {

        LocalDate today =
                LocalDate.now();


        fromDateField.setText(
                today.toString());


        toDateField.setText(
                today.toString());


        generateReport();
    }


    // ==================================================
    // THIS MONTH
    // ==================================================

    private void showThisMonth() {

        LocalDate today =
                LocalDate.now();


        fromDateField.setText(
                today
                        .withDayOfMonth(1)
                        .toString());


        toDateField.setText(
                today.toString());


        generateReport();
    }


    // ==================================================
    // GENERATE REPORT
    // ==================================================

    private void generateReport() {

        try {

            String fromText =
                    fromDateField
                            .getText()
                            .trim();


            String toText =
                    toDateField
                            .getText()
                            .trim();


            if (fromText.isEmpty()
                    || toText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter both dates.");

                return;
            }


            Date fromDate =
                    Date.valueOf(
                            fromText);


            Date toDate =
                    Date.valueOf(
                            toText);


            if (toDate.before(
                    fromDate)) {

                JOptionPane.showMessageDialog(
                        this,
                        "To date cannot be before From date.");

                return;
            }


            // ------------------------------------------
            // SALES
            // ------------------------------------------

            BigDecimal salesTotal =
                    reportDAO
                            .getSalesTotal(
                                    fromDate,
                                    toDate);


            int completedSales =
                    reportDAO
                            .getCompletedSalesCount(
                                    fromDate,
                                    toDate);


            int cancelledSales =
                    reportDAO
                            .getCancelledSalesCount(
                                    fromDate,
                                    toDate);


            salesTotalLabel.setText(
                    "Rs. "
                    + salesTotal.toPlainString());


            completedSalesLabel.setText(
                    String.valueOf(
                            completedSales));


            cancelledSalesLabel.setText(
                    String.valueOf(
                            cancelledSales));


            // ------------------------------------------
            // ATTENDANCE
            // ------------------------------------------

            presentLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getAttendanceCount(
                                            "PRESENT",
                                            fromDate,
                                            toDate)));


            absentLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getAttendanceCount(
                                            "ABSENT",
                                            fromDate,
                                            toDate)));


            lateLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getAttendanceCount(
                                            "LATE",
                                            fromDate,
                                            toDate)));


            leaveLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getAttendanceCount(
                                            "LEAVE",
                                            fromDate,
                                            toDate)));


            // ------------------------------------------
            // INVENTORY
            // ------------------------------------------

            lowStockLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getLowStockCount()));


            outOfStockLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getOutOfStockCount()));


            // ------------------------------------------
            // MASTER DATA
            // ------------------------------------------

            activeProductsLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getActiveProductCount()));


            activeEmployeesLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getActiveEmployeeCount()));


            activeSuppliersLabel.setText(
                    String.valueOf(
                            reportDAO
                                    .getActiveSupplierCount()));


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