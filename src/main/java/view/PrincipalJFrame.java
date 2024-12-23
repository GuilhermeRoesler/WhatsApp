package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Message;
import model.User;
import network.HttpClientUtils;
import utils.Constants;
import utils.DesignUtils;
import utils.UserPreferences;
import view.components.PlaceholderTextField;

public class PrincipalJFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, searchPanel;
    private JPanel chatPanel;
    private JPanel sidePanel;
    private JTextField messageField, searchField;
    private JTextArea chatArea;
    private JButton sendButton, settingsButton;
    private JButton loginButton;
    private JScrollPane scrollPane;
    private JList<String> contactsList;
    private JButton addContactButton;
    private JPanel loginPanel;
    private JPanel addContactPanel;

    private User savedUser; // Main user
    private Map<String, JTextArea> conversations; // all conversations
    private String currentContact; // Main user name

    private Timer messagePoller;
    private long lastMessageTimestamp = 0;

    private List<String> contacts;
    private Set<String> processedMessageIds = new HashSet<>();

    public PrincipalJFrame() {
        setTitle("WhatsApp");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Constants.LOGO);

        initComponents();
        styleComponents();
        layoutComponents();
        initListeners();

        updateUserProfile();
        verifyContacts();

        /**
         * Starts a timer that polls for new messages every 1 second.
         * This is used to periodically check for new messages from the server
         * and update the chat UI accordingly.
         */
        messagePoller = new Timer(1000, e -> {
            pollMessages();
        });
        messagePoller.start();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 236, 236));

        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setPreferredSize(new Dimension(250, getHeight()));

        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(229, 221, 213));

        // Chat components
        chatArea = new JTextArea();
        scrollPane = new JScrollPane(chatArea);
        messageField = DesignUtils.createPrincipalStyledTextField("Type a message");

        // Side panel components
        searchField = new PlaceholderTextField("Search or start new chat"); // Create search field 🔍
        settingsButton = new JButton("⚙"); // Create settings button

        JPopupMenu settingsMenu = new JPopupMenu(); // Create popup menu for settings
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.addActionListener(e -> logout());
        settingsMenu.add(logoutItem);

        // Add settings button action
        settingsButton.addActionListener(e -> {
            settingsMenu.show(settingsButton, 0, settingsButton.getHeight());
        });

        // Buttons
        sendButton = DesignUtils.createPrincipalStyledButton("Send", new Color(0, 150, 136));
        loginButton = DesignUtils.createPrincipalStyledButton("Login", new Color(0, 150, 136));
        addContactButton = DesignUtils.createPrincipalStyledButton("Add Contact", new Color(0, 150, 136));

        // Contacts list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Recent Chats");
        contactsList = new JList<>(listModel);

        conversations = new HashMap<>();
    }

    private void styleComponents() {
        // Chat area styling
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(229, 221, 213));
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        // Scroll pane styling
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(229, 221, 213));

        // Contacts list styling
        contactsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactsList.setFixedCellHeight(50);
        contactsList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contactsList.setSelectionBackground(new Color(0, 150, 136));
        contactsList.setSelectionForeground(Color.WHITE);
        contactsList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Search field styling
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        // Setings button styling
        settingsButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        settingsButton.setForeground(new Color(0, 150, 136));
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        // Login panel setup
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.add(loginButton, BorderLayout.CENTER);

        // Search panel assembly panel setup
        JPanel searchWithSettingsPanel = new JPanel(new BorderLayout(5, 0));
        searchWithSettingsPanel.setBackground(Color.WHITE);
        searchWithSettingsPanel.add(searchField, BorderLayout.CENTER);
        searchWithSettingsPanel.add(settingsButton, BorderLayout.EAST);

        // Search panel setup
        searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setPreferredSize(new Dimension(250, 60));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(searchWithSettingsPanel, BorderLayout.CENTER);

        // Bottom panel setup
        addContactPanel = new JPanel(new BorderLayout());
        addContactPanel.setBackground(Color.WHITE);
        addContactPanel.add(addContactButton, BorderLayout.CENTER);

        // Side panel assembly
        sidePanel.add(loginPanel, BorderLayout.NORTH);
        sidePanel.add(new JScrollPane(contactsList), BorderLayout.CENTER);
        sidePanel.add(addContactPanel, BorderLayout.SOUTH);

        // Message panel setup
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Chat panel assembly
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        // Main panel assembly
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void initListeners() {
        sendButton.addActionListener(e -> sendMessage());

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        loginButton.addActionListener(e -> {
            setEnabled(false);
            Constants.frameLogin.setLocationRelativeTo(null);
            Constants.frameLogin.setVisible(true);
        });

        addContactButton.addActionListener(e -> {
            String newContactPhone = JOptionPane.showInputDialog(
                    this,
                    "Enter contact phone number:",
                    "Add New Contact",
                    JOptionPane.PLAIN_MESSAGE);

            if (newContactPhone != null && !newContactPhone.trim().isEmpty()) {
                User newContact;
                try {
                    newContact = HttpClientUtils.sendContactVerification(savedUser.getPhone(), newContactPhone);
                } catch (IOException e1) {
                    DesignUtils.showErrorMessage(this,
                            "Error sending contact verification request: Server is not responding");
                    return;
                }
                if (newContact != null) {
                    contacts.add(newContact.getName() + "/" + newContact.getPhone());
                    UserPreferences.saveContacts(contacts);
                    verifyContacts();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Contact not found or already added.",
                            "Contact Not Found",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedContact = contactsList.getSelectedValue();
                if (selectedContact != null && !selectedContact.equals("Recent Chats")) {
                    switchConversation(selectedContact);
                }
            }
        });
    }

    public void updateUserProfile() {
        savedUser = UserPreferences.loadUserCredentials();

        sidePanel.removeAll();

        // Side panel assembly
        if (savedUser == null) {
            sidePanel.add(loginPanel, BorderLayout.NORTH);

            loginButtonClick();
        } else {
            sidePanel.add(searchPanel, BorderLayout.NORTH);
        }
        sidePanel.add(new JScrollPane(contactsList), BorderLayout.CENTER);
        sidePanel.add(addContactPanel, BorderLayout.SOUTH);

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void verifyContacts() {
        contacts = UserPreferences.loadContacts();

        if (contacts != null) {
            DefaultListModel<String> model = (DefaultListModel<String>) contactsList.getModel();
            model.clear();
            model.addElement("Recent Chats");

            // contacts.forEach(model::addElement);
            for (String string : contacts) {
                model.addElement(string.split("/")[0]);
            }
        }
    }

    private void switchConversation(String contact) {
        currentContact = contact;
        clearProcessedMessages(); // Reset tracking when switching conversations

        if (!conversations.containsKey(contact)) {
            JTextArea newChatArea = new JTextArea();
            newChatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            newChatArea.setEditable(false);
            newChatArea.setLineWrap(true);
            newChatArea.setWrapStyleWord(true);
            newChatArea.setBackground(new Color(229, 221, 213));
            newChatArea.setMargin(new Insets(10, 10, 10, 10));

            // Load saved messages
            List<Message> savedMessages = UserPreferences
                    .loadMessages(Message.generateConversationId(savedUser.getName(), contact));
            for (Message msg : savedMessages) {
                String messageId = msg.getTimestamp() + "_" + msg.getSender() + "_" + msg.getContent();
                processedMessageIds.add(messageId); // Track loaded messages

                String timestamp = new SimpleDateFormat("HH:mm").format(new Date(msg.getTimestamp()));
                newChatArea.append(String.format("[%s] %s: %s\n", timestamp, msg.getSender(), msg.getContent()));
            }
            conversations.put(contact, newChatArea);
        }

        chatArea = conversations.get(contact);
        scrollPane.setViewportView(chatArea);
        revalidate();
        repaint();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (currentContact == null || message.isEmpty() || message.equals("Type a message"))
            return;

        messageField.setText("");
        try {
            long timestamp = System.currentTimeMillis();
            Message msg = new Message(savedUser.getName(), currentContact, message, timestamp);
            HttpClientUtils.sendMessage(msg);
            // Save message locally
            UserPreferences.saveMessages(msg.getConversationId(), msg);

            String messageId = msg.getTimestamp() + "_" + msg.getSender() + "_" + msg.getContent();
            processedMessageIds.add(messageId);
            lastMessageTimestamp = Math.max(lastMessageTimestamp, msg.getTimestamp());

            String timestampstr = new SimpleDateFormat("HH:mm").format(new Date(msg.getTimestamp()));
            chatArea.append(String.format("[%s] %s: %s\n", timestampstr, msg.getSender(), msg.getContent()));
        } catch (IOException e) {
            DesignUtils.showErrorMessage(this, "Server is not responding. Please try again later.");
        }
    }

    private void pollMessages() {
        if (currentContact == null || savedUser == null)
            return;

        try {
            List<Message> messages = HttpClientUtils.receiveMessages(savedUser.getName(), currentContact);
            for (Message message : messages) {
                String messageId = message.getTimestamp() + "_" + message.getSender() + "_" + message.getContent();

                // Only process new messages
                if (!processedMessageIds.contains(messageId)) {
                    String timestamp = new SimpleDateFormat("HH:mm")
                            .format(new Date(message.getTimestamp()));
                    chatArea.append(String.format("[%s] %s: %s\n",
                            timestamp, message.getSender(), message.getContent()));

                    processedMessageIds.add(messageId);
                    lastMessageTimestamp = Math.max(lastMessageTimestamp, message.getTimestamp());
                }
            }
        } catch (IOException e) {
            DesignUtils.showErrorMessage(this, "Server is not responding. Please try again later.");
        }
    }

    public void loginButtonClick() {
        loginButton.doClick();
    }

    private void logout() {
        UserPreferences.clearUserCredentials();
        conversations.clear();
        currentContact = null;
        DefaultListModel<String> model = (DefaultListModel<String>) contactsList.getModel();
        model.clear();
        updateUserProfile();
    }

    private void clearProcessedMessages() {
        processedMessageIds.clear();
        lastMessageTimestamp = 0;
    }
}