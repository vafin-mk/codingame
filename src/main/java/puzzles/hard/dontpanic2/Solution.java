package puzzles.hard.dontpanic2;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    Solver solver = new Solver(new Scanner(System.in));
    solver.solve();
  }
}

class Solver {
  final static int MIN_COST_DELTA = 4;
  final Scanner scanner;
  final int height, width, maxCommands, maxClones, newElevatorsCount, preBuildElevatorsCount;
  final Point enter, exit;
  final Direction enterDirection;
  final Map<Integer, List<Point>> elevatorsByFloor;
  final Map<Integer, Boolean> requiredElevator;
  final int distBetweenEnterAndExit;
  final int[][] minCostToReach;

  final Comparator<Path> PATH_COMPARATOR = Comparator.comparingDouble(this::pathValue);

  Solver(Scanner scanner) {
    this.scanner = scanner;

    height = scanner.nextInt();
    width = scanner.nextInt();
    maxCommands = scanner.nextInt();

    int exitY = scanner.nextInt();
    int exitX = scanner.nextInt();
    exit = new Point(exitX, exitY);

    maxClones = scanner.nextInt();
    newElevatorsCount = scanner.nextInt();

    preBuildElevatorsCount = scanner.nextInt();
    elevatorsByFloor = new HashMap<>(height);
    requiredElevator = new HashMap<>(height);
    for (int y = 0; y < height; y++) {
      elevatorsByFloor.put(y, new ArrayList<>());
    }
    for (int i = 0; i < preBuildElevatorsCount; i++) {
      int elevatorY = scanner.nextInt();
      int elevatorX = scanner.nextInt();
      elevatorsByFloor.get(elevatorY).add(new Point(elevatorX, elevatorY));
    }

    for (int y = 0; y < height; y++) {
      requiredElevator.put(y, elevatorsByFloor.get(y).isEmpty());
    }
    Point leftExitElevator = closestTarget(exit, Direction.LEFT);
    Point rightExitElevator = closestTarget(exit, Direction.RIGHT);
    boolean canElevateToExit = false;
    for (Point elevator : elevatorsByFloor.get(exit.y - 1)) {
      if (elevator.x > leftExitElevator.x && elevator.x < rightExitElevator.x) {
        canElevateToExit = true;
        break;
      }
    }
    if (!canElevateToExit) {
      requiredElevator.put(exit.y - 1, true);
    }

    int enterY = scanner.nextInt();
    int enterX = scanner.nextInt();
    enter = new Point(enterX, enterY);
    enterDirection = Direction.valueOf(scanner.next());
    distBetweenEnterAndExit = enter.dist(exit);
    minCostToReach = new int[width * height][newElevatorsCount + 1];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int built = 0; built <= newElevatorsCount; built++) {
          minCostToReach[pointToId(new Point(x, y))][built] = 1000;
        }
      }
    }
  }

  void solve() {
    List<Point> path = new ArrayList<>();
    path.add(enter);
    Path startPath = new Path(enter, path, 0, 0, 0, enterDirection);
    Queue<Path> paths = new PriorityQueue<>(PATH_COMPARATOR);
    paths.add(startPath);
    while (!paths.isEmpty()) {
      Path current = paths.poll();

      if (badPath(current)) {
        continue;
      }
      int minCost = minCostToReach[pointToId(current.currentPoint)][current.elevatorsBuilt];
      if (current.cost < minCost) {
        minCostToReach[pointToId(current.currentPoint)][current.elevatorsBuilt] = current.cost;
      }
      if (current.currentPoint.equals(exit)) {
        for (Command command : mapPathToCommands(current.innerPath)) {
          System.out.println(command);
        }
        return;
      }
      paths.addAll(availablePaths(current));
      paths.removeIf(this::badPath);
    }
    throw new IllegalStateException("Doesn't found a shit ;( ");
  }

  List<Path> availablePaths(Path from) {
    List<Path> result = new ArrayList<>();
    if (elevatorsByFloor.get(from.currentPoint.y).contains(from.currentPoint)) {
      result.add(elevate(from));
      return result;
    }

    Point leftTarget = closestTarget(from.currentPoint, Direction.LEFT);
    Point rightTarget = closestTarget(from.currentPoint, Direction.RIGHT);
    result.add(pathTo(from, leftTarget));
    result.add(pathTo(from, rightTarget));
    if (canBuilt(from)) {
      for (int x = leftTarget.x + 1; x < rightTarget.x ; x++) {
        Path path = pathTo(from, new Point(x, from.currentPoint.y));
        result.add(buildElevator(path));
      }
    }
    return result;
  }

  boolean canBuilt(Path current) {
    if (current.elevatorsBuilt >= newElevatorsCount) return false;
    if (requiredElevator.get(current.currentPoint.y)) return true;
    int mustHaveBuilts = 0;
    for (int y = current.currentPoint.y; y < exit.y; y++) {
      if (requiredElevator.get(y)) mustHaveBuilts++;
    }
    return (newElevatorsCount - current.elevatorsBuilt) > mustHaveBuilts;
  }

  Path elevate(Path current) {
    Point from = current.currentPoint;
    int cost = current.cost;
    Point nextPoint = new Point(from.x, from.y + 1);

    List<Point> path = new ArrayList<>(current.innerPath);
    path.add(nextPoint);
    cost++;

    while (!nextPoint.equals(exit) && elevatorsByFloor.get(nextPoint.y).contains(nextPoint)) {
      nextPoint = new Point(nextPoint.x, nextPoint.y + 1);
      path.add(nextPoint);
      cost++;
    }
    return new Path(nextPoint, path, cost, current.clonesUsed, current.elevatorsBuilt, current.currentDirection);
  }

  Path buildElevator(Path current) {
    Point from = current.currentPoint;
    Point nextPoint = new Point(from.x, from.y + 1);
    List<Point> path = new ArrayList<>(current.innerPath);
    path.add(nextPoint);
    return new Path(nextPoint, path, current.cost + 4, current.clonesUsed + 1, current.elevatorsBuilt + 1, current.currentDirection);
  }

  Path pathTo(Path from, Point target) {
    if (from.currentPoint.equals(target)) return from;

    int cost = from.cost;
    List<Point> path = new ArrayList<>(from.innerPath);
    int clonesUsed = from.clonesUsed;
    Direction directionToTarget = target.x < from.currentPoint.x ? Direction.LEFT : Direction.RIGHT;
    if (directionToTarget == Direction.LEFT) {
      for (int x = from.currentPoint.x - 1; x >= target.x; x--) {
        path.add(new Point(x, from.currentPoint.y));
        cost++;
      }
    } else { //right
      for (int x = from.currentPoint.x + 1; x <= target.x; x++) {
        path.add(new Point(x, from.currentPoint.y));
        cost++;
      }
    }

    boolean directionChanged = !from.currentDirection.equals(directionToTarget);
    if (directionChanged) {
      cost += 3;
      clonesUsed++;
    }
    Direction newDirection = directionChanged ? from.currentDirection.opposite() : from.currentDirection;
    return new Path(target, path, cost, clonesUsed, from.elevatorsBuilt, newDirection);
  }

  List<Command> mapPathToCommands(List<Point> innerPath) {
    List<Command> commands = new ArrayList<>();
    Direction currentDirection = enterDirection;
    for (int i = 0; i < innerPath.size() - 1; i++) {
      Point from = innerPath.get(i);
      Point to = innerPath.get(i + 1);
      int dy = to.y - from.y;
      if (dy > 1 || dy < 0) throw new IllegalStateException();
      if (dy == 1) {
        boolean existingElevator = elevatorsByFloor.get(from.y).contains(from);
        if (existingElevator) {
          commands.add(Command.WAIT);
        } else {
          commands.add(Command.ELEVATOR);
          commands.add(Command.WAIT);
          commands.add(Command.WAIT);
          commands.add(Command.WAIT);
        }
      } else {
        Direction direction = from.x > to.x ? Direction.LEFT : Direction.RIGHT;
        if (direction.equals(currentDirection)) {
          commands.add(Command.WAIT);
        } else {
          commands.add(Command.BLOCK);
          commands.add(Command.WAIT);
          commands.add(Command.WAIT);
          commands.add(Command.WAIT);
          currentDirection = currentDirection.opposite();
        }
      }
    }
    commands.add(Command.WAIT);
    return commands;
  }

  boolean badPath(Path path) {
    Point currentPoint = path.currentPoint;
    if (!insideMap(currentPoint)) return true;
    if (currentPoint.y > exit.y) return true;
    if (maxCommands - path.cost < currentPoint.dist(exit)) return true;
    if (path.cost >= maxCommands) return true;
    if (path.clonesUsed >= maxClones) return true;
    if (path.elevatorsBuilt > newElevatorsCount) return true;
    if (path.cost > minCostToReach[pointToId(path.currentPoint)][path.elevatorsBuilt] + MIN_COST_DELTA) return true;

    return false;
  }

  Point closestTarget(Point from, Direction direction) {
    Point res = new Point(direction == Direction.LEFT ? -1 : width, from.y);

    for (Point elevator : elevatorsByFloor.get(from.y)) {
      if (elevator.equals(from)) return elevator;

      if (direction == Direction.RIGHT && elevator.x > from.x && elevator.dist(from) < res.dist(from)) {
        res = elevator;
      } else if (direction == Direction.LEFT && elevator.x < from.x && elevator.dist(from) < res.dist(from)) {
        res = elevator;
      }
    }

    if (from.y == exit.y) {
      if (direction == Direction.RIGHT && exit.x > from.x && exit.dist(from) < res.dist(from)) {
        res = exit;
      } else if (direction == Direction.LEFT && exit.x < from.x && exit.dist(from) < res.dist(from)) {
        res = exit;
      }
    }
    return res;
  }

  boolean insideMap(Point point) {
    if (point.x < 0 || point.x >= width) return false;
    if (point.y < 0 || point.y >= height) return false;
    return true;
  }

  double pathValue(Path path) {
    return 1.0 * (newElevatorsCount - path.elevatorsBuilt)/ newElevatorsCount;
  }

  int pointToId(Point point) {
    return point.x + point.y * width;
  }
}

