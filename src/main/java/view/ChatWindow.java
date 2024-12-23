// package view;

// import java.awt.BorderLayout;
// import java.awt.Font;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
// import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.List;

// import javax.swing.BorderFactory;
// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
// import javax.swing.SwingUtilities;
// import javax.swing.Timer;

// import model.Message;
// import network.HttpClientUtils;

// public class ChatWindow extends JFrame {
//     private JTextArea chatArea;
//     private JTextField messageField;
//     private JPanel messagePanel;
//     private JButton sendButton;
//     private Timer messagePoller;
//     private long lastMessageTimestamp = 0;

//     public ChatWindow() {
//         setTitle("WhatsApp Clone");
//         setSize(500, 700);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLocationRelativeTo(null);

//         // Chat area setup
//         chatArea = new JTextArea();
//         chatArea.setEditable(false);
//         chatArea.setLineWrap(true);
//         chatArea.setWrapStyleWord(true);
//         chatArea.setFont(new Font("Arial", Font.PLAIN, 14));

//         JScrollPane scrollPane = new JScrollPane(chatArea);
//         scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

//         // Message input setup
//         messagePanel = new JPanel(new BorderLayout(5, 5));
//         messageField = new JTextField();
//         messageField.setFont(new Font("Arial", Font.PLAIN, 14));
//         sendButton = new JButton("Send");

//         messagePanel.add(messageField, BorderLayout.CENTER);
//         messagePanel.add(sendButton, BorderLayout.EAST);
//         messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

//         // Main layout
//         setLayout(new BorderLayout());
//         add(scrollPane, BorderLayout.CENTER);
//         add(messagePanel, BorderLayout.SOUTH);

//         // Action listeners
//         sendButton.addActionListener(e -> sendMessage());
//         messageField.addKeyListener(new KeyAdapter() {
//             @Override
//             public void keyPressed(KeyEvent e) {
//                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                     sendMessage();
//                 }
//             }
//         });

//         messagePoller = new Timer(1000, e -> pollMessages());
//         messagePoller.start();
//     }

//     private void sendMessage() {
//         String text = messageField.getText().trim();
//         if (!text.isEmpty()) {
//             // Add message to chat area
//             // String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
//             // chatArea.append(String.format("[%s] You: %s\n", timestamp, text));

//             // Send message to server
//             try {
//                 HttpClientUtils.sendMessage(new Message(System.getProperty("user.name"), text));
//             } catch (IOException ex) {
//                 showErrorMessage("Server is not responding. Please try again later.");
//                 return;
//             }

//             // Clear input field and scroll to bottom
//             messageField.setText("");
//             chatArea.setCaretPosition(chatArea.getDocument().getLength());
//         }
//         messageField.requestFocus();
//     }

//     private void pollMessages() {
//         List<Message> newMessages;
//         try {
//             newMessages = HttpClientUtils.receiveMessages();
//         } catch (IOException ex) {
//             showErrorMessage("Server is not responding. Please try again later.");
//             return;
//         }
//         for (Message message : newMessages) {
//             if (message.getTimestamp() > lastMessageTimestamp) {
//                 String timestamp = new SimpleDateFormat("HH:mm")
//                         .format(new Date(message.getTimestamp()));
//                 chatArea.append(String.format("[%s] %s: %s\n",
//                         timestamp, message.getSender(), message.getContent()));
//                 lastMessageTimestamp = message.getTimestamp();
//             }
//         }
//     }

//     private void showErrorMessage(String message) {
//         JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             ChatWindow chat = new ChatWindow();
//             chat.setVisible(true);
//         });
//     }
// }
