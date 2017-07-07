package puzzles.easy.asciiart;

import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int L = in.nextInt();
    int H = in.nextInt();
    in.nextLine();
    String T = in.nextLine();
    int[] chars = T.chars().toArray();
    StringBuilder builder = new StringBuilder(T.length());
    int wordIndex;
    for (int i = 0; i < H; i++) {
      String ROW = in.nextLine();
      for (int ch : chars) {
        if (ch >= 65 && ch <= 90) {
          wordIndex = ch - 65;
        } else if (ch >= 97 && ch <= 122) {
          wordIndex = ch - 97;
        } else {
          wordIndex = 26;
        }
        builder.append(ROW.substring(wordIndex * L, (wordIndex + 1) * L));
      }
      builder.append("\n");
    }
    builder.setLength(builder.length() - 1);

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");
    System.out.println(builder.toString());
  }
}
