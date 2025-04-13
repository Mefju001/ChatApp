package org.mefju.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerGUI extends JFrame{
    private final JTextArea chatArea;
    private final JTextField inputField;
    private PrintWriter output;

    public ChatServerGUI()
    {
        setTitle("Chat");
        setSize(600,600);
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

        startServer();
    }
private void sendMessage() {
    String message = inputField.getText().trim();
    if (!message.isEmpty()) {
        chatArea.append(STR."LocalHost: \{message}\n");
        output.println(message);
        inputField.setText("");
    }
}

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(1234)) {
                Socket clientSocket = serverSocket.accept();


                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                output = new PrintWriter(clientSocket.getOutputStream(), true);


                String message;
                while ((message = input.readLine()) != null) {
                    chatArea.append(STR."Klient: \{message}\n");
                }

            } catch (IOException e) {
                chatArea.append(STR."Błąd: \{e.getMessage()}\n");
            }
        }).start();
    }

}
