package Cards;

import Scenes.Play.Play;
import com.google.gson.Gson;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;

import static Controller.GameOperations.gameState;

public class CardAbility {

    public enum ApplicationTime{
        DRAW_CARD,
        END_TURN,
        SUMMON_CARD,
        BATTLE_CRY,
        DEATH_RATTLE
    }

    private String className;
    private String classJson;
    private ApplicationTime applicationTime;

    public CardAbility getAbility(){
         try {
             return (CardAbility) new Gson().fromJson(classJson , Class.forName("Cards." + className));
         } catch (ClassNotFoundException e) { e.printStackTrace(); }
         return new CardAbility();
     }

    public String getClassName() { return className; }
    public String getClassJson() { return classJson; }
    public ApplicationTime getApplicationTime(){return applicationTime;}
    public void handleOperations(FieldCard targetCard , Play play , FieldCard card){ }
    public void applyChangeToHero(Text targetHealth){}
    public void applyChangeToCard(FieldCard card , Play play){}

    public void chooseTarget(ArrayList <? extends Choosable> targetCards , Play play){
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: rgba(0 , 0 , 0 , 0.8)");
        hBox.setPrefWidth(1280);
        hBox.setPrefHeight(220);
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setPadding(new Insets(50 , 0 , 0 , 0));
        play.getGameField().getChildren().add(hBox);
        hBox.setTranslateY(250);
        for(Choosable fieldCard : targetCards) {
            Pane pane = new Pane();
            if (fieldCard instanceof FieldCard) {
                FieldCard clonedCard = ((FieldCard) fieldCard).cloneForCardAbility();
                pane = new Pane(clonedCard.getFieldCardPhoto());
            }
            else if (fieldCard instanceof Hero)
                pane = new Pane(((Hero) fieldCard).getImage());


            hBox.getChildren().add(pane);
            pane.setOnMouseClicked(e -> {
                if(fieldCard instanceof  FieldCard)
                    applyChangeToCard((FieldCard) fieldCard , play);
                else if(fieldCard instanceof Hero)
                    applyChangeToHero(fieldCard.getHealth());
                play.getGameField().getChildren().remove(hBox);
            });
        }
    }

    public void doAction(FieldCard targetCard, Play play, gameState gameState , FieldCard card){
        String applicationTime = this.applicationTime.name();
        if(gameState.name().equals(applicationTime) ||
                (applicationTime.equals("DEATH_RATTLE") && gameState.name().equals("DEAD_CARD") && targetCard.equals(card)) ||
                (applicationTime.equals("BATTLE_CRY") && gameState.name().equals("SUMMON_CARD") && targetCard.equals(card)))
            handleOperations(targetCard, play , card);
    }
}
