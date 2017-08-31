package puzzles.optimization.reverse;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

//pakman!!
class Solver {
  final Scanner scanner;

  private static final char BLOCK = '#';
  private static final char FLOOR = '_';
  private static final char UNKNOWN = '?';

  final int width;
  final int height;
  final int playersCount;

  int round = 0;

  Point currentPoint;
  List<Point> rivals;
  char[][] grid;

  public Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    playersCount = scanner.nextInt();

    rivals = new ArrayList<>();
    grid = new char[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        grid[x][y] = UNKNOWN;
      }
    }
  }

  void solve() {
    while (true) {
      round++;
      char up = scanner.next().charAt(0);
      char right = scanner.next().charAt(0);
      char down = scanner.next().charAt(0);
      char left = scanner.next().charAt(0);

      rivals.clear();
      for (int i = 0; i < playersCount; i++) {
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        Point point = new Point(x % width, y % height);
        if (i == playersCount - 1) {
          currentPoint = point;
        } else {
          rivals.add(point);
        }
      }

      updateGrid(left(currentPoint), left);
      updateGrid(up(currentPoint), up);
      updateGrid(right(currentPoint), right);
      updateGrid(down(currentPoint), down);
      updateGrid(currentPoint, FLOOR);
      for (Point rival : rivals) {
        updateGrid(rival, FLOOR);
      }
//      System.err.println("GRID");
//      System.err.println("Current -->" + currentPoint);
//      System.err.println("Rivals -->" + rivals);
      Action best = findBestMove();
      System.err.println("Best - " + best.name());
      System.err.println(debugGrid());
      System.out.println(best);
//      System.err.println("Round -> " + round);
    }
  }

  void updateGrid(Point point, char ch) {
    if (ch != BLOCK && ch != FLOOR) {
      throw new IllegalStateException("unknown type = " + ch);
    }
    grid[point.x][point.y] = ch;
  }

  char readGrid(Point point) {
    return grid[point.x % width][point.y % height];
  }

  Point left(Point from) {
    int x = from.x == 0 ? width - 1 : from.x - 1;
    return new Point(x, from.y);
  }

  Point up(Point from) {
    int y = from.y == 0 ? height - 1 : from.y - 1;
    return new Point(from.x, y);
  }

  Point right(Point from) {
    int x = from.x == width - 1 ? 0 : from.x + 1;
    return new Point(x, from.y);
  }

  Point down(Point from) {
    int y = from.y == height - 1 ? 0 : from.y + 1;
    return new Point(from.x, y);
  }

  Point predictPoint(Point current, Action action) {
    switch (action) {
      case LEFT: return left(current);
      case UP: return up(current);
      case RIGHT: return right(current);
      case DOWN: return down(current);
      case STAY: return current;
    }
    throw new IllegalStateException();
  }

  int distToClosestUnknownPoint(Point from) {
    int dist = 0;
    Set<Point> visited = new HashSet<>();
    Queue<Point> unvisited = new LinkedList<>();
    unvisited.add(from);
    while (!unvisited.isEmpty()) {
      List<Point> neighs = new ArrayList<>();
      while (!unvisited.isEmpty()) {
        Point curr = unvisited.poll();
        if (visited.contains(curr)) continue;
        visited.add(curr);
        if (readGrid(curr) == UNKNOWN) return dist;

        Point left = left(curr);
        if (readGrid(left) != BLOCK) neighs.add(left);

        Point up = up(curr);
        if (readGrid(up) != BLOCK) neighs.add(up);

        Point right = right(curr);
        if (readGrid(right) != BLOCK) neighs.add(right);

        Point down = down(curr);
        if (readGrid(down) != BLOCK) neighs.add(down);
      }
      unvisited.addAll(neighs);
      dist++;
    }

    return 1000;
  }

  Action findBestMove() {
    List<ActionValue> possibleActions = new ArrayList<>();
    if (readGrid(left(currentPoint)) != BLOCK) {
      possibleActions.add(evaluateAction(Action.LEFT));
    }

    if (readGrid(up(currentPoint)) != BLOCK) {
      possibleActions.add(evaluateAction(Action.UP));
    }

    if (readGrid(right(currentPoint)) != BLOCK) {
      possibleActions.add(evaluateAction(Action.RIGHT));
    }

    if (readGrid(down(currentPoint)) != BLOCK) {
      possibleActions.add(evaluateAction(Action.DOWN));
    }

//    System.err.println("Actions--->" + possibleActions);

    Action best = null;
    int bestValue = 0;

    for (ActionValue possibleAction : possibleActions) {
      if (possibleAction.value > bestValue) {
        best = possibleAction.action;
        bestValue = possibleAction.value;
      }
    }

    if (best == null) {
      return Action.STAY;
    }
    return best;
  }

  ActionValue evaluateAction(Action action) {
    int value = 0;

    Point nextPoint = predictPoint(currentPoint, action);
    if (deathDanger(nextPoint)) {
      value -= 1_000_000;
    }

    value += (1000 - distToClosestUnknownPoint(nextPoint));
    return new ActionValue(action, value);
  }

  boolean deathDanger(Point point) {
    if (rivals.contains(point)) return true;
    if (rivals.contains(left(point))) return true;
    if (rivals.contains(up(point))) return true;
    if (rivals.contains(right(point))) return true;
    if (rivals.contains(down(point))) return true;
    return false;
  }

  String debugGrid() {
    StringBuilder builder = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Point point = new Point(x, y);
        if (rivals.contains(point)) {
          builder.append(rivals.indexOf(point));
        } else if (currentPoint.equals(point)) {
          builder.append("@");
        } else {
          char ch = readGrid(point);
          if (ch == BLOCK) builder.append("X");
          else if (ch == FLOOR) builder.append(".");
          else builder.append("?");
        }
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}

class State {
  List<Point> path;
  int unkownReveals;
  char[][] grid;
  List<Point> rivals;

  List<Point> availableNeighs() {
    List<Point> result = new ArrayList<>();
    Point currPoint = path.get(path.size() - 1);
    char currField = grid[currPoint.x][currPoint.y];
    if (currField == '?') return result;
    if (rivals.contains(currPoint)) return result;



    return result;
  }

}

class ActionValue {
  final Action action;
  final int value;
  ActionValue(Action action, int value) {
    this.action = action;
    this.value = value;
  }

  @Override
  public String toString() {
    return action + " ---> " + value;
  }
}

enum Action {
  LEFT, UP, RIGHT, DOWN, STAY;

  @Override
  public String toString() {
    switch (this) {
      case LEFT: return "E";
      case UP: return "C";
      case RIGHT: return "A";
      case DOWN: return "D";
      case STAY: return "B";
    }

    throw new IllegalStateException();
  }
}

class Point {
  final int x;
  final int y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

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
    return x + " " + y;
  }
}