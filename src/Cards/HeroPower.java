package Cards;

import Controller.Main;
import Scenes.Play.InfoPassiveHandler;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class HeroPower {
    private Circle heroPowerImage;
    private int parity;
    private Text heroPowerCost;

    public HeroPower(Circle circle , Text text , int parity , Hero hero){
        this.heroPowerImage = circle;
        this.parity = parity;
        this.heroPowerImage.setFill(new ImagePattern(new Image("src/Images/" + hero.getName() + "HeroPower.png")));
        int cost = (Main.player.getInfoPassive().equals(InfoPassiveHandler.InfoPassive.FREE_POWER) ? 0 : hero.getHeroPowerCost());
        this.heroPowerCost =  text;
        this.heroPowerCost.setText(String.valueOf(cost));
    }
}
