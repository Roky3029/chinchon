import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SuppressWarnings("CallToPrintStackTrace")
public class Server {

//    public Server(){
//        System.out.println("Waking up the server...");
//        connectToServer();
//        System.out.println("Server up and running");
//    }

//    public static void sendMessageToClient(String msg){
//        pw.println(msg);
//        pw.flush();
//    }

    public static void connectToServer() {
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        try(ServerSocket serverSocket = new ServerSocket(9991)) {
            Socket connectionSocket = serverSocket.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, StandardCharsets.UTF_8);
            Scanner sc = new Scanner(System.in);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputFromServer, StandardCharsets.UTF_8), true);

            // --------------------------------------------------------------------
            int playerDebt = 0, cpuDebt = 0;
            Game game = new Game(false, playerDebt, cpuDebt);

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

            while(game.getKeepPlaying()){
                game.playerTurn(pw);
                game.remotePlayerTurn(pw, scanner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(){
        System.out.println("Waking up the server...");
        Server.connectToServer();
    }
}