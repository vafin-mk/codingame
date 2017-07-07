package puzzles.medium.war;

import java.util.*;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int n = in.nextInt(); // the number of cards for player 1
    Deque<String> firstDeck = new ArrayDeque<>(n * 2);
    List<String> firstPool = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      String cardp1 = in.next(); // the n cards of player 1
      firstDeck.offer(cardp1);
    }
    int m = in.nextInt(); // the number of cards for player 2
    Deque<String> secondDeck = new ArrayDeque<>(m * 2);
    List<String> secondPool = new ArrayList<>(m);
    for (int i = 0; i < m; i++) {
      String cardp2 = in.next(); // the m cards of player 2
      secondDeck.offer(cardp2);
    }
    System.err.println(Arrays.toString(firstDeck.toArray()));
    System.err.println(Arrays.toString(secondDeck.toArray()));
    int rounds = 0;
    while (!firstDeck.isEmpty() && !secondDeck.isEmpty()) {
      String first = firstDeck.poll();
      String second = secondDeck.poll();

      firstPool.add(first);
      secondPool.add(second);

      int res = cardValue(first) - cardValue(second);
      //System.err.println(String.format("%s round: %s vs %s == %s", rounds, first, second, res));

      if (res > 0) {
        firstDeck.addAll(firstPool);
        firstPool.clear();
        firstDeck.addAll(secondPool);
        secondPool.clear();
      } else if (res < 0) {
        secondDeck.addAll(firstPool);
        firstPool.clear();
        secondDeck.addAll(secondPool);
        secondPool.clear();
      } else {
        if (firstDeck.size() > 3 && secondDeck.size() > 3) {
          for (int i = 0; i< 3; i++) {
            firstPool.add(firstDeck.poll());
            secondPool.add(secondDeck.poll());
          }
          continue;
        } else {
          System.out.println("PAT");
          return;
        }
      }
      System.err.println(String.format("%s round results", rounds));
      System.err.println(Arrays.toString(firstDeck.toArray()));
      System.err.println(Arrays.toString(secondDeck.toArray()));
      rounds++;
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    result(firstDeck, secondDeck, rounds);
  }

  static void result(Deque<String> firstDeck, Deque<String> secondDeck, int rounds) {
    if (firstDeck.size() <= 3 && secondDeck.size() <= 3) {
      System.out.println("PAT");
    } else if (firstDeck.size() < 3) {
      System.out.println("2 " + rounds);
    } else {
      System.out.println("1 " + rounds);
    }
  }

  static int cardValue(String card) {
    String val = card.substring(0, card.length() - 1);
    switch (val) {
      case "2" : return 2;
      case "3" : return 3;
      case "4" : return 4;
      case "5" : return 5;
      case "6" : return 6;
      case "7" : return 7;
      case "8" : return 8;
      case "9" : return 9;
      case "10" : return 10;
      case "J" : return 11;
      case "Q" : return 12;
      case "K" : return 13;
      case "A" : return 14;
    }
    System.err.println("FUCK:" + val);
    return 15;
  }
}
