package Scenes.Play;
import Cards.Card;
import Cards.Hero;
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
//
//class FieldCard {
//    FieldCard(ImageView image , Card card , int parity){
//        this.cardImage = image;
//        Bounds bounds = cardImage.localToScene(cardImage.getBoundsInLocal());
//        this.startX = (bounds.getMinX() + bounds.getMaxX())/2;
//        this.startY = (bounds.getMinY() + bounds.getMaxY())/2;
//        this.card = card;
//        this.parity = parity;
//        if(parity == 0){
//            minY = 300;
//            maxY = 500;
//        }
//        else{
//            minY = 150;
//            maxY = 350;
//        }
//    }
//
//    ImageView cardImage , cardDuplicate;
//    Card card;
//    double startX , startY;
//    int parity , minY , maxY;
//}

public class Play {

    public ImageView myHeroImage;
    public HBox opponentFieldCards , friendFieldCards , friendDeckCards , opponentDeckCards , manaBox;
    public Text manaFraction , deckCardsLeft , heroHealth;
    public AnchorPane gameField;
    private int turn = 0 , manasLeft = 1 , turnParity = 0;
    private PlayerGraphics[] contestant;
    private FieldCard selectedCard;

    @FXML
    private void initialize(){

        PlayerGraphics friend = new PlayerGraphics(Main.player.getCurrentDeck().getDeckCards() , new ArrayList<>() , friendFieldCards , friendDeckCards );
        PlayerGraphics opponent = new PlayerGraphics(Hero.getRandomHero().getDefaultHand() , new ArrayList<>() , opponentFieldCards , opponentDeckCards);

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
                card.getCardDuplicate().setTranslateX(e.getX());
                card.getCardDuplicate().setTranslateY(e.getY());
            }
        });

        card.getDeckCardImage().setOnMouseDragged(e -> {
            if(card.getCard().getMana() <= manasLeft && turnParity == card.getParity()) {
                double X = e.getSceneX();
                double Y = e.getSceneY();
                card.getCardDuplicate().setTranslateX(X - card.getCardDuplicate().getTranslateX());
                card.getCardDuplicate().setTranslateY(Y - card.getCardDuplicate().getTranslateY());
            }
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
            Cards.FieldCard fieldCard = card.getCard().getFieldCard();
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
                    if(selectedCard != null)
                        selectedCard.getCardImage().setStroke(Color.LIGHTBLUE);
                    selectedCard = fieldCard;
                }
                else if(turnParity != card.getParity() && selectedCard != null){
                    attackHandler(selectedCard , fieldCard);
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

    private void attackHandler(FieldCard attacker , FieldCard attackee){
        attackee.setHealth(String.valueOf(Integer.parseInt(attackee.getHealth().getText()) - Integer.parseInt(attacker.getAttack().getText())));

        Pane attackerDuplicate = attacker.getFieldCardPhoto();
        gameField.getChildren().add(attackerDuplicate);
        Bounds attackerBounds = attacker.getFieldCardPhoto().localToScene(attacker.getFieldCardPhoto().getBoundsInLocal());
        Bounds attackeeBounds = attacker.getFieldCardPhoto().localToScene(attackee.getFieldCardPhoto().getBoundsInLocal());

        attackerDuplicate.setLayoutX(attackerBounds.getMinX());
        attackerDuplicate.setLayoutY(attackerBounds.getMinY());
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(500));
        translateTransition.setNode(attacker.getFieldCardPhoto());

        translateTransition.setByX(attackeeBounds.getMinX() - attackerBounds.getMinX());
        translateTransition.setByY(attackeeBounds.getMinY() - attackerBounds.getMinY());

        translateTransition.play();

        int health = Integer.parseInt(attackee.getHealth().getText());
        if(health <= 0){
            contestant[attackee.getParity()].fieldCards.remove(attackee);
            contestant[attackee.getParity()].fieldCardsBox.getChildren().remove(attackee.getCardImage());
        }

        attacker.getCardImage().setStroke(Color.LIGHTBLUE);
        selectedCard = null;
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
        if(turn < 1)
            addCardToDeck(contestant[turnParity].hand.get(0) , turnParity);

        for(Cards.FieldCard fieldCard : contestant[turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.LIGHTBLUE);

        for(Cards.FieldCard fieldCard : contestant[1-turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.BLACK);
    }
}