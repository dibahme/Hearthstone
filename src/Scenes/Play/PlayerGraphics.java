package Scenes.Play;

import Cards.Card;
import Cards.FieldCard;
import Cards.Hero;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class PlayerGraphics{

    public ArrayList<Card> hand  = new ArrayList<>() , deck = new ArrayList<>();
    public HBox fieldCardsBox, deckCardsBox;
    public ArrayList <FieldCard> fieldCards = new ArrayList<>();
    private FieldCard weapon;
    private Hero hero;

    PlayerGraphics(){}
    PlayerGraphics(ArrayList <Card> hand , HBox fieldCardsBox, HBox deckCardsBox , Hero hero){
        for(Card card : hand)
            this.hand.add(card.getCloned());
        deck = new ArrayList<>();
        this.fieldCardsBox = fieldCardsBox;
        this.deckCardsBox = deckCardsBox;
        this.hero = hero;
    }

    public FieldCard getWeapon() { return weapon; }
    public Hero getHero() { return hero; }
}
