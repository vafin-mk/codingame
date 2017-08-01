package puzzles.veryhard.musicscores;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
//    try {
//      new Solver(new Scanner(new File("src/main/java/puzzles/veryhard/musicscores/test1.txt"))).solve();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
  }
}

class Solver {
  final static byte WHITE = 0;
  final static byte BLACK = 1;
  final Scanner scanner;
  final int width;
  final int height;
  final byte[][] image;

  int lineHeight = 0;
  int lineSpacing = 0;
  int leftPadding = 0;
  int topPadding = 0;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    String img = scanner.nextLine();
    String[] split = img.split(" ");
    //filling image
    image = new byte[height][width];
    fillImage(split);
    findLineParameters();
//    debug();
  }

  void solve() {
    List<Note> notes = findNotes();
    StringBuilder result = new StringBuilder();
    for (Note note : notes) {
      result.append(note).append(" ");
    }
    result.setLength(result.length() - 1);
    System.out.println(result.toString());
  }

  List<Note> findNotes() {
    List<Note> notes = new ArrayList<>();
    for (int x = leftPadding; x < width; x++) {
      for (int line = 0; line < 6; line++) { // 5 mains + 1 optional
        int yLine = topPadding + line * (lineHeight + lineSpacing);
        int yAboveLine = yLine - 1;
        int yBelowLine = yLine - lineSpacing;
        int yBetweenLine = yLine - lineSpacing / 2;

        if (image[yBetweenLine][x] == BLACK) {
          //note between lines
          NoteType type = NoteType.Q;
          //move to center of note
          while (image[yBelowLine][x] == WHITE) {
            x++;
          }
          //if its half note move all white pixels
          while (image[yBetweenLine][x] == WHITE) {
            type = NoteType.H;
            x++;
          }
          //move to the end of note
          while (image[yBetweenLine][x] == BLACK) {
            x++;
          }

          notes.add(new Note(betweenLinesSymbol(line), type));
          break;
        } else if (image[yAboveLine][x] == BLACK) {
          //note on line
          int yOffset = 0;
          int xStart = x;
          int xLength = 0;
          while ((image[yAboveLine][x] == BLACK || image[yAboveLine - 1][x] == BLACK)
                 && yBetweenLine < yAboveLine) {
            while (image[yAboveLine - 1][x] == BLACK) {
              yAboveLine--;
              yOffset++;
              xLength = 0;
              xStart = x;
            }
            x++;
            xLength++;
          }
          //revert yAbove
          yAboveLine += yOffset;
          NoteType type = image[yAboveLine][xStart + xLength/2] == WHITE ? NoteType.H : NoteType.Q;
          //if its half note move all white pixels
          while (image[yAboveLine][x] == WHITE) {
            x++;
          }
          //move to the end of note
          while (image[yAboveLine][x] == BLACK) {
            x++;
          }

          notes.add(new Note(onLineSymbol(line), type));
          break;
        }
      }
    }
    return notes;
  }

  NoteSymbol betweenLinesSymbol(int line) {
    NoteSymbol symbol = null;
    switch (line) {
      case 0:
        symbol = NoteSymbol.G;
        break;
      case 1:
        symbol = NoteSymbol.E;
        break;
      case 2:
        symbol = NoteSymbol.C;
        break;
      case 3:
        symbol = NoteSymbol.A;
        break;
      case 4:
        symbol = NoteSymbol.F;
        break;
      case 5:
        symbol = NoteSymbol.D;
        break;
    }
    return symbol;
  }

  NoteSymbol onLineSymbol(int line) {
    NoteSymbol symbol = null;
    switch (line) {
      case 0:
        symbol = NoteSymbol.F;
        break;
      case 1:
        symbol = NoteSymbol.D;
        break;
      case 2:
        symbol = NoteSymbol.B;
        break;
      case 3:
        symbol = NoteSymbol.G;
        break;
      case 4:
        symbol = NoteSymbol.E;
        break;
      case 5:
        symbol = NoteSymbol.C;
        break;
    }
    return symbol;
  }

  void fillImage(String[] split) {
    int currX = 0, currY = -1;
    for (int i = 0; i < split.length; i += 2) {
      boolean black = split[i].equals("B");
      int count = Integer.valueOf(split[i + 1]);
      int x;
      for (x = currX; x < count + currX; x++) {
        if (x % width == 0) {
          currY++;
        }
        image[currY][x % width] = black ? BLACK : WHITE;
      }
      currX = x % width;
    }
  }

  void findLineParameters() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        boolean blackPixel = image[y][x] == BLACK;
        if (blackPixel && lineSpacing != 0) {
          //found everything
          return;
        }

        if (lineHeight != 0) {
          if (blackPixel) {
            lineHeight++;
          } else {
            lineSpacing++;
          }
          continue;
        }

        if (blackPixel) {
          //begin count
          lineHeight++;
          topPadding = y;
          leftPadding = x;
        }
      }
    }
  }

  void debug() {
    System.err.println("Width = " + width);
    System.err.println("Height = " + height);
    System.err.println("Top padding = " + topPadding);
    System.err.println("Left padding = " + leftPadding);
    System.err.println("Line height = " + lineHeight);
    System.err.println("Line spacing = " + lineSpacing);

  }
}

class Note {
  final NoteSymbol symbol;
  final NoteType type;

  Note(NoteSymbol symbol, NoteType type) {
    this.symbol = symbol;
    this.type = type;
  }

  @Override
  public String toString() {
    return symbol.name().concat(type.name());
  }
}
enum NoteSymbol {
  A,B,C,D,E,F,G
}

enum NoteType {
  H, Q
}