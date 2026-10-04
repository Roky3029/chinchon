package postClosing;

import dataStructures.Card;

import java.io.Serializable;
import java.util.List;

public final class Plan implements Serializable {
    protected final int points;
    protected final List<List<Card>> melds;
    protected Plan(int points, List<List<Card>> melds) { this.points = points; this.melds = melds; }
}