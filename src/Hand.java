import java.util.*;

public class Hand {
    private final List<Card> hand;
    public static final int STARTING_CARDS = 7;

    public Hand(){
        hand = new ArrayList<>();
    }

    public void add(Card c){
        hand.add(c);
    }

    public int getSize(){
        return hand.size(); // We reserve one space for the step in between obtaining one and discarding
    }

    public Card getRandom(){
        return hand.get((int) (Math.random() * hand.size() - 1));
    }

    public void remove(Card c){
        hand.remove(c);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            if(card != null) sb.append(card).append(" ");
        }

        return sb.toString();
    }

    public List<Card> sortHandBySuit(){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.compareTo(b);
            }
        }
        hand.sort(new Sorting());
        return hand;
    }

    public List<Card> sortHandByNumbers(){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.getValue() - b.getValue();
            }
        }
        hand.sort(new Sorting());
        return hand;
    }

    public List<Card> getSortedHandBySuit(){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.compareTo(b);
            }
        }
        return hand.stream().sorted(new Sorting()).toList();
    }

    public List<Card> getSortedHandByNumbers(){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.getValue() - b.getValue();
            }
        }
        return hand.stream().sorted(new Sorting()).toList();
    }

    public boolean checkCanClose(){
        // Closing in Chinchon is an action that can only be triggered if you have two groups of three cards. Those groups can be either a 3-card straight (same suit) or a same-number trio

        List<Card> suitSort = getSortedHandBySuit();
        List<Card> numberSort = getSortedHandByNumbers();

        System.out.println(numberSort);
        // Checking straights
        int maxStraightFoundLength = 1, straightsFound = 0;
        int prevNum = numberSort.getFirst().getValue(), prevSuit = numberSort.getFirst().getNumericalSuit();
        for(int i = 1; i < numberSort.size(); i++){
            Card current = numberSort.get(i);
            if(current.getValue() == prevNum + 1 && prevSuit == current.getNumericalSuit()){
                maxStraightFoundLength += 1;

                // TODO: implement that straights can be of 4 elements as well
                // TODO: check that after the 7 there is a jump up to 10
                if(maxStraightFoundLength == 3){
                    maxStraightFoundLength = 1;
                    straightsFound += 1;
                }
            }

            prevNum = current.getValue(); prevSuit = current.getNumericalSuit();
        }

        System.out.println("STRAIGHTS FOUND: " + straightsFound);

        return true;
    }

    public static void main(){
        Hand curr = new Hand();
        curr.add(new Card("B", 1));
        curr.add(new Card("B", 2));
        curr.add(new Card("B", 3));
        curr.add(new Card("O", 5));
        curr.add(new Card("O", 4));
        curr.add(new Card("O", 3));
        curr.add(new Card("O", 12));

        curr.checkCanClose();
    }

    public boolean containsCard(Card c){
        return hand.contains(c);
    }
}
