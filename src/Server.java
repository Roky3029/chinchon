import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

@SuppressWarnings("CallToPrintStackTrace")
public class Server {
    public static void main() {
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
            int playerDebt = 0, cpuDebt = 0;
            Game game = new Game(true, playerDebt, cpuDebt);

            // After giving cards to each player, we must get the first discard in the discard pile
            game.getFirstDiscard();
            String anotherRound;
            boolean playAnotherRound = true;

            while(playAnotherRound){
                game.playerTurn(pw);
                if(game.getKeepPlaying()) game.remotePlayerTurn(pw, scanner);

                if(!game.getKeepPlaying()){
                    String line = "Want to play another round of Chinchon? (Y/n)";
                    System.out.println(line);
                    anotherRound = sc.nextLine();
                    playAnotherRound = !anotherRound.equalsIgnoreCase("n");
                    game = new Game(true, game.getPlayerDebt(), game.getCpuDebt());
                    game.setKeepPlaying(true);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Player disconnected");
        }
    }
}