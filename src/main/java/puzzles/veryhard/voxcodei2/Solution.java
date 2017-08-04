package puzzles.veryhard.voxcodei2;

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
  final List<Bomb> bombs;
  final List<Spy> spiesAtBeginning;
  final List<Spy> spiesAtFirstRound;
  final List<Spy> spies;
  final Set<Blocker> blockers;
  final int maxRounds;
  final int maxBombs;
  final List<Zone> zones;
  final List<Point> allAvailablePoints;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }

    /*int rounds = */scanner.nextInt();
    /*int bombs = */scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    blockers = new HashSet<>();
    allAvailablePoints = new ArrayList<>();
    spiesAtBeginning = new ArrayList<>();
    for (int y = 0; y < height; y++) {
      String mapRow = scanner.nextLine();
      for (int x = 0; x < width; x++) {
        char ch = mapRow.charAt(x);
        if (ch == '#')  {
          blockers.add(new Blocker(new Point(x, y)));
        } else {
          allAvailablePoints.add(new Point(x, y));
        }
        if (ch == '@') spiesAtBeginning.add(new Spy(new Point(x, y)));
      }
    }

    System.out.println("WAIT");

    /*int rounds = */scanner.nextInt();
    /*int bombs = */scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    spiesAtFirstRound = new ArrayList<>();
    for (int y = 0; y < height; y++) {
      String mapRow = scanner.nextLine();
      for (int x = 0; x < width; x++) {
        char ch = mapRow.charAt(x);
        if (ch == '@') spiesAtFirstRound.add(new Spy(new Point(x, y)));
      }
    }

    System.out.println("WAIT");

    maxRounds = scanner.nextInt();
    maxBombs = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    spies = new ArrayList<>();
    for (int y = 0; y < height; y++) {
      String mapRow = scanner.nextLine();
      for (int x = 0; x < width; x++) {
        char ch = mapRow.charAt(x);
        if (ch == '@') spies.add(new Spy(new Point(x, y)));
      }
    }

    bombs = new ArrayList<>();
    zones = new ArrayList<>();
  }

  void calcDirections() {
    for (Spy spy : spies) {
      findDirection(spy);
    }
//    System.err.println(spies);
  }

  void findDirection(Spy spy) {
    if (spiesAtBeginning.contains(spy) && spiesAtFirstRound.contains(spy)) {
      spy.move = Move.NONE;
      return;
    }

    //check left
    Spy right1 = new Spy(spy.point.right());
    Spy right0 = new Spy(right1.point.right());
    if (spiesAtBeginning.contains(right0) && spiesAtFirstRound.contains(right1)) {
      spy.move = Move.LEFT;
      return;
    }

    //check right
    Spy left1 = new Spy(spy.point.left());
    Spy left0 = new Spy(left1.point.left());
    if (spiesAtBeginning.contains(left0) && spiesAtFirstRound.contains(left1)) {
      spy.move = Move.RIGHT;
      return;
    }

    //check down
    Spy up1 = new Spy(spy.point.up());
    Spy up0 = new Spy(up1.point.up());
    if (spiesAtBeginning.contains(up0) && spiesAtFirstRound.contains(up1)) {
      spy.move = Move.DOWN;
      return;
    }

    //check up
    Spy down1 = new Spy(spy.point.down());
    Spy down0 = new Spy(down1.point.down());
    if (spiesAtBeginning.contains(down0) && spiesAtFirstRound.contains(down1)) {
      spy.move = Move.UP;
      return;
    }

    //bounces
    if (spiesAtBeginning.contains(spy) && !spiesAtFirstRound.contains(spy)) {
      if (spiesAtFirstRound.contains(right1)) {
        spy.move = Move.LEFT;
        return;
      }

      if (spiesAtFirstRound.contains(left1)) {
        spy.move = Move.RIGHT;
        return;
      }

      if (spiesAtFirstRound.contains(up1)) {
        spy.move = Move.DOWN;
        return;
      }

      if (spiesAtFirstRound.contains(down1)) {
        spy.move = Move.UP;
        return;
      }
    }
  }

  void findZones() {
    List<Zone> draft = divideAndConquer();
    draft.sort(Comparator.comparingDouble(z ->
      (1.0 * z.availablePoints.size() / allAvailablePoints.size() + 1.0 * z.spiesToKill.size() / spies.size()) / 2)
    );

    int roundsLeft = maxRounds;
    int bombsLeft = maxBombs;
    for (int i = 0; i < draft.size(); i++) {
      Zone draftZone = draft.get(i);
      int rounds = 0;
      int bombs = 0;
      if (i == draft.size() - 1) {
        rounds = roundsLeft;
        bombs = bombsLeft;
      } else {
        double ratio = 1.0 * (draftZone.availablePoints.size() - draftZone.spiesToKill.size()) / (allAvailablePoints.size() - spies.size());
        rounds = Math.max(1, (int) Math.round(ratio * maxRounds));
        bombs = Math.max(1, (int) Math.round(ratio * maxBombs));
      }
      roundsLeft -= rounds;
      bombsLeft -= bombs;

      zones.add(new Zone(draftZone.availablePoints, rounds, bombs, draftZone.spiesToKill, isTrap(draftZone)));
    }

//    System.err.println("zones---" + zones);
  }

  boolean isTrap(Zone zone) {
    boolean verticalTrap = false;
    boolean horizontalTrap = false;

    for (Spy first : zone.spiesToKill) {
      for (Spy second : zone.spiesToKill) {
        if (first.equals(second)) continue;
        if (first.point.x == second.point.x) {
          if (first.move == Move.NONE && second.move == Move.NONE && first.point.dist(second.point) < 3) {
            verticalTrap = true;
          }
        }
        if (first.point.y == second.point.y) {
          if (first.move == Move.NONE && second.move == Move.NONE && first.point.dist(second.point) < 3) {
            horizontalTrap = true;
          }
        }
      }
    }

    if (!verticalTrap) return false;
    if (!horizontalTrap) return false;

    //check for spy paths intersection
    for (Spy first : zone.spiesToKill) {
      for (Spy second : zone.spiesToKill) {
        if (first.equals(second)) continue;
        if (first.move == Move.NONE || second.move == Move.NONE) {
          continue;
        }
        for (Point firstPathPoint : findPath(first)) {
          for (Point secondPathPoint : findPath(second)) {
            if (firstPathPoint.equals(secondPathPoint)) return false;
          }
        }
      }
    }

    return true;
  }

  Set<Point> findPath(Spy spy) {
    Set<Point> result = new HashSet<>();
    result.add(spy.point);
    if (spy.move == Move.NONE) {
      return result;
    }
    if (spy.move == Move.LEFT || spy.move == Move.RIGHT) {
      result.addAll(horizontalPath(spy.point));
    }
    if (spy.move == Move.UP || spy.move == Move.DOWN) {
      result.addAll(verticalPath(spy.point));
    }

    return result;
  }

  Set<Point> horizontalPath(Point start) {
    Set<Point> result = new HashSet<>();
    result.add(start);
    int x = start.x;
    int y = start.y;
    //LEFT
    for (int i = x; i >= 0; i--) {
      Point point = new Point(i, y);
      if (!allAvailablePoints.contains(point)) break;
      result.add(point);
    }
    //RIGHT
    for (int i = x; i < width; i++) {
      Point point = new Point(i, y);
      if (!allAvailablePoints.contains(point)) break;
      result.add(point);
    }
    return result;
  }

  Set<Point> verticalPath(Point start) {
    Set<Point> result = new HashSet<>();
    result.add(start);
    int x = start.x;
    int y = start.y;
    //TOP
    for (int i = y; i >= 0; i--) {
      Point point = new Point(x, i);
      if (!allAvailablePoints.contains(point)) break;
      result.add(point);
    }
    //BOTTOM
    for (int i = y; i < height; i++) {
      Point point = new Point(x, i);
      if (!allAvailablePoints.contains(point)) break;
      result.add(point);
    }
    return result;
  }

  void solve() {
    calcDirections();
    findZones();

    int offset = 0;
    for (Zone zone : zones) {
      offset += solveZone(zone, offset);
    }

    System.out.println("WAIT");
    System.out.println("WAIT");
    System.out.println("WAIT");
  }

  int solveZone(Zone zone, int offset) {
    State starter = new State(width, height, zone);
    starter.roundsLeft = zone.maxRounds;
    starter.bombsLeft = zone.maxBombs;
    starter.bombs = new ArrayList<>(bombs);
    starter.spies = new ArrayList<>(zone.spiesToKill);
    starter.applyMovesOffset(offset);
    starter.exploders = starter.exploders();
    starter.predictSpiesPositions();
    starter.placeCommands = new ArrayList<>();

    Deque<State> deque = new LinkedList<>();
    deque.add(starter);
    while (!deque.isEmpty()) {
      State current = deque.poll();
      if (current.win()) {
//        System.err.println(current.placeCommands);
        for (Bomb placeCommand : current.placeCommands) {
          if (placeCommand == null) {
            System.out.println("WAIT");
          } else {
            System.out.println(placeCommand.point);
          }
        }
        return current.placeCommands.size();
      }

      if (current.lost()) {
        continue;
      }

      deque.addFirst(current.addBomb(null));
      for (Bomb bomb : current.placePositions()) {
        deque.addFirst(current.addBomb(bomb));
      }
    }

    throw new IllegalStateException("Failed to find win state!!!");
  }

  List<Zone> divideAndConquer() {
    List<Zone> result = new ArrayList<>();
    List<Point> allPoints = new ArrayList<>();
    allPoints.addAll(allAvailablePoints);
    while (!allPoints.isEmpty()) {
      Point begin = allPoints.get(0);
      Set<Point> visited = new HashSet<>();
      Queue<Point> queue = new LinkedList<>();
      List<Spy> spiesToKill = new ArrayList<>();
      queue.add(begin);
      while (!queue.isEmpty()) {
        Point current = queue.poll();
        if (visited.contains(current)) continue;
        if (outsideGrid(current)) continue;
        if (!blockers.isEmpty() && blockers.contains(new Blocker(current))) continue;
        int index = spies.indexOf(new Spy(current));
        if (index != -1) {
          spiesToKill.add(spies.get(index));
        }
        visited.add(current);
        queue.add(current.left());
        queue.add(current.up());
        queue.add(current.right());
        queue.add(current.down());
      }

      List<Point> available = new ArrayList<>();
      available.addAll(visited);
      allPoints.removeAll(available);

      if (spiesToKill.size() == 0) {
        continue;
      }

      result.add(new Zone(available, 0, 0, spiesToKill, false));
    }
    return result;
  }

  boolean outsideGrid(Point point) {
    return point.x < 0 || point.x >= width || point.y < 0 || point.y >= height;
  }
}

