package puzzles.hard.winamax;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    Scanner scanner = new Scanner(System.in);
    int width = scanner.nextInt();
    int height = scanner.nextInt();
    Field field = new Field(width, height);
    for (int i = 0; i < height; i++) {
      String row = scanner.next();
      field.addRow(i, row);
    }

    System.out.println(field.solution());
  }
}

class Field {
  static final int SPACE = -1;
  static final int HAZARD = -2;
  static final int HOLE = -3;
  static final int OCCUPIED_HOLE = -4;
  static final int LEFT = -5;
  static final int TOP = -6;
  static final int RIGHT = -7;
  static final int BOTTOM = -8;
  final int width;
  final int height;
  final Map<Point, Integer> cells;

  Field(int width, int height) {
    this.width = width;
    this.height = height;
    cells = new HashMap<>(width * height);
  }

  void addRow(int height, String row) {
    for (int x = 0; x < row.length(); x++) {
      char ch = row.charAt(x);
      int cell;
      if (ch == '.') cell = SPACE;
      else if (ch == 'X') cell = HAZARD;
      else if (ch == 'H') cell = HOLE;
      else cell = Character.getNumericValue(ch);
      cells.put(new Point(x, height), cell);
    }
  }

  String solution() {
    Deque<State> states = new ArrayDeque<>();
    states.add(new State(width, height,cells));
    while (!states.isEmpty()) {
      State current = states.poll();
      Point max = current.maxBall();
      if (max == null) {
        return current.toResultString();
      }
      for (Direction direction : Direction.values()) {
        State newState = current.applyMove(max, direction);
        if (newState != null) states.addFirst(newState);
      }
    }

    throw new IllegalStateException("fail!");
  }
}

class State {
  final Map<Point, Integer> cells;
  final int width;
  final int height;
  State(int width, int height, final Map<Point, Integer> cells) {
    this.width = width;
    this.height = height;
    this.cells = cells;
  }

  Point maxBall() {
    int max = 0;
    Point ball = null;
    for (Map.Entry<Point, Integer> entry : cells.entrySet()) {
      if (entry.getValue() > max) {
        max = entry.getValue();
        ball = entry.getKey();
      }
    }
    return ball;
  }

  private boolean passable(int cell) {
    return cell == Field.HAZARD || cell == Field.SPACE;
  }

  private boolean landable(int cell) {
    return cell == Field.SPACE || cell == Field.HOLE;
  }

  State applyMove(Point ball, Direction direction) {
    Map<Point, Integer> newCells = new HashMap<>(cells);
    boolean possibleMove = false;
    switch (direction) {
      case LEFT:
        possibleMove = applyLeftMove(ball, newCells);
        break;
      case TOP:
        possibleMove = applyTopMove(ball, newCells);
        break;
      case RIGHT:
        possibleMove = applyRightMove(ball, newCells);
        break;
      case BOTTOM:
        possibleMove = applyBottomMove(ball, newCells);
        break;
    }
    if (!possibleMove) return null;
    return new State(width, height, newCells);
  }

  boolean applyLeftMove(Point ball, Map<Point, Integer> cells) {
    int power = cells.get(ball);
    int xFrom = ball.x;
    int xTo = ball.x - power;
    if (xTo < 0) return false;
    for (int x = xTo + 1; x <= xFrom; x++) {
      Point point = new Point(x, ball.y);
      int cell = cells.get(point);
      if (point.equals(ball) || passable(cell)) {
        cells.put(point, Field.LEFT);
      } else {
        return false;
      }
    }
    Point finishingPoint = new Point(xTo, ball.y);
    int finishingCell = cells.get(finishingPoint);
    if (finishingCell == Field.HOLE) {
      cells.put(finishingPoint, Field.OCCUPIED_HOLE);
      return true;
    }
    if (!canReachAnyHole(finishingPoint, power - 1)) {
      return false;
    }
    if (power > 1 && landable(finishingCell)) {
      cells.put(finishingPoint, power - 1);
      return true;
    }
    return false;
  }

