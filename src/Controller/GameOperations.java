package Controller;

import Cards.*;
import Scenes.Play.Play;
import javafx.animation.TranslateTransition;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

import static Cards.CardAttribute.CardAttributes;

public class GameOperations {

    private static GameOperations gameOperations = new GameOperations();
    public static GameOperations getInstance(){return gameOperations;}
    public enum gameState {
        END_TURN,
        SUMMON_CARD,
        DRAW_CARD,
        DEAD_CARD
    }

    public void transitionAction(FieldCard attacker , Node attackee , Play playScene){
        if(attacker.getCard() instanceof Weapon)
            return;
        playScene.getUsedMinions().add(attacker);
        FieldCard attackerDuplicate = FieldCard.getCard(attacker.getCard());
        attackerDuplicate.setHealth(attacker.getHealth().getText());
        attackerDuplicate.setAttack(attacker.getAttack().getText());
        Pane attackerDuplicatePane = attackerDuplicate.getFieldCardPhoto();
        playScene.getGameField().getChildren().add(attackerDuplicatePane);

        Bounds attackerBounds = attacker.getFieldCardPhoto().localToScene(attacker.getFieldCardPhoto().getBoundsInLocal());
        Bounds attackeeBounds = attackee.localToScene(attackee.getBoundsInLocal());
        attackerDuplicatePane.setLayoutX(attackerBounds.getMinX());
        attackerDuplicatePane.setLayoutY(attackerBounds.getMinY());
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(500));
        translateTransition.setNode(attackerDuplicatePane);

        translateTransition.setByX(attackeeBounds.getMinX() - attackerBounds.getMinX());
        translateTransition.setByY(attackeeBounds.getMinY() - attackerBounds.getMinY());
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);

        attacker.getFieldCardPhoto().setVisible(false);

        translateTransition.play();
        translateTransition.setOnFinished(e -> {
            attacker.getFieldCardPhoto().setVisible(true);
            playScene.getGameField().getChildren().remove(attackerDuplicatePane);
        });

        attacker.getCardImage().setStroke(Color.BLACK);
        playScene.setSelectedCard(null);
    }

    public synchronized void attackHandler(FieldCard attacker , FieldCard attackee , Play playScene){
        transitionAction(attacker , attackee.getFieldCardPhoto() , playScene);
        Weapon weapon = playScene.getContestant()[attacker.getParity()].getWeapon();

        if(attackee.getCard().getCardAttributes().contains(CardAttributes.DIVINE_SHIELD)){
            CardAttribute.getInstance().removeDivineShield(attackee);
            return;
        }

        if(attacker.getCard().getCardAttributes().contains(CardAttributes.POISONOUS)){
            attackee.setHealth("0");
            checkRemove(attackee , attackee.getHealth() , playScene);
            return;
        }

        if(attacker.getCard() instanceof Weapon) {
            changeHealth(new Text("1"), weapon.getDurabilityText());
            changeHealth(weapon.getAttackText() , attackee.getHealth());
            checkRemove(attacker , playScene.getContestant()[attacker.getParity()].getWeapon().getDurabilityText() , playScene);
        }
        else {
            changeHealth(attackee.getAttack(), attacker.getHealth());
            changeHealth(attacker.getAttack() , attackee.getHealth());
            checkRemove(attacker , attacker.getHealth() , playScene);
        }

        checkRemove(attackee , attackee.getHealth(), playScene);
    }

    public void checkRemove(FieldCard card , Text healthField , Play playScene){
        int health = Integer.parseInt(healthField.getText());
        if(health <= 0){
            if(card.getCard() instanceof Minion) {
                playScene.getContestant()[card.getParity()].fieldCards.remove(card);
                playScene.getContestant()[card.getParity()].fieldCardsBox.getChildren().remove(card.getFieldCardPhoto());
            }
            else if(card.getCard() instanceof Weapon)
                playScene.getContestant()[card.getParity()].getWeapon().stopPlaying();

            for(CardAbility cardAbility : card.getCard().getCardAbilities())
                cardAbility.doAction(card , playScene , gameState.DEAD_CARD , card);
        }
    }

    public void changeHealth(Text attacker , Text attackee){
        attackee.setText(String.valueOf(Integer.parseInt(attackee.getText()) - Integer.parseInt(attacker.getText())));
        checkGameFinish();
    }

    private void checkGameFinish(){
        // TODO
    }

    public void gameOver(){
        // TODO
    }
}