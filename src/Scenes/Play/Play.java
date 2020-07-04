package Scenes.Play;
import Cards.Card;
import Cards.CardAbility;
import Cards.Hero;
import Controller.GameOperations;
import Controller.Main;
import Logs.Log;
import Scenes.Scenes;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import Cards.FieldCard;

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

    private int turn = 0 , manasLeft = 1 , turnParity = 0 , manas = 1 , offCard = 0;
    private PlayerGraphics[] contestant;
    private FieldCard selectedCard;
    private boolean configExists = false;

    private ArrayList <FieldCard> usedMinions = new ArrayList<>();

    @FXML
    private void initialize(){

        File file = new File("src/Cards/Config/config.json");
        PlayerGraphics friend = new PlayerGraphics() , opponent = new PlayerGraphics();

        if(file.exists()){
            Gson gson = new Gson();
            try {
                Scanner sc = new Scanner(file);
                StringBuilder stringBuilder = new StringBuilder();
                while(sc.hasNext())
                    stringBuilder.append(sc.nextLine());

                ConfigReader configReader = gson.fromJson(stringBuilder.toString() , ConfigReader.class);
                friend = new PlayerGraphics(ConfigReader.getCards(configReader.getFriend()) , new ArrayList<>() , friendFieldCards , friendDeckCards );
                opponent = new PlayerGraphics(ConfigReader.getCards(configReader.getEnemy()) , new ArrayList<>() , opponentFieldCards , opponentDeckCards);
                configExists = true;
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }
        else{
            Hero opponentHero = Hero.getRandomHero();
            setOpponentHero(opponentHero);
            friend = new PlayerGraphics(Main.player.getCurrentDeck().getDeckCards() , new ArrayList<>() , friendFieldCards , friendDeckCards );
            opponent = new PlayerGraphics(opponentHero.getDefaultHand() , new ArrayList<>() , opponentFieldCards , opponentDeckCards);
        }

        myHeroImage.setOnMouseClicked(e -> {
            if(turnParity == 1 && selectedCard != null){
                FieldCard fieldCard = selectedCard;
                GameOperations.getInstance().transitionAction(selectedCard , myHeroImage , this);
                GameOperations.getInstance().changeHealth(fieldCard.getAttack() , myHeroHealth);
            }
        });

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
                if(card.getCard().getType().equals("Spell") || contestant[card.getParity()].fieldCardsBox.getChildren().size() < 7){
                    if(card.getCard().getType().equals("Minion"))
                        handleFieldPlace(card, e.getX());

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


            System.out.println("im card " + card.getCard().getName() + " and I'm here with parity " + card.getParity());
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

        ArrayList <CardAbility> cardAbilities = card.getCard().getCardAbilities();
        System.out.println("DEBUG  " + cardAbilities);
        if(cardAbilities != null) {
            for (CardAbility cardAbility : cardAbilities) {
                String applicationTime = cardAbility.getApplicationTime().name();
                if (applicationTime.equals("DESCRIPTION") || applicationTime.equals("BATTLE_CRY")) {
                    try {
                        System.out.println("NICE JOB!  " + cardAbility.getClassName() + "  " + cardAbility.getClass().getName());
                        Class cls = Class.forName("Cards." + cardAbility.getClassName());
                        cls.getMethod("getInstance" , FieldCard.class , Play.class).invoke(cls.newInstance(), card, this);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }
    }

    private void setOpponentHero(Hero hero){
        try {
            opponentHeroImage.setImage(new Image(new FileInputStream("src/Images/" + hero.getName() + "Icon.png")));
            opponentHeroImage.setOnMouseClicked(e -> {
                if(turnParity == 0 && selectedCard != null){
                    FieldCard fieldCard = selectedCard;
                    GameOperations.getInstance().transitionAction(selectedCard , opponentHeroImage , this);
                    GameOperations.getInstance().changeHealth(fieldCard.getAttack() , opponentHeroHealth);
                }
            });

        }catch(Exception ignored){ignored.printStackTrace();}
        opponentHeroHealth.setText(String.valueOf(hero.getHealth()));
    }

    @FXML
    private void backButton() {
        Scenes.menuScene();
        Log.logger("Button_Clicked" , "Back");
    }

    public void drawCard(int parity){
        if(!contestant[parity].hand.isEmpty())
            addCardToDeck(contestant[parity].hand.get(0) , parity);
    }

    @FXML
    private void endTurnAction(){
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
                System.out.println(Main.player.getInfoPassive().getValue());
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
}