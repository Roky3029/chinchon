import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    // This method will get all the possible runs and sets given a Hand and compute the most appropiate way to distribute the groups so that the deadwood is minimizedz
    public static int findBestGrouping(Hand cards){
        if(cards.getHand().isEmpty()) return 0;
        int best = 0;
        List<Card> currentHand = new ArrayList<>(cards.getHand());
//        List<Card> handDeadwood = cards.getHand();

        for(Card c : currentHand) best += c.getValue();
        List<List<Card>> allCombinations = Helpers.union(cards.getRuns(), cards.getSets());

        for(List<Card> comb : allCombinations){
            List<Card> newHand = new ArrayList<>(currentHand);
            newHand.removeAll(comb);
            int score = findBestGrouping(new Hand(newHand));
            best = Math.min(best, score);
        }

        for(Card c : currentHand){
            List<Card> newHand = new ArrayList<>(currentHand);
            newHand.remove(c);
            int score = c.getValue() + findBestGrouping(new Hand(newHand));
            best = Math.min(best, score);
        }

        return best;
    }

    public static void main(){
        Hand curr = new Hand();
        curr.add(new Card("O", 5));
        curr.add(new Card("O", 7));
        curr.add(new Card("O", 10));
        curr.add(new Card("O", 11));
        curr.add(new Card("B", 11));
        curr.add(new Card("E", 11));
        curr.add(new Card("C", 11));
        System.out.println(findBestGrouping(curr));
    }
}
