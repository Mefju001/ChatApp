package org.mefju.ChatServer;

import javax.swing.*;

public class ChatStarter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServerGUI::new);
        SwingUtilities.invokeLater(ChatClientGUI::new);

    }
}
