package puzzles.easy.thedescent;

import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int max;
    int mountainIndex;

    // game loop
    while (true) {
      max = 0;
      mountainIndex = 0;
      for (int i = 0; i < 8; i++) {

        int mountainH = in.nextInt(); // represents the height of one mountain, from 9 to 0.
        if (mountainH > max) {
          max = mountainH;
          mountainIndex = i;
        }
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");

      System.out.println("" + mountainIndex); // The number of the mountain to fire on.
    }
  }
}
