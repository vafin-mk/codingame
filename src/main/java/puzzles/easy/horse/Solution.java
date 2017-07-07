package puzzles.easy.horse;

import java.util.Arrays;
import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt();
    int[] pis = new int[N];
    for (int i = 0; i < N; i++) {
      int pi = in.nextInt();
      pis[i] = pi;
    }

    Arrays.sort(pis);
    final int[] res = {Integer.MAX_VALUE};
    Arrays.stream(pis).reduce((a,b) -> {
      if (b - a < res[0]) {
        res[0] = b - a;
      }
      return b;
    });

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println("" + res[0]);
  }
}
