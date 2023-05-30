package co.edu.icesi.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    ClientController gui;

    public Main() {
        gui = new ClientController();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL chatURL = getClass().getResource("/fxml/chat.fxml");
        System.out.println(chatURL);
        FXMLLoader fxmlLoader = new FXMLLoader(chatURL);
        fxmlLoader.setController(gui);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
