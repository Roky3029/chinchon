import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SuppressWarnings("CallToPrintStackTrace")
public class Server {
    private static boolean done;
    private static PrintWriter pw;

    public Server(){
        System.out.println("Waking up the server...");
        connectToServer();
        System.out.println("Server up and running");
    }

    public void sendMessageToClient(String msg){
        pw.println(msg);
    }

    public static void connectToServer() {
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        try(ServerSocket serverSocket = new ServerSocket(9991)) {
            Socket connectionSocket = serverSocket.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, StandardCharsets.UTF_8);
            pw = new PrintWriter(new OutputStreamWriter(outputFromServer, StandardCharsets.UTF_8), true);

            Game game = new Game(false, 0, 0);

//            Game.playerTurn(pw);

            //Have the server take input from the client and echo it back
            //This should be placed in a loop that listens for a terminator text e.g. bye

            while(game.getKeepPlaying()){
                done = false;
                game.playerTurn(pw);
                game.remotePlayerTurn(pw, scanner);
//                while(!done && scanner.hasNextLine()) {
//                    String line = scanner.nextLine();
//                    pw.println("Echo from <Your Name Here> Server: " + line);
//
//                    if(line.toLowerCase().trim().equals("peace")) {
//                        done = true;
//                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(){
        Server sv = new Server();
    }
}