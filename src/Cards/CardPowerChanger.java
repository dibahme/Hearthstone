package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static Controller.GameOperations.gameState;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

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
    BOTH("bothHandler");
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

    private void chooseTarget(ArrayList <FieldCard> targetCards , Play play){
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: rgba(0 , 0 , 0 , 0.8)");
        hBox.setPrefWidth(1280);
        hBox.setPrefHeight(220);
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setPadding(new Insets(50 , 0 , 0 , 0));
        for(FieldCard fieldCard : targetCards) {
            FieldCard clonedCard = fieldCard.cloneForCardAbility();
            hBox.getChildren().add(new Pane(clonedCard.getFieldCardPhoto()));
            clonedCard.getFieldCardPhoto().setOnMouseClicked(e -> {
                applyChangeToCard(fieldCard , play);
                play.getGameField().getChildren().remove(hBox);
            });
        }

        play.getGameField().getChildren().add(hBox);
        hBox.setTranslateY(250);
    }


    private int changeField(Text field , int number){
        int health = Integer.parseInt(field.getText());
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
        if(health <= 0) {
            PlayerGraphics graphics = play.getContestant()[target.getParity()];
            graphics.fieldCardsBox.getChildren().remove(target.getFieldCardPhoto());
            graphics.fieldCards.remove(target);
        }
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

    public void switchSelectionType(ArrayList <FieldCard> targetCards , FieldCard targetCard , Play play , FieldCard card ){
        switch (this.selectionType.name()){
            case "RANDOM":
                if(targetCards.size() > 0)
                    applyChangeToCard(targetCards.get(new Random().nextInt(targetCards.size())) , play);
                break;
            case "ALL":
                for(FieldCard fieldCard : targetCards)
                    applyChangeToCard(fieldCard , play);
                break;
            case "HIGH_PRIEST_AMET":
                healthNumber = Integer.parseInt(targetCard.getHealth().getText());
                attackNumber = Integer.parseInt(card.getAttack().getText());
                applyChangeToCard(card , play);
                break;
            case "CHOOSE":
                System.out.println("Got here right");
                chooseTarget(targetCards , play);
                //TODO :(
        }
    }

    public void bothHandler(FieldCard targetCard , Play play , FieldCard card){
        ArrayList <FieldCard> targetCards = new ArrayList<>() ,
                friend = play.getContestant()[targetCard.getParity()].fieldCards,
                enemy = play.getContestant()[1 - targetCard.getParity()].fieldCards;
        int parity = targetCard.getParity();

        switch(this.zone.name()){
            case "FRIEND":
                targetCards.addAll(friend);
                if(parity == 0)
                    targetCards.add(FieldCard.getHeroFieldCard(play.getMyHeroImage() , play.getMyHeroHealth()));
                else
                    targetCards.add(FieldCard.getHeroFieldCard(play.getOpponentHeroImage() , play.getOpponentHeroHealth()));
                break;
            case "ENEMY" :
                targetCards.addAll(enemy);
                if(parity == 1)
                    targetCards.add(FieldCard.getHeroFieldCard(play.getMyHeroImage() , play.getMyHeroHealth()));
                else
                    targetCards.add(FieldCard.getHeroFieldCard(play.getOpponentHeroImage() , play.getOpponentHeroHealth()));
                break;
            case "BOTH" :
                targetCards.addAll(friend);
                targetCards.addAll(enemy);
                targetCards.add(FieldCard.getHeroFieldCard(play.getMyHeroImage() , play.getMyHeroHealth()));
                System.out.println(targetCard.getCard().getName() + " is fine until here");
                targetCards.add(FieldCard.getHeroFieldCard(play.getOpponentHeroImage() , play.getOpponentHeroHealth()));
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