package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import org.json.simple.JSONObject;

public class SaveFile implements Serializable{
    private static final Path SAVE = Path.of("save.dat");

    public static void save(Game game){
        Path tmp = Path.of(SAVE + ".tmp"); // We create a temporal save file in case it crashes mid-game
        try{
            // Tries to write the Game object into the file save.dat
            try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tmp))){
                out.writeObject(game);
            }
//
//            JSONObject json = new JSONObject();
//            // Save the data we want to save
//            json.put("")

            Files.move(tmp, SAVE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }catch (IOException e){
            System.out.println("Warning! Could not save the game: " + e.getMessage());
        }
    }

    public static Game loadSavedGame(Scanner sc){
        if(!Files.exists(SAVE)) return null;
        System.out.println("Saved game found. Resume it? (Y/n)");
        if(sc.nextLine().equalsIgnoreCase("n")) return null;

        try(ObjectInputStream in = new ObjectInputStream(Files.newInputStream(SAVE))){
            return (Game) in.readObject();
        } catch(IOException | ClassNotFoundException e){
            System.out.println("Could not read the save file! Output: " + e.getMessage());
            return null;
        }
    }

    public static void deleteSave(){
        try{
            Files.deleteIfExists(SAVE);
        } catch(IOException ignored){}
    }

    public static void main(String[] args){
//        Game g = new Game(true, 0, 0);
//        g.getFirstDiscard();
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try{
//            try(ObjectOutputStream out = new ObjectOutputStream(bos)){
//                out.writeObject(g);
//            }
//            try(ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))){
//                Game copy = (Game) in.readObject();
//            }
//        } catch(Exception e){
//            System.out.println(e.getMessage());
//        }
    }
}
