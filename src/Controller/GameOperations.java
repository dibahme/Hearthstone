package Controller;

import Scenes.Play.Play;
import Cards.FieldCard;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class GameOperations {

    private static GameOperations gameOperations = new GameOperations();
    public static GameOperations getInstance(){return gameOperations;}


    public void transitionAction(FieldCard attacker , Node attackee , Play playScene){
        playScene.getUsedMinions().add(attacker);
        FieldCard attackerDuplicate = FieldCard.getCard(attacker.getCard());
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
        int health = Integer.parseInt(attackee.getHealth().getText());
        if(health <= 0){
            playScene.getContestant()[attackee.getParity()].fieldCards.remove(attackee);
            playScene.getContestant()[attackee.getParity()].fieldCardsBox.getChildren().remove(attackee.getFieldCardPhoto());
        }
    }

    public void changeHealth(Text attacker , Text attackee){
        attackee.setText(String.valueOf(Integer.parseInt(attackee.getText()) - Integer.parseInt(attacker.getText())));
        checkGameFinish();
    }

    private void checkGameFinish(){
        // TODO
    }
}