enum Direction {
  RIGHT, LEFT;
  Direction opposite() {
    return this == RIGHT ? LEFT : RIGHT;
  }
}

enum Command {
  WAIT, BLOCK, ELEVATOR
}

class Path {
  final int cost, clonesUsed, elevatorsBuilt;
  final Direction currentDirection;
  final Point currentPoint;
  final List<Point> innerPath;
  Path(Point currentPoint, List<Point> innerPath, int cost, int clonesUsed, int elevatorsBuilt, Direction currentDirection) {
    this.currentPoint = currentPoint;
    this.innerPath = new ArrayList<>(innerPath);
    this.cost = cost;
    this.clonesUsed = clonesUsed;
    this.elevatorsBuilt = elevatorsBuilt;
    this.currentDirection = currentDirection;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Cost:").append(cost).append("|").append("Clones used:").append(clonesUsed).append("|")
      .append("ElevatorsBuilt:").append(elevatorsBuilt).append("|").append("DIRECTION:").append(currentDirection).append("|")
      .append("PATH:");
    for (int i = Math.max(0, innerPath.size() - 10); i < innerPath.size(); i++) {
      builder.append(innerPath.get(i)).append(" ");
    }
    return builder.toString();
  }
}

class Line {
  final Point from, to;
  Line(Point from, Point to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return from.equals(line.from) && to.equals(line.to);
  }

  @Override
  public int hashCode() {
    int result = from.hashCode();
    result = 31 * result + to.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("[%s-->%s]", from, to);
  }
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //manhattan distance
  int dist(Point to) {
    return Math.abs(x - to.x) + Math.abs(y - to.y);
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
    return 100 * x + y;
  }

  @Override
  public String toString() {
    return x + "," + y;
  }
}