class State {
  final int width;
  final int height;
  final Zone zone;

  int roundsLeft;
  int bombsLeft;

  List<Bomb> bombs;
  Set<Bomb> exploders;
  List<Spy> spies;
  List<Spy> spiesInOneRounds;
  List<Spy> spiesInTwoRounds;
  List<Spy> spiesInThreeRounds;

  List<Bomb> placeCommands;

  State(int width, int height, Zone zone) {
    this.width = width;
    this.height = height;
    this.zone = zone;
  }

  boolean win() {
    return spiesInThreeRounds.isEmpty();
  }

  boolean lost() {
    return roundsLeft <= 0 || (bombs.isEmpty() && bombsLeft <= 0);
  }

  State addBomb(Bomb bomb) {
    State state = new State(width, height, zone);
    state.roundsLeft = roundsLeft - 1;
    state.bombsLeft = bombsLeft - (bomb == null ? 0 : 1);
    state.bombs = new ArrayList<>();
    for (Bomb bmb : bombs) {
      Bomb newBomb = new Bomb(bmb.point);
      newBomb.explodeIn = bmb.explodeIn;
      state.bombs.add(newBomb);
    }
    state.spies = new ArrayList<>(spies);
    state.placeCommands = new ArrayList<>(placeCommands);
    state.placeCommands.add(bomb);

    if (bomb != null) {
      state.bombs.add(new Bomb(bomb.point));
      state.chainBombs();
    }

    state.moveSpies();
    state.explode();

    state.exploders = state.exploders();
    state.predictSpiesPositions();

    return state;
  }

