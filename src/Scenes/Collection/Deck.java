package Scenes.Collection;

import Cards.*;
import Controller.*;
import Logs.Log;
import Scenes.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Deck{

    private ArrayList <Card> deckCards = new ArrayList<>();
    private String name = "", hero = "";
    private int wins = 0, totalPlays = 0;
    private double meanCost = 0;

    public static transient Deck primaryDeck;

    @FXML
    private transient TextField deckName;

    @FXML
    private transient ChoiceBox<String> heroBox;

    @FXML
    private transient ChoiceBox<String> cardBox;

    @FXML
    private transient Button createDeckButton;

    @FXML
    private transient GridPane gridPane;

    public Deck(String name , String hero , ArrayList <Card> deckCards){
        this.name = name;
        this.hero = hero;
        this.deckCards.addAll(deckCards);
    }

    public Deck(){}

    @FXML
    private void selectDeckAction(){
        Main.player.setCurrentDeck(this);
        Scenes.secondStage.close();
        createDeckButton.fire();
        Scenes.notificationBox("Deck's Selected" , 120 , 560);
        Log.logger("Button_Clicked" , "SelectDeck");
    }

    @FXML
    private void addButtonAction(){
        int numberInHand = 0 , numberInDeck = 1;
        String cardName = cardBox.getValue();

        for(Card card : Main.player.getHand())
            if(card.getName().equals(cardName))
                numberInHand++;

        for(Card card : this.getDeckCards())
            if(card.getName().equals(cardName))
                numberInDeck++;

        Card tmp = new Card(cardName);
        try {
            tmp = (Card) Class.forName("Cards." + tmp.getType()).getConstructor(String.class).newInstance(cardName);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) { e.printStackTrace(); }

        if(!tmp.getHero().equals(heroBox.getValue()) && !heroBox.getValue().equals("") && !tmp.getHero().equals("Neutral")) {
            Scenes.alertBox(Scenes.secondStage , "This Card Doesn't Match to Your Deck's Hero!");
            Log.logger("Error" , "Card and Hero don't Match");
        }
        else if(numberInHand >= numberInDeck) {
            Card card = new Card(cardName);
            try {
                deckCards.add((Card) Class.forName("Cards." + card.getType()).getConstructor(String.class).newInstance(card.getName()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.logger("Button_Clicked" , "Add " + cardName);
        }
        else {
            Scenes.alertBox(Scenes.secondStage , "You Don't Have This Card");
            Log.logger("Error" , "Card Doesn't Exist");
        }

        this.name = this.deckName.getText();
        this.hero = this.heroBox.getValue();
        showDeck();
    }

    @FXML
    private void removeButtonAction(){
        String cardName = cardBox.getValue();
        boolean flag = false;
        for(Card card : this.deckCards){
            if(card.getName().equals(cardName)){
                flag = true;
                this.deckCards.remove(card);
                break;
            }
        }

        if(!flag) {
            Scenes.alertBox(Scenes.secondStage , "No Such Card Exist in the Deck");
            Log.logger("Error" , "Card Doesn't Exist");
        }
        else {
            this.name = this.deckName.getText();
            this.hero = this.heroBox.getValue();
            Log.logger("Button_Clicked" , "Remove Card " + cardName);
            showDeck();
        }
    }

    @FXML
    private void cancelButtonAction(){
        Scenes.secondStage.close();
        Log.logger("Button_Clicked" , "ExitDeck");
    }

    @FXML
    private void deleteDeckAction(){
        for(Deck deck : Main.player.getAllDecks()){
            if(deck.getName().equals(primaryDeck.getName())){
                Main.player.getAllDecks().remove(deck);
                break;
            }
        }

        if(Main.player.getCurrentDeck() == primaryDeck)
            Main.player.setCurrentDeck(null);

        Scenes.secondStage.close();
        Scenes.collectionScene();
        Scenes.notificationBox("Deck's Removed" , 120 , 560);
        Log.logger("Button_Clicked" , "Remove Deck " + primaryDeck.getName());
    }

    @FXML
    private void createDeckButtonAction(){
        boolean exist = false;
        this.setName(deckName.getText());
        this.setHero(heroBox.getValue());
        for(Deck playerDeck : Main.player.getAllDecks())
            if (playerDeck.getName().equals(this.getName())) {
                exist = true;
                break;
            }

        if(exist && !primaryDeck.getName().equals(this.getName())) {
            Scenes.alertBox(Scenes.secondStage , "Deck with this name already exist!");
            Log.logger("Error" , "Deck Already Exist");
        }
        else if(this.getHero().equals("")) {
            Scenes.alertBox(Scenes.secondStage , "You need to choose a hero for your deck!");
            Log.logger("Error", "Deck Has No Hero");
        }
        else {
            Main.player.getAllDecks().add(this);
            Scenes.secondStage.close();
            Main.player.getAllDecks().remove(primaryDeck);
            if(Main.player.getCurrentDeck() == primaryDeck)
                Main.player.setCurrentDeck(this);
            Scenes.collectionScene();
            Log.logger("Button_Clicked" , "Created Deck " + this.getName());
        }

    }

    public void showDeck(){
        cardBox.getItems().clear();
        heroBox.getItems().clear();

        heroBox.setValue(this.hero);
        deckName.setText(this.name);
        ArrayList<Card> allCards = new ArrayList<>(Main.player.getHand());

        for(Card card : allCards) {
            boolean flag = true;
            for(String item : cardBox.getItems()){
                if(item.equals(card.getName()))
                    flag = false;
            }

            if(flag)
                cardBox.getItems().add(card.getName());
        }

        boolean flag = true;

        for(Card card : this.deckCards){
            if(!card.getHero().equals("Neutral") && heroBox.getItems().size() == 0){
                heroBox.getItems().add(card.getHero());
                flag = false;
            }
        }

        if(flag){
            File file = new File("src/Cards/HeroesInfo/HeroesDescription/");
            for(String name : Objects.requireNonNull(file.list()))
                heroBox.getItems().add(name.substring(0 , name.length()-5));
        }

        gridPane.getChildren().clear();
        int row = 0 , col = 0;
        for(Card card : this.deckCards){
            try {
                ImageView cardImage = new ImageView(new Image(new FileInputStream("src/Cards/CardsInfo/ShopCards/" + card.getName() + ".png")));
                cardImage.setFitWidth(190);
                cardImage.setFitHeight(260);
                GridPane.setConstraints(cardImage, col, row);
                gridPane.getChildren().add(cardImage);
                if(col == 2)
                    row++;
                col = (col+1)%3;
            }catch(Exception e){e.printStackTrace();}
        }
    }


    public static class DeckComparator implements Comparator <Deck>{
        @Override
        public int compare (Deck deck, Deck t1){
            if (deck.wins * t1.totalPlays == deck.totalPlays * t1.wins) {
                if (deck.wins == t1.wins) {
                    if (deck.totalPlays == t1.totalPlays)
                        return Double.compare(deck.meanCost, t1.meanCost);

                    return deck.totalPlays - t1.totalPlays;
                }
                return deck.wins - t1.wins;
            }
            return deck.wins * t1.totalPlays - deck.totalPlays * t1.wins;
        }
    }

    public void setAttributes(Deck deck){
        this.name = deck.getName();
        this.hero = deck.getHero();
        this.getDeckCards().addAll(deck.getDeckCards());
        Deck.primaryDeck = deck;
    }

    public ArrayList <Card> getDeckCards(){return deckCards;}
    public String getName(){return name;}
    public String getHero(){return hero;}
    public double getMeanCost(){return meanCost;}
    public void setName(String name){this.name = name;}
    public void setHero(String hero){this.hero = hero;}
}