  boolean applyRightMove(Point ball, Map<Point, Integer> cells) {
    int power = cells.get(ball);
    int xFrom = ball.x;
    int xTo = ball.x + power;
    if (xTo >= width) return false;
    for (int x = xFrom; x < xTo; x++) {
      Point point = new Point(x, ball.y);
      int cell = cells.get(point);
      if (point.equals(ball) || passable(cell)) {
        cells.put(point, Field.RIGHT);
      } else {
        return false;
      }
    }
    Point finishingPoint = new Point(xTo, ball.y);
    int finishingCell = cells.get(finishingPoint);
    if (finishingCell == Field.HOLE) {
      cells.put(finishingPoint, Field.OCCUPIED_HOLE);
      return true;
    }
    if (!canReachAnyHole(finishingPoint, power - 1)) {
      return false;
    }
    if (power > 1 && landable(finishingCell)) {
      cells.put(finishingPoint, power - 1);
      return true;
    }
    return false;
  }

  boolean applyTopMove(Point ball, Map<Point, Integer> cells) {
    int power = cells.get(ball);
    int yFrom = ball.y;
    int yTo = ball.y - power;
    if (yTo < 0) return false;
    for (int y = yTo + 1; y <= yFrom; y++) {
      Point point = new Point(ball.x, y);
      int cell = cells.get(point);
      if (point.equals(ball) || passable(cell)) {
        cells.put(point, Field.TOP);
      } else {
        return false;
      }
    }
    Point finishingPoint = new Point(ball.x, yTo);
    int finishingCell = cells.get(finishingPoint);
    if (finishingCell == Field.HOLE) {
      cells.put(finishingPoint, Field.OCCUPIED_HOLE);
      return true;
    }
    if (!canReachAnyHole(finishingPoint, power - 1)) {
      return false;
    }
    if (power > 1 && landable(finishingCell)) {
      cells.put(finishingPoint, power - 1);
      return true;
    }
    return false;
  }

  boolean applyBottomMove(Point ball, Map<Point, Integer> cells) {
    int power = cells.get(ball);
    int yFrom = ball.y;
    int yTo = ball.y + power;
    if (yTo >= height) return false;
    for (int y = yFrom; y < yTo; y++) {
      Point point = new Point(ball.x, y);
      int cell = cells.get(point);
      if (point.equals(ball) || passable(cell)) {
        cells.put(point, Field.BOTTOM);
      } else {
        return false;
      }
    }
    Point finishingPoint = new Point(ball.x, yTo);
    int finishingCell = cells.get(finishingPoint);
    if (finishingCell == Field.HOLE) {
      cells.put(finishingPoint, Field.OCCUPIED_HOLE);
      return true;
    }
    if (!canReachAnyHole(finishingPoint, power - 1)) {
      return false;
    }
    if (power > 1 && landable(finishingCell)) {
      cells.put(finishingPoint, power - 1);
      return true;
    }
    return false;
  }

  boolean canReachAnyHole(Point from, int power) {
    int powerSum = power * (power + 1) / 2;
    for (Map.Entry<Point, Integer> entry : cells.entrySet()) {
      if (entry.getValue() == Field.HOLE && from.dist(entry.getKey()) <= powerSum) {
        return true;
      }
    }
    return false;
  }

  private String cellToString(int cell) {
    switch (cell) {
      case Field.SPACE: return ".";
      case Field.HAZARD: return "X";
      case Field.HOLE: return "H";
      case Field.OCCUPIED_HOLE: return "H";
      case Field.LEFT: return "<";
      case Field.TOP: return "^";
      case Field.RIGHT: return ">";
      case Field.BOTTOM: return "v";
    }
    return String.valueOf(cell);
  }

  private String cellToResultString(int cell) {
    switch (cell) {
      case Field.LEFT: return "<";
      case Field.TOP: return "^";
      case Field.RIGHT: return ">";
      case Field.BOTTOM: return "v";
    }
    return ".";
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Point point = new Point(x, y);
        int cell = cells.get(point);
        builder.append(cellToString(cell));
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  String toResultString() {
    StringBuilder builder = new StringBuilder();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Point point = new Point(x, y);
        int cell = cells.get(point);
        builder.append(cellToResultString(cell));
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}

enum Direction {
  LEFT, TOP, RIGHT, BOTTOM;
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //manhattan dist
  int dist(Point other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
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
    return x + "," + y;
  }
}