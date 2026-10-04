package postClosing;

import dataStructures.Card;

import java.io.Serializable;

public final class Placement implements Serializable {
    public final int player;     // index in `hands` of the player laying the card off
    public final Card card;
    public final int groupIndex; // index in RoundResult.table
    public Placement(int player, Card card, int groupIndex) {
        this.player = player; this.card = card; this.groupIndex = groupIndex;
    }
}