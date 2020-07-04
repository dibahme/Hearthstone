package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.scene.text.Text;
import java.lang.reflect.InvocationTargetException;
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
    HERO("heroHandler"),
    MINION("minionHandler");

    private String value;
    private TargetType(String function) { value = function; }
    public String getValue() { return this.value; }
}

public class CardPowerChanger extends CardAbility {

    private int attackNumber , healthNumber;
    private boolean change;
    private Zone zone;
    private TargetType targetType;
    private SelectionType selectionType;
    private ArrayList<CardAttribute> cardAttributes;

    private int changeField(Text field , int number){
        int health = Integer.parseInt(field.getText());
        health = (change ? number : health - number);
        field.setText(String.valueOf(health));
        return number;
    }

    public void applyChangeToHero(Text targetHealth){
        int health = changeField(targetHealth , healthNumber);
        if(health <= 0)
            GameOperations.getInstance().gameOver();
        else
            targetHealth.setText(String.valueOf(health));
    }

    public void applyChangeToCard(FieldCard target , Play play){
        int health = changeField(target.getHealth() , healthNumber);
        changeField(target.getAttack() , attackNumber);
        if(health <= 0) {
            PlayerGraphics graphics = play.getContestant()[target.getParity()];
            graphics.fieldCardsBox.getChildren().remove(target.getFieldCardPhoto());
            graphics.fieldCards.remove(target);
        }
    }

    private void heroHandler(FieldCard card , Play play){
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

    private void minionHandler(FieldCard card , Play play){
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
                applyChangeToCard(targetCards.get(new Random().nextInt(targetCards.size())) , play);
                break;
            case "All":
                for(FieldCard fieldCard : targetCards)
                    applyChangeToCard(fieldCard , play);
                break;
            case "CHOOSE":
                //TODO :(
        }
    }

    public void handleOperations(FieldCard card ,Play play){
        try {
            System.out.println("HOW YOU DOIN");
            this.getClass().getMethod(this.targetType.name()).invoke(this , card , play);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { e.printStackTrace(); }
    }

}