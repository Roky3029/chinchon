import java.util.Comparator;
import java.util.List;

public class CustomSortings {
    public static List<Card> sortHandBySuit(List<Card> hand){
        class Sorting implements Comparator<Card> {
            public int compare(Card a, Card b){
                return a.compareTo(b);
            }
        }
        hand.sort(new Sorting());
        return hand;
    }

    public static List<Card> sortHandByNumbers(List<Card> hand){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.getValue() - b.getValue();
            }
        }
        hand.sort(new Sorting());
        return hand;
    }

    public static List<Card> getSortedHandBySuit(List<Card> hand){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.compareTo(b);
            }
        }
        return hand.stream().sorted(new Sorting()).toList();
    }

    public static List<Card> getSortedHandByNumbers(List<Card> hand){
        class Sorting implements Comparator<Card>{
            public int compare(Card a, Card b){
                return a.getValue() - b.getValue();
            }
        }
        return hand.stream().sorted(new Sorting()).toList();
    }
}
