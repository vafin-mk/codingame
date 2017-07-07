package puzzles.medium.gift;

import java.util.*;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int oods = in.nextInt();
    int price = in.nextInt();
    List<Integer> budgets = new ArrayList<>();
    int budgetSum = 0;
    for (int i = 0; i < oods; i++) {
      int budget = in.nextInt();
      budgets.add(budget);
      budgetSum += budget;
    }

    if (budgetSum < price) {
      System.out.println("IMPOSSIBLE");
      return;
    }
    Collections.sort(budgets);
    int mean = price / oods;

    for (Integer budget : budgets) {
      int toPay;
      if (oods == 1) {
        toPay = price;
      } else if (budget > mean) {
        toPay = mean;
      } else {
        toPay = budget;
      }

      System.out.println(toPay);
      price -= toPay;
      oods--;
      if (oods != 0) mean = price / oods;
    }
  }
}
