package Scenes.Play;

import Cards.Card;
import Cards.FieldCard;
import javafx.scene.layout.HBox;

import java.util.ArrayList;



public class PlayerGraphics{
    public ArrayList<Card> hand  = new ArrayList<>() , deck = new ArrayList<>();
    public HBox fieldCardsBox, deckCardsBox;
    public ArrayList <FieldCard> fieldCards = new ArrayList<>();


    PlayerGraphics(){}
    PlayerGraphics(ArrayList <Card> hand , ArrayList<Card> deck , HBox fieldCardsBox, HBox deckCardsBox){
        for(Card card : hand)
            this.hand.add(card.getCloned());
        for(Card card : deck)
            this.deck.add(card.getCloned());
        this.deck = deck;
        this.fieldCardsBox = fieldCardsBox;
        this.deckCardsBox = deckCardsBox;
    }


}
