package Cards;

import Scenes.Play.Play;
import javafx.scene.Node;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

enum Zone {
    FRIEND,
    ENEMY,
    BOTH
}

enum SelectionType {
    RANDOM,
    ALL,
    CHOOSE
}

enum TargetType{
    HERO,
    MINION,
    WEAPON,
    BOTH
}

public class CardPowerChanger extends CardAbility {

    private int attackNumber , healthNumber;
    private boolean change;
    private Zone zone;
    private TargetType targetType;
    private SelectionType selectionType;
    private ArrayList<CardAttribute> cardAttributes;
    private transient static final String functionName = "cardPowerHandler";

    public void applyChangeToHero(Node targetHealth){
        //TODO
    }

    public void applyChangeToCard(FieldCard target){

    }

    public void cardPowerHandler(FieldCard card ,Play play){
        String target = this.targetType.name();
        if(target.equals("HERO")){
            Text friend = play.getMyHeroHealth() , enemy = play.getOpponentHeroHealth();
            switch(this.zone.name()){
                case "FRIEND" :
                    applyChangeToHero(card.getParity() == 0 ? friend : enemy);
                    break;
                case "ENEMY" :
                    applyChangeToHero(card.getParity() == 0 ? enemy : friend);
                    break;
                case "BOTH" :
                    //not happened yet! :D
            }
        }
        else if(target.equals("MINION")){
            ArrayList <FieldCard> targetCards = new ArrayList<>() ,
                    friend = play.getContestant()[card.getParity()].fieldCards,
                    enemy = play.getContestant()[1 - card.getParity()].fieldCards;

            switch(this.zone.name()){
                case "FRIEND":
                    targetCards.addAll(friend);
                    break;
                case "ENEMY" :
                    targetCards.addAll(enemy);
                    break;
                case "BOTH" :
                    targetCards.addAll(friend);
                    targetCards.addAll(enemy);
                    break;
            }

            switch (this.selectionType.name()){
                case "RANDOM":
                    applyChangeToCard(targetCards.get(new Random().nextInt(targetCards.size())));
                    break;
                case "All":
                    for(FieldCard fieldCard : targetCards)
                        applyChangeToCard(fieldCard);
                    break;
                case "CHOOSE":
                    //TODO :(
            }
        }

    }

    public static String getFunctionName() { return functionName; }
}
