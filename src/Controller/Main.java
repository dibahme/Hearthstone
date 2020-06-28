package Controller;

import Player.Player;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        Controller.commandHandler();
    }

    public static Player player = new Player();
    public static void main(String[] args) {
        launch(args);
    }
}
