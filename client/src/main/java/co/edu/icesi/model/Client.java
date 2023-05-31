package co.edu.icesi.model;

//import co.edu.icesi.ui.ClientController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.*;
import java.security.*;
import java.net.Socket;
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


    public Client(Socket socket, String username) throws IOException {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.username = username;
            System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                encryptAndSendMessage(messageToSend, this);
//                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException ioe) {
            System.out.println("Error sending message to the server.");
            ioe.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        System.out.println("Message sent to server");
    }

    public void readMessage(){
        new Thread(() -> {
            String msg;
                while (socket.isConnected()){
                    try {
                        msg = bufferedReader.readLine();
                        receiveAndDecryptMessage(msg.getBytes());
//                        System.out.println(msg);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException error){
            error.printStackTrace();
        }
    }

    /*Diffie Hellman Implementation*/

    public void encryptAndSendMessage(String message, Client client) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher  = Cipher.getInstance("AES_256/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            client.receiveAndDecryptMessage(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCommonSecretKey() {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, true);
            secretKey = keyAgreement.generateSecret();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateKeys() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);

            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey  = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void receiveAndDecryptMessage(byte[] message) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES_256/ECB/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            secretMessage = new String(cipher.doFinal(message));
            showSecretMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivePublicKeyFrom(Client client) {
        receivedPublicKey = client.getPublicKey();
    }

    public void showSecretMessage() {
        System.out.println(secretMessage);
    }


    public static void main(String[] args) throws IOException{
        Socket socket = new Socket("localhost", 1234);
        Client a = new Client(socket,"a");
        Client b = new Client(socket,"b");

        a.generateKeys();
        b.generateKeys();

        a.receivePublicKeyFrom(b);
        b.receivePublicKeyFrom(a);

        a.generateCommonSecretKey();
        b.generateCommonSecretKey();

        a.encryptAndSendMessage("Hello", b);
        b.showSecretMessage();


//       Scanner scanner = new Scanner(System.in);
//       System.out.println("Enter your username");
//       String username = scanner.nextLine();
//       Socket socket = new Socket("localhost", 1234);
//       Client client = new Client(socket,username);
//       client.generateKeys();
//
//        ServerSocket serverSocket = new ServerSocket(1234);
//        Socket otherClientSocket = serverSocket.accept();
//        Client otherClient = new Client(otherClientSocket, username);
//        otherClient.generateKeys();
//
//        // Exchange public keys
//        client.receivePublicKeyFrom(otherClient);
//        otherClient.receivePublicKeyFrom(client);
//
//        // Generate common secret keys
//        client.generateCommonSecretKey();
//        otherClient.generateCommonSecretKey();
//
//        System.out.println("Both clients connected and keys exchanged.");
//
//        client.readMessage();
//        client.sendMessage();
    }



//    private Thread listenerThread;
//
//    public Client(Socket socket) throws IOException {
//        this.socket = socket;
//        this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
//        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
//        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
//    }
//
//    public void receiveMessageFromServer(VBox messagesVB) {
//        System.out.println("Listening for server incoming messages...");
//        listenerThread = new Thread(() -> {
//            while (socket.isConnected()) {
//                try {
//                    String messageFromServer = bufferedReader.readLine();
//                    ClientController.addBubble(messageFromServer, messagesVB);
//                } catch (IOException ioe) {
//                    System.out.println("Error receiving message from server.");
//                    ioe.printStackTrace();
//                    break;
//                }
//            }
//        });
//
//        listenerThread.start();
//    }
//
//    public void sendMessageToServer(String messageToSend) {
//        try {
//            bufferedWriter.write(messageToSend);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();
//        } catch (IOException ioe) {
//            System.out.println("Error sending message to the server.");
//            ioe.printStackTrace();
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//        System.out.println("Message sent to server");
//    }
//
//    public void closeEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
//        try {
//            if (bufferedReader != null) bufferedReader.close();
//            if (bufferedWriter != null) bufferedWriter.close();
//            if (socket != null) socket.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public Thread getListenerThread() {
//        return listenerThread;
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public BufferedReader getBufferedReader() {
//        return bufferedReader;
//    }
//
//    public BufferedWriter getBufferedWriter() {
//        return bufferedWriter;
//    }
}
