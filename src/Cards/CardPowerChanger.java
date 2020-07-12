package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import static Cards.CardAttribute.CardAttributes;
import static Controller.GameOperations.gameState;

enum Zone {
    FRIEND,
    DAMAGED_FRIEND,
    ENEMY,
    BOTH,
    SELF
}

enum SelectionType {
    RANDOM,
    ALL,
    CHOOSE,
    HIGH_PRIEST_AMET
}

enum TargetType{
    HERO("heroHandler"),
    MINION("minionHandler"),
    BOTH("bothHandler"),
    WEAPON("weaponHandler");
    private String value;
    TargetType(String function) { value = function; }
    public String getValue() { return this.value; }
}

public class CardPowerChanger extends CardAbility {

    private int attackNumber , healthNumber;
    private boolean change;
    private Zone zone;
    private TargetType targetType;
    private SelectionType selectionType;
    private ArrayList<CardAttributes> cardAttributes;

    public CardPowerChanger(int attackNumber , int healthNumber , boolean change , TargetType targetType ,
                            SelectionType selectionType , ArrayList<CardAttributes> cardAttributes , Zone zone){
        this.attackNumber = attackNumber;
        this.healthNumber = healthNumber;
        this.change = change;
        this.targetType = targetType;
        this.selectionType = selectionType;
        this.cardAttributes = cardAttributes;
    }


    private int changeField(Text field , int number){
        int health = Integer.parseInt(field.getText());
        if(change && number < 0)
            return health;
        health = (change ? number : health - number);
        field.setText(String.valueOf(health));
        return health;
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

        for(CardAttributes cardAttribute : cardAttributes){
            if(!target.getCard().getCardAttributes().contains(cardAttribute))
                target.getCard().getCardAttributes().add(cardAttribute);
        }

        if(health <= 0) {
            PlayerGraphics graphics = play.getContestant()[target.getParity()];
            graphics.fieldCardsBox.getChildren().remove(target.getFieldCardPhoto());
            graphics.fieldCards.remove(target);
        }
    }

    public void applyChange(Choosable choosable , Play play){
//        Choosable choosable = targetCards.get(new Random().nextInt(targetCards.size()));
        if(choosable instanceof Hero)
            applyChangeToHero(choosable.getHealth());
        else if(choosable instanceof FieldCard)
            applyChangeToCard((FieldCard) choosable , play);
    }

    public void switchSelectionType(ArrayList <? extends Choosable> targetCards , FieldCard targetCard , Play play , FieldCard card ){
        switch (this.selectionType.name()){
            case "RANDOM":
                if(targetCards.size() > 0)
                    applyChange(targetCards.get(new Random().nextInt(targetCards.size())) , play);
                break;
            case "ALL":
                for(Choosable fieldCard : targetCards)
                    applyChange(fieldCard , play);
                break;
            case "HIGH_PRIEST_AMET":
                healthNumber = Integer.parseInt(targetCard.getHealth().getText());
                attackNumber = Integer.parseInt(card.getAttack().getText());
                applyChangeToCard(card , play);
                break;
            case "CHOOSE":
                chooseTarget(targetCards , play);
        }
    }

    public void weaponHandler(FieldCard targetCard , Play play , FieldCard card){
        PlayerGraphics player = play.getContestant()[targetCard.getParity()];
        int attack = Integer.parseInt(player.getWeapon().getAttackText().getText());
        player.getWeapon().getAttackText().setText(String.valueOf(attack + 1));
        player.getWeapon().setHealth(attack+1);
    }

    private void heroHandler(FieldCard targetCard , Play play , FieldCard card){
        Text friend = play.getMyHeroHealth() , enemy = play.getOpponentHeroHealth();
        switch(this.zone.name()){
            case "FRIEND" :
                applyChangeToHero(targetCard.getParity() == 0 ? friend : enemy);
                break;
            case "ENEMY" :
                applyChangeToHero(targetCard.getParity() == 0 ? enemy : friend);
                break;
            case "BOTH" :
                //not happened yet! :D
                break;
            case "SELF":
                //not happened yet! :D
                break;
        }
    }

    public void minionHandler(FieldCard targetCard , Play play , FieldCard card){
        ArrayList <FieldCard> targetCards = new ArrayList<>() ,
                friend = play.getContestant()[targetCard.getParity()].fieldCards,
                enemy = play.getContestant()[1 - targetCard.getParity()].fieldCards;

        switch(this.zone.name()){
            case "FRIEND":
                targetCards.addAll(friend);
                break;
            case "DAMAGED_FRIEND":
                targetCards.addAll(friend);
                targetCards.removeIf(fieldCard -> Integer.parseInt(fieldCard.getHealth().getText()) == fieldCard.getCard().getHealth());
                break;
            case "ENEMY" :
                targetCards.addAll(enemy);
                break;
            case "BOTH" :
                targetCards.addAll(friend);
                targetCards.addAll(enemy);
                break;
            case "SELF":
                applyChangeToCard(targetCard , play);
                return;
        }

        switchSelectionType(targetCards , targetCard , play , card);
    }

    public void bothHandler(FieldCard targetCard , Play play , FieldCard card){

        int parity = targetCard.getParity();
        ArrayList <Choosable> targetCards = new ArrayList<>();
        ArrayList <FieldCard >friend = play.getContestant()[parity].fieldCards,
                enemy = play.getContestant()[1 - parity].fieldCards;

        switch(this.zone.name()){
            case "FRIEND":
                targetCards.addAll(friend);
                targetCards.add(play.getContestant()[parity].getHero());
                break;
            case "ENEMY" :
                targetCards.addAll(enemy);
                targetCards.add(play.getContestant()[1-parity].getHero());
                break;
            case "BOTH" :
                targetCards.addAll(friend);
                targetCards.addAll(enemy);
                targetCards.add(play.getContestant()[0].getHero());
                targetCards.add(play.getContestant()[1].getHero());
                break;
        }

        switchSelectionType(targetCards , targetCard , play , card);
    }

    public void handleOperations(FieldCard targetCard ,Play play , FieldCard card){
        try {
            this.getClass().getDeclaredMethod(targetType.getValue() , FieldCard.class , Play.class , FieldCard.class).invoke(this , targetCard , play , card);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) { e.printStackTrace(); }
    }

    public void doAction(FieldCard targetCard , Play play , gameState gameState , FieldCard card){
        super.doAction(targetCard , play , gameState , card);
    }

}