import dataStructures.LinkedListPOI;
import dataStructures.ListPOI;

import java.util.Scanner;

@SuppressWarnings("UnnecessaryModifier")
public class Game {
    public static final int[] possibleNums = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    public static final String[] possibleSuits = {"B", "C", "O", "E"}; // Bastos, Copas, Oros, Espadas in the Spanish deck
    private static ListPOI<Card> library;
    private static Hand playerCards, cpuCards;
    private static Card topDiscard;
    private static Scanner sc;
    private static boolean isPlayerTurn = true;

    public Game(){
        sc = new Scanner(System.in);
        playerCards = new Hand();
        cpuCards = new Hand();
        library = new LinkedListPOI<>();

        for(int n : possibleNums) {
            for (String s : possibleSuits) {
                Card c = new Card(s, n);
                // Chinchon is played with two sets of the Spanish deck, without 8s and 9s
                library.add(c);
                library.add(c);
            }
        }

        Helpers.dealCards(playerCards, possibleNums, possibleSuits, library);
        Helpers.dealCards(cpuCards, possibleNums, possibleSuits, library);
    }

    private static void playerTurn(){
        ANSICodes.clearTerminal();

        System.out.println("---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------");
        System.out.println();
        System.out.println();
        System.out.println("\t Top of discard pile: " + topDiscard);
        System.out.println();
        System.out.println("\t Your hand: " + playerCards);
        System.out.println("\t CPU's hand: " + cpuCards);
        System.out.print("\t Do you wish to get the discarded card (1) or take one from the library (2)? ");
        int action = sc.nextInt();

        while(action != 1 && action != 2){
            System.out.print("Huh? Please input a valid action: ");
            action = sc.nextInt();
        }

        if(action == 1){
            playerCards.add(topDiscard);
        } else {
            Card randomCard = library.getRandom();
            playerCards.add(randomCard);
            System.out.println("\t You've obtained the " + randomCard);
            library.remove(randomCard);
        }

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
        isPlayerTurn = false;
    }

    public static void simulateCpu() throws InterruptedException{
        ANSICodes.clearTerminal();
        System.out.println("---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------");
        System.out.println();
        System.out.println();
        System.out.println("\t Top of discard pile: " + topDiscard);
        System.out.println();
        System.out.println("\t Your hand: " + playerCards);
        System.out.println("\t CPU's hand: " + cpuCards);
        Thread.sleep(1000);
        System.out.print("\t CPU chooses to get one from the library...");

        Card randomCard = library.getRandom();
        cpuCards.add(randomCard);
        library.remove(randomCard);


        Thread.sleep(1000);
        Card c = cpuCards.getRandom();
        System.out.print("\t CPU chooses to discard " + c + "\n");
        topDiscard = c;
        cpuCards.remove(c);
        isPlayerTurn = true;
    }

    public static void main() throws InterruptedException{
        Game game = new Game();

        // After shuffling and giving cards to each player, we must get the first card to be in the discard pile
        topDiscard = library.getRandom();
        library.remove(topDiscard);

        while(true){
            playerCards.sortHandBySuit();
            cpuCards.sortHandByNumbers();
            playerTurn();
            playerCards.sortHandBySuit();
            cpuCards.sortHandByNumbers();
            simulateCpu();
        }
    }
}
