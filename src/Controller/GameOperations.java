package Controller;

import Scenes.Play.Play;
import Cards.FieldCard;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class GameOperations {

    private static GameOperations gameOperations = new GameOperations();
    public static GameOperations getInstance(){return gameOperations;}

    public synchronized void attackHandler(FieldCard attacker , FieldCard attackee , Play playScene){

        attackee.setHealth(String.valueOf(Integer.parseInt(attackee.getHealth().getText()) - Integer.parseInt(attacker.getAttack().getText())));

        FieldCard attackerDuplicate = FieldCard.getCard(attacker.getCard());
        Pane attackerDuplicatePane = attackerDuplicate.getFieldCardPhoto();
        playScene.getGameField().getChildren().add(attackerDuplicatePane);

        Bounds attackerBounds = attacker.getFieldCardPhoto().localToScene(attacker.getFieldCardPhoto().getBoundsInLocal());
        Bounds attackeeBounds = attackee.getFieldCardPhoto().localToScene(attackee.getFieldCardPhoto().getBoundsInLocal());

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

        System.out.println(attackee.getHealth().getText() + "   "+ attacker.getAttack().getText());
        int health = Integer.parseInt(attackee.getHealth().getText()) - Integer.parseInt(attacker.getAttack().getText());
        System.out.println(health);
        if(health <= 0){
            playScene.getContestant()[attackee.getParity()].fieldCards.remove(attackee);
            System.out.println(playScene.getContestant()[attackee.getParity()].fieldCardsBox.getChildren().remove(attackee.getFieldCardPhoto()));
            for(FieldCard fieldCard : playScene.getContestant()[attackee.getParity()].fieldCards){
                System.out.println("DEBUG: " + fieldCard.getCard().getName());
            }
        }
        else
            attackee.getHealth().setText(String.valueOf(health));

        attacker.getCardImage().setStroke(Color.BLACK);
        playScene.setSelectedCard(null);
    }

}
