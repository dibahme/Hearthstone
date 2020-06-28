package Scenes.Status;
import Scenes.*;
import Controller.Main;
import Logs.Log;
import Scenes.Collection.Deck;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Status {
    @FXML
    private VBox vBox;

    @FXML
    private void backButton(){
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    private Text buildText(String inside , String fontName , int size , Color col){
        Text txt = new Text(inside);
        txt.setFont(Font.font(fontName , FontWeight.EXTRA_BOLD , size));
        txt.setFill(col);
        return txt;
    }

    public void showStatus(){
        Collections.sort(Main.player.getAllDecks() , new Deck.DeckComparator());
        ArrayList<Deck> playerDecks = Main.player.getAllDecks();
        for(int i = 0 ; i < Math.min(10 , playerDecks.size()); i++){
            Deck deck = playerDecks.get(i);
            HBox hBox = new HBox();
            hBox.setSpacing(100);
            hBox.setAlignment(Pos.CENTER);
            hBox.setMinHeight(300);
            try {
                hBox.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("src/Images/Status.png")),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT)));
            }catch(Exception ignored){ignored.printStackTrace();}
            try {
                ImageView image = new ImageView(new Image(new FileInputStream("src/Images/" + deck.getHero() + "Icon.png")));
                image.setFitHeight(200);
                image.setFitWidth(200);
                hBox.getChildren().add(image);
            }catch(Exception ignored){ignored.printStackTrace();}

            Text text1 = buildText("#" + (i+1) + "\nName: " + deck.getName() + "\nHero: " + deck.getHero() ,
                    "Copperplate Gothic Bold" , 24 , Color.LIGHTSEAGREEN);
            Text text2 = buildText("Wins : 0\nGames: 0\nWinning Probability: 0\nMost Used Card: None\nMean Mana Cost: " + deck.getMeanCost() ,
                    "Copperplate Gothic Light" , 15 , Color.GOLDENROD);

            hBox.getChildren().addAll(text1 , text2);

            vBox.getChildren().add(hBox);
        }
    }
}
