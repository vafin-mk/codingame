package puzzles.medium.bender;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int height = in.nextInt();
    int width = in.nextInt();
    if (in.hasNextLine()) {
      in.nextLine();
    }
    Grid grid = new Grid(height, width);
    for (int i = 0; i < height; i++) {
      grid.applyRow(i, in.nextLine());
    }
    grid.solve();
  }
}

class Grid {
  private final int height;
  private final int width;
  private Map<Point, Cell> cells;

  public Grid(int height, int width) {
    this.height = height;
    this.width = width;
    cells = new HashMap<>(height * width);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        cells.put(new Point(x, y), Cell.BLANK);
      }
    }
  }

  public void applyRow(int height, String row) {
    for (int x = 0; x < row.length(); x++) {
      char cell = row.charAt(x);
      cells.put(new Point(x, height), charToCell(cell));
    }
  }

  public Cell charToCell(char ch) {
    switch (ch) {
      case '@': return Cell.START;
      case '$': return Cell.END;
      case '#': return Cell.UNDESTRUCTIBLE;
      case 'X': return Cell.DESTRUCTIBLE;
      case 'S': return Cell.SOUTH;
      case 'E': return Cell.EAST;
      case 'N': return Cell.NORTH;
      case 'W': return Cell.WEST;
      case 'I': return Cell.INVERTER;
      case 'B': return Cell.BREAKER;
      case 'T': return Cell.TELEPORT;
    }
    return Cell.BLANK;
  }

  private Point findStart() {
    for (Map.Entry<Point, Cell> entry : cells.entrySet()) {
      if (entry.getValue() == Cell.START) {
        return entry.getKey();
      }
    }
    throw new IllegalStateException();
  }

  private Point nextPoint(Point from, Move move) {
    switch (move) {
      case EAST: return new Point(from.x + 1, from.y);
      case WEST: return new Point(from.x - 1, from.y);
      case SOUTH: return new Point(from.x, from.y + 1);
      case NORTH: return new Point(from.x, from.y - 1);
    }
    System.err.println("point " + from.x + "||" + from.y + "|||" + move);
    throw new IllegalStateException();
  }

  private boolean passable(Cell cell, boolean breaking) {
    switch (cell) {
      case START: return true;
      case END: return true;
      case UNDESTRUCTIBLE: return false;
      case DESTRUCTIBLE: return breaking;
      case SOUTH: return true;
      case EAST: return true;
      case NORTH: return true;
      case WEST: return true;
      case INVERTER: return true;
      case BREAKER: return true;
      case TELEPORT: return true;
      case BLANK: return true;
    }
    System.err.println(cell);
    throw new IllegalStateException();
  }

  private Point findOtherTeleportPosition(Point from) {
    for (Map.Entry<Point, Cell> entry : cells.entrySet()) {
      if (entry.getValue() == Cell.TELEPORT && !from.equals(entry.getKey())) {
        return entry.getKey();
      }
    }
    throw new IllegalStateException();
  }

  private List<Move> checkMoves(boolean inverted) {
    List<Move> result = new ArrayList<>();
    result.add(Move.SOUTH);
    result.add(Move.EAST);
    result.add(Move.NORTH);
    result.add(Move.WEST);
    if (inverted) {
      Collections.reverse(result);
    }
    return result;
  }

  public void solve() {
    Point current = findStart();
    Cell currentCell;
    Move currentMove = Move.SOUTH;
    boolean inverted = false;
    boolean breaking = false;
    List<Move> moves = new ArrayList<>();
    outer:while (true) {
      if (moves.size() > 5 * width * height) {
        System.out.println("LOOP");
        return;
      }
      currentCell = cells.get(current);
      switch (currentCell) {
        case END: break outer;
        case DESTRUCTIBLE: cells.put(current, Cell.BLANK); break;
        case SOUTH: currentMove = Move.SOUTH; break;
        case EAST: currentMove = Move.EAST; break;
        case NORTH: currentMove = Move.NORTH; break;
        case WEST: currentMove = Move.WEST; break;
        case INVERTER: inverted = !inverted; break;
        case BREAKER: breaking = !breaking; break;
        case TELEPORT: current = findOtherTeleportPosition(current); break;
      }

      Point nextPoint = nextPoint(current, currentMove);
      Cell nextCell = cells.get(nextPoint);
      if (passable(nextCell, breaking)) {
        moves.add(currentMove);
        current = nextPoint;
        continue;
      }

      for (Move move : checkMoves(inverted)) {
        nextPoint = nextPoint(current, move);
        nextCell = cells.get(nextPoint);
        if (passable(nextCell, breaking)){
          moves.add(move);
          current = nextPoint;
          currentMove = move;
          continue outer;
        }
      }
    }
    for (Move move : moves) {
      System.out.println(move);
    }
  }
}

class Point{
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

    if (x != point.x) return false;
    return y == point.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }
}

enum Cell {
  START, END, UNDESTRUCTIBLE, DESTRUCTIBLE, SOUTH, EAST, NORTH, WEST, INVERTER, BREAKER, TELEPORT, BLANK
}

enum Move {
  SOUTH, EAST, NORTH, WEST
}
