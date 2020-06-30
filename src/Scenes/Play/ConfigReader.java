package Scenes.Play;

import Cards.Card;

import java.util.ArrayList;

public class ConfigReader {

    ArrayList<String> friend = new ArrayList<>() , enemy = new ArrayList<>();

    public static ArrayList<Card> getCards(ArrayList <String> list){
        ArrayList <Card> ret = new ArrayList<>();
        for(String s : list)
            ret.add(new Card(s));

        return ret;
    }

    public ArrayList<String> getFriend() { return friend; }
    public ArrayList<String> getEnemy() { return enemy; }
}
