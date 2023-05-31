package co.edu.icesi.model;

//import co.edu.icesi.ui.ClientController;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;


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
                bufferedWriter.write(username + ": " + messageToSend);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;

                while (socket.isConnected()){
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
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

    public static void main(String[] args) throws IOException{
       Scanner scanner = new Scanner(System.in);
       System.out.println("Enter your username");
       String username = scanner.nextLine();
       Socket socket = new Socket("localhost", 1234);
       Client client = new Client(socket,username);
       client.readMessage();
       client.sendMessage();
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