  List<Bomb> placePositions() {
    List<Bomb> positions = new ArrayList<>();
    if (bombsLeft <= 0) return positions;
    int minBlows = Math.max(spies.size() / bombsLeft, 1);

    for (Point point : zone.availablePoints) {
//      if (blockers.contains(new Blocker(point))) continue;
      if (bombs.contains(new Bomb(point))) continue;
      if (spies.contains(new Spy(point))) continue;
      Bomb bomb = new Bomb(point);
      findExplodeIn(bomb);
      if (willBlow(bomb).size() < minBlows) continue;
      positions.add(bomb);
    }

    positions.sort(Comparator.comparingInt(b -> willBlow(b).size()));
    return positions;
  }

  void findExplodeIn(Bomb newBomb) {
    for (Bomb exploder : exploders) {
      if (exploder.equals(newBomb)) {
        newBomb.explodeIn = exploder.explodeIn;
        return;
      }
    }
  }

  Set<Spy> willBlow(Bomb bomb) {
    Set<Spy> result = new HashSet<>();
    switch (bomb.explodeIn) {
      case 3:
        for (Bomb explode : exploders(bomb)) {
          int index = spiesInThreeRounds.indexOf(new Spy(explode.point));
          if (index != -1) {
            result.add(spiesInThreeRounds.get(index));
          }
        }
        break;
      case 2:
        for (Bomb explode : exploders(bomb)) {
          int index = spiesInTwoRounds.indexOf(new Spy(explode.point));
          if (index != -1) {
            result.add(spiesInTwoRounds.get(index));
          }
        }
        break;
      case 1:
        for (Bomb explode : exploders(bomb)) {
          int index = spiesInOneRounds.indexOf(new Spy(explode.point));
          if (index != -1) {
            result.add(spiesInOneRounds.get(index));
          }
        }
        break;
    }

    if (zone.trap) {
      boolean allNonesEliminated = true;
      for (Spy spy : spies) {
        if (spy.move == Move.NONE) {
          allNonesEliminated = false;
          break;
        }
      }
      if (!allNonesEliminated) {
        boolean hasNone = false;
        boolean hasMoving = false;
        for (Spy spy : result) {
          if (spy.move == Move.NONE) hasNone = true;
          else hasMoving = true;
        }

        if (!hasNone || !hasMoving) {
          result.clear();
        }
      }
    }

    return result;
  }

