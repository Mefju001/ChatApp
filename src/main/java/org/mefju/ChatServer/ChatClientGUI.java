package org.mefju.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI extends JFrame {
    private final JTextArea chatArea;
    private final JTextField inputField;
    private PrintWriter output;


    public ChatClientGUI() {
        setTitle("Chat Klienta");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        inputField = new JTextField();
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton sendButton = new JButton("Wyślij");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e->sendMessage());
        setVisible(true);

        connectToServer();
    }
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append(STR."Ty: \{message}\n");
            output.println(message);
            inputField.setText("");
        }
    }
    private void connectToServer() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 1234)) {

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = input.readLine()) != null) {
                    chatArea.append(STR."Serwer: \{message}\n");
                }

            } catch (IOException e) {
                chatArea.append(STR."Błąd: \{e.getMessage()}\n");
            }
        }).start();
    }

}
