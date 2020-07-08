package Cards;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Weapon extends Card {

    private Text durabilityText , attackText;
    private Circle weaponImage;

    public Weapon(String name) {
        super(name);
    }
    public Weapon(){}
    public static Weapon getBasicWeapon(Text durability , Text attack , Circle image){
        Weapon weapon = new Weapon();
        weapon.durabilityText = durability;
        weapon.attackText = attack;
        weapon.weaponImage = image;
        return weapon;
    }

    public void startPlaying(FieldCard card){
        System.out.println("playing is started successfully!");
        this.getAttackText().setText(String.valueOf(this.getDamage()));
        this.getDurabilityText().setText(String.valueOf(this.getHealth()));

        this.getAttackText().setVisible(true);
        this.getDurabilityText().setVisible(true);

        this.getWeaponImage().setDisable(false);
        this.getWeaponImage().setVisible(true);
        try {
            this.getWeaponImage().setFill(new ImagePattern(new Image(new FileInputStream("src/Cards/CardsInfo/FieldCards/" + card.getCard().getName() + ".jpg"))));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public void stopPlaying(){
        System.out.println("playing is stopped successfully!");
        this.getWeaponImage().setVisible(false);
        this.getWeaponImage().setDisable(true);
        this.getDurabilityText().setVisible(false);
        this.getAttackText().setVisible(false);
        this.getWeaponImage().removeEventHandler(MouseEvent.MOUSE_CLICKED , this.getWeaponImage().getOnMouseClicked());
    }

    public Text getDurabilityText() { return durabilityText; }
    public void setDurabilityText(Text durability) { this.durabilityText = durability; }
    public Text getAttackText() { return attackText; }
    public void setAttackText(Text attack) { this.attackText = attack; }
    public void setWeaponImage(Circle weaponImage) { this.weaponImage = weaponImage; }
    public Circle getWeaponImage() { return weaponImage; }
}
