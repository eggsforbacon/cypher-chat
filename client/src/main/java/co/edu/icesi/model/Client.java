package co.edu.icesi.model;

import co.edu.icesi.ui.ClientController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey  receivedPublicKey;
    private byte[] secretKey;
    private String secretMessage;
    private Thread listenerThread;
    private DiffieHellman diffieHellman;
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.diffieHellman = new DiffieHellman();

        diffieHellman.generateKeys();

        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
    }

    public void receiveMessageFromServer(VBox messagesVB) {
        System.out.println("Listening for server incoming messages...");
        listenerThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if(messageFromServer.contains("SYN/ACK ")){
                        diffieHellman.receivePublicKeyFrom(messageFromServer.replaceAll("SYN/ACK ", ""));
                        sendMessageToServer("ACK ");
                    } else {
                        String decryptedMessage = diffieHellman.decryptMessage(messageFromServer);
                        ClientController.addBubble(decryptedMessage, messagesVB);
                    }
                } catch (IOException ioe) {
                    System.out.println("Error receiving message from server.");
                    ioe.printStackTrace();
                    break;
                }
            }
        });

        listenerThread.start();
    }

    public String getPublicKey(){
        return Base64.getEncoder().encodeToString(diffieHellman.getPublicKey().getEncoded());
    }

    public void sendMessageToServer(String messageToSend) {
        try {
            String encryptedMsg = messageToSend;
            if(!messageToSend.contains("SYN ") & !messageToSend.contains("ACK ")){
                encryptedMsg = new String(diffieHellman.encryptMessage(messageToSend));
            }
            System.out.println(encryptedMsg);
            bufferedWriter.write(encryptedMsg);
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

    public Thread getListenerThread() {
        return listenerThread;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
