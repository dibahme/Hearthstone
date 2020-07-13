package Scenes.Play;

import Cards.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class PlayerGraphics{

    public ArrayList<Card> hand  = new ArrayList<>() , deck = new ArrayList<>();
    public HBox fieldCardsBox, deckCardsBox;
    public ArrayList <FieldCard> fieldCards = new ArrayList<>();
    private Hero hero;
    private Weapon weapon;
    private HeroPower heroPower;
    private ArrayList <Quest> quests = new ArrayList<>();

    PlayerGraphics(){}
    PlayerGraphics(ArrayList <Card> hand , HBox fieldCardsBox, HBox deckCardsBox , Hero hero , HeroPower heroPower, Weapon weapon){
        for(Card card : hand)
            this.hand.add(card.getCloned());
        deck = new ArrayList<>();
        this.fieldCardsBox = fieldCardsBox;
        this.deckCardsBox = deckCardsBox;
        this.hero = hero;
        this.weapon = weapon;
        this.heroPower = heroPower;
    }

    public Weapon getWeapon() { return weapon; }
    public Hero getHero() { return hero; }
    public void setWeapon(Weapon weapon){this.weapon = weapon;}
    public ArrayList<Quest> getQuests() { return quests; }
}
