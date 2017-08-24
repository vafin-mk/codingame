package multiplayer.hypersonic.ai;

import common.ai.AbstractAI;
import common.model.Command;
import common.model.Point2I;
import multiplayer.hypersonic.model.*;

import java.util.*;

public class AI extends AbstractAI {

  Set<Point2I> findExplodedCells(Bomb bomb) {
    Set<Point2I> result = new HashSet<>();
    result.add(bomb.position);
    int startX = bomb.position.x;
    int startY = bomb.position.y;
    //up
    for (int y = startY - 1; y > Math.max(-1, startY - bomb.explosionRange); y--) {
      Point2I point = new Point2I(startX, y);
      result.add(point);
      if (STOP_EXPLOSION.contains(grid.get(point))) {
        break;
      }
    }
    //down
    for (int y = startY + 1; y < Math.min(height, startY + bomb.explosionRange); y++) {
      Point2I point = new Point2I(startX, y);
      result.add(point);
      if (STOP_EXPLOSION.contains(grid.get(point))) {
        break;
      }
    }
    //left
    for (int x = startX - 1; x > Math.max(-1, startX - bomb.explosionRange); x--) {
      Point2I point = new Point2I(x, startY);
      result.add(point);
      if (STOP_EXPLOSION.contains(grid.get(point))) {
        break;
      }
    }
    //right
    for (int x = startX + 1; x < Math.min(width, startX + bomb.explosionRange); x++) {
      Point2I point = new Point2I(x, startY);
      result.add(point);
      if (STOP_EXPLOSION.contains(grid.get(point))) {
        break;
      }
    }
    return result;
  }

  Set<Point2I> neighs(Point2I point) {
    Set<Point2I> result = new HashSet<>();
    Point2I left = point.left();
    if (validNeigh(left, available.get(point) + 1)) {
      result.add(left);
    }
    Point2I right = point.right();
    if (validNeigh(right, available.get(point) + 1)) {
      result.add(right);
    }
    Point2I up = point.up();
    if (validNeigh(up, available.get(point) + 1)) {
      result.add(up);
    }
    Point2I down = point.down();
    if (validNeigh(down, available.get(point) + 1)) {
      result.add(down);
    }
    return result;
  }

  boolean validNeigh(Point2I neigh, int roundToReach) {
    if (outsideGrid(neigh)) return false;
    if (NON_PASSABLE.contains(grid.get(neigh))) return false;
    if (heatMap.get(neigh) == roundToReach) return false;
    return true;
  }

  boolean outsideGrid(Point2I point) {
    return point.x < 0 || point.x >= width || point.y < 0 || point.y >= height;
  }

  List<Point2I> findExplodedBoxes(Bomb bomb) {
    List<Point2I> result = new ArrayList<>();
    for (Point2I point : findExplodedCells(bomb)) {
      if (BOXES.contains((grid.get(point)))) {
        result.add(point);
      }
    }
    return result;
  }

  List<Bomb> findAffectedBombs(Bomb bomb) {
    List<Bomb> result = new ArrayList<>();
    for (Point2I point : findExplodedCells(bomb)) {
      if (point.equals(bomb.position)) continue;
      if (bombs.contains(new Bomb(point, -1, -1, -1))) {
        result.add(bomb);
      }
    }
    return result;
  }

  boolean canPlaceBomb(Point2I point) {
    if (hero.bombsLeft <= 0) return false;
    if(!Cell.FLOOR.equals(grid.get(point))) return false;

    Set<Point2I> safePoints = new HashSet<>(available.keySet());
    safePoints.removeIf(p -> heatMap.get(p) < 1_000_000);
    Bomb bomb = new Bomb(point, -1, -1, hero.bombRange);
    safePoints.removeAll(findExplodedCells(bomb));

    return !safePoints.isEmpty();
  }

  Point2I findBestPlaceToBomb() {
    List<Point2I> places = new ArrayList<>();

    for (Point2I point : available.keySet()) {
      if (!canPlaceBomb(point)) {
        continue;
      }
      if (findExplodedBoxes(new Bomb(point, -1, -1, hero.bombRange)).size() == 0) {
        continue;
      }
      places.add(point);
    }

    places.sort((p1, p2) -> {
      int p1Score = (2 * findExplodedBoxes(new Bomb(p1, -1, -1, hero.bombRange)).size() - available.get(p1));
      int p2Score = (2 * findExplodedBoxes(new Bomb(p2, -1, -1, hero.bombRange)).size() - available.get(p2));
      return Integer.compare(p2Score, p1Score);
    });

    if (places.isEmpty()) return null;
    return places.get(0);
  }

