package puzzles.hard.voxcodei;

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
  final Map<Point, Cell> cells = new HashMap<>();
  final Map<Point, Integer> explodeMap = new HashMap<>();
  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    for (int y = 0; y < height; y++) {
      String mapRow = scanner.nextLine();
      for (int x = 0; x < mapRow.length(); x++) {
        char ch = mapRow.charAt(x);
        cells.put(new Point(x, y), charToCell(ch));
        explodeMap.put(new Point(x, y), State.WONT_EXPLODE);
      }
    }
  }

  Cell charToCell(char ch) {
    if (ch == '@') return Cell.TARGET;
    if (ch == '#') return Cell.BLOCK;
    return Cell.SPACE;
  }

  void solve() {
    int rounds = scanner.nextInt();
    int bombs = scanner.nextInt();
    List<Point> bestPositions = findBestPositions(rounds, bombs);
    for (Point bestPosition : bestPositions) {
      if (bestPosition == null) {
        System.out.println("WAIT");
      } else {
        System.out.println(bestPosition);
      }
    }
  }

  List<Point> findBestPositions(int rounds, int bombs) {
    State startState = new State(width, height);
    startState.cells = new HashMap<>(cells);
    startState.explodeMap = new HashMap<>(explodeMap);
    startState.positions = new ArrayList<>();
    startState.rounds = rounds;
    startState.bombs = bombs;

    List<Point> result = null;
    Deque<State> states = new ArrayDeque<>(rounds);
    states.addFirst(startState);
    while (!states.isEmpty()) {
      State current = states.poll();
      if (current.win()) {
        result = current.positions;
        break;
      }
      if (current.lost()) {
        continue;
      }
      for (Point point : current.targets()) {
        states.addFirst(current.applyBomb(point));
      }
    }
    for (int i = result.size(); i < rounds; i++) {
      result.add(null);
    }
    return result;
  }
}

class State {
  static final int bombPower = 3;
  static final int bombTime = 3;
  static final int WONT_EXPLODE = -1;
  static final int DFS_BRANCH = 3;

  final int width;
  final int height;
  State(int width, int height) {
    this.width = width;
    this.height = height;
  }

  Map<Point, Cell> cells;
  Map<Point, Integer> explodeMap;
  List<Point> positions;
  int rounds;
  int bombs;

  State applyBomb(Point point) {
    State newState = new State(width, height);
    newState.cells = new HashMap<>(cells);
    newState.explodeMap = new HashMap<>(explodeMap);
    newState.positions = new ArrayList<>(positions);
    newState.rounds = rounds - 1;
    newState.bombs = bombs;
    for (Map.Entry<Point, Integer> entry : newState.explodeMap.entrySet()) {
      if (entry.getValue() > 1) {
        newState.explodeMap.put(entry.getKey(), entry.getValue() - 1);
      } else if (entry.getValue() == 1) {
        newState.explodeMap.put(entry.getKey(), WONT_EXPLODE);
        newState.cells.put(entry.getKey(), Cell.SPACE);
      } else if (entry.getValue() == 0) throw new IllegalStateException();
    }
    if (point != null) {
      newState.bombs--;
      newState.cells.put(point, Cell.BOMB);
      for (Point p : explodingTiles(point)) {
        newState.explodeMap.put(p, bombTime);
      }
      newState.positions.add(point);
    } else {
      newState.positions.add(null);
    }

    return newState;
  }

  boolean win() {
    return unaffectedNodes() == 0;
  }

  int unaffectedNodes() {
    int unaffectedNodes = 0;
    for (Map.Entry<Point, Cell> entry : cells.entrySet()) {
      if (entry.getValue() == Cell.TARGET && explodeMap.get(entry.getKey()) == WONT_EXPLODE) {
        unaffectedNodes++;
      }
    }
    return unaffectedNodes;
  }

  boolean lost() {
    if (rounds <= 0) return true;
    int maxExplodes = 0;
    for (Point point : cells.keySet()) {
      Cell cell = cells.get(point);
      if (cell == Cell.SPACE || (cell == Cell.TARGET && explodeMap.get(point) != WONT_EXPLODE)) {
        int size = newExplodingNodes(point);
        if (size > maxExplodes) {
          maxExplodes = size;
        }
      }
    }

    return unaffectedNodes() > maxExplodes * bombs;
  }

  Set<Point> explodingTiles(Point placeBomb) {
    Set<Point> result = new HashSet<>();
    result.add(placeBomb);
    int x = placeBomb.x;
    int y = placeBomb.y;
    int toX = -1;
    int toY = -1;
    //LEFT
    toX = Math.max(0, x - bombPower);
    for (int i = x; i >= toX; i--) {
      Point point = new Point(i, y);
      if (cells.get(point) == Cell.BLOCK) break;
      result.add(point);
    }
    //RIGHT
    toX = Math.min(width - 1, x + bombPower);
    for (int i = x; i <= toX; i++) {
      Point point = new Point(i, y);
      if (cells.get(point) == Cell.BLOCK) break;
      result.add(point);
    }
    //TOP
    toY = Math.max(0, y - bombPower);
    for (int i = y; i >= toY; i--) {
      Point point = new Point(x, i);
      if (cells.get(point) == Cell.BLOCK) break;
      result.add(point);
    }
    //BOTTOM
    toY = Math.min(height - 1, y + bombPower);
    for (int i = y; i <= toY; i++) {
      Point point = new Point(x, i);
      if (cells.get(point) == Cell.BLOCK) break;
      result.add(point);
    }
    return result;
  }

  int newExplodingNodes(Point point) {
    if (point == null) return 0;
    return (int)explodingTiles(point).stream().filter(p -> cells.get(p) == Cell.TARGET && explodeMap.get(p) == WONT_EXPLODE).count();
  }

  List<Point> targets() {
    List<Point> points = new ArrayList<>();
    if (bombs == 0) {
      points.add(null);
      return points;
    }
    for (Point point : cells.keySet()) {
      if (cells.get(point) != Cell.SPACE) continue;
      int size = newExplodingNodes(point);
      if (size > 0) points.add(point);
    }
    points.sort(Comparator.comparingInt(this::newExplodingNodes));
    if (points.size() > DFS_BRANCH) {
      points = points.subList(points.size() - DFS_BRANCH, points.size());
    }
    List<Point> res = new ArrayList<>();
    res.add(null);
    res.addAll(points);
    return res;
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

enum Cell {
  TARGET, BLOCK, SPACE, BOMB
}