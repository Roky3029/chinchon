import dataStructures.ListPOI;

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
}
