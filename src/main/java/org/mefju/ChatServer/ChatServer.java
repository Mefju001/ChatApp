package org.mefju.ChatServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer{
    private static final int PORT = 1234;
    private static final int PORT2 = 1235;


    public static void main(String[] args) throws IOException {
        new ChatServer().startChat();
        new ChatServer().startFileTransfer();
    }
    public void startChat() {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        System.out.println("Serwer jest włączony czeka na użytkowników");
        Socket user1 = serverSocket.accept();
        System.out.println("Użytkownik 1 wszedł właśnie na chat");

        Socket user2 = serverSocket.accept();
        System.out.println("Użytkownik 2 wszedł właśnie na chat");

        PrintWriter output1 = new PrintWriter(user1.getOutputStream(), true);
        PrintWriter output2 = new PrintWriter(user2.getOutputStream(), true);

        output1.println("Partner: Online");
        output2.println("Partner: Online");

        BufferedReader in1 = new BufferedReader(new InputStreamReader(user1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(user2.getInputStream()));

        new Thread(() -> forwardMessages(in1, output2, output1)).start();
        new Thread(() -> forwardMessages(in2, output1, output2)).start();
    }catch (IOException e){
        System.err.println(STR."Błąd serwera: \{e.getMessage()}");
    }
}
public void startFileTransfer() throws IOException {
    ServerSocket serverSocketForPicture = new ServerSocket(PORT2);
    System.out.println("Serwer plików działa na porcie 1235...");

    while (true) {
        Socket socket = serverSocketForPicture.accept();

        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                String sender = dis.readUTF();
                String fileName = dis.readUTF();
                long size = dis.readLong();

                File outFile = new File("received_" + fileName);
                FileOutputStream fos = new FileOutputStream(outFile);

                byte[] buffer = new byte[4096];
                int count;
                while (size > 0 && (count = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) > 0) {
                    fos.write(buffer, 0, count);
                    size -= count;
                }

                System.out.println("Plik od " + sender + ": " + fileName);

                fos.close();
                dis.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
    private static void forwardMessages(BufferedReader in, PrintWriter out,PrintWriter ownOut) {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                out.println(msg);
                ownOut.println(msg);
            }
        } catch (IOException e) {
            out.println("Partner: Offline");
            ownOut.println("Partner: Offline");
        }
    }
}