  Point2I findClosestSafePoint(Bomb newBomb, Set<Point2I> safePoints) {
    Point2I closestSafePoint = null;
    if (newBomb != null) {
      safePoints.removeAll(findExplodedCells(newBomb));
    }
    for (Point2I safePoint : safePoints) {
      if (closestSafePoint == null
        || available.get(safePoint) < available.get(closestSafePoint)) {
        closestSafePoint = safePoint;
      }
    }

    return closestSafePoint;
  }

  @Override
  protected Command think() {
    Set<Point2I> safePoints = new HashSet<>(available.keySet());
    safePoints.removeIf(p -> heatMap.get(p) < 1_000_000);
    if (safePoints.isEmpty()) {
      Move command = new Move(hero.position, Move.MoveType.MOVE);
      command.message = "IT'S A TRAP!";
      return command;
    }
    if (safePoints.size() > 1) {
      safePoints.remove(hero.position);
    }

    if (hero.bombRange < 8) {
      List<Point2I> items = new ArrayList<>();
      for (Point2I point : available.keySet()) {
        if (available.get(point) > 4) continue;
        //blow danger!
        if (heatMap.get(point) < 1_000_000) continue;
        if (ITEMS.contains(grid.get(point))) {
          items.add(point);
        }
      }

      Point2I closestItem = null;
      for (Point2I item : items) {
        if (closestItem == null
          || available.get(item) < available.get(closestItem)) {

          closestItem = item;
        }
      }

      if (closestItem != null) {
        return new Move(closestItem, Move.MoveType.MOVE);
      }
    }

    Point2I bestPlaceToBomb = findBestPlaceToBomb();
    if (bestPlaceToBomb != null) {
      if (hero.position.equals(bestPlaceToBomb)) {
        Point2I closestSafePoint = findClosestSafePoint(
          new Bomb(hero.position, -1, -1, hero.bombRange),
          new HashSet<>(safePoints)
        );
        return new Move(closestSafePoint != null ? closestSafePoint : bestPlaceToBomb, Move.MoveType.BOMB);
      } else {
        return new Move(bestPlaceToBomb, Move.MoveType.MOVE);
      }
    }

    Point2I closestSafePoint = findClosestSafePoint(null, new HashSet<>(safePoints));
    if (closestSafePoint != null) {
      return new Move(closestSafePoint, Move.MoveType.MOVE);
    }

    Move command = new Move(hero.position, Move.MoveType.MOVE);
    command.message = "IT'S A TRAP!";
    return command;
  }

  @Override
  protected void sendOutput(Command command) {
    command.execute();
  }

  @Override
  protected void init() {
    width = scanner.nextInt();
    height = scanner.nextInt();
    myId = scanner.nextInt();

    rivals = new ArrayList<>();
    bombs = new ArrayList<>();
    grid = new HashMap<>(width * height);
    heatMap = new HashMap<>(width * height);

    scores = new HashMap<>(4);
    scores.put(0, 0);
    scores.put(1, 0);
    scores.put(2, 0);
    scores.put(3, 0);

    available = new HashMap<>(width * height);
  }

  @Override
  protected void readInput() {
    for (int y = 0; y < height; y++) {
      String row = scanner.next();
      for (int x = 0; x < row.length(); x++) {
        char ch = row.charAt(x);
        grid.put(new Point2I(x, y), charToCell(ch));
      }
    }

    rivals.clear();
    bombs.clear();
    int entities = scanner.nextInt();
    for (int i = 0; i < entities; i++) {
      int entityType = scanner.nextInt();
      int owner = scanner.nextInt();
      int x = scanner.nextInt();
      int y = scanner.nextInt();
      int param1 = scanner.nextInt();
      int param2 = scanner.nextInt();

      switch (entityType) {
        case HERO_ENTITY_TYPE:
          if (owner == myId) {
            hero = new Hero(new Point2I(x, y), owner, param1, param2);
          } else {
            rivals.add(new Hero(new Point2I(x, y), owner, param1, param2));
          }
          break;
        case BOMB_ENTITY_TYPE:
          bombs.add(new Bomb(new Point2I(x, y), owner, param1, param2));
          grid.put(new Point2I(x, y), Cell.BOMB);
          break;
        case ITEM_ENTITY_TYPE:
          if (param1 == 1) {
            grid.put(new Point2I(x, y), Cell.RANGE_ITEM);
          } else {
            grid.put(new Point2I(x, y), Cell.BOMB_ITEM);
          }
          break;
        default:
          throw new IllegalStateException();
      }
    }

    recalculateHeatMap();
    recalculateAvailablePoints();

    printDebugInfo();
  }

