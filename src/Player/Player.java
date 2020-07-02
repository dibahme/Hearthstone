package Player;

import Cards.Card;
import java.util.ArrayList;
import java.util.Arrays;
import Scenes.Collection.Deck;
import static Scenes.Play.InfoPassiveHandler.InfoPassive;

public class Player {

    private int gems;
    private String username , password , registrationDate;

    private ArrayList <Card> hand = new ArrayList<>();
    private transient Deck currentDeck;
    private ArrayList <Deck> allDecks = new ArrayList<>();
    private transient String hero = "Mage";
    private transient InfoPassive infoPassive;

    public Player(String user, String pass, String regDate) {
        this.username = user;
        this.password = pass;
        this.registrationDate = regDate;
        this.gems = 50;
        this.hand.addAll(Arrays.asList(new Card("Humility"),
                new Card("Deadly Shot") ,
                new Card("Healing Touch"),
                new Card("Frostwolf Grunt"),
                new Card("Phantom Militia")));
    }

    public Player() {
        this.hero = "Mage";
    }

    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
    public ArrayList <Card> getHand(){return hand;}
    public void setHand(ArrayList <Card> hand){this.hand = hand;}
    public String getHero(){return hero;}
    public ArrayList <Deck> getAllDecks(){return allDecks;}
    public Deck getCurrentDeck(){return currentDeck;}
    public void setCurrentDeck(Deck deck){this.currentDeck = deck;}
    public void setInfoPassive(InfoPassive infoPassive){this.infoPassive = infoPassive;}
    public InfoPassive getInfoPassive(){return infoPassive;}
}
