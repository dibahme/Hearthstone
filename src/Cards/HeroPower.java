package Cards;

import Controller.GameOperations;
import Controller.Main;
import Scenes.Play.InfoPassiveHandler;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Random;

public class HeroPower implements Choosable{
    private Circle heroPowerImage;
    private int parity;
    private Text heroPowerCost;
    private Hero hero;

    public HeroPower(Circle circle , Text text , int parity , Hero hero){
        this.heroPowerImage = circle;
        this.parity = parity;
        this.heroPowerImage.setFill(new ImagePattern(new Image("src/Images/" + hero.getName() + "HeroPower.png")));
        int cost = (Main.player.getInfoPassive().equals(InfoPassiveHandler.InfoPassive.FREE_POWER) ? 0 : hero.getHeroPowerCost());
        this.heroPowerCost =  text;
        this.heroPowerCost.setText(String.valueOf(cost));
        this.hero = hero;
    }

    public void mouseClickedAction(Play play){
        PlayerGraphics opponent = play.getContestant()[1 - parity] , friend = play.getContestant()[parity];;
        CardPowerChanger cardPowerChanger;
        FieldCard fieldCard = new FieldCard().setCardAttributes(new Card() , parity);
        switch(hero.getName()){
            case "Mage":
                cardPowerChanger = new CardPowerChanger(0 , 1 , false,
                        TargetType.BOTH , SelectionType.CHOOSE , null , Zone.BOTH);
                cardPowerChanger.bothHandler(fieldCard , play , null);
                break;
            case "Rogue":
                if(!opponent.deck.isEmpty()) {
                    Card card = opponent.deck.get(new Random().nextInt(opponent.deck.size()));
                    opponent.deck.remove(card);
                    friend.hand.add(card);
                    play.addCardToDeck(card , parity);
                }

                if(friend.getWeapon().getWeaponImage().isVisible() && !opponent.hand.isEmpty()){
                    Card card = opponent.hand.get(new Random().nextInt(opponent.hand.size()));
                    opponent.hand.remove(card);
                    friend.hand.add(card);
                    play.addCardToDeck(card , parity);
                }
                break;
            case "Warlock":
                GameOperations.getInstance().changeHealth(friend.getHero().getHealth() , new Text("2"));
                int r = new Random().nextInt(2);
                if(r == 0)
                    play.drawCard(parity);
                else{
                    cardPowerChanger = new CardPowerChanger(-1 , -1 , false,
                            TargetType.MINION , SelectionType.RANDOM , null , Zone.BOTH);
                    cardPowerChanger.minionHandler(fieldCard , play , null);
                }
                break;
            case "Priest":
                cardPowerChanger = new CardPowerChanger(0 , -4 , false,
                        TargetType.BOTH , SelectionType.CHOOSE , null , Zone.BOTH);
                cardPowerChanger.bothHandler(fieldCard , play , null);
                break;
        }
    }

    public Text getHealth() { return null; }
    public Text getAttack() { return null; }
}