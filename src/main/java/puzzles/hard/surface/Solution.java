package puzzles.hard.surface;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  static final int LAND = 0;
  static final int UNKNOWN = -1;
  static final int CALCULATING = -2;
  final Scanner scanner;
  final int width;
  final int height;
  final int testSize;
  final int[][] cells;
  final List<Point> testCells;
  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    cells = new int[width][height];
    for (int y = 0; y < height; y++) {
      String row = scanner.nextLine();
      for (int x = 0; x < row.length(); x++) {
        char ch = row.charAt(x);
        cells[x][y] = (ch == '#') ? LAND : UNKNOWN;
      }
    }
    testSize = scanner.nextInt();
    testCells = new ArrayList<>();
    for (int i = 0; i < testSize; i++) {
      int X = scanner.nextInt();
      int Y = scanner.nextInt();
      testCells.add(new Point(X, Y));
    }
  }

  void solve() {
    recalculateSizes();
    for (int i = 0; i < testSize; i++) {
      Point checkPoint = testCells.get(i);
      System.out.println(cells[checkPoint.x][checkPoint.y]);
    }
  }

  void calculateSize(Point from) {
    Queue<Point> unvisited = new LinkedList<>();
    Set<Point> lake = new HashSet<>();
    unvisited.add(from);
    while (!unvisited.isEmpty()) {
      Point curr = unvisited.poll();
      if (cells[curr.x][curr.y] != UNKNOWN) continue;
      lake.add(curr);
      cells[curr.x][curr.y] = CALCULATING;
      unvisited.addAll(adjacents(curr));
    }
    for (Point point : lake) {
      cells[point.x][point.y] = lake.size();
    }
  }

  void recalculateSizes() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (cells[x][y] != UNKNOWN) continue;
        calculateSize(new Point(x, y));
      }
    }
  }

  List<Point> adjacents(Point from) {
    List<Point> result = new ArrayList<>(4);
    if (from.x > 0) {
      result.add(new Point(from.x - 1, from.y));
    }
    if (from.x < width - 1) {
      result.add(new Point(from.x + 1, from.y));
    }
    if (from.y > 0) {
      result.add(new Point(from.x, from.y - 1));
    }
    if (from.y < height - 1) {
      result.add(new Point(from.x, from.y + 1));
    }
    return result;
  }
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    Point point = (Point) o;
    return x == point.x && y == point.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }

  @Override
  public String toString() {
    return x + "," + y;
  }
}