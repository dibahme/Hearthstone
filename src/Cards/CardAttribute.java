package Cards;

import Scenes.Play.Play;
import Scenes.Play.PlayerGraphics;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CardAttribute {
    public enum CardAttributes {
        TAUNT("src/Images/Taunt.png"),
        RUSH("src/Images/Rush.png"),
        DIVINE_SHIELD("src/Images/Divine Shield.png"),
        POISONOUS(null),
        TWIN_SPELL(null);

        private String value;
        CardAttributes(String path) {
            value = path;
        }
        public String getValue() {
            return this.value;
        }
    }

    private static CardAttribute cardAttribute = new CardAttribute();
    public static CardAttribute getInstance(){return cardAttribute;}


    public void applyCardAttributeEffects(FieldCard fieldCard){
        for(CardAttribute.CardAttributes ability : fieldCard.getCard().getCardAttributes()) {
            String path = ability.getValue();
            if(path == null) {
                if (ability.name().equals("POISONOUS"))
                    fieldCard.getPoisonousIcon().setVisible(true);
            }
            else {
                try {
                    fieldCard.getBackgroundEffect().setImage(new Image(new FileInputStream(path)));
                } catch (FileNotFoundException e) { e.printStackTrace(); }
            }
        }
    }

    public void playedCardAttributes(FieldCard card , Play play){
        ArrayList<CardAttributes> cardAttributes = card.getCard().getCardAttributes();
        if(cardAttributes.contains(CardAttributes.TWIN_SPELL)){
            PlayerGraphics player = play.getContestant()[card.getParity()];
            Card twinCard = new Card(card.getCard().getName());
            twinCard.getCardAttributes().clear();
            player.hand.add(twinCard);
            play.addCardToDeck(twinCard , card.getParity());
        }
    }

    public boolean hasTaunt(int parity , Play play){
        ArrayList <FieldCard> fieldCards = play.getContestant()[parity].fieldCards;
        for(FieldCard fieldCard : fieldCards)
            if(fieldCard.getCard().getCardAttributes().contains(CardAttributes.TAUNT))
                return true;

        return false;
    }

    public void removeDivineShield(FieldCard card){
        card.getBackgroundEffect().setImage(null);
        card.getCard().getCardAttributes().remove(CardAttributes.DIVINE_SHIELD);
    }
}