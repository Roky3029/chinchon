import dataStructures.LinkedListPOI;
import dataStructures.ListPOI;

import java.util.Scanner;
import java.util.Arrays;

@SuppressWarnings("UnnecessaryModifier")
public class Game {
    private static int[] possibleNums = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    private static String[] possibleSuits = {"B", "C", "O", "E"}; // Bastos, Copas, Oros, Espadas in the Spanish deck
    private static ListPOI<Card> library;
    private static Hand playerCards, cpuCards;
    private static Card topDiscard;
    private static Scanner sc;

    public Game(){
        sc = new Scanner(System.in);
        playerCards = new Hand(new Card[7]);
        cpuCards = new Hand(new Card[7]);
        library = new LinkedListPOI<>();

        for(int n : possibleNums){
            for(String s : possibleSuits){
                library.add(new Card(s, n));
            }
        }

        for(int i = 0; i < playerCards.getSize(); i++){
            int randomNum = possibleNums[(int) (Math.random() * possibleNums.length)];
            String randomSuit = possibleSuits[(int) (Math.random() * possibleSuits.length)];

            Card cardToInsert = new Card(randomSuit, randomNum);
            while(playerCards.containsCard(cardToInsert)){
                randomNum = possibleNums[(int) (Math.random() * possibleNums.length)];
                randomSuit = possibleSuits[(int) (Math.random() * possibleSuits.length)];
                cardToInsert = new Card(randomSuit, randomNum);
            }

            playerCards.add(cardToInsert);
            library.remove(cardToInsert);
        }

        for(int i = 0; i < cpuCards.getSize(); i++){
            int randomNum = possibleNums[(int) (Math.random() * possibleNums.length)];
            String randomSuit = possibleSuits[(int) (Math.random() * possibleSuits.length)];

            Card cardToInsert = new Card(randomSuit, randomNum);
            while(playerCards.containsCard(cardToInsert) || cpuCards.containsCard(cardToInsert)){
                randomNum = possibleNums[(int) (Math.random() * possibleNums.length)];
                randomSuit = possibleSuits[(int) (Math.random() * possibleSuits.length)];
                cardToInsert = new Card(randomSuit, randomNum);
            }

            cpuCards.add(cardToInsert);
            library.remove(cardToInsert);
        }
    }

    private static void printLayout(){
        System.out.println("---------CHINCHON, A TRADITIONAL SPANISH CARD GAME---------");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("\t Top of discard pile: " + topDiscard);
        System.out.println();
        System.out.println("\t Your hand: " + playerCards);
        System.out.println("\t CPU's hand: " + cpuCards);
        System.out.print("\t Do you wish to get the discarded card (1) or take one from the library (2)? ");
        int action = sc.nextInt();
        System.out.print("\t What card do you wish to discard (Separate suit and number by a space)? ");
        String card = sc.nextLine();
        System.out.println("You've decided to discard " + card);
    }

    public static void main(){
        Game game = new Game();

        // After shuffling and giving cards to each player, we must get the first card to be in the discard pile
        topDiscard = library.getRandom();
        library.remove(topDiscard);

        System.out.println(library);
        printLayout();
    }
}
