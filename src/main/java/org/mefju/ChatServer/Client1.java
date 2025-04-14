package org.mefju.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Client1 extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JLabel statusLabel;
    private PrintWriter writer;
    private String nick;

    public Client1(String nick) throws HeadlessException {
        this.nick = nick;
        setTitle(STR."Czat - \{nick}");
        setSize(400,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        statusLabel = new JLabel("👤 Partner: ???");
        add(statusLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        JButton sendButton = new JButton("Wyślij");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);


        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e->sendMessage());
        connectToServer();
        setVisible(true);

    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 1234);
            writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = reader.readLine()) != null) {
                        if (msg.startsWith("[SYSTEM]")) {
                            statusLabel.setText(msg.replace("[SYSTEM]", "").trim());
                        } else {
                            chatArea.append(msg + "\n");
                        }
                    }
                } catch (IOException e) {
                    chatArea.append("❌ Połączenie przerwane.\n");
                    statusLabel.setText("❌ Partner: Offline");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Nie udało się połączyć z serwerem.", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            writer.println(STR."[\{timestamp}] \{nick}: \{msg}");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        String nick = JOptionPane.showInputDialog("Podaj swój nick:");
        if (nick != null && !nick.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> new Client1(nick.trim()));
        }
    }
}
