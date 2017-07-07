package puzzles.medium.stock;

import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int n = in.nextInt();
    int[] vals = new int[n];
    for (int i = 0; i < n; i++) {
      vals[i] = in.nextInt();
    }

    int max = Integer.MIN_VALUE;
    int maxLoss = 0;

    for (int val : vals) {
      if (val < max) {
        int loss = max - val;
        if (loss > maxLoss) {
          maxLoss = loss;
        }
      }
      if (val > max) {
        max = val;
      }
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println("" + -maxLoss);
  }
}
