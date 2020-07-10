package Scenes.Play;

import Cards.*;
import Controller.GameOperations;
import Controller.Main;
import Logs.Log;
import Scenes.Scenes;
import com.google.gson.Gson;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static Controller.GameOperations.gameState;
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
    private Text myHeroHealth;
    @FXML
    private Text opponentHeroHealth;
    @FXML
    private AnchorPane gameField;
    @FXML
    private Circle myHeroPowerImage;
    @FXML
    private Circle opponentHeroPowerImage;
    @FXML
    private Circle myWeaponImage;
    @FXML
    private Circle opponentWeaponImage;
    @FXML
    private Text myWeaponDurability;
    @FXML
    private Text opponentWeaponDurability;
    @FXML
    private Text myWeaponAttack;
    @FXML
    private Text opponentWeaponAttack;

    private int turn = 0 , manasLeft = 1 , turnParity = 0 , manas = 1 , offCard = 0;
    private PlayerGraphics[] contestant;
    private FieldCard selectedCard;
    private boolean configExists = false;
    private ArrayList <FieldCard> usedMinions = new ArrayList<>();
    private Hero myHero , opponentHero;

    @FXML
    private void initialize(){

        File file = new File("src/Cards/Config/config.json");
        PlayerGraphics friend = new PlayerGraphics() , opponent = new PlayerGraphics();
        Hero opponentHero = Hero.getRandomHero();
        opponentHeroImage.setImage(opponentHero.getImage().getImage());
        setHeroAttributes(opponentHero , opponentHeroImage , opponentHeroHealth , 1);
        Weapon friendWeapon = Weapon.getBasicWeapon(myWeaponDurability , myWeaponAttack , myWeaponImage) ,
                opponentWeapon = Weapon.getBasicWeapon(opponentWeaponDurability , opponentWeaponAttack , opponentWeaponImage);
        if(file.exists()){
            Gson gson = new Gson();
            try {
                Scanner sc = new Scanner(file);
                StringBuilder stringBuilder = new StringBuilder();
                while(sc.hasNext())
                    stringBuilder.append(sc.nextLine());

                myHero = Hero.getRandomHero();
                ConfigReader configReader = gson.fromJson(stringBuilder.toString() , ConfigReader.class);
                friend = new PlayerGraphics(ConfigReader.getCards(configReader.getFriend())  , friendFieldCards , friendDeckCards , myHero , myHeroPowerImage , friendWeapon);
                opponent = new PlayerGraphics(ConfigReader.getCards(configReader.getEnemy()) , opponentFieldCards , opponentDeckCards , opponentHero , opponentHeroPowerImage , opponentWeapon);
                configExists = true;
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }
        else{
            myHero = new Hero(Main.player.getCurrentDeck().getHero());
            friend = new PlayerGraphics(Main.player.getCurrentDeck().getDeckCards() , friendFieldCards , friendDeckCards , myHero, myHeroPowerImage , friendWeapon);
            opponent = new PlayerGraphics(opponentHero.getDefaultHand() , opponentFieldCards , opponentDeckCards , opponentHero, opponentHeroPowerImage , opponentWeapon);
        }

        setHeroAttributes(myHero , myHeroImage , myHeroHealth , 0);
        contestant = new PlayerGraphics[] {friend , opponent};

        for(int i = 0 ; i < 2; i++) {
            ArrayList <Card> hand = contestant[i].hand;

            if(!configExists)
                Collections.shuffle(hand);

            int sz = hand.size();
            for(int j = 0 ; j < Math.min(sz , 3) ; j++)
                addCardToDeck(contestant[i].hand.get(0) , i);
        }

        handleManasLeft(manas);
    }

    public FieldCard addCardToDeck(Card card , int parity){
        try {
            FieldCard fieldCard = FieldCard.getCard(card).setCardAttributes(card , parity);

            Card.setCardSize(fieldCard.getDeckCardImage() , 91 , 75);
            contestant[parity].deckCardsBox.getChildren().add(fieldCard.getDeckCardImage());
            contestant[parity].hand.remove(card);
            contestant[parity].deck.add(card);
            deckCardPreparation(fieldCard);

            return fieldCard;
        }catch(Exception ignored){ignored.printStackTrace();}
        return new FieldCard();
    }

    private void deckCardPreparation(FieldCard card){
        card.getDeckCardImage().setOnMousePressed(e -> {
            if(card.getCard().getMana() + offCard <= manasLeft && turnParity == card.getParity()) {
                card.setCardDuplicate(new ImageView(card.getDeckCardImage().getImage()));
                Card.setCardSize(card.getCardDuplicate(), 180, 150);

                card.getCardDuplicate().setOpacity(0.8);
                gameField.getChildren().add(card.getCardDuplicate());
                card.getCardDuplicate().setLayoutX(e.getSceneX());
                card.getCardDuplicate().setLayoutY(e.getSceneY());
            }
        });

        card.getDeckCardImage().setOnMouseDragged(e -> {
            if(card.getCard().getMana() <= manasLeft + offCard && turnParity == card.getParity())
                card.getCardDuplicate().relocate(e.getSceneX() , e.getSceneY());
        });

        card.getDeckCardImage().setOnMouseReleased(e -> {
            double Y = e.getSceneY();
            if(card.getCard().getMana() <= manasLeft + offCard && card.getMinY() <= Y && Y <= card.getMaxY() && turnParity == card.getParity()){
                if(card.getCard() instanceof Spell || contestant[card.getParity()].fieldCardsBox.getChildren().size() < 7){
                    if (Minion.class.equals(card.getCard().getClass()))
                        handleFieldPlace(card, e.getX());
                    else if (Weapon.class.equals(card.getCard().getClass()))
                        handleWeaponCard(card);

                    handlePlayedCardOperation(card);
                }
            }

            gameField.getChildren().remove(card.getCardDuplicate());
        });
    }

    private void handleWeaponCard(FieldCard card){

        Weapon weapon = new Weapon(card.getCard().getName());
        weapon.setWeaponImage(contestant[card.getParity()].getWeapon().getWeaponImage());
        weapon.setDurabilityText(contestant[card.getParity()].getWeapon().getDurabilityText());
        weapon.setAttackText(contestant[card.getParity()].getWeapon().getAttackText());
        weapon.startPlaying(card);

        weapon.getWeaponImage().setOnMouseClicked(e -> {
            if(turnParity == card.getParity())
                selectedCard = card;
        });
    }

    public void handleFieldPlace(FieldCard card , double loc){
        try {
            Cards.FieldCard fieldCard = card;
            HBox fieldCardsBox = contestant[turnParity].fieldCardsBox;
            List<Node> myFieldCards = fieldCardsBox.getChildren();

            boolean flag = false;

            for (int i = 0; i < myFieldCards.size(); i++) {
                Node node = myFieldCards.get(i);
                if (loc < node.getLayoutX() + node.getTranslateX() && !flag) {
                    fieldCardsBox.getChildren().add(i, fieldCard.getFieldCardPhoto());
                    contestant[turnParity].fieldCards.add(fieldCard);
                    flag = true;
                }
            }

            if(!flag) {
                fieldCardsBox.getChildren().add(fieldCard.getFieldCardPhoto());
                contestant[turnParity].fieldCards.add(fieldCard);
            }

            fieldCard.getFieldCardPhoto().setOnMouseClicked(e -> {
                if(turnParity == card.getParity() && card.getSummonedTurn() != turn && !usedMinions.contains(fieldCard)){
                    fieldCard.getCardImage().setStroke(Color.RED);
                    if(selectedCard != null) {
                        selectedCard.getCardImage().setStroke(Color.LIGHTBLUE);
                        selectedCard = null;
                    }
                    selectedCard = fieldCard;
                }
                else if(turnParity != card.getParity() && selectedCard != null)
                    GameOperations.getInstance().attackHandler(selectedCard , fieldCard , this);
            });
        }catch(Exception ignored){ignored.printStackTrace();}
    }

    public void handleManasLeft(int number){
        manasLeft = number;

        for(int i = manas ; i < 10 ; i++)
            manaBox.getChildren().get(i).setVisible(false);

        for(int i = 0 ; i < manas ; i++){
            ImageView image = (ImageView) manaBox.getChildren().get(i);
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(0);
            image.setEffect(monochrome);
            image.setVisible(true);
        }

        for(int i = number ; i < manas ; i++){
            ImageView image = (ImageView) manaBox.getChildren().get(i);
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(-1);
            image.setEffect(monochrome);
        }

        manaFraction.setText(manasLeft + "/" + Math.min(10 , manas));
    }

    private void handlePlayedCardOperation(FieldCard card){
        Card deckCard = card.getCard();
        Log.logger("Card_Played_By_" + (card.getParity() == 0 ? "Friend" : "Opponent") , deckCard.getName());
        notificationBox(deckCard.getName() + " Is Played" , 400 , 100);
        contestant[card.getParity()].deck.remove(deckCard);
        card.setSummonedTurn(turn);
        handleManasLeft(manasLeft - deckCard.getMana() - offCard);
        contestant[turnParity].deckCardsBox.getChildren().remove(card.getDeckCardImage());

        for(FieldCard targetCard : contestant[turnParity].fieldCards)
            for(CardAbility cardAbility : targetCard.getCard().getCardAbilities())
                cardAbility.doAction(targetCard , this , gameState.SUMMON_CARD , card);
        if(!(card.getCard() instanceof Minion))
            for(CardAbility cardAbility : card.getCard().getCardAbilities())
                cardAbility.doAction(card , this , gameState.SUMMON_CARD , card);
    }

    private void setHeroAttributes(Hero hero , ImageView image , Text health , int parity){
        try {
            hero.setHealthText(health);
            image.setOnMouseClicked(e -> {
                if(turnParity != parity && selectedCard != null){
                    FieldCard fieldCard = selectedCard;

                    GameOperations.getInstance().transitionAction(selectedCard , opponentHeroImage , this);
                    GameOperations.getInstance().changeHealth(fieldCard.getAttack() , opponentHeroHealth);
                    if(fieldCard.getCard() instanceof Weapon) {
                        Weapon weapon = contestant[fieldCard.getParity()].getWeapon();
                        GameOperations.getInstance().changeHealth(new Text("1"), weapon.getDurabilityText());
                        GameOperations.getInstance().checkRemove(fieldCard , weapon.getDurabilityText() , this);
                    }
                }
            });

        }catch(Exception ignored){ignored.printStackTrace();}
    }

    @FXML
    private void backButton() {
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    public void drawCard(int parity){
        if(!contestant[parity].hand.isEmpty()){
            FieldCard card = addCardToDeck(contestant[parity].hand.get(0) , parity);
            for(FieldCard fieldCard : contestant[parity].fieldCards)
                for(CardAbility cardAbility : fieldCard.getCard().getCardAbilities())
                    cardAbility.doAction(fieldCard , this , gameState.DRAW_CARD , card);
        }
    }

    @FXML
    private void endTurnAction(){
        for(FieldCard fieldCard : contestant[turnParity].fieldCards)
            for(CardAbility cardAbility : fieldCard.getCard().getCardAbilities())
                cardAbility.doAction(fieldCard , this , gameState.END_TURN , null);

        turn++;
        turnParity = 1 - turnParity;
        manas = Math.min(turn/2 + 1 , 10);

        handleManasLeft(manas);
        if(turn > 1)
            drawCard(turnParity);

        for(Cards.FieldCard fieldCard : contestant[turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.LIGHTBLUE);

        for(Cards.FieldCard fieldCard : contestant[1-turnParity].fieldCards)
            fieldCard.getCardImage().setStroke(Color.BLACK);

        usedMinions.clear();
        if(turnParity == 0){
            try {
                InfoPassiveHandler.class.getMethod(Main.player.getInfoPassive().getValue() , Play.class).invoke(null , this);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { e.printStackTrace(); }
        }
    }

    public AnchorPane getGameField() { return gameField; }
    public void setGameField(AnchorPane gameField) { this.gameField = gameField; }
    public FieldCard getSelectedCard() { return selectedCard; }
    public void setSelectedCard(FieldCard selectedCard) { this.selectedCard = selectedCard; }
    public PlayerGraphics[] getContestant() { return contestant; }
    public void setContestant(PlayerGraphics[] contestant) { this.contestant = contestant; }
    public ImageView getMyHeroImage() { return myHeroImage; }
    public ArrayList<FieldCard> getUsedMinions() { return usedMinions; }
    public int getManas() { return manas; }
    public void setManas(int manas) { this.manas = manas; }
    public int getTurn() { return turn; }
    public void setOffCard(int offCard) { this.offCard = offCard; }
    public Text getOpponentHeroHealth() { return opponentHeroHealth; }
    public Text getMyHeroHealth() { return myHeroHealth; }
    public ImageView getOpponentHeroImage() { return opponentHeroImage; }
}