  void recalculateHeatMap() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        heatMap.put(new Point2I(x, y), 1_000_000);
      }
    }

    //chaining
    for (Bomb bomb : bombs) {
      for (Bomb chained : findAffectedBombs(bomb)) {
        int min = Math.min(bomb.roundsLeft, chained.roundsLeft);
        bomb.roundsLeft = min;
        chained.roundsLeft = min;
      }
    }

    for (Bomb bomb : bombs) {
      for (Point2I exploded  : findExplodedCells(bomb)) {
        heatMap.put(exploded, Math.min(heatMap.get(exploded), bomb.roundsLeft));

        //add score
        if (bomb.roundsLeft == 1 && BOXES.contains(grid.get(exploded))) {
          scores.put(bomb.owner, scores.get(bomb.owner) + 1);
        }
      }
    }
  }

  void recalculateAvailablePoints() {
    available.clear();
    int roundsToReach = 0;

    Set<Point2I> visited = new HashSet<>();
    Set<Point2I> toVisit = new HashSet<>();
    Set<Point2I> visitors = new HashSet<>();
    toVisit.add(hero.position);
    while (!toVisit.isEmpty()) {
      visitors.clear();
      visitors.addAll(toVisit);
      toVisit.clear();
      for (Point2I visitor : visitors) {
        if (visited.contains(visitor)) continue;
        visited.add(visitor);
        available.put(visitor, roundsToReach);
        for (Point2I point : neighs(visitor)) {
          if (visited.contains(point)) continue;
          toVisit.add(point);
        }
      }
      roundsToReach++;
    }

    if (heatMap.get(hero.position) == 2) {
      available.remove(hero.position);
    }
  }

  public AI(Scanner scanner) {
    super(scanner);
  }

  Cell charToCell(char ch) {
    switch (ch) {
      case '.': return Cell.FLOOR;
      case '0': return Cell.EMPTY_BOX;
      case '1': return Cell.RANGE_BOX;
      case '2': return Cell.BOMB_BOX;
      case 'X': return Cell.WALL;
    }

    throw new IllegalStateException();
  }

  void printDebugInfo() {
    System.err.println("Round " + round);
    printScores();
    printAvailablePoints();
  }

  void printScores() {
    System.err.println(scores);
  }

  void printAvailablePoints() {
    System.err.println(available);
  }

  int width;
  int height;
  int myId;

  Hero hero;
  List<Hero> rivals;
  List<Bomb> bombs;
  Map<Point2I, Cell> grid;
  Map<Point2I, Integer> heatMap;
  //owner --> score
  Map<Integer, Integer> scores;
  //available points and rounds to reach them
  Map<Point2I, Integer> available;

  final static int HERO_ENTITY_TYPE = 0;
  final static int BOMB_ENTITY_TYPE = 1;
  final static int ITEM_ENTITY_TYPE = 2;

  final static EnumSet<Cell> BOXES = EnumSet.of(Cell.EMPTY_BOX, Cell.RANGE_BOX, Cell.BOMB_BOX);
  final static EnumSet<Cell> ITEMS = EnumSet.of(Cell.RANGE_ITEM, Cell.BOMB_ITEM);
  final static EnumSet<Cell> STOP_EXPLOSION = EnumSet.of(Cell.EMPTY_BOX, Cell.RANGE_BOX, Cell.BOMB_BOX, Cell.RANGE_ITEM, Cell.BOMB_ITEM, Cell.BOMB, Cell.WALL);
  final static EnumSet<Cell> NON_PASSABLE = EnumSet.of(Cell.EMPTY_BOX, Cell.RANGE_BOX, Cell.BOMB_BOX, Cell.BOMB, Cell.WALL);
}