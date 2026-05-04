import java.util.InputMismatchException;

public class Card {
    private final String suit;
    private final int value;
    private final int numericalSuit;

    public Card(String s, int v){
        this.suit = s;
        this.value = v;

        if(!s.equals("B") && !s.equals("O") && !s.equals("E") && !s.equals("C")) throw new InputMismatchException("The suit should be either B, O, E or C");

        switch (suit) {
            case "B" -> numericalSuit = 0; // Bastos
            case "O" -> numericalSuit = 1; // Oros
            case "E" -> numericalSuit = 2; // Espadas
            default -> numericalSuit = 3; // Copas
        }
    }

    public String getSuit(){
        return suit;
    }

    public int getValue(){
        return value;
    }
    public int getNumericalSuit(){
        return numericalSuit;
    }

    @Override
    public String toString(){
        String ANSIColor = switch (suit) {
            case "B" -> ANSICodes.ANSI_BLUE; // Bastos
            case "O" -> ANSICodes.ANSI_YELLOW; // Oros
            case "E" -> ANSICodes.ANSI_GREEN; // Espadas
            default -> ANSICodes.ANSI_RED; // Copas
        };

        return ANSIColor + getSuit() + getValue() + ANSICodes.ANSI_RESET;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Card o)) return false;

        return this.suit.equals(o.suit) && this.value == o.value;
    }

    public int compareTo(Card other){
        // Returns positive if this is greater than other

        int numericalSubstraction = this.numericalSuit - other.numericalSuit; // Value will be positive if this has a numerical value which is greater
        // The sorting order will follow the order: bastos, oros, espadas and then copas
        if(numericalSubstraction != 0) return numericalSubstraction;

        return this.getValue() - other.getValue();
    }
}
