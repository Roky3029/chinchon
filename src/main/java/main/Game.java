package main;

import dataStructures.Card;
import dataStructures.Hand;
import dataStructures.LinkedListPOI;
import dataStructures.ListPOI;
import utilities.GameData;
import utilities.Helpers;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.Scanner;

import static utilities.Helpers.sendMsg;
import static utilities.Helpers.print;

@SuppressWarnings("UnnecessaryModifier")
public class Game implements Serializable {
    public static final int[] possibleNums = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    public static final String[] possibleSuits = {"B", "C", "O", "E"}; // Bastos, Copas, Oros, Espadas in the Spanish deck
    private static ListPOI<Card> library;
    private static Hand playerCards, cpuCards;
    private static Card topDiscard;
    private static Scanner sc;
    private static GameData gd;
    private static boolean hostTurn;

    public Game(boolean debug, int pD, int cD){
        sc = new Scanner(System.in);
        playerCards = new Hand();
        cpuCards = new Hand();
        library = new LinkedListPOI<>();
        gd = new GameData(true, pD, cD);
        hostTurn = true;

        for(int n : possibleNums) {
            for (String s : possibleSuits) {
                Card c = new Card(s, n);
                // Chinchon is played with two sets of the Spanish deck, without 8s and 9s
                library.add(c);
                library.add(c);
            }
        }

        if(debug){
            playerCards.add(new Card("O", 1));
            playerCards.add(new Card("O", 2));
            playerCards.add(new Card("O", 3));
            playerCards.add(new Card("O", 4));
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
            cpuCards.add(new Card("C", 10));

            library.remove(new Card("E", 1));
            library.remove(new Card("E", 4));
            library.remove(new Card("E", 2));
            library.remove(new Card("E", 3));
            library.remove(new Card("B", 10));
            library.remove(new Card("O", 11));
            library.remove(new Card("C", 10));

        } else {
            Helpers.dealCards(playerCards, possibleNums, possibleSuits, library);
            Helpers.dealCards(cpuCards, possibleNums, possibleSuits, library);
        }
    }

    public int getPlayerDebt(){ return gd.getPlayerDebt(); }
    public int getCpuDebt(){ return gd.getCpuDebt(); }
    public boolean getKeepPlaying() {return gd.getKeepPlaying();}
    public void setKeepPlaying(boolean b){gd.setKeepPlaying(b);}
    public boolean isHostTurn(){return hostTurn;}
    public void setHostTurn(boolean h){hostTurn = h;}

    private int getAction(Scanner scanner, PrintWriter printer){
        int action;

        try{
            if(scanner == null && printer == null) action = sc.nextInt();
            else action = scanner.nextInt();
        } catch(InputMismatchException e){
            action = -1;
        }

        while(action < 0 || action > 2){
            if(scanner == null && printer == null) {
                print("Huh? Please input a valid action: ");
                sc.nextLine();
            } else {
                sendMsg(printer, "Huh? Please input a valid action: \n");
                scanner.nextLine();
            }
            
            try{
                if(scanner == null && printer == null) action = sc.nextInt();
                else action = scanner.nextInt();
            } catch(InputMismatchException e){
                action = -1;
            }
        }
        
        return action;
    }

    public void playerTurn(PrintWriter printer){
        GameFlow.clearTerminal(null);

        GameFlow.printGameHeader(topDiscard, playerCards, cpuCards, printer, false);

        int action = getAction(null, null);

        switch (action) {
            case 1 -> GameFlow.getFromDiscardPile(playerCards, topDiscard);
            case 2 -> GameFlow.getFromLibrary(library, playerCards, null);
        }

        print("\t What card do you wish to discard? ");
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

        GameFlow.finishRound(printer, true, playerCards, cpuCards, gd, sc);
        hostTurn = false;
    }

    public void remotePlayerTurn(PrintWriter printer, Scanner scanner){
        if(scanner == null || printer == null) return;

        GameFlow.printGameHeader(topDiscard, cpuCards, playerCards, printer, true);

        int action = getAction(scanner, printer);
        switch (action) {
            case 1 -> GameFlow.getFromDiscardPile(cpuCards, topDiscard);
            case 2 -> GameFlow.getFromLibrary(library, cpuCards, printer);
        }

        sendMsg(printer, "\t What card do you wish to discard? \n");
        String input = scanner.next();
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
        
        GameFlow.finishRound(printer, false, cpuCards, playerCards, gd, scanner);
        hostTurn = true;
    }

    public void getFirstDiscard(){
        topDiscard = library.getRandom();
        library.remove();
    }
}