  void explode() {
    Set<Bomb> explodeNow = new HashSet<>();
    for (Bomb bomb : bombs) {
      if (bomb.explodeIn > 1) {
        bomb.explodeIn--;
      } else {
        explodeNow.add(bomb);
        for (Bomb bmb : exploders(bomb)) {
          spies.removeIf(spy -> spy.point.equals(bmb.point));
        }
      }
    }

    bombs.removeAll(explodeNow);
  }

  void predictSpiesPositions() {
    spiesInOneRounds = new ArrayList<>();
    //first round
    for (Spy spy : spies) {
      Spy newSpy = moveSpy(spy);
      boolean willBlow = false;
      for (Bomb exploder : exploders) {
        if (newSpy.point.equals(exploder.point) && exploder.explodeIn == 1) {
          willBlow = true;
          break;
        }
      }
      if (!willBlow) {
        spiesInOneRounds.add(newSpy);
      }
    }
    //second round
    spiesInTwoRounds = new ArrayList<>();
    for (Spy spy : spiesInOneRounds) {
      Spy newSpy = moveSpy(spy);
      boolean willBlow = false;
      for (Bomb exploder : exploders) {
        if (newSpy.point.equals(exploder.point) && exploder.explodeIn == 2) {
          willBlow = true;
          break;
        }
      }
      if (!willBlow) {
        spiesInTwoRounds.add(newSpy);
      }
    }
    //third round
    spiesInThreeRounds = new ArrayList<>();
    for (Spy spy : spiesInTwoRounds) {
      Spy newSpy = moveSpy(spy);
      boolean willBlow = false;
      for (Bomb exploder : exploders) {
        if (newSpy.point.equals(exploder.point) && exploder.explodeIn == 3) {
          willBlow = true;
          break;
        }
      }
      if (!willBlow) {
        spiesInThreeRounds.add(newSpy);
      }
    }
  }

