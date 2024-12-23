package view.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

public class PlaceholderTextField extends JTextField {
    public PlaceholderTextField(String placeholder) {
        setText(placeholder);
        setForeground(Color.GRAY);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add focus listener to handle placeholder visibility
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.DARK_GRAY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                }
            }
        });
    }
}
