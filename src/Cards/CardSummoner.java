package Cards;

import Controller.GameOperations;
import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import static Cards.CardAttribute.CardAttributes;
enum TargetPlace{
    FIELD,
    HAND,
    DECK
}

enum CardType{
    RANDOM,
    CHOOSE,
    ALL,
    ANTI_SPELL
}

enum SourcePlace{
    FIELD,
    HAND,
    DECK,
    THIS,
    ALL,
    WEAPON
}

public class CardSummoner extends CardAbility{

    ArrayList <TargetPlace> targetPlaces;
    CardType cardType;
    SourcePlace sourcePlace;
    boolean change;
    int attackNumber , healthNumber;
    private ArrayList<CardAttributes> cardAttributes;

    public CardSummoner(CardType cardType , SourcePlace sourcePlace
            , ArrayList <TargetPlace> targetPlaces){
        this.cardType = cardType;
        this.sourcePlace = sourcePlace;
        this.targetPlaces = targetPlaces;
        this.cardAttributes = cardAttributes;
    }

    public void changeAttackAndHealth(Text changeField , int number){
        if(change){
            if(number != -1) {
                changeField.setText(String.valueOf(number));
            }
        }
        else
            changeField.setText(String.valueOf(Integer.parseInt(changeField.getText()) - number));
    }

    public void applyChangeToCard(FieldCard targetCard , Play play){
        PlayerGraphics player = play.getContestant()[targetCard.getParity()];
        Weapon weapon = player.getWeapon();

        for(TargetPlace targetPlace : targetPlaces) {
            switch (targetPlace.name()){
                case "FIELD":
                    if(targetCard.getCard() instanceof Weapon)
                        play.handleWeaponCard(targetCard);
                    else
                        play.handleFieldPlace(targetCard , 1280);
                    break;
                case "HAND":
                    player.hand.add(targetCard.getCard());
                    break;
                case "DECK":
                    player.hand.add(targetCard.getCard());
                    play.addCardToDeck(targetCard.getCard() , targetCard.getParity());
                    break;
            }
        }

        changeAttackAndHealth(targetCard.getCard() instanceof Weapon ? weapon.getAttackText() : targetCard.getAttack() , attackNumber);
        changeAttackAndHealth(targetCard.getCard() instanceof Weapon ? weapon.getDurabilityText() : targetCard.getHealth() , healthNumber);
        if(player.getHero().getName().equals("Hunter"))
            targetCard.getCard().getCardAttributes().add(CardAttributes.RUSH);
    }

    public void chooseCard(ArrayList <Card> sourceCards , FieldCard targetCard , Play play){
        switch(cardType.name()){
            case "RANDOM":
            case "ANTI_SPELL":
                if(sourceCards.size() == 0)
                    return;
                Card chosenCard = sourceCards.get(new Random().nextInt(sourceCards.size()));
                if(cardType.name().equals("RANDOM") || !chosenCard.getType().equals("Spell"))
                    applyChangeToCard(FieldCard.getCard(chosenCard).setCardAttributes(chosenCard , targetCard.getParity()) , play);
                break;
            case "ALL":
                for(Card card : sourceCards)
                    applyChangeToCard(FieldCard.getCard(card).setCardAttributes(card , targetCard.getParity()) , play);
                break;
            case "CHOOSE":
                ArrayList sourceFieldCards = new ArrayList();
                for(Card card : sourceCards)
                    sourceFieldCards.add(FieldCard.getCard(card));
                chooseTarget(sourceFieldCards , play);
                break;
            case "MINION" :
                //TODO :(
        }
    }

    public void handleOperations(FieldCard targetCard ,Play play , FieldCard card){
        ArrayList <Card> sourceCards = new ArrayList<>();
        PlayerGraphics player = play.getContestant()[targetCard.getParity()];
        File file = new File("src/Cards/CardsInfo/CardsDescription/");
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
                break;
            case "ALL":
                for(File cardFile : file.listFiles())
                    sourceCards.add(new Card(cardFile.getName().substring(0 ,cardFile.getName().length() - ".json".length())));
                break;
            case "WEAPON":
                for(String cardName : file.list()){
                    Card tmp = new Card(cardName.substring(0 ,cardName.length() - ".json".length()));
                    if(tmp.getType().equals("Weapon"))
                        sourceCards.add(new Weapon(tmp.getName()));
                }
        }
        chooseCard(sourceCards , targetCard , play);
    }

    public void doAction(FieldCard targetCard , Play play , GameOperations.gameState gameState , FieldCard card){
        super.doAction(targetCard , play , gameState , card);
    }
}