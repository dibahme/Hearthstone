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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class HeroPower implements Choosable{
    private Circle heroPowerImage;
    private int parity;
    private Text heroPowerCost;
    private Hero hero;
    private int lastTurn = -1;
    public HeroPower(Circle circle , Text text , int parity , Hero hero , Play play){
        this.heroPowerImage = circle;
        this.parity = parity;
        try {
            this.heroPowerImage.setFill(new ImagePattern(new Image(new FileInputStream("src/Images/" + hero.getName() + "HeroPower.png"))));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
        InfoPassiveHandler.InfoPassive passive = Main.player.getInfoPassive();
        int cost = (passive != null && passive.equals(InfoPassiveHandler.InfoPassive.FREE_POWER) ? 0 : hero.getHeroPowerCost());
        this.heroPowerCost =  text;
        this.heroPowerCost.setText(String.valueOf(cost));
        this.hero = hero;

        circle.setOnMouseClicked(e -> {
            System.out.println("here we are :))");
            if(lastTurn != play.getTurn())
                mouseClickedAction(play);
        });
    }

    public void mouseClickedAction(Play play){
        System.out.println("im here in mouseclickedAction");
        PlayerGraphics opponent = play.getContestant()[1 - parity] , friend = play.getContestant()[parity];;
        CardPowerChanger cardPowerChanger;
        FieldCard fieldCard = new FieldCard();
        fieldCard.setParity(parity);
        switch(hero.getName()){
            case "Mage":
                cardPowerChanger = new CardPowerChanger(0 , 1 , false,
                        TargetType.BOTH , SelectionType.CHOOSE , null , Zone.BOTH);
                cardPowerChanger.bothHandler(fieldCard , play , null);
                break;
            case "Rogue":
                if(!opponent.deck.isEmpty()) {
                    int rand = new Random().nextInt(opponent.deck.size());
                    Card card = opponent.deck.get(rand);
                    opponent.deck.remove(card);
                    opponent.deckCardsBox.getChildren().remove(rand);
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
                GameOperations.getInstance().changeHealth(friend.getHero().getHealth() , new Text("2") , play);
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