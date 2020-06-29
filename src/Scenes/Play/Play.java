package Scenes.Play;
import Cards.Card;
import Cards.Hero;
import Controller.GameOperations;
import Controller.Main;
import Logs.Log;
import Scenes.Scenes;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Cards.FieldCard;
import javafx.util.Duration;

import static Scenes.Scenes.notificationBox;

public class Play {

    @FXML
    private ImageView myHeroImage;
    @FXML
    private ImageView opponentHeroImage;
    @FXML
    private HBox opponentFieldCards;
    @FXML
    private HBox friendFieldCards;
    @FXML
    private HBox friendDeckCards;
    @FXML
    private HBox opponentDeckCards;
    @FXML
    private HBox manaBox;
    @FXML
    private Text manaFraction;
    @FXML
    private Text deckCardsLeft;
    @FXML
    private Text heroHealth;
    @FXML
    private Text opponentHealth;
    @FXML
    private AnchorPane gameField;


    private int turn = 0 , manasLeft = 1 , turnParity = 0;
    private PlayerGraphics[] contestant;
    private FieldCard selectedCard;

    @FXML
    private void initialize(){

        Hero opponentHero = Hero.getRandomHero();
        setOpponentHero(opponentHero);

        PlayerGraphics friend = new PlayerGraphics(Main.player.getCurrentDeck().getDeckCards() , new ArrayList<>() , friendFieldCards , friendDeckCards );
        PlayerGraphics opponent = new PlayerGraphics(opponentHero.getDefaultHand() , new ArrayList<>() , opponentFieldCards , opponentDeckCards);

        contestant = new PlayerGraphics[] {friend , opponent};

        for(int i = 0 ; i < 2; i++) {
            ArrayList <Card> hand = contestant[i].hand;
            Collections.shuffle(hand);

            int sz = hand.size();
            for(int j = 0 ; j < Math.min(sz , 3) ; j++)
                addCardToDeck(contestant[i].hand.get(0) , i);
        }

        handleManasLeft(turn + 1);
    }

    private void addCardToDeck(Card card , int parity){
        try {
            FieldCard fieldCard = FieldCard.getCard(card).setCardAttributes(card , parity);

            Card.setCardSize(fieldCard.getDeckCardImage() , 91 , 75);
            contestant[parity].deckCardsBox.getChildren().add(fieldCard.getDeckCardImage());
            contestant[parity].hand.remove(card);
            contestant[parity].deck.add(card);
            deckCardPreparation(fieldCard);

        }catch(Exception ignored){ignored.printStackTrace();}
    }

    private void deckCardPreparation(FieldCard card){
        card.getDeckCardImage().setOnMousePressed(e -> {
            if(card.getCard().getMana() <= manasLeft && turnParity == card.getParity()) {
                card.setCardDuplicate(new ImageView(card.getDeckCardImage().getImage()));
                Card.setCardSize(card.getCardDuplicate(), 180, 150);

                card.getCardDuplicate().setOpacity(0.8);
                gameField.getChildren().add(card.getCardDuplicate());
                card.getCardDuplicate().setLayoutX(e.getSceneX());
                card.getCardDuplicate().setLayoutY(e.getSceneY());
            }
        });

        card.getDeckCardImage().setOnMouseDragged(e -> {
            if(card.getCard().getMana() <= manasLeft && turnParity == card.getParity())
                card.getCardDuplicate().relocate(e.getSceneX() , e.getSceneY());
        });

        card.getDeckCardImage().setOnMouseReleased(e -> {
            double Y = e.getSceneY();
            if(card.getCard().getMana() <= manasLeft && card.getMinY() <= Y && Y <= card.getMaxY() && turnParity == card.getParity()){
                if(card.getCard().getType().equals("Spell") || contestant[card.getParity()].fieldCardsBox.getChildren().size() < 7){
                    if(card.getCard().getType().equals("Minion"))
                        handleFieldPlace(card , e.getX());

                    handlePlayedCardOperation(card);
                }
            }

            gameField.getChildren().remove(card.getCardDuplicate());
        });
    }

