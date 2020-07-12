package Cards;

import Scenes.Play.Play;

import java.util.ArrayList;
import java.util.Collections;

public class Quest extends Card {

    private int numberOfManas = 0;
    public Quest(String name) {
        super(name);
    }
    public static void checkPlayerQuests(ArrayList <Quest> quests , int parity , int manas , Card card , Play play){
        for(int i = 0 ; i < quests.size() ; i++){
            Quest quest = quests.get(i);
            FieldCard fieldCard = new FieldCard().setCardAttributes(new Card() , parity);
            if (quest.getName().equals("Strength in Numbers") && card instanceof Minion){
                quest.setNumberOfManas(quest.numberOfManas + manas);
                if(quest.numberOfManas >= 10){
                    CardSummoner cardSummoner = new CardSummoner(CardType.ANTI_SPELL , SourcePlace.DECK ,
                            new ArrayList<>(Collections.singleton(TargetPlace.FIELD)));
                    cardSummoner.handleOperations(fieldCard , play , null);
                }
            }
            else if (quest.getName().equals("Learn Draconic") && card instanceof Spell){
                quest.setNumberOfManas(quest.numberOfManas + manas);
                if(quest.numberOfManas >= 8){
                    CardSummoner cardSummoner = new CardSummoner(CardType.ANTI_SPELL , SourcePlace.ALL ,
                            new ArrayList<>(Collections.singleton(TargetPlace.FIELD)));
                    cardSummoner.handleOperations(fieldCard , play , null);
                }
            }
        }
    }

    public int getNumberOfManas() { return numberOfManas; }
    public void setNumberOfManas(int numberOfManas) { this.numberOfManas = numberOfManas; }
}
