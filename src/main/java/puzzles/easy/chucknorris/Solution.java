package puzzles.easy.chucknorris;

import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String MESSAGE = in.nextLine();

    StringBuilder builder = new StringBuilder();
    for (int index = 0; index < MESSAGE.length(); index++) {
      String binary = Integer.toBinaryString(MESSAGE.charAt(index));
      while (binary.length() < 7) {
        binary = "0" + binary;
      }
      builder.append(binary);
    }

    String binaryFormat = builder.toString();

    char currentChar = binaryFormat.charAt(0);
    StringBuilder res = new StringBuilder();
    if (currentChar == '0') {
      res.append("00 0");
    } else {
      res.append("0 0");
    }
    for (int index = 1; index < binaryFormat.length(); index++) {
      char ch = binaryFormat.charAt(index);
      if (ch == currentChar) {
        res.append("0");
        continue;
      }
      currentChar = ch;
      res.append(" ");
      if (currentChar == '0') {
        res.append("00 0");
      } else {
        res.append("0 0");
      }
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println(res.toString());
  }
}