    private void handleFieldPlace(FieldCard card , double loc){
        try {
            Cards.FieldCard fieldCard = card;
            HBox fieldCardsBox = contestant[turnParity].fieldCardsBox;
            List<Node> myFieldCards = fieldCardsBox.getChildren();

            for (int i = 0; i < myFieldCards.size(); i++) {
                Node node = myFieldCards.get(i);
                if (loc < node.getLayoutX() + node.getTranslateX()) {
                    fieldCardsBox.getChildren().add(i, fieldCard.getFieldCardPhoto());
                    return;
                }
            }

            fieldCardsBox.getChildren().add(fieldCard.getFieldCardPhoto());
            contestant[turnParity].fieldCards.add(fieldCard);

            fieldCard.getCardImage().setOnMouseClicked(e -> {
                if(turnParity == card.getParity() && card.getSummonedTurn() != turn){
                    fieldCard.getCardImage().setStroke(Color.RED);
                    if(selectedCard != null) {
                        selectedCard.getCardImage().setStroke(Color.LIGHTBLUE);
                        selectedCard = null;
                    }
                    selectedCard = fieldCard;
                }
                else if(turnParity != card.getParity() && selectedCard != null){
                    GameOperations.getInstance().attackHandler(selectedCard , fieldCard , this);
                }
            });

        }catch(Exception ignored){ignored.printStackTrace();}
    }

    private void handleManasLeft(int number){
        manasLeft = number;

        for(int i = turn/2+1 ; i < 10 ; i++)
            manaBox.getChildren().get(i).setVisible(false);

        for(int i = 0 ; i <= turn/2 ; i++){
            ImageView image = (ImageView) manaBox.getChildren().get(i);
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(0);
            image.setEffect(monochrome);
            image.setVisible(true);
        }

        for(int i = number ; i <= turn/2 ; i++){
            ImageView image = (ImageView) manaBox.getChildren().get(i);
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(-1);
            image.setEffect(monochrome);
        }

        manaFraction.setText(manasLeft + "/" + Math.min(10 , turn/2 + 1));
    }

    private void handlePlayedCardOperation(FieldCard card){
        Card deckCard = card.getCard();
        Log.logger("Card_Played_By_" + (card.getParity() == 0 ? "Friend" : "Opponent") , deckCard.getName());
        notificationBox(deckCard.getName() + " Is Played" , 400 , 100);
        contestant[card.getParity()].deck.remove(deckCard);
        card.setSummonedTurn(turn);
        handleManasLeft(manasLeft - deckCard.getMana());
        contestant[turnParity].deckCardsBox.getChildren().remove(card.getDeckCardImage());
    }

    private void setOpponentHero(Hero hero){
        try {
            opponentHeroImage.setImage(new Image(new FileInputStream("src/Images/" + hero.getName() + "Icon.png")));
        }catch(Exception ignored){ignored.printStackTrace();}
        opponentHealth.setText(String.valueOf(hero.getHealth()));
    }

    @FXML
    private void backButton() {
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    @FXML
    private void endTurnAction(){
        turn++;
        turnParity = 1 - turnParity;
        handleManasLeft(Math.min(10 , turn/2 + 1));
        if(turn > 1)
            addCardToDeck(contestant[turnParity].hand.get(0) , turnParity);

        for(Cards.FieldCard fieldCard : contestant[turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.LIGHTBLUE);

        for(Cards.FieldCard fieldCard : contestant[1-turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.BLACK);
    }

    public AnchorPane getGameField() { return gameField; }
    public void setGameField(AnchorPane gameField) { this.gameField = gameField; }
    public FieldCard getSelectedCard() { return selectedCard; }
    public void setSelectedCard(FieldCard selectedCard) { this.selectedCard = selectedCard; }
    public PlayerGraphics[] getContestant() { return contestant; }
    public void setContestant(PlayerGraphics[] contestant) { this.contestant = contestant; }
    public ImageView getMyHeroImage() { return myHeroImage; }
}