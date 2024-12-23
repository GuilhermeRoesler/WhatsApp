// package view;

// import java.awt.EventQueue;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
// import java.io.IOException;

// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JTextField;
// import javax.swing.border.EmptyBorder;

// import model.Message;
// import network.HttpClientUtils;

// public class Teste extends JFrame {
//     public static void main(String[] args) {
//         EventQueue.invokeLater(new Runnable() {
//             public void run() {
//                 try {
//                     Teste frame = new Teste();
//                     frame.setLocationRelativeTo(null);
//                     frame.setVisible(true);
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         });
//     }

//     public Teste() {
//         setTitle("Teste");
//         setSize(400, 100);
//         setLocationRelativeTo(null);
//         setResizable(false);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//         initComponents();
//     }

//     public void initComponents() {
//         JPanel contentPane = new JPanel();
//         contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

//         setContentPane(contentPane);
//         contentPane.setLayout(null);

//         JTextField tfdMain = new JTextField();
//         tfdMain.setBounds(10, 10, 374, 25);
//         contentPane.add(tfdMain);

//         JButton button = new JButton("ENVIAR");
//         button.setBounds(10, 30, 374, 25);
//         contentPane.add(button);

//         button.addActionListener(e -> {
//             String message = tfdMain.getText();
//             tfdMain.setText("");
//             tfdMain.requestFocus();
//             try {
//                 HttpClientUtils.sendMessage(new Message("Guilherme", message));
//             } catch (IOException ex) {
//                 showErrorMessage("Server is not responding. Please try again later.");
//                 return;
//             }
//         });

//         tfdMain.addKeyListener(new KeyAdapter() {
//             @Override
//             public void keyPressed(KeyEvent e) {
//                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                     button.doClick();
//                 }
//             }
//         });
//     }

//     private void showErrorMessage(String message) {
//         JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
//     }
// }
