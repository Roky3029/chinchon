package main;

import dataStructures.Card;
import dataStructures.Hand;
import utilities.Helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameLogic implements Serializable {
    public static List<Card> removeEach(List<Card> cards, List<Card> toRemove){
        List<Card> result = new ArrayList<>(cards);
        for(Card c : toRemove) result.remove(c);
        return result;
    }

    // This method will get all the possible runs and sets given a dataStructures.Hand and compute the most appropiate way to distribute the groups so that the deadwood is minimizedz
    public static int findBestGrouping(Hand cards, boolean searchForChinchon){
        if(cards.getHand().isEmpty()) return 0;
        int best = 0;
        List<Card> currentHand = new ArrayList<>(cards.getHand());

        // Chinchon detection
        boolean isChinchon = true;
        Card lastCard = null;
        // Take advantage of this loop to, at the same time, check for chinchon
        for(Card c : currentHand){
            best += c.getValue();

            if(isChinchon && searchForChinchon){
                if(lastCard == null) lastCard = c;
                else if (lastCard.getNumericalSuit() != c.getNumericalSuit()
                        || (lastCard.getValue() != c.getValue() - 1
                        && (c.getValue() != 10 || lastCard.getValue() != 7))){
                    // They must be in consecutive order, recalling that a 10 is the consecutive number of 7
                    isChinchon = false;
                } else lastCard = c;
            }
        }

        if(isChinchon && searchForChinchon) return -30;// Random value to determine that -30 is the code equivalent value for chinchon
        List<List<Card>> allCombinations = Helpers.union(cards.getRuns(), cards.getSets());

        for(List<Card> comb : allCombinations){
//            List<Card> newHand = new ArrayList<>(currentHand);
//            newHand.removeAll(comb);
//            int score = findBestGrouping(new Hand(newHand), false);
//            best = Math.min(best, score);
            List<Card> newHand = removeEach(currentHand, comb);
            int score = findBestGrouping(new Hand(newHand), false);
            best = Math.min(best, score);
        }

        for(Card c : currentHand){
            List<Card> newHand = new ArrayList<>(currentHand);
            newHand.remove(c);
            int score = c.getValue() + findBestGrouping(new Hand(newHand), false);
            best = Math.min(best, score);
        }

        return best;
    }

    public static void main(){
        Hand curr = new Hand();
        curr.add(new Card("O", 4));
        curr.add(new Card("O", 5));
        curr.add(new Card("O", 6));
        curr.add(new Card("O", 7));
        curr.add(new Card("O", 10));
        curr.add(new Card("O", 11));
        curr.add(new Card("O", 12));
        System.out.println(findBestGrouping(curr, true));
    }
}
