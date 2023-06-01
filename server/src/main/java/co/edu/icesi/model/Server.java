package co.edu.icesi.model;

import co.edu.icesi.ui.ServerController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Thread listenerThread;
    private DiffieHellman diffieHellman;

    public Server(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.socket = serverSocket.accept();
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.diffieHellman = new DiffieHellman();

        diffieHellman.generateKeys();

        System.out.println("Server is up and running at " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
    }

    public void receiveMessageFromClient(VBox messagesVB) {
        listenerThread = new Thread(() -> {
            System.out.println("Listening for client incoming messages...");
            while (socket.isConnected()) {
                try {
                    String messageFromClient = bufferedReader.readLine();
                    if(messageFromClient.contains("SYN ")){
                        diffieHellman.receivePublicKeyFrom(messageFromClient.replaceAll("SYN ", ""));
                        establishConnection();
                    }
                    ServerController.addBubble(messageFromClient, messagesVB);
                } catch (IOException ioe) {
                    System.out.println("Error receiving message from client.");
                    closeEverything(serverSocket, socket, bufferedReader, bufferedWriter);
                    ioe.printStackTrace();
                    break;
                }
            }
        });

        listenerThread.start();
    }

    private void establishConnection(){
        sendMessageToClient("SYN/ACK " + Base64.getEncoder().encodeToString(diffieHellman.getPublicKey().getEncoded()));
    }
    public void sendMessageToClient(String messageToSend) {
        try {
            String encryptedMsg = messageToSend;
            if(!messageToSend.contains("SYN/ACK ")){
                encryptedMsg = new String(diffieHellman.encryptMessage(messageToSend));
            }
            bufferedWriter.write(encryptedMsg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ioe) {
            System.out.println("Error sending message to the client.");
            ioe.printStackTrace();
            closeEverything(serverSocket, socket, bufferedReader, bufferedWriter);
        }
        System.out.println("Message sent to client");
    }

    public void closeEverything (ServerSocket serverSocket, Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Thread getListenerThread() {
        return listenerThread;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
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