  Set<Bomb> exploders() {
    Set<Bomb> exploders = new HashSet<>();
    for (Bomb bomb : bombs) {
      exploders.addAll(exploders(bomb));
    }
    return exploders;
  }

  Set<Bomb> exploders(Bomb bomb) {
    Set<Bomb> result = new HashSet<>();
    result.add(bomb);
    int x = bomb.point.x;
    int y = bomb.point.y;
    int toX = -1;
    int toY = -1;
    //LEFT
    toX = Math.max(0, x - Bomb.BOMB_POWER);
    for (int i = x; i >= toX; i--) {
      Point point = new Point(i, y);
      if (!zone.availablePoints.contains(point)) break;
      Bomb bmb = new Bomb(point);
      bmb.explodeIn = bomb.explodeIn;
      result.add(bmb);
    }
    //RIGHT
    toX = Math.min(width - 1, x + Bomb.BOMB_POWER);
    for (int i = x; i <= toX; i++) {
      Point point = new Point(i, y);
      if (!zone.availablePoints.contains(point)) break;
      Bomb bmb = new Bomb(point);
      bmb.explodeIn = bomb.explodeIn;
      result.add(bmb);
    }
    //TOP
    toY = Math.max(0, y - Bomb.BOMB_POWER);
    for (int i = y; i >= toY; i--) {
      Point point = new Point(x, i);
      if (!zone.availablePoints.contains(point)) break;
      Bomb bmb = new Bomb(point);
      bmb.explodeIn = bomb.explodeIn;
      result.add(bmb);
    }
    //BOTTOM
    toY = Math.min(height - 1, y + Bomb.BOMB_POWER);
    for (int i = y; i <= toY; i++) {
      Point point = new Point(x, i);
      if (!zone.availablePoints.contains(point)) break;
      Bomb bmb = new Bomb(point);
      bmb.explodeIn = bomb.explodeIn;
      result.add(bmb);
    }
    return result;
  }

  void chainBombs() {
    for (Bomb first : bombs) {
      for (Bomb second : bombs) {
        if (first.equals(second)) continue;
        if (first.explodeIn == second.explodeIn) continue;
        Set<Bomb> exploders = exploders(first);
        if (exploders.contains(second)) {
          first.explodeIn = Math.min(first.explodeIn, second.explodeIn);
          second.explodeIn = Math.min(first.explodeIn, second.explodeIn);
        }
      }
    }
  }

  void moveSpies() {
    List<Spy> newSpies = new ArrayList<>();
    for (Spy spy : spies) {
      newSpies.add(moveSpy(spy));
    }

    spies.clear();
    spies.addAll(newSpies);
  }

