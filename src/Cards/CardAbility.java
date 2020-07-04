package Cards;

public class CardAbility {
    public enum ApplicationTime{
        DEATH_RATTLE,
        BATTLE_CRY,
        DESCRIPTION
    }

    private String className;
    private ApplicationTime applicationTime;
    public ApplicationTime getApplicationTime() { return applicationTime; }
    public  String getClassName() { return className; }
}
