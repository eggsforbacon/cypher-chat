//package co.edu.icesi.ui;
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.net.URL;
//
//public class Main extends Application {
//
//    ClientController gui;
//
//    public Main() {
//        gui = new ClientController();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        URL chatURL = getClass().getResource("/fxml/chat.fxml");
//        FXMLLoader fxmlLoader = new FXMLLoader(chatURL);
//        fxmlLoader.setController(gui);
//        Parent root = fxmlLoader.load();
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    @Override
//    public void stop() throws Exception {
//        super.stop();
//        if (gui.getListenerThread() != null) {
//            gui.getListenerThread().interrupt();
//            try {
//                gui.getListenerThread().join();
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
//        }
//        gui.closeEverything();
//        Platform.exit();
//    }
//}
