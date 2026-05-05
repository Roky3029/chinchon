import dataStructures.ListPOI;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

public class Helpers {
    public static void dealCards(Hand cards, int[] nums, String[] suits, ListPOI<Card> library){
        for(int i = 0; i < Hand.STARTING_CARDS; i++){
            int randomNum = nums[(int) (Math.random() * nums.length)];
            String randomSuit = suits[(int) (Math.random() * suits.length)];

            Card cardToInsert = new Card(randomSuit, randomNum);
            while(cards.containsCard(cardToInsert)){
                randomNum = nums[(int) (Math.random() * nums.length)];
                randomSuit = suits[(int) (Math.random() * suits.length)];
                cardToInsert = new Card(randomSuit, randomNum);
            }

            cards.add(cardToInsert);
            library.remove(cardToInsert);
        }
    }

    public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }
}
