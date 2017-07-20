package puzzles.hard.cgxformatter;

import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final String text;
  Solver(Scanner scanner) {
    this.scanner = scanner;
    int N = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < N; i++) {
      String cGXLine = scanner.nextLine();
      builder.append(cGXLine);
    }
    text = builder.toString();
  }

  boolean insideString = false;
  boolean newLineJustPrinted = true;
  int indentLevel = 0;
  StringBuilder builder = new StringBuilder();
  void solve() {
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (insideString) {
        builder.append(ch);
        if (ch == '\'') insideString = false;
        continue;
      }

      switch (ch) {
        case '(':
          if (!newLineJustPrinted) newLine();
          write(ch);
          newLine();
          indentLevel++;
          break;
        case ')':
          indentLevel--;
          if (!newLineJustPrinted) newLine();
          write(ch);
          break;
        case '\'':
          insideString = true;
          write(ch);
          break;
        case ';':
          write(ch);
          newLine();
          break;
        case ' ':
        case '\t':
          break;
        default:
          write(ch);
          break;
      }
    }

    System.err.println("text-->" + text);
    System.out.println(builder.toString().trim());
  }

  void indent() {
    for (int i = 0; i < indentLevel * 4; i++) {
      builder.append(" ");
    }
  }

  void newLine() {
    builder.append("\n");
    newLineJustPrinted = true;
  }

  void write(char ch) {
    if (newLineJustPrinted) {
      indent();
      newLineJustPrinted = false;
    }
    builder.append(ch);
  }
}