import dataStructures.Card;
import dataStructures.Hand;
import dataStructures.LinkedListPOI;
import dataStructures.ListPOI;
import utilities.ANSICodes;
import utilities.Helpers;

import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

import static utilities.Helpers.sendMsg;
import static utilities.Helpers.print;
import static utilities.Helpers.sendBoth;

@SuppressWarnings("UnnecessaryModifier")
public class Game {
    public static final int[] possibleNums = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    public static final String[] possibleSuits = {"B", "C", "O", "E"}; // Bastos, Copas, Oros, Espadas in the Spanish deck
    private static ListPOI<Card> library;
    private static Hand playerCards, cpuCards;
    private static Card topDiscard;
    private static Scanner sc;
    private static boolean keepPlaying = true;

    private static int playerDebt, cpuDebt;

    public Game(boolean debug, int pD, int cD){
        sc = new Scanner(System.in);
        playerCards = new Hand();
        cpuCards = new Hand();
        library = new LinkedListPOI<>();
        playerDebt = pD;
        cpuDebt = cD;

        for(int n : possibleNums) {
            for (String s : possibleSuits) {
                Card c = new Card(s, n);
                // Chinchon is played with two sets of the Spanish deck, without 8s and 9s
                library.add(c);
                library.add(c);
            }
        }

        if(debug){
            playerCards.add(new Card("C", 1));
            playerCards.add(new Card("C", 4));
            playerCards.add(new Card("C", 2));
            playerCards.add(new Card("C", 3));
            playerCards.add(new Card("C", 5));
            playerCards.add(new Card("C", 6));
            playerCards.add(new Card("C", 7));

            library.remove(new Card("C", 1));
            library.remove(new Card("C", 4));
            library.remove(new Card("C", 2));
            library.remove(new Card("C", 3));
            library.remove(new Card("C", 5));
            library.remove(new Card("C", 6));
            library.remove(new Card("C", 7));

            cpuCards.add(new Card("E", 1));
            cpuCards.add(new Card("E", 4));
            cpuCards.add(new Card("E", 2));
            cpuCards.add(new Card("E", 3));
            cpuCards.add(new Card("B", 10));
            cpuCards.add(new Card("O", 11));
            cpuCards.add(new Card("E", 7));

            library.remove(new Card("C", 1));
            library.remove(new Card("C", 4));
            library.remove(new Card("C", 2));
            library.remove(new Card("C", 3));
            library.remove(new Card("B", 11));
            library.remove(new Card("O", 11));
            library.remove(new Card("E", 11));

        } else {
            Helpers.dealCards(playerCards, possibleNums, possibleSuits, library);
            Helpers.dealCards(cpuCards, possibleNums, possibleSuits, library);
        }
    }

    public int getPlayerDebt(){ return playerDebt; }
    public int getCpuDebt(){ return cpuDebt; }
    public boolean getKeepPlaying() {return keepPlaying;}
    public void setKeepPlaying(boolean b){keepPlaying = b;}

    public void playerTurn(PrintWriter printer){
        ANSICodes.clearTerminal();
        boolean discard = true;
        String[] lines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\n",
                "\t Top of discard pile: " + topDiscard + "\n", "\t Your hand: " + playerCards + "\n",
                "\t Do you wish to get the discarded card (1), take one from the library (2) or finish the round (3)? \n"};
        String criticalLine = "\t Your hand: " + playerCards + "\n";

        int i = 0;
        for(String line : lines){
            print(line);
            if(i != lines.length - 1 && !line.equals(criticalLine)) sendMsg(printer, line);
            i++;
        }
        sendMsg(printer, "\t Your hand: " + cpuCards + "\n");

        int action;

        try{
            action = sc.nextInt();
        } catch(InputMismatchException e){
            action = -1;
        }

        while(action < 0 || action > 3){
            print("Huh? Please input a valid action: ");
            sc.nextLine();
            try{
                action = sc.nextInt();
            } catch(InputMismatchException e){
                action = -1;
            }
        }

