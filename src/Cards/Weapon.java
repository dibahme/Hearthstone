package Cards;


import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Weapon extends Card {

    Text durability , attack;
    Circle weaponImage;
    public Weapon(String name) {
        super(name);
    }

    public Weapon(){}

    public static Weapon getBasicWeapon(Text durability , Text attack , Circle image){
        Weapon weapon = new Weapon();
        weapon.durability = durability;
        weapon.attack = attack;
        weapon.weaponImage = image;
        return weapon;
    }

    public Text getDurability() { return durability; }
    public void setDurability(Text durability) { this.durability = durability; }
    public Text getAttack() { return attack; }
    public void setAttack(Text attack) { this.attack = attack; }
    public void setWeaponImage(Circle weaponImage) { this.weaponImage = weaponImage; }
    public Circle getWeaponImage() { return weaponImage; }
}
