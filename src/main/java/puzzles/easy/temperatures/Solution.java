package puzzles.easy.temperatures;

import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int n = in.nextInt(); // the number of temperatures to analyse
    in.nextLine();
    String temps = in.nextLine(); // the n temperatures expressed as integers ranging from -273 to 5526
    if (temps.trim().isEmpty()) {
      System.out.println("0");
      return;
    }

    Integer closest = 5526;
    Integer temp;

    for (String t : temps.split(" ")) {
      temp = Integer.parseInt(t);
      if (Math.abs(temp) < Math.abs(closest)) {
        closest = temp;
      } else if (temp.equals(closest)) {
        closest = temp;
      } else if (Math.abs(temp) == Math.abs(closest)) {
        closest = Math.abs(temp);
      }
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println(closest.toString());
  }
}
