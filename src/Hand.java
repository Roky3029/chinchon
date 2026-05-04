import java.util.*;

@SuppressWarnings("CollectionAddedToSelf")
public class Hand {
    private final List<Card> hand;
    public static final int STARTING_CARDS = 7;

    public Hand(){
        hand = new ArrayList<>();
    }

    public void add(Card c){
        hand.add(c);
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

//    public List<Card> sortHandBySuit(){
//        class Sorting implements Comparator<Card>{
//            public int compare(Card a, Card b){
//                return a.compareTo(b);
//            }
//        }
//        hand.sort(new Sorting());
//        return hand;
//    }
//
//    public List<Card> sortHandByNumbers(){
//        class Sorting implements Comparator<Card>{
//            public int compare(Card a, Card b){
//                return a.getValue() - b.getValue();
//            }
//        }
//        hand.sort(new Sorting());
//        return hand;
//    }
//
//    public List<Card> getSortedHandBySuit(){
//        class Sorting implements Comparator<Card>{
//            public int compare(Card a, Card b){
//                return a.compareTo(b);
//            }
//        }
//        return hand.stream().sorted(new Sorting()).toList();
//    }
//
//    public List<Card> getSortedHandByNumbers(){
//        class Sorting implements Comparator<Card>{
//            public int compare(Card a, Card b){
//                return a.getValue() - b.getValue();
//            }
//        }
//        return hand.stream().sorted(new Sorting()).toList();
//    }

    public List<List<Card>> getSets(){
        Map<Integer, List<Card>> byValue = new HashMap<>();

        for(Card c : hand){
            if(!byValue.containsKey(c.getValue())) byValue.put(c.getValue(), new ArrayList<>());
            byValue.get(c.getValue()).add(c);
        }

        List<List<Card>> sets = new ArrayList<>();

        for(List<Card> c : byValue.values()){
            if(c.size() >= 3){
                int n = c.size();
                for(int mask = 0; mask < (1 << n); mask++){
                    List<Card> subset = new ArrayList<>();
                    for(int i = 0; i < n; i++){
                        if((mask & (1 << i)) != 0) subset.add(c.get(i));
                    }
                    if(subset.size() >= 3) sets.add(subset);
                }
            }
        }

        return sets;
    }

    public List<List<Card>> getRuns(){
        Map<Integer, List<Card>> bySuit = new HashMap<>(); // Recall that 0 = bastos; 1 = oros; 2 = espadas and 3 = copas

        for(Card c : hand){
            if(!bySuit.containsKey(c.getNumericalSuit())) bySuit.put(c.getNumericalSuit(), new ArrayList<>());
            bySuit.get(c.getNumericalSuit()).add(c);
        }

        List<List<Card>> sets = new ArrayList<>();

        for(List<Card> c : bySuit.values()){
            for(int j = 0; j < c.size(); j++){
                if(c.size() - j >= 3){
                    int n = c.size();
                    // We sort the cards with the same suit so that it eases the task of checking for runs
                    Comparator<Card> sortByNumber = Card::compareTo;
                    c.sort(sortByNumber);

                    List<Card> subset = new ArrayList<>();
                    int prevValue = -1;
                    for(int i = j; i < n; i++){
                        Card current = c.get(i);
                        if(prevValue > 0){
                            if(prevValue == current.getValue() - 1) subset.add(current);
                            else if(prevValue == current.getValue()){
                                continue;
                            }else {
                                subset.removeAll(subset);
                                subset.add(current);
                            }
                        } else {
                            subset.add(current);
                        }

                        prevValue = current.getValue();

//                        System.out.println("executing ahh. size = " + subset.size());
//                        System.out.println("subset: " + subset);
                        if(subset.size() >= 3 && !sets.contains(subset)) sets.add(new ArrayList<>(subset));
                    }
//                System.out.println(sets);
                }
            }
        }
//
        return sets;
    }

    public static void main(){
        Hand curr = new Hand();
        curr.add(new Card("O", 2));
        curr.add(new Card("O", 3));
        curr.add(new Card("O", 3));
        curr.add(new Card("O", 4));
        System.out.println(curr.getRuns());
    }

    public boolean containsCard(Card c){
        return hand.contains(c);
    }
}
