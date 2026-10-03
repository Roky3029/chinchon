package postClosing;

import dataStructures.Card;

import java.util.List;

public final class Plan {
    final int points;
    final List<List<Card>> melds;
    Plan(int points, List<List<Card>> melds) { this.points = points; this.melds = melds; }
}