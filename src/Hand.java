import java.util.Arrays;

public class Hand {
    private final Card[] hand;
    int index = 0;

    public Hand(Card[] h){
        this.hand = h;
    }

    public void add(Card c){
        hand[index] = c;
        index++;
    }

    public int getSize(){
        return hand.length;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card).append(" ");
        }

        return sb.toString();
    }

    public boolean containsCard(Card c){
        return Arrays.asList(hand).contains(c);
    }
}
