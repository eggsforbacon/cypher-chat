package co.edu.icesi.model;

import co.edu.icesi.ui.ClientController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
    }

    public void receiveMessageFromServer(VBox messagesVB) {
        System.out.println("Listening for server incoming messages...");
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    ClientController.addBubble(messageFromServer, messagesVB);
                } catch (IOException ioe) {
                    System.out.println("Error receiving message from server.");
                    ioe.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void sendMessageToServer(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ioe) {
            System.out.println("Error sending message to the server.");
            ioe.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        System.out.println("Message sent to server");
    }

    public void closeEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
