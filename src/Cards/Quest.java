package Cards;

import Scenes.Play.Play;
import java.util.ArrayList;

public class Quest extends Card {

    private int numberOfManas = 0;
    public Quest(String name) {
        super(name);
    }

    public void getReward(int parity , Card card , Play play){
        FieldCard fieldCard = FieldCard.getCard(card).setCardAttributes(card , parity);
        for(CardAbility cardAbility : this.getCardAbilities())
            cardAbility.handleOperations(fieldCard , play , null);
    }

    public static void checkPlayerQuests(ArrayList <Quest> quests , int parity , int manas , Card card , Play play){
        for(int i = 0 ; i < quests.size() ; i++){
            Quest quest = quests.get(i);
            if (quest.getName().equals("Strength in Numbers") && card instanceof Minion){
                quest.setNumberOfManas(quest.numberOfManas + manas);
                if(quest.numberOfManas >= 10){
                    quest.getReward(parity , card , play);
                }
            }
            else if (quest.getName().equals("Learn Draconic") && card instanceof Spell){
                quest.setNumberOfManas(quest.numberOfManas + manas);
                if(quest.numberOfManas >= 8){
                    quest.getReward(parity , card , play);
                }
            }
        }
    }

    public int getNumberOfManas() { return numberOfManas; }
    public void setNumberOfManas(int numberOfManas) { this.numberOfManas = numberOfManas; }
}