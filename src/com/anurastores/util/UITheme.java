package com.anurastores.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

public class UITheme {

    private static boolean darkModeEnabled = false;

    // =========================================
    // COLORS
    // =========================================
    public static final Color PRIMARY = new Color(34, 112, 147);
    public static final Color PRIMARY_DARK = new Color(25, 85, 112);
    public static final Color SECONDARY = new Color(46, 204, 113);
    public static final Color WARNING = new Color(241, 196, 15);
    public static final Color DANGER = new Color(231, 76, 60);

    // Light & Dark Palette Values
    public static final Color LIGHT_BG_VAL = new Color(245, 247, 250);
    public static final Color DARK_BG_VAL = new Color(30, 30, 30);

    public static final Color CARD_BG_LIGHT = Color.WHITE;
    public static final Color CARD_BG_DARK = new Color(45, 45, 45);

    public static final Color TEXT_DARK_VAL = new Color(44, 62, 80);
    public static final Color TEXT_LIGHT_VAL = new Color(230, 230, 230);

    public static final Color BORDER_LIGHT = new Color(210, 215, 223);
    public static final Color BORDER_DARK = new Color(70, 70, 70);

    // Dynamic references used across UI methods
    public static Color LIGHT_BG = LIGHT_BG_VAL;
    public static Color CARD_BG = CARD_BG_LIGHT;
    public static Color TEXT_DARK = TEXT_DARK_VAL;
    public static Color BORDER = BORDER_LIGHT;
    public static final Color TEXT_LIGHT = Color.WHITE;

    public static void setDarkMode(boolean enabled) {
        darkModeEnabled = enabled;
        if (enabled) {
            LIGHT_BG = DARK_BG_VAL;
            CARD_BG = CARD_BG_DARK;
            TEXT_DARK = TEXT_LIGHT_VAL;
            BORDER = BORDER_DARK;
        } else {
            LIGHT_BG = LIGHT_BG_VAL;
            CARD_BG = CARD_BG_LIGHT;
            TEXT_DARK = TEXT_DARK_VAL;
            BORDER = BORDER_LIGHT;
        }
    }

    public static boolean isDarkMode() {
        return darkModeEnabled;
    }


    // =========================================
    // FONTS
    // =========================================
    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 24);

    public static final Font SUBTITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 16);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    public static final Font TABLE_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font TABLE_HEADER_FONT =
            new Font("Segoe UI", Font.BOLD, 13);


    // =========================================
    // GENERAL FRAME / PANEL
    // =========================================
    public static void stylePanel(JPanel panel) {
        panel.setBackground(LIGHT_BG);
    }

    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(CARD_BG);
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
        );
    }

    public static void styleTitleLabel(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_DARK);
    }

    public static void styleSubtitleLabel(JLabel label) {
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_DARK);
    }
    
    public static void styleSectionLabel(JLabel label) {
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_DARK);
    }

    public static void styleNormalLabel(JLabel label) {
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT_DARK);
    }


    // =========================================
    // TEXT FIELDS / COMBO
    // =========================================
    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        field.setPreferredSize(new Dimension(180, 32));
        field.setBackground(darkModeEnabled ? new Color(60, 60, 60) : Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setBorder(createRoundedBorder());
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
        comboBox.setPreferredSize(new Dimension(180, 32));
        comboBox.setBackground(darkModeEnabled ? new Color(60, 60, 60) : Color.WHITE);
        comboBox.setForeground(TEXT_DARK);
        comboBox.setBorder(createRoundedBorder());
    }

    private static Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
    }


    // =========================================
    // BUTTONS
    // =========================================
    public static void stylePrimaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleSuccessButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(SECONDARY);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleWarningButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(WARNING);
        button.setForeground(darkModeEnabled ? Color.WHITE : TEXT_DARK_VAL);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(DANGER);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(darkModeEnabled ? new Color(60, 60, 60) : Color.WHITE);
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    // =========================================
    // JOPTIONPANE STYLING
    // =========================================
    public static void setupJOptionPaneTheme() {
        javax.swing.UIManager.put("Panel.background", LIGHT_BG);
        javax.swing.UIManager.put("OptionPane.background", LIGHT_BG);
        javax.swing.UIManager.put("OptionPane.messageFont", NORMAL_FONT);
        javax.swing.UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        javax.swing.UIManager.put("Button.font", BUTTON_FONT);
        javax.swing.UIManager.put("Button.background", PRIMARY);
        javax.swing.UIManager.put("Button.foreground", TEXT_LIGHT);
        javax.swing.UIManager.put("Button.focus", PRIMARY_DARK);
    }

    // =========================================
    // TABLE
    // =========================================
    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(24);
        table.setGridColor(BORDER);
        table.setBackground(darkModeEnabled ? new Color(40, 40, 40) : Color.WHITE);
        table.setForeground(TEXT_DARK);
        table.setSelectionBackground(darkModeEnabled ? new Color(52, 152, 219) : new Color(204, 229, 255));
        table.setSelectionForeground(darkModeEnabled ? Color.WHITE : TEXT_DARK_VAL);

        JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(PRIMARY);
        header.setForeground(TEXT_LIGHT);
    }


    // =========================================
    // TITLED BORDER
    // =========================================
    public static TitledBorder createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                title
        );
        border.setTitleFont(SUBTITLE_FONT);
        border.setTitleColor(TEXT_DARK);
        return border;
    }


    // =========================================
    // APPLY DEFAULT LABEL STYLE
    // =========================================
    public static void styleAllLabels(Component component) {
        if (component instanceof JLabel) {
            styleNormalLabel((JLabel) component);
        }

        if (component instanceof JPanel) {
            Component[] components = ((JPanel) component).getComponents();
            for (Component child : components) {
                styleAllLabels(child);
            }
        }
    }


    // =========================================
    // GENERIC COMPONENT BACKGROUND
    // =========================================
    public static void setWhiteBackground(JComponent component) {
        component.setBackground(darkModeEnabled ? new Color(45, 45, 45) : Color.WHITE);
    }
}