package puzzles.hard.labyrinth;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver{
  final Scanner scanner;
  final int alarm;
  final Maze maze;
  Point currentPosition = new Point(-1, -1);
  Solver(Scanner scanner) {
    this.scanner = scanner;
    int height = scanner.nextInt();
    int width = scanner.nextInt();
    maze = new Maze(width, height);
    alarm = scanner.nextInt();
  }

  void solve() {
    List<Direction> pathToTarget = new ArrayList<>();
    while (true) {
      int y = scanner.nextInt();
      int x = scanner.nextInt();
      currentPosition = new Point(x, y);
      maze.update(scanner);
      if (!maze.target.unknown()) {
        pathToTarget = maze.findPath(currentPosition, maze.target);
        if (!pathToTarget.isEmpty()) {
          break;
        }
      }
      System.out.println(exploreMove());
    }
    for (Direction direction : pathToTarget) {
      System.out.println(direction);
    }
    List<Direction> pathToStart = maze.findPath(maze.target, maze.start);
    for (Direction direction : pathToStart) {
      System.out.println(direction);
    }
  }

  private Direction exploreMove() {
    return maze.pathToClosestUnknown(currentPosition).get(0);
  }

}

class Node {
  Point point;
  List<Direction> path;

  Node(Point point, List<Direction> path) {
    this.point = point;
    this.path = new ArrayList<>(path);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Node node = (Node) o;

    return point.equals(node.point);
  }

  @Override
  public int hashCode() {
    return point.hashCode();
  }
}

enum Direction {
  UP, DOWN, LEFT, RIGHT
}

class Maze {
  final Map<Point, CellType> grid;
  final int width, height;
  Point start = new Point(-1, -1), target = new Point(-1, -1);

  Maze(int width, int height) {
    this.width = width;
    this.height = height;
    grid = new HashMap<>(width * height);
  }

  void update(Scanner scanner) {
    for (int y = 0; y < height; y++) {
      String ROW = scanner.next();
      for (int x = 0; x < width; x++) {
        char ch = ROW.charAt(x);
        if (ch == 'T') start = new Point(x, y);
        if (ch == 'C') target = new Point(x, y);
        grid.put(new Point(x, y), charToType(ch));
      }
    }
  }

  CellType charToType(char ch) {
    switch (ch) {
      case '#': return CellType.WALL;
      case '?': return CellType.UNKNOWN;
    }
    return CellType.SPACE;
  }

  List<Point> neighs(Point from) {
    List<Point> result = new ArrayList<>();

    for (Direction direction : Direction.values()) {
      Point check = from.byDirection(direction);
      if (grid.containsKey(check) && grid.get(check) == CellType.SPACE) {
        result.add(check);
      }
    }

    return result;
  }

  List<Point> neighsWithUnknowns(Point from) {
    List<Point> result = new ArrayList<>();

    for (Direction direction : Direction.values()) {
      Point check = from.byDirection(direction);
      if (grid.containsKey(check) && grid.get(check) != CellType.WALL) {
        result.add(check);
      }
    }

    return result;
  }

  List<Direction> findPath(Point from, Point to) {
    Node start = new Node(from, new ArrayList<>());
    Queue<Node> unvisited = new ArrayDeque<>();
    Set<Node> visited = new HashSet<>();
    unvisited.add(start);
    while (!unvisited.isEmpty()) {
      Node node = unvisited.poll();
      if (node.point.equals(to)) {
        return node.path;
      }
      if (visited.contains(node)) {//todo path len
        continue;
      }
      visited.remove(node);
      visited.add(node);
      for (Point neigh : neighs(node.point)) {
        List<Direction> path = new ArrayList<>(node.path);
        path.add(node.point.byPoint(neigh));
        unvisited.add(new Node(neigh, path));
      }
    }
    System.err.println("NOT FOUND");
    return new ArrayList<>();
  }

  List<Direction> pathToClosestUnknown(Point from) {
    Node start = new Node(from, new ArrayList<>());
    Queue<Node> unvisited = new ArrayDeque<>();
    Set<Node> visited = new HashSet<>();
    unvisited.add(start);
    while (!unvisited.isEmpty()) {
      Node node = unvisited.poll();
      if (grid.get(node.point) == CellType.UNKNOWN) {
        return node.path;
      }
      if (visited.contains(node)) {//todo path len
        continue;
      }
      visited.remove(node);
      visited.add(node);
      for (Point neigh : neighsWithUnknowns(node.point)) {
        List<Direction> path = new ArrayList<>(node.path);
        path.add(node.point.byPoint(neigh));
        unvisited.add(new Node(neigh, path));
      }
    }
    System.err.println("NOT FOUND");
    return new ArrayList<>();
  }
}

enum CellType{
  WALL, SPACE, UNKNOWN;
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  boolean unknown() {
    return x == -1 && y == -1;
  }

  Point byDirection(Direction direction) {
    switch (direction) {
      case UP: return new Point(x, y - 1);
      case DOWN: return new Point(x, y + 1);
      case LEFT: return new Point(x - 1, y);
      case RIGHT: return new Point(x + 1, y);
    }
    throw new IllegalStateException();
  }

  Direction byPoint(Point to) {
    if (to.x > x) return Direction.RIGHT;
    if (to.x < x) return Direction.LEFT;
    if (to.y > y) return Direction.DOWN;
    if (to.y < y) return Direction.UP;
    throw new IllegalStateException();
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

  @Override
  public String toString() {
    return x + "|" + y;
  }
}