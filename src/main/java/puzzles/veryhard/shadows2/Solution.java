package puzzles.veryhard.shadows2;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final int width;
  final int height;
  final int maxJumps;

  boolean[][] checkedForBomb;

  Range xRange;
  Range yRange;

  Point current;
  Point previous;
  Point beforePrevious;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    xRange = new Range(0, width);

    height = scanner.nextInt();
    yRange = new Range(0, height);

    maxJumps = scanner.nextInt();
    System.err.println("W=" + width + "|H=" + height + "|J=" + maxJumps);

    int x = scanner.nextInt();
    int y = scanner.nextInt();

    current = new Point(x, y);
    previous = new Point(x, y);
    beforePrevious = new Point(x, y);

    checkedForBomb = new boolean[width][height];
  }

  void solve() {
    while (true) {
      Info info = Info.valueOf(scanner.next());
      if (zoneSize() > 1000000) {
        //try to reduce zone
        if (info == Info.WARMER) {
          xRange = xRange.cut(current.x, previous.x, true);
          yRange = yRange.cut(current.y, previous.y, true);
        } else if (info == Info.COLDER) {
          xRange = xRange.cut(current.x, previous.x, false);
          yRange = yRange.cut(current.y, previous.y, false);
        }

        beforePrevious = previous;
        previous = current;
        current = new Point(xRange.length() / 2, yRange.length() / 2);
      } else {
        for (int x = xRange.from; x < xRange.to; x++) {
          for (int y = yRange.from; y < yRange.to; y++) {
            if (!checkedForBomb[x][y]) {
              checkedForBomb[x][y] = checkForBomb(x, y, current, previous, info);
            }
          }
        }

        beforePrevious = previous;
        previous = current;
        current = findCentroid();
        fixEdgeCases();
      }

      System.out.println(current);
    }
  }

  boolean checkForBomb(int x, int y, Point current, Point previous, Info info) {
    Point point = new Point(x, y);
    switch (info) {
      case COLDER:
        return previous.distSquare(point) >= current.distSquare(point);
      case WARMER:
        return previous.distSquare(point) <= current.distSquare(point);
      case SAME:
        return previous.distSquare(point) != current.distSquare(point);
    }
    return false;
  }

  int zoneSize() {
    return xRange.length() * yRange.length();
  }

  Point findCentroid() {
    double possibleBombs = 0;
    int totalX = 0;
    int totalY = 0;
    for (int x = xRange.from; x < xRange.to; x++) {
      for (int y = yRange.from; y < yRange.to; y++) {
        if (!checkedForBomb[x][y]) {
          possibleBombs++;
          totalX += x;
          totalY += y;
        }
      }
    }

    return new Point((int) Math.round(totalX / possibleBombs), (int) Math.round(totalY / possibleBombs));
  }

  void fixEdgeCases() {
    if (!current.equals(previous) && !current.equals(beforePrevious)) return;

    if (current.x > 0 && !checkedForBomb[current.x - 1][current.y]) {
      current = new Point(current.x - 1, current.y);
    } else if (current.y > 0 && !checkedForBomb[current.x][current.y - 1]) {
      current = new Point(current.x, current.y - 1);
    } else if (current.y < height - 1 && !checkedForBomb[current.x][current.y + 1]) {
      current = new Point(current.x, current.y + 1);
    } else if (current.x < width - 1 && !checkedForBomb[current.x + 1][current.y]) {
      current = new Point(current.x + 1, current.y);
    } else if (current.x > 0 && current.y > 0 && !checkedForBomb[current.x - 1][current.y - 1]) {
      current = new Point(current.x - 1, current.y - 1);
    } else if (current.x > 0 && current.y < height - 1 && !checkedForBomb[current.x - 1][current.y + 1]) {
      current = new Point(current.x - 1, current.y + 1);
    } else if (current.x < width - 1 && current.y > 0 && !checkedForBomb[current.x + 1][current.y - 1]) {
      current = new Point(current.x + 1, current.y - 1);
    } else if (current.x < width - 1 && current.y < height - 1 && !checkedForBomb[current.x + 1][current.y + 1]) {
      current = new Point(current.x + 1, current.y + 1);
    }
  }
}

enum Info {
  COLDER, WARMER, SAME, UNKNOWN
}

class Range {
  final int from, to;

  Range(int from, int to) {
    this.from = from;
    this.to = to;
  }

  int length() {
    return to - from;
  }

  Range cut(int curr, int prev, boolean warmer) {
    if (curr == prev) return this;
    int offset = Math.abs(curr - prev);
    if (warmer) {
      if (curr > prev) {
        return new Range(prev + offset, to);
      } else if (curr < prev) {
        return new Range(from, prev - offset);
      }
    } else {
      if (curr > prev) {
        return new Range(from, curr - offset);
      } else if (curr < prev) {
        return new Range(curr + offset, to);
      }
    }

    throw new IllegalStateException();
  }
}

class Point {
  final int x, y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int distSquare(Point other) {
    return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point)) return false;

    Point point = (Point) o;

    return x == point.x && y == point.y;
  }

  @Override
  public int hashCode() {
    return 100000 * x + y;
  }

  @Override
  public String toString() {
    return x + " " + y;
  }
}