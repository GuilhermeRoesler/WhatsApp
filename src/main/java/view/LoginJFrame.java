package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.User;
import network.HttpClientUtils;
import utils.Constants;
import utils.DesignUtils;
import utils.UserPreferences;

public class LoginJFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton backButton;
    private JLabel logoLabel;

    public LoginJFrame() {
        setTitle("WhatsApp Login");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setIconImage(Constants.LOGO);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setFocusable(true);
        requestFocus();

        initComponents();
        styleComponents();
        layoutComponents();
        initListeners();
    }

    private void initComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 150, 136),
                        0, getHeight(), new Color(0, 121, 107));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        logoLabel = new JLabel("WhatsApp");
        logoLabel.setIcon(Constants.WPP_IMAGE_ICON);

        phoneField = DesignUtils.createLoginStyledTextField("Phone Number");
        passwordField = new JPasswordField();
        DesignUtils.styleLoginPasswordField(passwordField, "Password");

        loginButton = DesignUtils.createLoginStyledButton("LOGIN", new Color(0, 150, 136));
        registerButton = DesignUtils.createLoginStyledButton("REGISTER", new Color(76, 175, 80));
        backButton = DesignUtils.createLoginStyledButton("BACK", new Color(158, 158, 158));
    }

    private void styleComponents() {
        mainPanel.setLayout(null);

        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void layoutComponents() {
        setContentPane(mainPanel);

        logoLabel.setBounds(50, 50, 300, 100);
        phoneField.setBounds(50, 180, 300, 40);
        passwordField.setBounds(50, 240, 300, 40);
        loginButton.setBounds(50, 300, 300, 45);
        registerButton.setBounds(50, 360, 300, 45);
        backButton.setBounds(50, 420, 300, 45);

        mainPanel.add(logoLabel);
        mainPanel.add(phoneField);
        mainPanel.add(passwordField);
        mainPanel.add(loginButton);
        mainPanel.add(registerButton);
        mainPanel.add(backButton);
    }

    private void initListeners() {
        loginButton.addActionListener(e -> performLogin());

        registerButton.addActionListener(e -> showRegistrationDialog());

        backButton.addActionListener(e -> {
            dispose();
            clearFields();
            Constants.framePrincipal.setEnabled(true);
            Constants.framePrincipal.setVisible(true);
        });

        KeyListener enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        phoneField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                Constants.framePrincipal.setEnabled(true);
                Constants.framePrincipal.setVisible(true);
            }
        });
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setFocusable(true);
        dialog.requestFocus();

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(0, 150, 136));

        JTextField nameField = DesignUtils.createLoginStyledTextField("Full Name");
        JTextField phoneField = DesignUtils.createLoginStyledTextField("Phone Number");
        JPasswordField passwordField = new JPasswordField();
        JButton registerBtn = DesignUtils.createLoginStyledButton("CREATE ACCOUNT", new Color(76, 175, 80));

        nameField.setBounds(25, 30, 280, 40);
        phoneField.setBounds(25, 90, 280, 40);
        passwordField.setBounds(25, 150, 280, 40);
        registerBtn.setBounds(25, 210, 280, 45);

        DesignUtils.styleLoginPasswordField(passwordField, "Password");

        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String password = new String(passwordField.getPassword());

            if (!name.isEmpty() && !phone.isEmpty() && !password.isEmpty()) {
                User newUser = new User(name, phone, password);
                try {
                    HttpClientUtils.sendRegistrationToServer(newUser);
                } catch (IOException ex) {
                    DesignUtils.showErrorMessage(this, "Server is not responding. Please try again later.");
                    return;
                }
                dialog.dispose();
                UserPreferences.saveUserCredentials(newUser);
                setFields();
                DesignUtils.showSuccessMessage(this, "Registration successful!");
            } else {
                DesignUtils.showErrorMessage(this, "Please fill all fields");
            }
        });

        panel.add(nameField);
        panel.add(phoneField);
        panel.add(passwordField);
        panel.add(registerBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Performs the login process by validating the user's input, sending the login
     * request to the server, and displaying a success message.
     * This method is an implementation detail within the `LoginJFrame` class and is
     * not part of the public API.
     */
    private void performLogin() {
        String phone = phoneField.getText();
        String password = new String(passwordField.getPassword());

        if (validateFields(phone, password)) {
            User user = new User(phone, password);
            User loggedUser;
            try {
                loggedUser = HttpClientUtils.sendLoginRequest(user);
            } catch (IOException e) {
                DesignUtils.showErrorMessage(this, "Server is not responding. Please try again later.");
                return;
            }

            if (loggedUser != null) {
                UserPreferences.saveUserCredentials(loggedUser);
                DesignUtils.showSuccessMessage(this, "Login successful!");
                clearFields();
                dispose();
                Constants.framePrincipal.updateUserProfile();
                Constants.framePrincipal.verifyContacts();
                Constants.framePrincipal.setEnabled(true);
                Constants.framePrincipal.setVisible(true);
            } else {
                DesignUtils.showErrorMessage(this, "Invalid credentials");
            }
        }
    }

    private boolean validateFields(String phone, String password) {
        if (phone.isEmpty() || password.isEmpty()) {
            DesignUtils.showErrorMessage(this, "Please fill in all fields");
            return false;
        }
        return true;
    }

    private void clearFields() {
        phoneField.setText("");
        passwordField.setText("");
    }

    private void setFields() {
        phoneField.setText(UserPreferences.getUserPhone());
        passwordField.setText(UserPreferences.getUserPassword());
        passwordField.setEchoChar('•');
        passwordField.setForeground(Color.DARK_GRAY);
    }
}
