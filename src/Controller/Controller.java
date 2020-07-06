package Controller;

import Cards.Card;
import Logs.Log;
import Player.Player;
import Scenes.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.google.gson.Gson;

public class Controller {

    private static final Gson gson = new Gson();
    private static final Date date = new Date();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss:SSS");

    private static String getJson(String user) {
        File userFile = new File("src/Player/PlayersInfo/" + user + ".json");
        try {
            Scanner userScanner = new Scanner(userFile);
            StringBuilder userInfo = new StringBuilder("");
            while (userScanner.hasNextLine())
                userInfo.append(userScanner.nextLine());

            return userInfo.toString();
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    public static boolean enter(String acc, String user, String pass){
        try {
            String userInfo = getJson(user);
            if (userInfo.equals(""))
                throw new FileNotFoundException();

            if (acc.equals("n")) {
                Scenes.alertBox(Scenes.currentStage , "There is already an account with this username.");
                return false;
            }

            Main.player = gson.fromJson(userInfo, Player.class);

            if (Main.player.getPassword().equals(pass)) {
                Scenes.notificationBox("Successfully Logged In!" , 500 , 100);
                Log.logger("Sign_in" , Main.player.getUsername());
                return true;
            }
            else
                Scenes.alertBox(Scenes.currentStage , "Invalid Password.");

        } catch (FileNotFoundException e) {
            if (acc.equals("y")) {
                Scenes.alertBox(Scenes.currentStage , "There isn't such an account!");
            } else {
                Main.player = new Player(user , pass , sdf.format(date));
                String userInfo = gson.toJson(Main.player);
                File userFile = new File("src/Player/PlayersInfo/" + user + ".json");

                try {
                    userFile.createNewFile();
                    FileWriter userFileWriter = new FileWriter(userFile);
                    userFileWriter.append(userInfo);
                    userFileWriter.close();
                } catch (Exception ignored) {}

                Scenes.notificationBox("Successfully Signed Up and Logged In!" , 500 , 100);
                Log.logger("Sign_Up" , Main.player.getUsername());
                return true;
            }
        }

        return false;
    }

    public static List<Card> showDealableCards(String arg) {
        ArrayList<Card> ret = new ArrayList<Card>();

        if (arg.contains("sell")) {
            ret.addAll(Main.player.getHand());
        } else {
            File cards = new File("src/Cards/CardsInfo/CardsDescription");
            ArrayList <Card> playerCards = new ArrayList <Card>();
            playerCards.addAll(Main.player.getHand());

            for (String card : cards.list()) {
                if(card.contains(".json")) {
                    Card check = new Card(card.substring(0, card.length() - ".json".length()));
                    try {
                        check = (Card) Class.forName(check.getType()).getConstructors()[0].newInstance(check.getName());
                    } catch (InstantiationException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) { e.printStackTrace(); }
                    int num = 0;
                    for (Card playerCard : playerCards)
                        if (playerCard.getName().equals(card))
                            num++;
                    if (check.getPrice() <= Main.player.getGems() && playerCards.size() < 15 && num < 2) {
                        ret.add(check);
                    }
                }
            }
        }

        if(arg.length() <= 4) {
            Log.logger("List", "Cards: The ones that we can " + arg);
        }
        return ret;
    }

    public static List<Card> showHandCards(String arg) {
        ArrayList<Card> ret = new ArrayList<Card>();
        File file = new File("src/Cards/CardsInfo/CardsDescription");
        for(String name : file.list()){
            if(name.contains(".json")) {
                Card card = new Card(name.substring(0, name.length() - ".json".length()));
                try {
                    ret.add((Card) Class.forName("Cards." + card.getType()).getConstructors()[0].newInstance(card.getName()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) { e.printStackTrace(); }
            }
        }

        if(!arg.equals("a")){
            ArrayList <Card> hand = new ArrayList<>();
            hand.addAll(Main.player.getHand());

            for(int i = 0 ; i < ret.size() ; i++){
                Card retCard = ret.get(i);
                boolean flag = false;
                for(Card handCard : hand)
                    if(handCard.getName().equals(retCard.getName()))
                        flag = true;
                if((arg.equals("n") && flag) || (arg.equals("m") && !flag)) {
                    ret.remove(i);
                    i--;
                }
            }
        }

        return ret;
    }

    public static void commandHandler() {
        Scenes.enterScene();
    }

}