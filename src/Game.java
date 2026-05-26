import dataStructures.LinkedListPOI;
import dataStructures.ListPOI;

import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

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
            playerCards.add(new Card("B", 11));
            playerCards.add(new Card("O", 11));
            playerCards.add(new Card("E", 11));

            library.remove(new Card("C", 1));
            library.remove(new Card("C", 4));
            library.remove(new Card("C", 2));
            library.remove(new Card("C", 3));
            library.remove(new Card("B", 11));
            library.remove(new Card("O", 11));
            library.remove(new Card("E", 11));

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
    public void setKeepPlaying(boolean b){this.keepPlaying = b;}

    public void playerTurn(PrintWriter printer){
        ANSICodes.clearTerminal();
        boolean discard = true;
        String[] lines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------\n", "\n", "\n", "\n",
                "\t Top of discard pile: " + topDiscard + "\n", "\t Your hand: " + playerCards + "\n",
                "\t Do you wish to get the discarded card (1), take one from the library (2) or finish the round (3)? \n"};
        String criticalLine = "\t Your hand: " + playerCards + "\n";

        int i = 0;
        for(String line : lines){
            System.out.println(line);
            if(i != lines.length - 1 && !line.equals(criticalLine)) {
                printer.print(line);
                printer.flush();
            }
            i++;
        }
        printer.print("\t Your hand: " + cpuCards + "\n");
        printer.flush();

        int action;

        try{
            action = sc.nextInt();
        } catch(InputMismatchException e){
            action = -1;
        }

        while(action < 0 || action > 3){
            System.out.print("Huh? Please input a valid action: ");
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
                System.out.println("\t You've obtained the " + randomCard);
                library.remove(randomCard);
                break;
            case 3:
                int deadwood = GameLogic.findBestGrouping(playerCards);
                if(deadwood > 3){
                    System.out.println("Sorry, you cannot finish the round. Your actual deadwood is " + deadwood);
                    discard = false;
                    break;
                }
                System.out.print("Are you sure you want to finish the round (1 = yes; 0 = no)? Your current deadwood is " + deadwood);
                System.out.println(deadwood == 0 ? " (-10 point bonus)" : "");
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
                    cpuDebt += GameLogic.findBestGrouping(cpuCards);
                    playerDebt += deadwood == 0 ? -10 : deadwood;

                    System.out.println("---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------");
                    System.out.println();
                    System.out.println();
                    System.out.println("\t SCOREBOARD");
                    System.out.println("\t Player's points: " + playerDebt);
                    System.out.println("\t CPU's points: " + cpuDebt);

                    if(playerDebt >= 102) {
                        System.out.println(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if(cpuDebt >= 102){
                        System.out.println(ANSICodes.ANSI_GREEN + "The CPU exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                }

                discard = false;
        }

        if(!discard) return;

        System.out.print("\t What card do you wish to discard (Separate suit and number by a space)? ");
        String suit = String.valueOf(sc.next().charAt(0));
        int num = sc.nextInt();
        Card c = new Card(suit, num);
        while(!playerCards.containsCard(c)){
            System.out.print("Huh? You do not have that card in your hand. Please input a valid card: ");
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
            if(i != lines.length - 1 && !line.equals(criticalLine)) {
                System.out.println(line);
            }
            printer.print(line);
            printer.flush();
            i++;
        }

        System.out.print("\t Your hand: " + playerCards + "\n");

//        Card randomCard = library.getRandom();

        int action;

        try{
            action = scanner.nextInt();
        } catch(InputMismatchException e){
//            System.out.println("Error reading the input. Please try again");
            action = -1;
        }

        printer.println(action);
        printer.flush();

        while(action < 0 || action > 3){
            printer.println("Huh? Please input a valid action: ");
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
                printer.println("\t You've obtained the " + randomCard);
                library.remove(randomCard);
                break;
            case 3:
                int deadwood = GameLogic.findBestGrouping(cpuCards);
                if(deadwood > 3){
                    printer.println("Sorry, you cannot finish the round. Your actual deadwood is " + deadwood);
                    discard = false;
                    break;
                }
                printer.print("Are you sure you want to finish the round (1 = yes; 0 = no)? Your current deadwood is " + deadwood);
                printer.println(deadwood == 0 ? " (-10 point bonus)" : "");
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
                    playerDebt += GameLogic.findBestGrouping(playerCards);
                    cpuDebt += deadwood == 0 ? -10 : deadwood;

                    String[] finishLines = {"---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------", "", "", "\t SCOREBOARD", "\t Player's points: " + playerDebt,
                            "\t CPU's points: " + cpuDebt};

                    for(String line : finishLines) {
                        System.out.println(line);
                        printer.println(line);
                    }

                    if(playerDebt >= 102) {
                        System.out.println(ANSICodes.ANSI_GREEN + "The server's player exceeded the threshold of 101 points. You WIN :D" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    } else if(cpuDebt >= 102){
                        System.out.println(ANSICodes.ANSI_RED + "You exceeded the threshold of 101 points. You lose :(" + ANSICodes.ANSI_RESET);
                        System.exit(0);
                    }
                }

                discard = false;
        }

        if(!discard) return;

        printer.print("\t What card do you wish to discard (Separate suit and number by a space)? ");
        String suit = String.valueOf(scanner.next().charAt(0));
        int num = scanner.nextInt();
        Card c = new Card(suit, num);
        while(!cpuCards.containsCard(c)){
            printer.print("Huh? You do not have that card in your hand. Please input a valid card: ");
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
//    public static void main() {
//        int playerDebt = 0, cpuDebt = 0;
//        Game game = new Game(true, playerDebt, cpuDebt);
//
//        // After shuffling and giving cards to each player, we must get the first card to be in the discard pile
//        topDiscard = library.getRandom();
//        library.remove(topDiscard);
//
//        Scanner in = new Scanner(System.in);
//        String anotherRound;
//        boolean playAnotherRound = true;
//
////        System.out.println("--------Test-----");
////        Server sv = new Server();
////        System.out.println("Sending message to client...");
////        sv.sendMessageToClient("Hola! Mensaje desde Game.java");
//
//        while(playAnotherRound){
//            playerCards.sortHandBySuit();
//            cpuCards.sortHandByNumbers();
//            playerTurn(null);
//            playerCards.sortHandBySuit();
//            cpuCards.sortHandByNumbers();
//            if(keepPlaying) remotePlayerTurn(null);
//
//            if(!keepPlaying){
//                System.out.println("Want to play another round of Chinchon? (Y/n)");
//                anotherRound = in.nextLine();
//                playAnotherRound = !anotherRound.equalsIgnoreCase("n");
//                game = new Game(true, game.getPlayerDebt(), game.getCpuDebt());
//                keepPlaying = true;
//            }
//        }
//    }
}
