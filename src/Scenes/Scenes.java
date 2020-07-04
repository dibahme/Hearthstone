package Scenes;

import Scenes.Collection.*;
import Controller.*;
import Cards.Card;
import Logs.Log;
import Scenes.Collection.Collection;
import static Scenes.Play.InfoPassiveHandler.InfoPassive;
import Scenes.Play.Play;
import Scenes.Shop.Shop;
import Scenes.Status.Status;
import com.google.gson.Gson;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Scenes {

    public static Stage currentStage = new Stage();
    public static Stage secondStage = new Stage();
    public static TextField txt = new TextField();

    public static void alertBox(Stage father , String message){
        Stage stage = new Stage();
        VBox vBox = new VBox();
        Label label = new Label(message);
        Button button = new Button("OK");

        button.setOnAction(e -> stage.close());
        label.setStyle("-fx-font-weight : bold; -fx-text-fill: White; -fx-font-size: 20px;");
        button.setStyle("-fx-background-color : rgba(25 , 25 , 112 , 0.3); -fx-font-weight : bold; -fx-text-fill: White; -fx-font-size: 16px;");
        vBox.getChildren().addAll(label , button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        try {
            vBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/Images/AlertBox.png")),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));
        }catch(Exception ignored){ignored.printStackTrace();}
        Scene scene = new Scene(vBox , 600 , 300);
        stage.setTitle("Alert");
        stage.setScene(scene);
        stage.initOwner(father);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    public static void notificationBox(String message , int difX , int difY){
        txt.setText(message);
        txt.setStyle("-fx-text-fill: white; -fx-background-color: rgba(50, 0, 0 , 0.5) ; -fx-font-weight : bold; -fx-font-size : 20px;");
        txt.setVisible(true);
        txt.setTranslateX(difX);
        txt.setTranslateY(difY);
        PauseTransition wait = new PauseTransition(Duration.seconds(2));
        wait.setOnFinished(e -> {
            txt.setText(null);
            txt.setVisible(false);
        });

        wait.play();
    }

    public static void menuScene(){
       Stage stage = new Stage();
        VBox vBox = new VBox();
        Button play = new Button("Play"),
                collections = new Button("Collections"),
                shop = new Button("Shop"),
                status = new Button("Status"),
                setting = new Button("Setting"),
                exit = new Button("Exit");
        play.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25 , 25 , 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        play.setMinWidth(300);
        collections.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25 , 25 , 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        collections.setMinWidth(300);
        shop.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25 , 25 , 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        shop.setMinWidth(300);
        status.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25 , 25 , 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        status.setMinWidth(300);
        setting.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25, 25, 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        setting.setMinWidth(300);
        exit.setStyle("-fx-text-fill: burlywood;  -fx-background-color: rgb(25 , 25 , 112); -fx-font-weight : bold; -fx-font-size : 25px;");
        exit.setMinWidth(300);
        collections.setOnAction(e -> {
            stage.close();
            collectionScene();
            Log.logger("Click_button" , "Collections");
        });
        shop.setOnAction(e->{
            stage.close();
            shopScene("sell");
            Log.logger("Click_button" , "Shop");
        });
        status.setOnAction(e -> {
            stage.close();
            statusScene();
            Log.logger("Click_button" , "Status");
        });
        play.setOnAction(e -> {
            stage.close();
            if(Main.player.getCurrentDeck() == null)
                collectionScene();
            else
                playScene();
            Log.logger("Click_button" , "Play");
        });
        exit.setOnAction(e -> {
            File file = new File("src/Player/playersInfo/" + Main.player.getUsername() + ".json");
            try {
                FileWriter fileWriter = new FileWriter(file , false);
                Gson gson = new Gson();
                fileWriter.write(gson.toJson(Main.player));
                fileWriter.close();
            } catch (Exception ignored) { ignored.printStackTrace(); }
            Log.logger("Exit" , "");
            System.exit(0);
        });

        vBox.setFillWidth(false);
        vBox.getChildren().addAll(play , collections , shop , status , setting , exit , txt);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        try {
            vBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/Images/Menu.png")),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));
        }catch(Exception ignored){ignored.printStackTrace();}



        Scene scene = new Scene(vBox , 1280 , 720);
        currentStage.setScene(scene);
        currentStage.setFullScreen(true);
        currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    private static void statusScene() {
        FXMLLoader loader = new FXMLLoader(Status.class.getResource("Status.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            Status status = loader.getController();
            status.showStatus();
        }catch(Exception ignored){ignored.printStackTrace();}
    }

    public static void enterScene(){
        GridPane gridPane = new GridPane();
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Label userLabel = new Label("Username: "), passLabel = new Label("Password: ");
        Button login = new Button("Login"), register = new Button("Register");
        Stage stage = new Stage();

        userLabel.setFont(Font.font(Font.getFontNames().get(0) , FontWeight.EXTRA_BOLD , 16));
        passLabel.setFont(Font.font(Font.getFontNames().get(0) , FontWeight.EXTRA_BOLD , 16));
        userLabel.setTextFill(Color.LIGHTSALMON);
        passLabel.setTextFill(Color.LIGHTSALMON);

        userField.setStyle("-fx-text-fill: white;  -fx-background-color: rgba(255, 255, 255, .3); -fx-font-weight : bold;");
        passField.setStyle("-fx-text-fill: white;  -fx-background-color: rgba(255, 255, 255, .3); -fx-font-weight : bold;");
        login.setStyle("-fx-text-fill: burlywood; -fx-background-color: rgba(255, 255, 255, .15); -fx-font-weight : bold;");
        register.setStyle("-fx-text-fill: burlywood; -fx-background-color: rgba(255, 255, 255, .15); -fx-font-weight : bold;");
        try {
            gridPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/Images/Login.png")),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));
        }catch(Exception ignored){ignored.printStackTrace();}
        login.setOnAction(e -> {
            if(Controller.enter("y" , userField.getText() , passField.getText())) {
                stage.close();
                menuScene();
            }
            else{
                userField.clear();
                passField.clear();
            }
        });

        register.setOnAction(e -> {
            if(Controller.enter("n" , userField.getText() , passField.getText())) {
                stage.close();
                menuScene();
            }
            else{
                userField.clear();
                passField.clear();
            }
        });

        GridPane.setConstraints(userLabel, 0, 0);
        GridPane.setConstraints(passLabel, 0, 1);
        GridPane.setConstraints(userField, 1, 0);
        GridPane.setConstraints(passField, 1, 1);
        GridPane.setConstraints(login, 0, 2);
        GridPane.setConstraints(register, 1, 2);

        gridPane.getChildren().addAll(userLabel, userField, passLabel, passField, login, register);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        Scene scene = new Scene(gridPane, 1280 , 720);

        currentStage.setFullScreen(true);
        currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        currentStage.setScene(scene);
        currentStage.setTitle("Hearthstone");
        currentStage.show();
    }

    public static void collectionScene(){
        try {
            FXMLLoader loader = new FXMLLoader(Collection.class.getResource("Collection.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root , 1280, 720);
            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            Collection collection = loader.getController();
            Collection.updateCollection(collection.gridPane , collection.decksBox);

        }catch(Exception e){e.printStackTrace();}
    }

    public static void shopScene(String which){
        try {
            Shop.firstEntrance = true;
            FXMLLoader loader = new FXMLLoader(Shop.class.getResource("Shop.fxml"));
            Parent root = loader.load();
            Shop.firstEntrance = false;
            Scene scene = new Scene(root , 1280, 720);
            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            Shop shop = loader.getController();

            if(which.equals("buy"))
                shop.tabPane.getSelectionModel().select(shop.buyTab);
            shop.showCards((which.equals("sell") ? shop.sellGrid : shop.buyGrid) , which);
        }catch(Exception e){e.printStackTrace();}
    }

    public static void cardScene(Card card , Image image , String which){
        Log.logger("Button_Clicked" , "CardDescription");
        try {
            FXMLLoader loader = new FXMLLoader(Shop.class.getResource("Card.fxml"));
            Parent root = loader.load();
            Shop window = loader.getController();
            window.typeText.setText(card.getType());
            window.priceText.setText(String.valueOf(card.getPrice()));
            window.rarityText.setText(card.getRarity());
            window.classText.setText(card.getHero());
            window.cardImage.setImage(image);
            window.sellButton.setDisable(true);

            int num = 0;

            for(Card playerCard : Main.player.getHand())
                if(playerCard.getName().equals(card.getName()))
                    num++;

            if(num != 0)
                window.sellButton.setDisable(false);

            for(Deck deck : Main.player.getAllDecks()){
                int deckNum = 0;
                for(Card deckCard : deck.getDeckCards()){
                    if(deckCard.getName().equals(card.getName()))
                        deckNum++;
                }

                if(deckNum == num)
                    window.sellButton.setDisable(true);
            }

            if(Main.player.getGems() < card.getPrice())
                window.buyButton.setDisable(true);

            window.numberText.setText(String.valueOf(num));

            Scene scene = new Scene(root , 800  , 600);
            secondStage.setTitle("Card Info");
            secondStage.setScene(scene);
            try {
                secondStage.initOwner(Scenes.currentStage);
                secondStage.initModality(Modality.WINDOW_MODAL);
            }catch(Exception ignored){}
            secondStage.show();
            window.sellButton.setOnAction(e ->{
                for(Card playerCard : Main.player.getHand()){
                    if(playerCard.getName().equals(card.getName())){
                        Main.player.getHand().remove(playerCard);
                        break;
                    }
                }

                Main.player.setGems(Main.player.getGems() + card.getPrice());
                cardScene(card , image , which);

                if(which.equals("collection"))  Scenes.collectionScene();
                else    Scenes.shopScene(which);
                Log.logger("Sell_Card" , card.getName());
            });
            window.buyButton.setOnAction(e -> {
                Main.player.getHand().add(card);
                Main.player.setGems(Main.player.getGems() - card.getPrice());
                cardScene(card , image , which);

                if(which.equals("collection"))  Scenes.collectionScene();
                else    Scenes.shopScene(which);
                Log.logger("Buy_Card" , card.getName());
            });

        }catch(Exception e){e.printStackTrace();}
    }

    public static void playScene(){
        FXMLLoader loader = new FXMLLoader(Play.class.getResource("Play.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

            Play play = loader.getController();
            play.getMyHeroImage().setImage(new Image(new FileInputStream("src/Images/" + Main.player.getCurrentDeck().getHero() + "Icon.png")));

            VBox vBox = new VBox();
            vBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/Images/InfoPassive.png")),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));

            ArrayList<InfoPassive> infoPassiveList = new ArrayList<InfoPassive>(Arrays.asList(InfoPassive.values()));
            Collections.shuffle(infoPassiveList);

            for (int i=0; i<3; i++) {
                InfoPassive infoPassive = infoPassiveList.get(i);
                Button button = new Button(infoPassive.name());
                button.setStyle("-fx-text-fill: lightseagreen;  -fx-background-color: rgba(255, 255, 255, .15);" +
                        " -fx-font-size : 25px; -fx-font-weight : bold;");
                button.setOnAction(e ->{
                    Main.player.setInfoPassive(infoPassive);
                    secondStage.close();
                    Log.logger("Choose_Info_Passive" , infoPassive.name());
                });

                vBox.getChildren().add(button);
            }

            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);
            scene = new Scene(vBox, 1280 , 720);

            secondStage.setScene(scene);
            secondStage.setAlwaysOnTop(true);
            try {
                secondStage.initOwner(Scenes.currentStage);
                secondStage.initModality(Modality.WINDOW_MODAL);
            }catch(Exception ignored){}
            secondStage.show();
        }catch(Exception ignored){ignored.printStackTrace();}
    }
}
