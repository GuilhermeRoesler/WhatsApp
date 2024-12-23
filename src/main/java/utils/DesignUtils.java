package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import view.components.PlaceholderTextField;

public class DesignUtils {

    public static void showSuccessMessage(Component c, String message) {
        JOptionPane.showMessageDialog(c, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorMessage(Component c, String message) {
        JOptionPane.showMessageDialog(c, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showExitErrorMessage(Component c, String message) {
        int option = JOptionPane.showOptionDialog(c, 
            message,
            "Error", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new String[]{"Close Application", "Continue"},
            "Continue");
            
        if (option == 0) {
            System.exit(0);
        }
    }

    public static JTextField createPrincipalStyledTextField(String placeholder) {
        JTextField field = new PlaceholderTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JButton createPrincipalStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(Color.WHITE);
                g2d.drawString(text,
                        (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static JTextField createLoginStyledTextField(String placeholder) {
        JTextField field = new PlaceholderTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0, 150, 136), 2, true),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    public static void styleLoginPasswordField(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setEchoChar((char) 0);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0, 150, 136), 2, true),
                new EmptyBorder(5, 10, 5, 10)));
        field.addFocusListener(new FocusAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(Color.DARK_GRAY);
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static JButton createLoginStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Clear the background
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw button background
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }

                // Use proper dimensions for the rounded rectangle
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Draw text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setColor(Color.WHITE);
                g2d.drawString(text,
                        (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 0, 0, 0));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
