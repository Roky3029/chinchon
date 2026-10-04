package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server implements Serializable {
    public static void main(String[] args) {
        System.out.println("Attempting to  create a server...");
        System.out.println("Player 2, connect via the command 'nc <host ip> 9991'");
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        try(ServerSocket serverSocket = new ServerSocket(9991)) {
            Socket connectionSocket = serverSocket.accept();

            //Create Input & Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, StandardCharsets.UTF_8);
            Scanner sc = new Scanner(System.in);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputFromServer, StandardCharsets.UTF_8), true);

            // --------------------------------------------------------------------

            Game game = SaveFile.loadSavedGame(sc);

            if(game == null){
                game = new Game(true, 0, 0);
                game.getFirstDiscard();
            }
            SaveFile.save(game);

            // After giving cards to each player, we must get the first discard in the discard pile
            boolean playAnotherRound = true;

            while(playAnotherRound){
                boolean hostTurn = game.isHostTurn();
                try{
                    if(hostTurn){
                        GameFlow.clearTerminal(null);
                        game.playerTurn(pw);
                    } else {
                        GameFlow.clearTerminal(pw);
                        game.remotePlayerTurn(pw, scanner);
                    }
                }catch (NoSuchElementException e){
                    if(hostTurn){
                        pw.println("The host left. The game has been saved");
                        System.out.println("Input closed. Game paused, restart the server");
                    } else {
                        System.out.println("Player 2 disconnected. Game paused, restart the server to resume");
                    }
                    return;
                }
                if(game.getKeepPlaying()) {
                    game.setHostTurn(!hostTurn);
                    SaveFile.save(game);
                } else {
                    System.out.println("Want to play another round of Chinchon? (Y/n)");
                    playAnotherRound = !sc.nextLine().equalsIgnoreCase("n");
                    game = new Game(true, game.getPlayerDebt(), game.getCpuDebt());
                    game.getFirstDiscard();
                    game.setKeepPlaying(true);
                    if(playAnotherRound) SaveFile.save(game);
                    else SaveFile.deleteSave();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Connection problem: " + e.getMessage());
        }
    }
}