package Cards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class Hero {

    private String name;
    private int health;
    private ArrayList<Card> defaultHand;

    public Hero(){}

    public Hero(String name){
        File userPath = new File("src/Cards/HeroesInfo/" + name + ".json");
        try {
            Scanner sc = new Scanner(userPath);
            StringBuilder userFile = new StringBuilder();
            while(sc.hasNextLine())
                userFile.append(sc.nextLine());
            Gson gson = new Gson();

            Hero tmp = gson.fromJson(userFile.toString(), Hero.class);
            Class cls = tmp.getClass();
            Field[] fields =  cls.getDeclaredFields();

            for(Field field : fields)
                field.set(this, field.get(tmp));

        }catch(Exception ignored){
            ignored.printStackTrace();
        }
    }

    public static Hero getRandomHero(){
        File file = new File("src/Cards/HeroesInfo");
        String name = file.list()[new Random().nextInt(file.list().length)];
        return new Hero(name.substring(0 , name.length() - ".json".length()));
    }

    public int getHealth(){return health;}
    public void setHealth(int health){this.health = health;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public ArrayList<Card> getDefaultHand(){return defaultHand;}
}
