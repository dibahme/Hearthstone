package Controller;

import Scenes.Play.Play;
import Cards.FieldCard;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


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
        changeHealth(attacker.getAttack() , attackee.getHealth());
        changeHealth(attackee.getAttack() , attacker.getHealth());
        checkRemove(attacker , playScene);
        checkRemove(attackee , playScene);

    }

    public void checkRemove(FieldCard card , Play playScene){
        int health = Integer.parseInt(card.getHealth().getText());
        if(health <= 0){
            playScene.getContestant()[card.getParity()].fieldCards.remove(card);
            playScene.getContestant()[card.getParity()].fieldCardsBox.getChildren().remove(card.getFieldCardPhoto());
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