package Scenes.Shop;

import Controller.*;
import Scenes.*;
import Cards.Card;
import Logs.Log;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Shop {

    public GridPane buyGrid , sellGrid;
    public Tab buyTab , sellTab;
    public TabPane tabPane;
    public Text typeText , rarityText , classText , priceText , numberText;
    public ImageView cardImage;
    public Button sellButton , buyButton;
    public ScrollPane scrollBuy , scrollSell;

    public static boolean firstEntrance = true;
    public static String currentTab = "sell";

    @FXML
    public void buyTabAction() {
        if (buyTab.isSelected() && currentTab.equals("sell")) {
            currentTab = "buy";
            Scenes.shopScene("buy");
            Log.logger("Button_Clicked" , "BuyTab");
        }
    }

    @FXML
    public void sellTabAction() {
        if (!firstEntrance && sellTab.isSelected() && currentTab.equals("buy")) {
            currentTab = "sell";
            Scenes.shopScene("sell");
            Log.logger("Button_Clicked" , "SellTab");
        }
    }

    @FXML
    private void backButton(){
        currentTab = "sell";
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    public void showCards(GridPane gridPane , String which){

        ArrayList<Card> cards = (ArrayList<Card>) Controller.showDealableCards(which);

        int row = 0 , col = 0;

        for(Card card : cards) {
            try {
                Image image = new Image(new FileInputStream("src/Cards/CardsInfo/ShopCards/" + card.getName() + ".png"));
                ImageView cardImage = new ImageView(image);

                cardImage.setFitHeight(267.0);
                cardImage.setFitWidth(196.0);

                Button button = new Button("", cardImage);
                button.setStyle("-fx-background-color: transparent;");
                button.setOnAction(e -> Scenes.cardScene(card , image , which));
                VBox vBox = new VBox();
                HBox hBox = new HBox();
                ImageView gem = new ImageView(new Image(new FileInputStream("src/Images/gem.png")));
                gem.setFitWidth(30);
                gem.setFitHeight(30);
                Text text = new Text(String.valueOf(card.getPrice()));
                text.setFill(Color.WHITE);
                Font font = Font.font("Verdana" , FontWeight.EXTRA_BOLD , 25);
                text.setFont(font);
                hBox.getChildren().addAll(gem , text);
                vBox.getChildren().addAll(button , hBox);
                hBox.setAlignment(Pos.CENTER);

                GridPane.setConstraints(vBox, col, row);
                gridPane.getChildren().add(vBox);


                if (col == 4)
                    row ++;
                col = (col + 1) % 5;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
