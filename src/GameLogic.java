import java.util.List;

public class GameLogic {


    public static int findBestGrouping(List<Card> cards){
        int best = 0;

        for (Card card : cards) best += card.getValue();

        return best;
    }
}
