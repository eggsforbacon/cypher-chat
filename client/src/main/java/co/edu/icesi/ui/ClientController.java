package co.edu.icesi.ui;

import co.edu.icesi.model.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private Client client;

    private boolean lightsAreOut;

    @FXML
    private BorderPane chatBorderPane = new BorderPane();

    @FXML
    private VBox messagesVB = new VBox();

    @FXML
    private ScrollPane mainSP = new ScrollPane();

    @FXML
    private TextField messageTF = new TextField();

    @FXML
    private ImageView themeIconIMV = new ImageView();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lightsAreOut = false;

        try {
            client = new Client(new Socket("localhost", 5130));
            establishConnection();
        } catch (IOException e) {
            System.out.println("Error creating the client.");
            e.printStackTrace();
        }

        messagesVB.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            mainSP.setVvalue((Double) newValue);

        });

        client.receiveMessageFromServer(messagesVB);
    }

    @FXML
    void sendFromButton(MouseEvent event) {
        sendMessage();
    }

    @FXML
    void sendFromTextField(ActionEvent event) {
        sendMessage();
    }

    private void establishConnection(){
        client.sendMessageToServer("SYN " + client.getPublicKey());
    }

    private void sendMessage() {
        String messageToSend = messageTF.getText();
        if (!messageToSend.isEmpty() && !messageToSend.isBlank()) {
            HBox messageHB = new HBox();
            messageHB.setAlignment(Pos.CENTER_RIGHT);

            Text text = new Text(messageToSend);
            TextFlow textFlow = new TextFlow(text);
            textFlow.getStyleClass().add(0, "chat__bubble");
            textFlow.getStyleClass().add(1, "chat__bubble__out");

            messageHB.getChildren().add(textFlow);
            messagesVB.getChildren().add(messageHB);

            client.sendMessageToServer(messageToSend);
            messageTF.clear();
        }
    }

    @FXML
    void switchTheme(MouseEvent event) {
        String lightThemeURL = Objects.requireNonNull(getClass().getResource("/css/chat.css")).toExternalForm();
        String darkThemeURL = Objects.requireNonNull(getClass().getResource("/css/chat-dark.css")).toExternalForm();
        chatBorderPane.getStylesheets().clear();
        chatBorderPane.getStylesheets().add(lightsAreOut ? lightThemeURL : darkThemeURL);
        themeIconIMV.setImage(new Image(String.valueOf(getClass().getResource(lightsAreOut ? "/icons/moon.png" : "/icons/sun.png"))));
        lightsAreOut = !lightsAreOut;
    }

    public static void addBubble(String messageFromServer, VBox vbox) {
        HBox messageHB = new HBox();
        messageHB.setAlignment(Pos.CENTER_LEFT);

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.getStyleClass().add(0, "chat__bubble");
        textFlow.getStyleClass().add(1, "chat__bubble__in");

        messageHB.getChildren().add(textFlow);

        Platform.runLater(() -> vbox.getChildren().add(messageHB));
    }

    public Thread getListenerThread() {
        return client.getListenerThread();
    }

    public void closeEverything() {
        client.closeEverything( client.getSocket(), client.getBufferedReader(), client.getBufferedWriter());
    }
}
