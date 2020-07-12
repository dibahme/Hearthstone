package Cards;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import java.io.FileInputStream;

public class FieldCard implements Choosable {
    @FXML
    private Pane fieldCardPhoto;
    @FXML
    private Text attack;
    @FXML
    private Text health;
    @FXML
    private Ellipse cardImage;
    @FXML
    private ImageView backgroundEffect;
    @FXML
    private ImageView poisonousIcon;

    private ImageView deckCardImage , cardDuplicate;
    private Card card;
    private double startX , startY;
    private int parity , minY , maxY , summonedTurn;

    public static FieldCard getCard(Card card){
        FieldCard fieldCard = new FieldCard();
        try {
            FXMLLoader loader = new FXMLLoader(FieldCard.class.getResource("FieldCard.fxml"));
            loader.load();
            fieldCard = loader.getController();
            try {
                fieldCard.cardImage.setFill(new ImagePattern(new Image(new FileInputStream("src/Cards/CardsInfo/FieldCards/" + card.getName() + ".jpg"))));
            }catch(Exception ignored){ignored.printStackTrace();}
            fieldCard.setHealth(String.valueOf(card.getHealth()));
            fieldCard.setAttack(String.valueOf(card.getDamage()));
            fieldCard.card = card;

            CardAttribute.getInstance().applyCardAttributeEffects(fieldCard);

        }catch(Exception ignored){
            ignored.printStackTrace();
        }

        return fieldCard;
    }

    public static FieldCard getHeroFieldCard(ImageView image , Text health){
        FieldCard fieldCard = new FieldCard();
        fieldCard.health = new Text(health.getText());
        Pane pane = new Pane();
        pane.getChildren().add(image);
        fieldCard.setFieldCardPhoto(pane);
        return fieldCard;
    }

    public FieldCard setCardAttributes(Card card , int parity){
        try{
            this.deckCardImage = new ImageView(new Image(new FileInputStream("src/Cards/CardsInfo/ShopCards/" + card.getName() + ".png")));
        }catch(Exception ignored){}
        Bounds bounds = deckCardImage.localToScene(deckCardImage.getBoundsInLocal());
        this.startX = this.deckCardImage.getLayoutX();
        this.startY = this.deckCardImage.getLayoutY();
        this.card = card;
        this.parity = parity;
        if(parity == 0){
            this.minY = 300;
            this.maxY = 500;
        }
        else{
            this.minY = 150;
            this.maxY = 350;
        }

        return this;
    }

    public void restore(){
        this.setHealth(String.valueOf(new Card(this.getCard().getName()).getHealth()));
    }

    public FieldCard cloneForCardAbility(){
        System.out.println("just checking " + this.getCard().getName());
        FieldCard fieldCard = getCard(this.getCard());
        fieldCard.setHealth(this.getHealth().getText());
        fieldCard.setAttack(this.getAttack().getText());
        return fieldCard;
    }

    public void setHealth(String health){this.health.setText(health);}
    public void setAttack(String attack){this.attack.setText(attack);}
    public Text getAttack(){return attack;}
    public Text getHealth(){return health;}
    public Pane getFieldCardPhoto(){return fieldCardPhoto;}
    public Ellipse getCardImage(){return cardImage;}
    public int getParity() { return parity; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }
    public ImageView getDeckCardImage() { return deckCardImage; }
    public ImageView getCardDuplicate() { return cardDuplicate; }
    public void setCardDuplicate(ImageView cardDuplicate) { this.cardDuplicate = cardDuplicate; }
    public int getSummonedTurn() { return summonedTurn; }
    public void setSummonedTurn(int summonedTurn) { this.summonedTurn = summonedTurn;}
    public void setFieldCardPhoto(Pane fieldCardPhoto) { this.fieldCardPhoto = fieldCardPhoto; }
    public ImageView getBackgroundEffect() { return backgroundEffect; }
    public ImageView getPoisonousIcon() { return poisonousIcon; }
}
