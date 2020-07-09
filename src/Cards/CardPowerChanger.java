package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
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

//    private void chooseTarget(ArrayList <? extends Choosable> targetCards , Play play){
//        HBox hBox = new HBox();
//        hBox.setStyle("-fx-background-color: rgba(0 , 0 , 0 , 0.8)");
//        hBox.setPrefWidth(1280);
//        hBox.setPrefHeight(220);
//        hBox.setAlignment(Pos.BASELINE_CENTER);
//        hBox.setPadding(new Insets(50 , 0 , 0 , 0));
//        play.getGameField().getChildren().add(hBox);
//        hBox.setTranslateY(250);
//        for(Choosable fieldCard : targetCards) {
//            Pane pane = new Pane();
//            if (fieldCard instanceof FieldCard) {
//                FieldCard clonedCard = ((FieldCard) fieldCard).cloneForCardAbility();
//                pane = new Pane(clonedCard.getFieldCardPhoto());
//            }
//            else if (fieldCard instanceof Hero) {
//                ImageView image = ((Hero) fieldCard).getImage();
//                pane = new Pane(((Hero) fieldCard).getImage());
//            }
//
//            hBox.getChildren().add(pane);
//            pane.setOnMouseClicked(e -> {
//                if(fieldCard instanceof  FieldCard)
//                    applyChangeToCard((FieldCard) fieldCard , play);
//                else if(fieldCard instanceof Hero)
//                    applyChangeToHero(fieldCard.getHealth());
//                play.getGameField().getChildren().remove(hBox);
//            });
//        }
//    }

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

        for(CardAttribute cardAttribute : cardAttributes){
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
                //TODO :(
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

    public void bothHandler(FieldCard targetCard , Play play , FieldCard card){
        ArrayList <Choosable> targetCards = new ArrayList<>();

        ArrayList <FieldCard >friend = play.getContestant()[targetCard.getParity()].fieldCards,
                enemy = play.getContestant()[1 - targetCard.getParity()].fieldCards;

        int parity = targetCard.getParity();

        switch(this.zone.name()){
            case "FRIEND":
                targetCards.addAll(friend);
                targetCards.add(play.getContestant()[parity].getHero());
                break;
            case "ENEMY" :
                targetCards.addAll(enemy);
                play.getContestant()[1-parity].getHero();
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