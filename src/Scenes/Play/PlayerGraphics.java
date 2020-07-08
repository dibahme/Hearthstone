package Scenes.Play;

import Cards.Card;
import Cards.FieldCard;
import Cards.Hero;
import Cards.Weapon;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class PlayerGraphics{

    public ArrayList<Card> hand  = new ArrayList<>() , deck = new ArrayList<>();
    public HBox fieldCardsBox, deckCardsBox;
    public ArrayList <FieldCard> fieldCards = new ArrayList<>();
    private Hero hero;
    private Circle heroPowerImage;
    private Weapon weapon;

    PlayerGraphics(){}
    PlayerGraphics(ArrayList <Card> hand , HBox fieldCardsBox, HBox deckCardsBox , Hero hero , Circle heroPowerImage, Weapon weapon){
        for(Card card : hand)
            this.hand.add(card.getCloned());
        deck = new ArrayList<>();
        this.fieldCardsBox = fieldCardsBox;
        this.deckCardsBox = deckCardsBox;
        this.hero = hero;
        this.weapon = weapon;
        this.heroPowerImage = heroPowerImage;
    }

    public Weapon getWeapon() { return weapon; }
    public Hero getHero() { return hero; }
    public Circle getHeroPowerImage() { return heroPowerImage; }
    public void setWeapon(Weapon weapon){this.weapon = weapon;}
}