        switch(action){
            case 1:
                playerCards.add(topDiscard);
                break;
            case 2:
                Card randomCard = library.getRandom();
                playerCards.add(randomCard);
                print("\t You've obtained the " + randomCard);
                library.remove(randomCard);
                break;
            case 3:
                int deadwood = GameLogic.findBestGrouping(playerCards, true);
                if(deadwood > 3){
                    print("Sorry, you cannot finish the round. Your actual deadwood is " + deadwood);
                    discard = false;
                    break;
                }
                print("Are you sure you want to finish the round (1 = yes; 0 = no)? Your current deadwood is " + deadwood);
                print(deadwood == 0 ? " (-10 point bonus)\n" : "\n");

                int confirmation = -1;
                while(confirmation != 0 && confirmation != 1){
                    try{
                        confirmation = sc.nextInt();
                    } catch(InputMismatchException e){
                        confirmation = -1;
                    }
                }

                if(confirmation == 1) {
                    keepPlaying = false;
                    cpuDebt += GameLogic.findBestGrouping(cpuCards, true);
                    playerDebt += deadwood == 0 ? -10 : deadwood;

                    sendMsg(printer, "Server's player has decided to finish the round...\n");
                    String[] ls = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\t SCOREBOARD\n",
                            "\t Player's points: " + playerDebt + "\n", "\t Remote player's points: " + cpuDebt + "\n"};

                    for(String l : ls) sendBoth(printer, l);

                    if(playerDebt >= 102) {
                        print(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if(cpuDebt >= 102){
                        print(ANSICodes.ANSI_GREEN + "The remote player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                }
                return;
        }

        if(!discard) {
            playerTurn(printer);
            return;
        }

        print("\t What card do you wish to discard? ");
//        String suit = String.valueOf(sc.next().charAt(0));
//        int num = sc.nextInt();
        String input = sc.next();
        String suit = String.valueOf(input.charAt(0));
        int num = Integer.parseInt(input.substring(1));
        Card c = new Card(suit, num);
        while(!playerCards.containsCard(c)){
            print("Huh? You do not have that card in your hand. Please input a valid card: ");
            suit = String.valueOf(sc.next().charAt(0));
            num = sc.nextInt();
            c = new Card(suit, num);
        }
        topDiscard = c;
        playerCards.remove(c);
    }

    public void remotePlayerTurn(PrintWriter printer, Scanner scanner){
        ANSICodes.clearTerminal();
        boolean discard = true;

        if(scanner == null) return;
        if(printer == null) return;

        String[] lines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\n",
                "\t Top of discard pile: " + topDiscard + "\n", "\t Your hand: " + cpuCards + "\n",
                "\t Do you wish to get the discarded card (1), take one from the library (2) or finish the round (3)? \n"};
        String criticalLine = "\t Your hand: " + cpuCards + "\n";

        int i = 0;
        for(String line : lines){
            if(i != lines.length - 1 && !line.equals(criticalLine)) print(line + "\n");
            sendMsg(printer, line);
            i++;
        }

        print("\t Your hand: " + playerCards + "\n");
        int action;

        try{
            action = scanner.nextInt();
        } catch(InputMismatchException e){
            action = -1;
        }

        sendMsg(printer, action);

        while(action < 0 || action > 3){
            sendMsg(printer, "Huh? Please input a valid action: \n");
            scanner.nextLine();
            try{
                action = scanner.nextInt();
            } catch(InputMismatchException e){
                action = -1;
            }
        }

        switch(action){
            case 1:
                cpuCards.add(topDiscard);
                break;
            case 2:
                Card randomCard = library.getRandom();
                cpuCards.add(randomCard);
                sendMsg(printer, "\t You've obtained the " + randomCard + "\n");
                library.remove(randomCard);
                break;
            case 3:
                int deadwood = GameLogic.findBestGrouping(cpuCards, true);
                if(deadwood > 3){
                    sendMsg(printer, "Sorry, you cannot finish the round. Your actual deadwood is " + deadwood + "\n");
                    discard = false;
                    break;
                }
                sendMsg(printer, "Are you sure you want to finish the round (1 = yes; 0 = no)? Your current deadwood is " + deadwood + "\n");
                sendMsg(printer, deadwood == 0 ? " (-10 point bonus)\n" : "\n");

                int confirmation = -1;
                while(confirmation != 0 && confirmation != 1){
                    try{
                        confirmation = scanner.nextInt();
                    } catch(InputMismatchException e){
                        confirmation = -1;
                    }
                }

                if(confirmation == 1) {
                    keepPlaying = false;
                    playerDebt += GameLogic.findBestGrouping(playerCards, true);
                    cpuDebt += deadwood == 0 ? -10 : deadwood;

                    String[] finishLines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------", "", "", "\t SCOREBOARD", "\t Player's points: " + playerDebt,
                            "\t CPU's points: " + cpuDebt};

                    for(String line : finishLines) sendBoth(printer, line);

                    if(playerDebt >= 102) {
                        print(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if(cpuDebt >= 102){
                        print(ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                }

                discard = false;
        }

        if(!discard) {
            remotePlayerTurn(printer, scanner);
            return;
        }

        sendMsg(printer, "\t What card do you wish to discard? \n");
        String input = sc.next();
        String suit = String.valueOf(input.charAt(0));
        int num = Integer.parseInt(input.substring(1));
        Card c = new Card(suit, num);
        while(!cpuCards.containsCard(c)){
            sendMsg(printer, "Huh? You do not have that card in your hand. Please input a valid card: \n");
            suit = String.valueOf(scanner.next().charAt(0));
            num = scanner.nextInt();
            c = new Card(suit, num);
        }
        topDiscard = c;
        cpuCards.remove(c);
    }

    public void getFirstDiscard(){
        topDiscard = library.getRandom();
        library.remove();
    }
}
