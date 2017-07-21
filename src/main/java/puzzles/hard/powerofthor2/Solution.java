package puzzles.hard.powerofthor2;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  Point thor;
  List<Point> giants;
  int hammerStrikes;
  Solver(Scanner scanner) {
    this.scanner = scanner;
    int TX = scanner.nextInt();
    int TY = scanner.nextInt();
    thor = new Point(TX, TY);
    giants = new ArrayList<>();
  }

  void solve() {
    while (true) {
      hammerStrikes = scanner.nextInt();
      giants.clear();
      int giantsCount = scanner.nextInt();
      for (int i = 0; i < giantsCount; i++) {
        int X = scanner.nextInt();
        int Y = scanner.nextInt();
        giants.add(new Point(X, Y));
      }

      Command command = bestCommand();
      System.out.println(command);
      thor = thorPositionAfterCommand(command);
    }
  }

  Command bestCommand() {
    if (killedGiants(thor) == giants.size()) return Command.STRIKE;
    List<Point> available = availablePoints(thor);
    if (available.isEmpty()) return Command.STRIKE;

    Point furthestGiant = furthestGiant();

    Point best = null;
    int min = 1000;
    for (Point point : available) {
      int dist = point.manhattanDist(furthestGiant);
      if ((point.x == furthestGiant.x || point.y == furthestGiant.y)
        && (thor.manhattanDist(furthestGiant) <= dist + 1)) {
          dist += 10;
      }
      if (dist < min) {
        min = dist;
        best = point;
      }
    }
    return thor.toward(best);
  }

  List<Point> availablePoints(Point from) {
    List<Point> result = new ArrayList<>();
    if (!giantNearby(thor)) {
      result.add(thor);
    }
    for (Point neigh : from.neighs()) {
      if (!giantNearby(neigh)) {
        result.add(neigh);
      }
    }
    return result;
  }

  Point furthestGiant() {
    int maxDist = 0;
    Point furthest = null;
    for (Point giant : giants) {
      int dist = thor.manhattanDist(giant);
      if (dist > maxDist) {
        maxDist = dist;
        furthest = giant;
      }
    }

    return furthest;
  }

  boolean giantNearby(Point from) {
    for (Point giant : giants) {
      if (giant.x >= from.x - 1 && giant.x <= from.x + 1
        && giant.y >= from.y - 1 && giant.y <= from.y + 1) {
        return true;
      }
    }
    return false;
  }

  int killedGiants(Point from) {
    int killed = 0;
    for (Point giant : giants) {
      if (giant.x >= from.x - 4 && giant.x <= from.x + 4
        && giant.y >= from.y - 4 && giant.y <= from.y + 4) {
          killed++;
      }
    }
    return killed;
  }

  Point thorPositionAfterCommand(Command command) {
    switch (command) {
      case WAIT:
      case STRIKE: return thor;
      case N: return new Point(thor.x, thor.y - 1);
      case NE: return new Point(thor.x + 1, thor.y - 1);
      case E: return new Point(thor.x + 1, thor.y);
      case SE: return new Point(thor.x + 1, thor.y + 1);
      case S: return new Point(thor.x, thor.y + 1);
      case SW: return new Point(thor.x - 1, thor.y + 1);
      case W: return new Point(thor.x - 1, thor.y);
      case NW: return new Point(thor.x - 1, thor.y - 1);
    }
    throw new IllegalStateException();
  }
}

class Point {
  final int x, y;
  Point(int x, int y) {
    if (x > 39) x = 39;
    else if (x < 0) x = 0;
    if (y > 17) y = 17;
    else if (y < 0) y = 0;

    this.x = x;
    this.y = y;
  }

  int manhattanDist(Point other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }

  Command toward(Point other) {
    if (equals(other)) return Command.WAIT;
    int xOffset = x - other.x;
    int yOffset = y - other.y;

    StringBuilder angle = new StringBuilder();
    if (yOffset > 0) {
      angle.append("N");
    } else if (yOffset < 0) {
      angle.append("S");
    }

    if (xOffset > 0) {
      angle.append("W");
    } else if (xOffset < 0) {
      angle.append("E");
    }

    return Command.valueOf(angle.toString());
  }

  Set<Point> neighs() {
    Set<Point> result = new HashSet<>();
    result.add(new Point(x + 1, y));
    result.add(new Point(x + 1, y + 1));
    result.add(new Point(x + 1, y - 1));
    result.add(new Point(x, y + 1));
    result.add(new Point(x, y - 1));
    result.add(new Point(x - 1, y));
    result.add(new Point(x - 1, y + 1));
    result.add(new Point(x - 1, y - 1));
    return result;
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
    return x + "'" + y;
  }
}

enum Command {
  WAIT, STRIKE, N, NE, E, SE, S, SW, W, NW
}