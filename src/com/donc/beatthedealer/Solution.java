package com.donc.beatthedealer;

import java.util.*;

public class Solution {

    private static final String[] SUITES = new String[]{"S", "H", "D", "C"};
    private static final String[] RANKS = new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};

    private static final int TARGET_SCORE = 21;

    List<Card> newDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            for (String suit : SUITES) {
                if (i == 1) {
                    deck.add(new Card(RANKS[i-1].concat(suit), 11));
                } else {
                    switch (i) {
                        case 11, 12, 13:
                            deck.add(new Card(RANKS[i-1].concat(suit), 10));
                            break;
                        default:
                            deck.add(new Card(RANKS[i-1].concat(suit), i));
                    }
                }
            }
        }
        return deck;
    }

    Set<Player> play(List<Player> players, List<Card> deck) {
        Set<Player> winners = new HashSet<>();

        deal(players, deck);
        for (Player player : players) {
            int score = player.getCards().stream().mapToInt(Card::val).sum();
            while ((TARGET_SCORE - score) > player.getRiskVariation()) {
                Card c = deck.removeFirst();
                player.getCards().add(c);
                score += c.val();
            }
            player.setScore(score);
            if (score > TARGET_SCORE) {
                System.out.println(player.getName() + " busts with a score of " + score);
            }
        }

        int bestScore = players.stream()
                .mapToInt(Player::getScore)
                .filter(s -> s <= TARGET_SCORE)
                .max().orElse(0);

        for (Player player : players) {
            if (player.getScore() == bestScore) {
                winners.add(player);
            }
        }

        return winners;
    }

    void deal(List<Player> players, List<Card> deck) {
        if (deck.size() < (players.size() * 2)) {
            List<Card> newDeck = newDeck();
            Collections.shuffle(newDeck);
            deck.addAll(newDeck);
        }
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                player.getCards().add(deck.removeFirst());
            }
        }
    }

    void reset(List<Player> players) {
        for (Player player : players) {
            player.setScore(0);
            player.getCards().clear();
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        List<Card> deck = solution.newDeck();
        List<Player> players = List.of(new Player("P1", 3),
                new Player("Dealer", 0));

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(deck);
            Set<Player> winners = solution.play(players, deck);
            winners.forEach(p -> System.out.println(p.getName() + " wins with a score of " + p.getScore()));
            solution.reset(players);
            System.out.println("=====");
        }
    }

}

record Card(String suit, int val) {
}

class Player {
    private final String name;
    private final List<Card> cards;
    private int score;
    private int riskVariation;

    public Player(String name, int riskVariation) {
        this.name = name;
        this.riskVariation = riskVariation;
        this.cards = new ArrayList<>();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRiskVariation() {
        return riskVariation;
    }

    public void setRiskVariation(int riskVariation) {
        this.riskVariation = riskVariation;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCards() {
        return cards;
    }
}