  void applyMovesOffset(int offset) {
    for (int i = 0; i < offset; i++) {
      moveSpies();
    }
  }

  Spy moveSpy(Spy spy) {
    Move newMove = spy.move;
    Point newPoint = null;
    Spy newSpy = null;
    switch (spy.move) {
      case NONE:
        return spy;
      case LEFT:
        newPoint = spy.point.left();
        if (!zone.availablePoints.contains(newPoint)) {
          newPoint = spy.point.right();
          newMove = Move.RIGHT;
        }
        newSpy = new Spy(newPoint);
        newSpy.move = newMove;
        return newSpy;
      case UP:
        newPoint = spy.point.up();
        if (!zone.availablePoints.contains(newPoint)) {
          newPoint = spy.point.down();
          newMove = Move.DOWN;
        }
        newSpy = new Spy(newPoint);
        newSpy.move = newMove;
        return newSpy;
      case RIGHT:
        newPoint = spy.point.right();
        if (!zone.availablePoints.contains(newPoint)) {
          newPoint = spy.point.left();
          newMove = Move.LEFT;
        }
        newSpy = new Spy(newPoint);
        newSpy.move = newMove;
        return newSpy;
      case DOWN:
        newPoint = spy.point.down();
        if (!zone.availablePoints.contains(newPoint)) {
          newPoint = spy.point.up();
          newMove = Move.UP;
        }
        newSpy = new Spy(newPoint);
        newSpy.move = newMove;
        return newSpy;
    }
    throw new IllegalStateException();
  }
}

class Zone {
  final List<Point> availablePoints;
  final int maxRounds;
  final int maxBombs;
  final List<Spy> spiesToKill;
  //dirty hack for trap cases
  final boolean trap;

  Zone(List<Point> availablePoints, int maxRounds, int maxBombs, List<Spy> spiesToKill, boolean trap) {
    this.availablePoints = availablePoints;
    this.maxRounds = maxRounds;
    this.maxBombs = maxBombs;
    this.spiesToKill = spiesToKill;
    this.trap = trap;
  }

  @Override
  public String toString() {
    return maxBombs + "||" + maxRounds + "||" + spiesToKill.size() + "||" + availablePoints.size() + "||" + trap;
  }
}

class Spy {
  final Point point;
  Move move = Move.UNDEFINED;
  Spy(Point point) {
    this.point = point;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Spy)) return false;

    Spy spy = (Spy) o;

    return point.equals(spy.point);
  }

  @Override
  public int hashCode() {
    return point.hashCode();
  }

  @Override
  public String toString() {
    return point + "|" + move;
  }
}

class Bomb {
  static final int BOMB_TIME = 3;
  static final int BOMB_POWER = 3;
  final Point point;
  int explodeIn = BOMB_TIME;

  Bomb(Point point) {
    this.point = point;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Bomb)) return false;

    Bomb bomb = (Bomb) o;

    return point.equals(bomb.point);
  }

  @Override
  public int hashCode() {
    return point.hashCode();
  }

  @Override
  public String toString() {
    return point + "|" + explodeIn;
  }
}

class Blocker {
  final Point point;
  Blocker(Point point) {
    this.point = point;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Blocker)) return false;

    Blocker blocker = (Blocker) o;

    return point.equals(blocker.point);
  }

  @Override
  public int hashCode() {
    return point.hashCode();
  }
}

enum Move {
  NONE, LEFT, UP, RIGHT, DOWN, UNDEFINED
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  Point left() {
    return new Point(x - 1, y);
  }

  Point up() {
    return new Point(x, y - 1);
  }

  Point right() {
    return new Point(x + 1, y);
  }

  Point down() {
    return new Point(x, y + 1);
  }

  int dist(Point other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
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
    int result = x;
    result = 31 * result + y;
    return result;
  }

  @Override
  public String toString() {
    return x + " " + y;
  }
}