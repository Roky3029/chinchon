import dataStructures.Card;
import dataStructures.Hand;
import dataStructures.ListPOI;
import utilities.ANSICodes;
import utilities.GameData;


import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

import static utilities.Helpers.*;

public class GameFlow {
    public static void clearTerminal(PrintWriter printer){
        if(printer == null){
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } else {
            sendMsg(printer, "\033[H\033[2J");
        }
    }

    public static void getFromDiscardPile(Hand cards, Card topDiscard){
        cards.add(topDiscard);
    }

    public static void getFromLibrary(ListPOI<Card> library, Hand cards, PrintWriter printer){
        Card randomCard = library.getRandom();
        cards.add(randomCard);

        if(printer == null) print("\t You've obtained the " + randomCard);
        else sendMsg(printer, "\t You've obtained the " + randomCard + "\n");

        library.remove(randomCard);
    }

    public static void finishRound(PrintWriter printer, boolean isHostPlayer, Hand playerCards, Hand cpuCards, GameData gd, Scanner sc){
        int deadwood = GameLogic.findBestGrouping(playerCards, true);

        if (deadwood > 3) {
            if(isHostPlayer) print("Sorry, you cannot finish the round. Your actual deadwood is " + deadwood);
            else sendMsg(printer, "Sorry, you cannot finish the round. Your actual deadwood is " + deadwood + "\n");

            gd.setDiscard(false);
        } else {
            String currentDeadwood = deadwood == -30
                    ? ANSICodes.ANSI_YELLOW + "KEEP IN MIND: You have Chinchon, which automatically wins the whole game" + ANSICodes.ANSI_RESET
                    : "Your current deadwood is " + deadwood;

            if(isHostPlayer){
                print("Are you sure you want to finish the round (1 = yes; 0 = no)? " + currentDeadwood);
                print(deadwood == 0 ? " (-10 point bonus)\n" : "\n");
            } else {
                sendMsg(printer, "Are you sure you want to finish the round (1 = yes; 0 = no)? " + currentDeadwood);
                sendMsg(printer, deadwood == 0 ? " (-10 point bonus)\n" : "\n");
            }

            int confirmation = -1;
            while (confirmation != 0 && confirmation != 1) {
                try {
                    confirmation = sc.nextInt();
                } catch (InputMismatchException e) {
                    confirmation = -1;
                }
            }

            if (confirmation == 1) {
                gd.setKeepPlaying(false);
                gd.setDiscard(false);
                gd.setCpuDebt(gd.getCpuDebt() + GameLogic.findBestGrouping(cpuCards, true));
                gd.setPlayerDebt(gd.getPlayerDebt() + (deadwood == 0 ? -10 : deadwood));

                if(isHostPlayer) sendMsg(printer, "Host player has decided to finish the round...\n");
                else print("The other player has decided to finish the round...\n");

                if(deadwood == -30){
                    if(printer == null){
                        // Chinchon for the host player
                        sendBoth(printer, ANSICodes.ANSI_BLUE + "Host player wins by CHINCHON with " + ANSICodes.ANSI_RESET + playerCards);
                    } else if(printer != null){
                        sendBoth(printer, ANSICodes.ANSI_BLUE + "Remote player wins by CHINCHON with " + ANSICodes.ANSI_RESET + playerCards);
                    }

                    System.exit(0);
                }

                String[] ls = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\t SCOREBOARD\n",
                        "\t Your points: " + gd.getPlayerDebt() + "\n", "\t Other player's points: " + gd.getCpuDebt() + "\n"};

                for (String l : ls) sendBoth(printer, l);

                if(isHostPlayer){
                    if (gd.getPlayerDebt() >= 102) {
                        print(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if (gd.getCpuDebt() >= 102) {
                        print(ANSICodes.ANSI_GREEN + "The remote player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET + "\n");
                        sendMsg(printer, ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                } else {
                    if (gd.getPlayerDebt() >= 102) {
                        // Remote player loses
                        sendMsg(printer, ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET + "\n");
                        print(ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if (gd.getCpuDebt() >= 102) {
                        // Remote player wins
                        sendMsg(printer, ANSICodes.ANSI_GREEN + "The remote player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET + "\n");
                        print(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                }
            } else {
                gd.setDiscard(false);
            }
            if(!isHostPlayer) gd.setDiscard(false);
        }
    }
}
