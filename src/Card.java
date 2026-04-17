public class Card {
    private final String suit;
    private final int value;

    public Card(String s, int v){
        this.suit = s;
        this.value = v;
    }

    public String getSuit(){
        return suit;
    }

    public int getValue(){
        return value;
    }

    @Override
    public String toString(){
        String ANSIColor;

        if(suit.equals("B")) ANSIColor = ANSIColors.ANSI_BLUE; // Bastos
        else if(suit.equals("O")) ANSIColor = ANSIColors.ANSI_YELLOW; // Oros
        else if(suit.equals("E")) ANSIColor = ANSIColors.ANSI_GREEN; // Espadas
        else ANSIColor = ANSIColors.ANSI_RED; // Copas

        return ANSIColor + getSuit() + getValue() + ANSIColors.ANSI_RESET;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Card)) return false;

        Card o = (Card)other;

        return this.suit.equals(o.suit) && this.value == o.value;
    }
}
