package Scenes.Play;
import Cards.FieldCard;
import java.util.ArrayList;
import java.util.Random;

public class InfoPassiveHandler{
    public enum InfoPassive {
        TWICE_DRAW("twiceDraw"),
        OFF_CARDS("offCards"),
        NURSE("nurse"),
        MANA_JUMP("manaJump"),
        FREE_POWER("freePower");

        private String value;
        private InfoPassive(String function) { value = function; }
        public String getValue() { return this.value; }
    }

    public static void twiceDraw(Play play){
        play.drawCard(0);
    }

    public static void offCards(Play play){
        play.setOffCard(-1);
    }

    public static void nurse(Play play){
        ArrayList<FieldCard> card = play.getContestant()[0].fieldCards;
        if(!card.isEmpty()) {
            int r = new Random().nextInt(card.size());
            card.get(r).restore();
        }
    }

    public static void manaJump(Play play){
        play.setManas(play.getTurn()/2 + 2);
        play.handleManasLeft(play.getManas());
    }

    public static void freePower(){

    }

}
