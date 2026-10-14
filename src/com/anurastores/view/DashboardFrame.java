package com.anurastores.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.anurastores.model.User;
import com.anurastores.util.UITheme;

public class DashboardFrame extends JFrame {

    private User loggedInUser;


    public DashboardFrame(User user) {

        this.loggedInUser = user;

        setTitle(
                "Anura Stores Management System - Dashboard");

        setSize(
                900,
                600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        initComponents();
    }


    private void initComponents() {
        

        // =================================================
        // TOP AREA
        // =================================================

        JLabel titleLabel =
                new JLabel(
                        "ANURA STORES MANAGEMENT SYSTEM",
                        SwingConstants.CENTER);

        UITheme.styleTitleLabel(titleLabel);


        JLabel userLabel =
                new JLabel(
                        "Logged in as: "
                        + loggedInUser.getUsername()
                        + " | Role: "
                        + loggedInUser.getRole(),
                        SwingConstants.CENTER);
        UITheme.styleNormalLabel(userLabel);

        JPanel topPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1,
                                5,
                                5));
        UITheme.stylePanel(topPanel);


        topPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        10,
                        15,
                        10));


        topPanel.add(titleLabel);
        topPanel.add(userLabel);


        // =================================================
        // MENU AREA
        // =================================================

        JPanel menuPanel =
                new JPanel(
                        new GridLayout(
                                4,
                                2,
                                20,
                                20));
        UITheme.stylePanel(menuPanel);


        menuPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        40,
                        25,
                        40));


        // =================================================
        // BUTTONS
        // =================================================

        JButton productButton = new JButton("Product Management");
        UITheme.stylePrimaryButton(productButton);

        JButton supplierButton = new JButton("Supplier Management");
        UITheme.stylePrimaryButton(supplierButton);

        JButton employeeButton = new JButton("Employee Management");
        UITheme.stylePrimaryButton(employeeButton);

        JButton attendanceButton = new JButton("Attendance Handling");
        UITheme.stylePrimaryButton(attendanceButton);

        JButton inventoryButton = new JButton("Inventory Management");
        UITheme.stylePrimaryButton(inventoryButton);

        JButton salesButton = new JButton("Sales Management");
        UITheme.stylePrimaryButton(salesButton);

        JButton reportsButton = new JButton("Management Reports");
        UITheme.stylePrimaryButton(reportsButton);

        JButton userAccountButton = new JButton("User Account Management");
        UITheme.stylePrimaryButton(userAccountButton);


        // =================================================
        // ADD BUTTONS TO MENU
        // =================================================

        menuPanel.add(productButton);
        menuPanel.add(supplierButton);

        menuPanel.add(employeeButton);
        menuPanel.add(attendanceButton);

        menuPanel.add(inventoryButton);
        menuPanel.add(salesButton);

        menuPanel.add(reportsButton);
        menuPanel.add(userAccountButton);


        // =================================================
        // BOTTOM / SETTINGS & LOGOUT AREA
        // =================================================

        JButton settingsButton = new JButton("Settings");
        UITheme.styleSecondaryButton(settingsButton);

        JButton logoutButton = new JButton("Logout");
        UITheme.styleDangerButton(logoutButton);


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        UITheme.stylePanel(bottomPanel);

        bottomPanel.add(settingsButton);
        bottomPanel.add(logoutButton);


        // =================================================
        // ADD PANELS TO FRAME
        // =================================================

        add(
                topPanel,
                BorderLayout.NORTH);


        add(
                menuPanel,
                BorderLayout.CENTER);


        add(
                bottomPanel,
                BorderLayout.SOUTH);


        // =================================================
        // EVENTS
        // =================================================

        productButton.addActionListener(e ->

                new ProductManagementFrame(
                        loggedInUser)
                        .setVisible(true));


        supplierButton.addActionListener(e ->

                new SupplierManagementFrame()
                        .setVisible(true));


        employeeButton.addActionListener(e ->

                new EmployeeManagementFrame()
                        .setVisible(true));


        attendanceButton.addActionListener(e ->

                new AttendanceManagementFrame(
                        loggedInUser)
                        .setVisible(true));


        inventoryButton.addActionListener(e ->

                new InventoryManagementFrame(
                        loggedInUser)
                        .setVisible(true));


        salesButton.addActionListener(e ->

                new SalesManagementFrame(
                        loggedInUser)
                        .setVisible(true));


        reportsButton.addActionListener(e ->

                new ReportsFrame(
                        loggedInUser)
                        .setVisible(true));


        userAccountButton.addActionListener(e ->

                new UserAccountManagementFrame(
                        loggedInUser)
                        .setVisible(true));


        // =================================================
        // ROLE BASED ACCESS
        // =================================================

        if (!"ADMIN".equalsIgnoreCase(
                loggedInUser.getRole())) {

            reportsButton.setEnabled(false);
            userAccountButton.setEnabled(false);
        }


        // =================================================
        // SETTINGS POPUP ACTION
        // =================================================

        settingsButton.addActionListener(e -> openSettingsPopup());


        // =================================================
        // LOGOUT
        // =================================================

        logoutButton.addActionListener(e -> {

            int answer =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Do you want to logout?",
                            "Logout",
                            JOptionPane.YES_NO_OPTION);


            if (answer
                    == JOptionPane.YES_OPTION) {

                dispose();

                new LoginFrame()
                        .setVisible(true);
            }
        });
    }


 // =====================================================
    // SETTINGS POPUP DIALOG
    // =====================================================

    private void openSettingsPopup() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(350, 200);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        UITheme.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Appearance Settings", SwingConstants.CENTER);
        UITheme.styleSubtitleLabel(titleLabel);

        JCheckBox darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        darkModeCheckBox.setFont(UITheme.NORMAL_FONT);
        darkModeCheckBox.setForeground(UITheme.TEXT_DARK);
        darkModeCheckBox.setBackground(UITheme.LIGHT_BG);
        darkModeCheckBox.setFocusPainted(false);
        darkModeCheckBox.setSelected(UITheme.isDarkMode());
        darkModeCheckBox.setHorizontalAlignment(SwingConstants.CENTER);

        JButton saveButton = new JButton("Apply Changes");
        UITheme.stylePrimaryButton(saveButton);

        saveButton.addActionListener(e -> {
            boolean isDark = darkModeCheckBox.isSelected();
         // Apply the selected theme globally
            UITheme.setDarkMode(isDark);

            // Update JOptionPane colors
            UITheme.setupJOptionPaneTheme();

            // Refresh the dashboard's component colors
            refreshDashboardTheme();

            // Refresh the Swing component UI
            SwingUtilities.updateComponentTreeUI(DashboardFrame.this);

            DashboardFrame.this.invalidate();
            DashboardFrame.this.validate();
            DashboardFrame.this.repaint();
            // 2. Refresh all other open application windows and dialogs
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
                window.invalidate();
                window.validate();
                window.repaint();
            }

            settingsDialog.dispose();
        });

        panel.add(titleLabel);
        panel.add(darkModeCheckBox);
        panel.add(saveButton);

        settingsDialog.add(panel);
        settingsDialog.setVisible(true);
    }
    
    
    private void refreshDashboardTheme() {

        // Refresh panel background colors without changing label fonts
        refreshPanelColors(getContentPane());

        // Refresh the dashboard
        getContentPane().revalidate();
        getContentPane().repaint();
    }
   

    private void refreshPanelColors(java.awt.Container container) {

        for (java.awt.Component component : container.getComponents()) {

            if (component instanceof javax.swing.JPanel) {
                javax.swing.JPanel panel = (javax.swing.JPanel) component;

                UITheme.stylePanel(panel);
            }

            if (component instanceof java.awt.Container) {
                refreshPanelColors((java.awt.Container) component);
            }
        }
    }
}