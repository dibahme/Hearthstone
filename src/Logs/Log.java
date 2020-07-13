package Logs;

import Controller.Main;
import Player.Player;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class Log {
    private static final Date date = new Date();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");
    private static File path = null;
    private static FileWriter fileWriter = null;
    private static Player player = Main.player;

    public static void logger(String event , String description){
        path = new File("src/Logs/" + player.getUsername() + player.getRegistrationDate().replace(':' , '.') + ".txt");
        if(event.equals("Sign_Up")) {
            try {
                path.createNewFile();
                fileWriter = new FileWriter(path , true);
                fileWriter.write("USER: " + player.getUsername() + "\nCREATED_AT: " + player.getRegistrationDate() + "\nPASSWORD: " + player.getPassword() + "\n\n");
                fileWriter.close();
                logger("Sign_In" , player.getUsername());
                return;
            }
            catch(Exception ignored){ ignored.printStackTrace();}
        }

        try {
            fileWriter = new FileWriter(path , true);
            fileWriter.write(event + " " + sdf.format(date) + " " + description + "\n");
            fileWriter.close();
        } catch (Exception ignored) {}

    }
}