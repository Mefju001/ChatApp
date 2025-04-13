package org.mefju.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class ChatServer{
    private static final int PORT = 1234;

    public static void main(String[] args) throws IOException {
        new ChatServer().start();
    }
    public void start() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Serwer jest włączony czeka na użytkowników");

        Socket user1 = serverSocket.accept();
        System.out.println("Użytkownik 1 wszedł własnie na chat");

        Socket user2 = serverSocket.accept();
        System.out.println("Użytkownik 2 wszedł własnie na chat");

        PrintWriter output1 = new PrintWriter(user1.getOutputStream(),true);
        PrintWriter output2 = new PrintWriter(user2.getOutputStream(),true);

        output1.println("Partner: Online");
        output2.println("Partner: Online");

        BufferedReader in1 = new BufferedReader(new InputStreamReader(user1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(user2.getInputStream()));

        // Przekazywanie wiadomości
        new Thread(() -> forwardMessages(in1, output2, "[1]")).start();
        new Thread(() -> forwardMessages(in2, output1, "[2]")).start();
    }
    private static void forwardMessages(BufferedReader in, PrintWriter out, String tag) {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                out.println(msg);
            }
        } catch (IOException e) {
            out.println("Partner: Offline");
        }
    }
}
