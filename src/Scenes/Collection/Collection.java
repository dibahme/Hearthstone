package Scenes.Collection;

import Scenes.*;
import Cards.Card;
import Controller.*;
import Logs.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

public class Collection {

    @FXML
    public GridPane gridPane;
    @FXML
    private TextField searchText;
    public VBox decksBox;

    public Button mana0, mana1 , mana2 , mana3 , mana4 , mana5 , mana6 , mana7 , mana8 , mana9 , manaAll , search;
    public ImageView manaImage0 , manaImage1 , manaImage2 , manaImage3 , manaImage4 , manaImage5 , manaImage6,
            manaImage7 , manaImage8 , manaImage9 , manaImageAll;
    private static int mana = -1;
    private static String filter = "a" , subString = "";

    @FXML
    public void initialize(){
        Button[] manas = new Button[]{mana0 , mana1 , mana2 , mana3 , mana4 , mana5 , mana6 , mana7 , mana8 , mana9 , manaAll};
        ImageView[] manaImages = new ImageView[]{manaImage0 , manaImage1 , manaImage2 , manaImage3 , manaImage4
                , manaImage5 , manaImage6, manaImage7 , manaImage8 , manaImage9 , manaImageAll};

        searchText.setText(subString);
        for(int i = 0 ; i <= 10 ; i++) {
            int finalI = (i == 10 ? -1 : i);

            if(mana == i){
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setHue(0.8);
                manaImages[i].setEffect(colorAdjust);
            }

            manas[i].setOnAction(e ->{
                mana = finalI;

                Scenes.collectionScene();
            });
        }
    }

    @FXML
    private void searchButton(){
        subString = searchText.getText();
        Scenes.collectionScene();
        Log.logger("Button_Clicked" , "Search");
    }

    @FXML
    private void backButton(){
        mana = -1;
        filter = new String("a");
        subString = new String("");
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    @FXML
    private void allButton(){
        filter = new String("a");
        Scenes.collectionScene();
        Log.logger("Button_Clicked" , "FilterAll");
    }

    @FXML
    private void inButton(){
        filter = new String("m");
        Scenes.collectionScene();
        Log.logger("Button_Clicked" , "FilterIn");
    }

    @FXML
    private void outButton(){
        filter = new String("n");
        Scenes.collectionScene();
        Log.logger("Button_Clicked" , "FilterOut");
    }

    @FXML
    private void addButton(){
        try {
            FXMLLoader loader = new FXMLLoader(Deck.class.getResource("Deck.fxml"));
            Pane root = loader.load() ;
            Scene scene = new Scene(root);
            Stage stage = Scenes.secondStage;
            stage.setScene(scene);
            stage.setTitle("Deck");
            try {
                stage.initOwner(Scenes.currentStage);
                stage.initModality(Modality.WINDOW_MODAL);
            }catch(Exception ignored){}
            stage.show();
            Deck deckController = loader.getController();
            Deck.primaryDeck = new Deck();
            deckController.showDeck();
            Log.logger("Button_Clicked" , "AddDeck");
        }catch(Exception e){ e.printStackTrace();}
    }


    public static void updateCollection(GridPane gridPane , VBox decksBox){
        int col = 0 , row = 0;
        String lastName = "";
        ArrayList <Card> filteredCards = new ArrayList<>(Controller.showHandCards(filter));
        Comparator <Card> cm1 = Comparator.comparing(Card::getHero);
        filteredCards.sort(cm1);
        for(Card card : filteredCards){
            if((card.getMana() == mana || mana == -1) && card.getName().contains(subString)){
                try {

                    if(!lastName.equals(card.getHero())){
                        Text text = new Text("   " + card.getHero());
                        lastName = card.getHero();
                        Font font = Font.font(Font.getFontNames().get(0) , FontWeight.EXTRA_BOLD , 20);
                        text.setFill(Color.WHITE);
                        text.setFont(font);
                        if(col != 0){
                            col = 0;
                            row++;
                        }
                        gridPane.addRow(row , text);
                        row++;
                    }

                    Image image = new Image(new FileInputStream("src/Cards/CardsInfo/ShopCards/" + card.getName() + ".png"));
                    ImageView cardImage = new ImageView(image);
                    if(card.getPrice() > Main.player.getGems()){
                        ColorAdjust monochrome = new ColorAdjust();
                        monochrome.setSaturation(-1);
                        cardImage.setEffect(monochrome);
                    }

                    cardImage.setFitHeight(267.0);
                    cardImage.setFitWidth(196.0);

                    Button button = new Button("" , cardImage);
                    button.setStyle("-fx-background-color: transparent;");
                    button.setOnAction(e -> Scenes.cardScene(card , image , "collection"));
                    GridPane.setConstraints(button, col, row);
                    gridPane.getChildren().add(button);

                    if(col == 3)
                        row++;
                    col = (col + 1) % 4;

                }catch(Exception e){ e.printStackTrace(); }

            }
        }

        decksBox.getChildren().add(Scenes.txt);
        decksBox.setFillWidth(false);
        for(Deck deck : Main.player.getAllDecks()){
            try {
                Button button = new Button("Name: " + deck.getName() + "\nHero: " + deck.getHero());
                button.setMaxWidth(Double.MAX_VALUE);
                button.setGraphic(new ImageView(new Image(new FileInputStream("src/Images/" + deck.getHero() + "Background.png"))));
                button.setContentDisplay(ContentDisplay.CENTER);
                button.setAlignment(Pos.CENTER_LEFT);
                button.setTextFill(Color.WHITE);
                button.setFont(Font.font(Font.getFontNames().get(0) , FontWeight.EXTRA_BOLD , 20));
                button.setStyle("-fx-background-color: transparent;");
                button.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(Deck.class.getResource("Deck.fxml"));
                        Pane root = loader.load();
                        Scene scene = new Scene(root);
                        Stage stage = Scenes.secondStage;
                        stage.setScene(scene);
                        stage.setTitle("Deck");
                        try {
                            stage.initOwner(Scenes.currentStage);
                            stage.initModality(Modality.WINDOW_MODAL);
                        }catch(Exception ignored){}
                        stage.show();

                        Deck deckController = loader.getController();
                        deckController.setAttributes(deck);
                        deckController.showDeck();
                    }catch(Exception ignored){ignored.printStackTrace();}
                });
                decksBox.getChildren().add(button);
            }catch(Exception ignored){ignored.printStackTrace();}

        }
    }
}
