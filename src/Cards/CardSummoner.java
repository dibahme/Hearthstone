package Cards;

import java.util.ArrayList;

enum TargetPlace{
    FIELD,
    HAND,
    DECK
}

enum CardType{
    RANDOM,
    THIS
}

public class CardSummoner {
    ArrayList <TargetPlace> targetPlaces;
    CardType cardType;
}
