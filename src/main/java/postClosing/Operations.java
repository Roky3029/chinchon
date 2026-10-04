package postClosing;

import dataStructures.Card;
import dataStructures.Hand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.GameLogic;
import utilities.Helpers;

public class Operations implements Serializable {
    private static final int LAYOFF_PASSES = 3;

    private static int rank(Card c) {
        int v = c.getValue();
        return v <= 7 ? v : v - 2;
    }

    private static boolean isSet(List<Card> meld) {
        return meld.get(0).getValue() == meld.get(1).getValue();
    }

    private static boolean canAttach(Card c, List<Card> group) {
        if (isSet(group)) {
            return group.size() < 8 && c.getValue() == group.get(0).getValue();
        }
        if (group.get(0).getNumericalSuit() != c.getNumericalSuit()) return false;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (Card m : group) {
            min = Math.min(min, rank(m));
            max = Math.max(max, rank(m));
        }
        return rank(c) == min - 1 || rank(c) == max + 1;
    }

    private static int findTarget(Card c, List<List<Card>> melds) {
        int setMatch = -1;
        for (int i = 0; i < melds.size(); i++) {
            if (!canAttach(c, melds.get(i))) continue;
            if (!isSet(melds.get(i))) return i;
            if (setMatch < 0) setMatch = i;
        }
        return setMatch;
    }

    private static Plan findBestPlan(Hand hand) {
        List<Card> current = new ArrayList<>(hand.getHand());
        int total = 0;
        for (Card c : current) total += c.getValue();
        Plan best = new Plan(total, new ArrayList<>());

        for (List<Card> comb : Helpers.union(hand.getRuns(), hand.getSets())) {
            List<Card> rest = GameLogic.removeEach(current, comb);
            Plan sub = findBestPlan(new Hand(rest));
            if (sub.points < best.points) {
                List<List<Card>> melds = new ArrayList<>(sub.melds);
                melds.add(comb);
                best = new Plan(sub.points, melds);
            }
        }
        for (Card c : current) {
            List<Card> rest = new ArrayList<>(current);
            rest.remove(c);
            Plan sub = findBestPlan(new Hand(rest));
            if (c.getValue() + sub.points < best.points) {
                best = new Plan(c.getValue() + sub.points, sub.melds);
            }
        }
        return best;
    }

    public static RoundResult resolveRound(List<Hand> hands, int closer) {
        int n = hands.size();
        List<List<Card>> table = new ArrayList<>();
        List<Integer> owners = new ArrayList<>();
        List<List<Card>> loose = new ArrayList<>();

        // 1. Everyone shows the groups of their best arrangement; the rest are loose cards
        for (int p = 0; p < n; p++) {
            Plan plan = findBestPlan(hands.get(p));
            List<Card> rest = new ArrayList<>(hands.get(p).getHand());
            for (List<Card> meld : plan.melds) {
                table.add(new ArrayList<>(meld));
                owners.add(p);
                for (Card c : meld) rest.remove(c);
            }
            loose.add(rest);
        }

        // 2. Turn order after the closer, the closer last
        List<Integer> order = new ArrayList<>();
        for (int k = 1; k < n; k++) order.add((closer + k) % n);
        order.add(closer);

        // 3. Layoff passes
        List<Placement> placements = new ArrayList<>();
        for (int pass = 0; pass < LAYOFF_PASSES; pass++) {
            for (int p : order) {
                layOffAll(p, loose.get(p), table, placements);
            }
        }

        int[] points = new int[n];
        for (int p = 0; p < n; p++) {
            for (Card c : loose.get(p)) points[p] += c.getValue();
        }
        return new RoundResult(points, table, owners, placements);
    }

    /** One player's turn: keeps placing loose cards until none of them fits anywhere. */
    private static void layOffAll(int player, List<Card> loose, List<List<Card>> table, List<Placement> out) {
        boolean progress = true;
        while (progress) {
            progress = false;
            for (Iterator<Card> it = loose.iterator(); it.hasNext(); ) {
                Card c = it.next();
                int idx = findTarget(c, table);
                if (idx >= 0) {
                    table.get(idx).add(c);
                    out.add(new Placement(player, c, idx));
                    it.remove();
                    progress = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        Hand h1 = new Hand();

        h1.add(new Card("C", 1));
        h1.add(new Card("C", 2));
        h1.add(new Card("C", 3));
        h1.add(new Card("C", 4));
        h1.add(new Card("B", 5));
        h1.add(new Card("B", 6));
        h1.add(new Card("B", 7));

        Hand h2 = new Hand();

        h2.add(new Card("C", 10));
        h2.add(new Card("E", 10));
        h2.add(new Card("E", 2));
        h2.add(new Card("B", 3));
        h2.add(new Card("B", 4));
        h2.add(new Card("B", 5));
        h2.add(new Card("O", 12));

        List<Hand> hands = new ArrayList<>();
        hands.add(h1);
        hands.add(h2);

        RoundResult r = resolveRound(hands, 0);
        // h1 closes. List of groups:
        // [C3, C4, C5], [E2, E3, E4]
        // h2 adds C2
        // h1 adds C1 and O6, O7 -> final deadwoods: h1 = 1 ; h2 = 0

        for (int p = 0; p < hands.size(); p++) System.out.printf("Player %d adds %d points\n", p + 1, r.points[p]);
        for(Placement pl : r.placements) System.out.println("Player " + pl.player + " adds " + pl.card + " to a group of player " + r.groupOwner.get(pl.groupIndex));
    }
}