package puzzles.medium.scrabble;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt();
    in.nextLine();
    List<String> words = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      String W = in.nextLine();
      words.add(W);
    }
    String LETTERS = in.nextLine();
    List<Character> letters = new ArrayList<Character>();
    for(char c : LETTERS.toCharArray()) {
      letters.add(c);
    }

    String bestWord = "";
    int bestValue = 0;
    for (String word : words) {
      int wordVal = wordValue(word, new ArrayList<>(letters));
      if (wordVal > bestValue) {
        bestWord = word;
        bestValue = wordVal;
      }
    }

    System.out.println(bestWord);
  }

  private static int wordValue(String word, List<Character> letters) {
    int val = 0;
    for (Character ch : word.toCharArray()) {
      if (!letters.contains(ch)) {
        return -1;
      }
      letters.remove(ch);
      val+=charValue(ch);
    }

    return val;
  }

  private static int charValue(char ch) {
    switch (ch) {
      case 'e':
      case 'a':
      case 'i':
      case 'o':
      case 'n':
      case 'r':
      case 't':
      case 'l':
      case 's':
      case 'u':
        return 1;
      case 'd':
      case 'g':
        return 2;
      case 'b':
      case 'c':
      case 'm':
      case 'p':
        return 3;
      case 'f':
      case 'h':
      case 'v':
      case 'w':
      case 'y':
        return 4;
      case 'k':
        return 5;
      case 'j':
      case 'x':
        return 8;
      case 'q':
      case 'z':
        return 10;
    }
    throw new IllegalArgumentException();
  }
}
