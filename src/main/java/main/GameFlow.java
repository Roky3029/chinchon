package main;

import dataStructures.Card;
import dataStructures.Hand;
import dataStructures.ListPOI;
import postClosing.Operations;
import postClosing.RoundResult;
import utilities.ANSICodes;
import utilities.GameData;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static utilities.Helpers.*;

public class GameFlow implements Serializable {
    public static void clearTerminal(PrintWriter printer){
        if(printer == null){
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } else {
            sendMsg(printer, "\033[H\033[2J");
        }
    }

    public static void printGameHeader(Card topDiscard, Hand userCards, Hand remoteCards, PrintWriter printer, boolean isRemotePlayer){
        String[] lines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\n",
                "\t Top of discard pile: " + topDiscard + "\n", "\t Your hand: " + userCards + "\n",
                "\t Do you wish to get the discarded card (1) or take one from the library (2)? \n"};
        String criticalLine = "\t Your hand: " + userCards + "\n";

        int i = 0;
        for(String line : lines){
            boolean b = i != lines.length - 1 && !line.equals(criticalLine);
            if(!isRemotePlayer){
                print(line);
                if(b) sendMsg(printer, line);
            } else {
                if(b) print(line + "\n");
                sendMsg(printer, line);
            }
            
            i++;
        }

        if(!isRemotePlayer) sendMsg(printer, "\t Your hand: " + remoteCards + "\n");
        else print("\t Your hand: " + remoteCards + "\n");
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

        if (deadwood <= 3) {
            String[] text = {"You could finish the round right now, do you want to do so? (1 = yes; 0 = no)? ", " (-10 point bonus)\n"};
            String currentDeadwood = deadwood == -30
                    ? ANSICodes.ANSI_YELLOW + "KEEP IN MIND: You have Chinchon, which automatically wins the whole game" + ANSICodes.ANSI_RESET
                    : "Your current deadwood is " + deadwood;

            if(isHostPlayer){
                print(text[0] + currentDeadwood);
                print(deadwood == 0 ? text[1] : "\n");
            } else {
                sendMsg(printer, text[0] + currentDeadwood);
                sendMsg(printer, deadwood == 0 ? text[1] : "\n");
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

                List<Hand> hands = new ArrayList<>();
                hands.add(playerCards); hands.add(cpuCards);
                RoundResult r = Operations.resolveRound(hands, 0);

                // If the player closes with -10, you cannot add cards to the final groups, therefore any card's value you don't have grouped is added to your count
                if(deadwood == 0) {
                    // The player closes with -10
                    gd.setCpuDebt(gd.getCpuDebt() + GameLogic.findBestGrouping(cpuCards, true));
                    gd.setPlayerDebt(gd.getPlayerDebt() + (deadwood == 0 ? -10 : deadwood));
                } else {
                    gd.setCpuDebt(gd.getCpuDebt() + r.points[1]);
                    gd.setPlayerDebt(gd.getPlayerDebt() + deadwood == 0 ? -10 : r.points[0]);
                }

                if(isHostPlayer) sendMsg(printer, "Host player has decided to finish the round...\n");
                else print("The other player has decided to finish the round...\n");

                if(deadwood == -30){
                    if(isHostPlayer){
                        // Chinchon for the host player
                        sendBoth(printer, ANSICodes.ANSI_BLUE + "Host player wins by CHINCHON with " + ANSICodes.ANSI_RESET + playerCards);
                    } else sendBoth(printer, ANSICodes.ANSI_BLUE + "Remote player wins by CHINCHON with " + ANSICodes.ANSI_RESET + playerCards);

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
            }
        }
    }
}
