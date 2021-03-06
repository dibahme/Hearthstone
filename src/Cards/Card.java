package Cards;

import com.google.gson.Gson;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;
import static Cards.CardAttribute.CardAttributes;
public class Card {

    private int price , mana , health , damage , restore;
    private String name , type , hero , description , battleCry , deathRattle , rarity , reward , quest;
    private ArrayList <CardAttributes> cardAttributes = new ArrayList<>();
    private ArrayList <CardAbility> cardActions;
    private transient ArrayList <CardAbility> cardAbilities;
    private transient Image cardImage;

    public Card(String name){
        File userPath = new File("src/Cards/CardsInfo/CardsDescription/" + name + ".json");
        try {
            Scanner sc = new Scanner(userPath);
            StringBuilder userFile = new StringBuilder();
            while(sc.hasNextLine())
                userFile.append(sc.nextLine());
           Gson gson = new Gson();
           Card tmp = gson.fromJson(userFile.toString(), Card.class);
           Class cls = tmp.getClass();
           Field[] fields =  cls.getDeclaredFields();

            for(Field field : fields)
                field.set(this , field.get(tmp));

            cardAbilities = new ArrayList<>();
            if(cardActions != null)
                for (CardAbility cardAction : cardActions)
                    cardAbilities.add(cardAction.getAbility());

        }catch(Exception ignored){
            ignored.printStackTrace();
        }
    }

    public Card(){}

    public static void setCardSize(ImageView card , int height , int width) {
        card.setFitWidth(width);
        card.setFitHeight(height);
    }

    public int getPrice(){return price;}
    public int getMana() {return mana;}
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getRestore() { return restore; }
    public String getName(){return name;}
    public String getType() { return type; }
    public String getHero() { return hero; }
    public String getDescription() { return description; }
    public String getBattleCry() { return battleCry; }
    public String getDeathRattle() { return deathRattle; }
    public String getRarity() { return rarity; }
    public FieldCard getFieldCard(){ return FieldCard.getCard(this); }
    public ArrayList<CardAbility> getCardActions() { return cardActions; }
    public Card getCloned(){
        try {
            return (Card) Class.forName("Cards." + this.getType()).getConstructor(String.class).newInstance(this.getName());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) { e.printStackTrace(); }
        return new Card(this.getName());
    }
    public ArrayList<CardAbility> getCardAbilities() { return cardAbilities; }
    public ArrayList<CardAttributes> getCardAttributes() { return cardAttributes; }
    public void setHealth(int health){this.health = health;}

}
