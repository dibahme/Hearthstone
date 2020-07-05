package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import com.google.gson.Gson;
import static Controller.GameOperations.gameState;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
         return new CardPowerChanger();
     }

    public String getClassName() { return className; }
    public String getClassJson() { return classJson; }
    public ApplicationTime getApplicationTime(){return applicationTime;}
    public void handleOperations(FieldCard targetCard , Play play , FieldCard card){
    }

    public void doAction(FieldCard targetCard, Play play, gameState gameState , FieldCard card){
        String applicationTime = this.applicationTime.name();
        if(gameState.name().equals(applicationTime) ||
                (applicationTime.equals("DEATH_RATTLE") && gameState.name().equals("DEAD_CARD") && targetCard.equals(card)) ||
                (applicationTime.equals("BATTLE_CRY") && gameState.name().equals("SUMMON_CARD") && targetCard.equals(card)))
            handleOperations(targetCard, play , card);
    }
}
