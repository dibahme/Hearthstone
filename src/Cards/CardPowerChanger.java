package Cards;

enum Zone {
    FRIEND,
    ENEMY,
    BOTH
}

enum SelectionType {
    RANDOM,
    ALL,
    CHOOSE
}

enum TargetType{
    HERO,
    MINION,
    WEAPON,
    BOTH
}

public class CardPowerChanger extends CardAbility {

    int attackNumber , healthNumber;
    Zone zone;
    TargetType effectedType;
    SelectionType selectionType;
}
