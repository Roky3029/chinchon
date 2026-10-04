package postClosing;

import dataStructures.Card;

import java.io.Serializable;
import java.util.List;

public final class RoundResult implements Serializable {
    public final int[] points;               // what each player adds upon ending the totality of the round
    public final List<List<Card>> table;     // final groups, including the added cards
    public final List<Integer> groupOwner;   // the index of the user who finished the round (0 == owner, 1 == remote)
    public final List<Placement> placements; // order in which groups were built

    public RoundResult(int[] points, List<List<Card>> table, List<Integer> groupOwner, List<Placement> placements) {
        this.points = points;
        this.table = table;
        this.groupOwner = groupOwner;
        this.placements = placements;
    }
}