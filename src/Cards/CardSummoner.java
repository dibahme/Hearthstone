package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;

import java.util.ArrayList;
import java.util.Random;

enum TargetPlace{
    FIELD,
    HAND,
    DECK
}

enum CardType{
    RANDOM,
    CHOOSE,
    ALL
}

enum SourcePlace{
    FIELD,
    HAND,
    DECK,
    THIS
}

public class CardSummoner extends CardAbility{

    ArrayList <TargetPlace> targetPlaces;
    CardType cardType;
    SourcePlace sourcePlace;

    public void applyChangeToCard(Card card , FieldCard targetCard , Play play){

    }

    public void chooseCard(ArrayList <Card> sourceCards , FieldCard targetCard , Play play){
        switch(cardType.name()){
            case "RANDOM":
                if(sourceCards.size() == 0)
                    return;
                applyChangeToCard(sourceCards.get(new Random().nextInt(sourceCards.size())) , targetCard , play);
                break;
            case "ALL":
                for(Card card : sourceCards)
                    applyChangeToCard(card , targetCard , play);
            case "CHOOSE":
                ArrayList sourceFieldCards = new ArrayList();
                for(Card card : sourceCards)
                    sourceFieldCards.add(FieldCard.getCard(card));
                chooseTarget(sourceFieldCards , play);
        }
    }

    public void handleOperations(FieldCard targetCard ,Play play , FieldCard card){
        ArrayList <Card> sourceCards = new ArrayList<>();
        PlayerGraphics player = play.getContestant()[targetCard.getParity()];
        switch (sourcePlace.name()){
            case "FIELD":
                for(FieldCard fieldCard : player.fieldCards)
                    sourceCards.add(fieldCard.getCard());
                break;
            case "HAND":
                sourceCards.addAll(player.hand);
                break;
            case "DECK":
                sourceCards.addAll(player.deck);
                break;
            case "THIS" :
                sourceCards.add(targetCard.getCard());
        }

        chooseCard(sourceCards , targetCard , play);
    }

    public void doAction(FieldCard targetCard , Play play , GameOperations.gameState gameState , FieldCard card){
        super.doAction(targetCard , play , gameState , card);
    }
}
