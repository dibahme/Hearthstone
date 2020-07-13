package Scenes.Play;

import Cards.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ConfigReader {

    ArrayList<String> friend = new ArrayList<>() , enemy = new ArrayList<>();

    public static ArrayList<Card> getCards(ArrayList <String> list){
        ArrayList <Card> ret = new ArrayList<>();
        for(String s : list) {
            switch(s) {
                case "Minion":
                    ret.add(new Minion("Scarlet Crusader"));
                    break;
                case "Spell":
                    ret.add(new Spell("Echo of Medivh"));
                    break;
                case "Quest":
                    ret.add(new Quest("Strength in Numbers"));
                    break;
                case "Weapon":
                    ret.add(new Weapon("Heavy Axe"));
                    break;
                default:
                    try {
                        ret.add((Card) Class.forName("Cards." + new Card(s).getType()).getConstructor(String.class).newInstance(s));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                            NoSuchMethodException | ClassNotFoundException e) { e.printStackTrace(); }
            }
        }

        return ret;
    }

    public ArrayList<String> getFriend() { return friend; }
    public ArrayList<String> getEnemy() { return enemy; }
}
