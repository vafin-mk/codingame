package puzzles.veryhard.lastcrusade3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
//    try {
//      new Solver(new Scanner(new File("src/main/java/puzzles/veryhard/lastcrusade3/test.txt"))).solve();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
  }
}

class Solver {
  final Scanner scanner;
  final int width;
  final int height;
  final Point enter;
  final Direction enterDirection;
  final Point exit;
  final Map<Point, Room> predefinedRooms;
  Map<Point, Room> rooms;
  List<Point> indyPath;
  final Set<Ball> rockStarters;
  Map<Ball, List<Point>> rocksPaths;
  Map<Ball, List<Point>> rocksStoppers;
  Map<Ball, Integer> rockIntersectionTime;


  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    predefinedRooms = new HashMap<>();
    for (int y = 0; y < height; y++) {
      String[] roomTypes = scanner.nextLine().split(" ");
      for (int x = 0; x < width; x++) {
        int type = Integer.valueOf(roomTypes[x]);
        predefinedRooms.put(new Point(x, y), Room.fromType(type));
      }
    }
    int exitX = scanner.nextInt();
    exit = new Point(exitX, height - 1);

    int enterX = scanner.nextInt();
    int enterY = scanner.nextInt();
    enter = new Point(enterX, enterY);
    enterDirection = Direction.valueOf(scanner.next());

    int R = scanner.nextInt(); // the number of rocks currently in the grid.
    for (int i = 0; i < R; i++) {
      int XR = scanner.nextInt();
      int YR = scanner.nextInt();
      String POSR = scanner.next();
    }

    rockStarters = new HashSet<>();
    fillRockStarters();
  }

  void fillRockStarters() {
    for (Map.Entry<Point, Room> entry : predefinedRooms.entrySet()) {
      if (!entry.getValue().locked || entry.getKey().equals(enter)) continue;
      if (entry.getKey().x == 0 && entry.getValue().canEnterFrom(Direction.LEFT)) {
        rockStarters.add(new Ball(entry.getKey(), Direction.LEFT));
      }
      if (entry.getKey().y == 0 && entry.getValue().canEnterFrom(Direction.TOP)) {
        rockStarters.add(new Ball(entry.getKey(), Direction.TOP));
      }
      if (entry.getKey().x == width - 1 && entry.getValue().canEnterFrom(Direction.RIGHT)) {
        rockStarters.add(new Ball(entry.getKey(), Direction.RIGHT));
      }
    }
  }

  void solve() {
    List<Command> commands = buildCommandsToReachExit();
    commands = cutFlips(commands);
    commands = defenceFromRocks(commands);
    commands = collapse(commands);
    for (Command command : commands) {
      System.out.println(command);
    }
  }

  List<Command> collapse(List<Command> commands) {
    int waits = 0;
    Iterator<Command> iterator = commands.iterator();
    while (iterator.hasNext()) {
      Command command = iterator.next();
      if (command.commandType == CommandType.WAIT) {
        waits++;
        iterator.remove();
      }
    }
    for (int i = 0; i < waits; i++) {
      commands.add(new Command(new Point(0,0), CommandType.WAIT, 0));
    }
    return commands;
  }

  List<Command> defenceFromRocks(List<Command> commands) {
    int waitIndex = -1;
    //quite a hack!
    for (int index = 0; index < commands.size(); index++) {
      Command command = commands.get(index);
      if (command.commandType == CommandType.WAIT && waitIndex == -1) {
        waitIndex = index;
      } else {
        if (distanceToClosestRockStarter(command.point) == 1) {
          if (waitIndex == -1) {
            break;
          }
          commands.remove(waitIndex);
          commands.add(waitIndex, command);
          commands.remove(index);
          commands.add(index, new Command(new Point(0,0), CommandType.WAIT, 0));
          break;
        }
      }
    }
    calculateThreats();

    for (int i = 0; i < commands.size(); i++) {
      Command command = commands.get(i);
      if (command.commandType == CommandType.WAIT) {
        Command defenceCommand  = defenceCommand(i);
        if (defenceCommand == null) break;
        commands.remove(i);
        commands.add(i, defenceCommand);
      }
    }
    return commands;
  }

  List<Command> cutFlips(List<Command> commands) {
    int closestWaitIndex = -1;
    for (int index = 0; index < commands.size(); index++) {
      Command command = commands.get(index);
      if (command.commandType == CommandType.WAIT) {
        closestWaitIndex = index;
      } else if (command.commandType == CommandType.FLIP) {
        commands.remove(closestWaitIndex);
        commands.add(closestWaitIndex, new Command(command.point, CommandType.ROTATE_RIGHT, command.maxDistance));
        commands.remove(index);
        commands.add(index, new Command(command.point, CommandType.ROTATE_RIGHT, command.maxDistance));
      }
    }
    return commands;
  }

  List<Command> buildCommandsToReachExit() {
    State beginState = new State();
    beginState.indy = new Ball(enter, enterDirection);
    beginState.rooms = predefinedRooms;
    beginState.commands = new ArrayList<>();
    beginState.depth = 0;
    beginState.flipsAvailable = 0;
    beginState.path = new ArrayList<>();
    beginState.path.add(enter);

    Deque<State> states = new LinkedList<>();
    states.add(beginState);

    while (!states.isEmpty()) {
      State current = states.poll();
      if (current.indy == null) continue;
      if (!current.rooms.get(current.indy.point).canEnterFrom(current.indy.direction)) continue;
      if (current.indy.point.equals(exit)) {
        this.rooms = current.rooms;
        this.indyPath = current.path;
        return current.commands;
      }
      for (Command command : commandsFromState(current)) {
        states.addFirst(current.applyCommand(command));
      }
    }
    System.err.println("PATH NOT FOUND !!!");
    return new ArrayList<>();
  }

  List<Command> commandsFromState(State state) {
    List<Command> commands = new ArrayList<>();
    Ball nextPosition = state.rooms.get(state.indy.point).nextBall(state.indy);
    if (nextPosition == null) return commands;

    Point nextPoint = nextPosition.point;
    if (outsideMap(nextPoint)) return commands;
    Room nextRoom = state.rooms.get(nextPoint);

    if (nextRoom.canEnterFrom(nextPosition.direction)) {
      commands.add(new Command(nextPoint, CommandType.WAIT, state.depth + 1));
    }
    if (nextRoom.canRotate()) {
      commands.add(new Command(nextPoint, CommandType.ROTATE_RIGHT, state.depth + 1));
    }
    if (nextRoom.canFlip()) {
      commands.add(new Command(nextPoint, CommandType.ROTATE_LEFT, state.depth + 1));
      if (state.flipsAvailable > 0) {
        commands.add(new Command(nextPoint, CommandType.FLIP, state.depth + 1));
      }
    }

    return commands;
  }

  void calculateThreats() {
    rocksPaths = new HashMap<>();
    rocksStoppers = new HashMap<>();
    rockIntersectionTime = new HashMap<>();
    for (Ball rockStarter : rockStarters) {
      List<Point> path = findPath(rockStarter);
      rocksPaths.put(rockStarter, path);
      rocksStoppers.put(rockStarter, path.stream()
        .filter(point -> !indyPath.contains(point) && rooms.get(point).canRotate())
        .collect(Collectors.toList()));

      int intersectIn = -1;
      for (int i = 0; i < path.size(); i++) {
        if (indyPath.contains(path.get(i))) {
          //rock intersect before indy will be at place
          if (indyPath.indexOf(path.get(i)) >= i) {
            intersectIn = i;
          }
          break;
        }
      }

      rockIntersectionTime.put(rockStarter, intersectIn);
    }

//    List<Ball> toRemove = new ArrayList<>();
//    for (Ball rockStarter : rockStarters) {
//      //not a threat
//      if (rockIntersectionTime.get(rockStarter) <= 0) {
//        //toRemove.add(rockStarter);
//      }
//      //can't do a shit about it
//      if (rocksStoppers.get(rockStarter).isEmpty()) {
//        //toRemove.add(rockStarter);
//      }
//    }
//
//    for (Ball ball : toRemove) {
//      rockIntersectionTime.remove(ball);
//      rocksStoppers.remove(ball);
//      rocksPaths.remove(ball);
//      rockStarters.remove(ball);
//    }
  }

  Command defenceCommand(int index) {
    Ball priorityThreat = null;
    int priorityTime = 10000;
    for (Ball rockStarter : rockStarters) {
      int intersectionTime = rockIntersectionTime.get(rockStarter);
      if (intersectionTime >= index && !rocksStoppers.get(rockStarter).isEmpty()) {
        if (priorityThreat == null || priorityTime > intersectionTime) {
          priorityThreat = rockStarter;
          priorityTime = intersectionTime;
        }
      }
    }
    if (priorityThreat != null) {
      List<Point> threatPath = rocksPaths.get(priorityThreat);
      for (Point point : rocksStoppers.get(priorityThreat)) {
        if (index < threatPath.indexOf(point)) {
          rockStarters.remove(priorityThreat);
          return new Command(point, CommandType.ROTATE_RIGHT, index);
        }
      }
    }

    List<Ball> defenders = new ArrayList<>();
    List<Ball> unavoidableThreats = new ArrayList<>();
    for (Ball rockStarter : rockStarters) {
      int intersectionTime = rockIntersectionTime.get(rockStarter);
      boolean haveStoppers = !rocksStoppers.get(rockStarter).isEmpty();
      if (intersectionTime < 0 && haveStoppers) {
        defenders.add(rockStarter);
      } else if (intersectionTime >= index && !haveStoppers) {
        unavoidableThreats.add(rockStarter);
      }
    }

    for (Ball defender : defenders) {
      for (Ball unavoidableThreat : unavoidableThreats) {
        Command command = interceptRocksBetweenThemselves(defender, unavoidableThreat);
        if (command != null) {
          rockStarters.remove(defender);
          rockStarters.remove(unavoidableThreat);
          return command;
        }
      }
    }

    return null;
  }

  Command interceptRocksBetweenThemselves(Ball defender, Ball threat) {
    List<Point> threatPath = rocksPaths.get(threat);
    for (Point point : rocksStoppers.get(defender)) {
      Room pointRoom = rooms.get(point);
      //try right
      rooms.put(point, pointRoom.rotate(Direction.RIGHT));
      List<Point> path = findPath(defender);
      path.retainAll(threatPath);
      //revert room
      rooms.put(point, pointRoom);
      //have intersections
      if (!path.isEmpty()) {
        return new Command(point, CommandType.ROTATE_RIGHT, 0);
      }

      //try left
      rooms.put(point, pointRoom.rotate(Direction.LEFT));
      path = findPath(defender);
      path.retainAll(threatPath);
      //revert room
      rooms.put(point, pointRoom);
      //have intersections
      if (!path.isEmpty()) {
        return new Command(point, CommandType.ROTATE_LEFT, 0);
      }
    }
    return null;
  }

  List<Point> findPath(Ball starter) {
    List<Point> result = new ArrayList<>();
    Ball ball = starter;
    result.add(ball.point);
    Room room = rooms.get(ball.point);
    while (true) {
      ball = room.nextBall(ball);
      if (ball == null) break;
      if (outsideMap(ball.point)) break;
      room = rooms.get(ball.point);
      if (!room.canEnterFrom(ball.direction)) break;
      result.add(ball.point);
    }
    return result;
  }

  boolean outsideMap(Point point) {
    return point.x < 0 || point.x >= width || point.y < 0 || point.y >= height;
  }

  int distanceToClosestRockStarter(Point from) {
    int closest = 1000;
    for (Ball rockStarter : rockStarters) {
      int dist = rockStarter.point.dist(from);
      if (dist < closest) closest = dist;
    }
    return closest;
  }
}

class State {
  Ball indy;
  Map<Point, Room> rooms;
  List<Command> commands;
  int depth;
  int flipsAvailable;
  List<Point> path;

  State applyCommand(Command command) {
    if (this.commands.contains(command)) throw new IllegalStateException();

    State newState = new State();
    newState.commands = new ArrayList<>(commands);
    newState.rooms = new HashMap<>(rooms);
    newState.depth = depth + 1;
    newState.flipsAvailable = flipsAvailable;
    newState.path = new ArrayList<>(path);

    Room affectedRoom = newState.rooms.get(command.point);

    switch (command.commandType) {
      case WAIT:
        newState.flipsAvailable++;
        break;
      case ROTATE_RIGHT:
        if (!affectedRoom.canRotate()) throw new IllegalStateException();
        newState.rooms.put(command.point, affectedRoom.rotate(Direction.RIGHT));
        break;
      case ROTATE_LEFT:
        if (!affectedRoom.canRotate()) throw new IllegalStateException();
        newState.rooms.put(command.point, affectedRoom.rotate(Direction.LEFT));
        break;
      case FLIP:
        if (!affectedRoom.canFlip() || newState.flipsAvailable <= 0) throw new IllegalStateException();
        newState.rooms.put(command.point, affectedRoom.flip());
        newState.flipsAvailable--;
        break;
    }
    newState.commands.add(command);
    newState.indy = newState.rooms.get(indy.point).nextBall(indy);
    newState.path.add(newState.indy.point);
    return newState;
  }
}

class Ball {
  final Point point;
  final Direction direction;
  Ball(Point point, Direction direction) {
    this.point = point;
    this.direction = direction;
  }

  @Override
  public String toString() {
    return point + " " + direction;
  }
}

enum Direction {
  TOP, RIGHT, LEFT
}

enum CommandType {
  WAIT, ROTATE_RIGHT, ROTATE_LEFT, FLIP
}

class Command {
  final Point point;
  final CommandType commandType;
  final int maxDistance;
  Command(Point point, CommandType commandType, int maxDistance) {
    this.point = point;
    this.commandType = commandType;
    this.maxDistance = maxDistance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Command command = (Command) o;
    return point.equals(command.point);
  }

  @Override
  public int hashCode() {
    return point.hashCode();
  }

  @Override
  public String toString() {
    switch (commandType) {
      case WAIT: return "WAIT";
      case ROTATE_RIGHT: return point + " " + "RIGHT";
      case ROTATE_LEFT: return point + " " + "LEFT";
      case FLIP: return point + " " + "FLIP";
    }
    throw new IllegalStateException();
  }
}

class Point{
  final int x;
  final int y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  Point right() {
    return new Point(x + 1, y);
  }

  Point left() {
    return new Point(x - 1, y);
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

abstract class Room {
  final boolean locked;
  Room(boolean locked) {
    this.locked = locked;
  }
  abstract Ball nextBall(Ball from);
  abstract boolean canRotate();
  abstract boolean canFlip();
  abstract Room rotate(Direction direction);
  abstract Room flip();
  abstract boolean canEnterFrom(Direction direction);

  static Room fromType(int type) {
    switch (Math.abs(type)) {
      case 0: return new Room0(true);
      case 1: return new Room1(type < 0);
      case 2: return new Room2(type < 0);
      case 3: return new Room3(type < 0);
      case 4: return new Room4(type < 0);
      case 5: return new Room5(type < 0);
      case 6: return new Room6(type < 0);
      case 7: return new Room7(type < 0);
      case 8: return new Room8(type < 0);
      case 9: return new Room9(type < 0);
      case 10: return new Room10(type < 0);
      case 11: return new Room11(type < 0);
      case 12: return new Room12(type < 0);
      case 13: return new Room13(type < 0);
    }
    throw new IllegalStateException("" + type);
  }
}

class Room0 extends Room {
  Room0(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    return null;
  }

  @Override boolean canRotate() {
    return false;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return this;
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return false;
  }
}

class Room1 extends Room {
  Room1(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return false;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return this;
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return true;
  }
}

class Room2 extends Room {
  Room2(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.TOP) return null;
    Point point = from.direction == Direction.LEFT ? from.point.right() : from.point.left();
    return new Ball(point, from.direction);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return new Room3(locked);
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.LEFT || direction == Direction.RIGHT;
  }
}

class Room3 extends Room {
  Room3(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction != Direction.TOP) return null;
    return new Ball(from.point.down(), from.direction);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return new Room2(locked);
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP;
  }
}

class Room4 extends Room {
  Room4(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.LEFT) return null;
    return from.direction == Direction.TOP
      ? new Ball(from.point.left(), Direction.RIGHT)
      : new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return new Room5(locked);
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP || direction == Direction.RIGHT;
  }
}

class Room5 extends Room {
  Room5(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.RIGHT) return null;
    return from.direction == Direction.TOP
      ? new Ball(from.point.right(), Direction.LEFT)
      : new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return false;
  }

  @Override Room rotate(Direction direction) {
    return new Room4(locked);
  }

  @Override Room flip() {
    return this;
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP || direction == Direction.LEFT;
  }
}

class Room6 extends Room {
  Room6(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.TOP) return null;
    Point point = from.direction == Direction.LEFT ? from.point.right() : from.point.left();
    return new Ball(point, from.direction);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room7(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room9(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room8(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.LEFT || direction == Direction.RIGHT;
  }
}

class Room7 extends Room {
  Room7(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.LEFT) return null;
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room8(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room6(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room9(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP || direction == Direction.RIGHT;
  }
}

class Room8 extends Room {
  Room8(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.TOP) return null;
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room9(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room7(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room6(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.LEFT || direction == Direction.RIGHT;
  }
}

class Room9 extends Room {
  Room9(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction == Direction.RIGHT) return null;
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room6(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room8(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room7(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP || direction == Direction.LEFT;
  }
}

class Room10 extends Room {
  Room10(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction != Direction.TOP) return null;
    return new Ball(from.point.left(), Direction.RIGHT);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room11(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room13(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room12(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP;
  }
}

class Room11 extends Room {
  Room11(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction != Direction.TOP) return null;
    return new Ball(from.point.right(), Direction.LEFT);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room12(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room10(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room13(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.TOP;
  }
}

class Room12 extends Room {
  Room12(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction != Direction.RIGHT) return null;
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room13(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room11(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room10(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.RIGHT;
  }
}

class Room13 extends Room {
  Room13(boolean locked) {
    super(locked);
  }

  @Override Ball nextBall(Ball from) {
    if (from.direction != Direction.LEFT) return null;
    return new Ball(from.point.down(), Direction.TOP);
  }

  @Override boolean canRotate() {
    return !locked;
  }

  @Override
  boolean canFlip() {
    return !locked;
  }

  @Override Room rotate(Direction direction) {
    if (direction == Direction.RIGHT) {
      return new Room10(locked);
    }
    if (direction == Direction.LEFT) {
      return new Room12(locked);
    }
    return null;
  }

  @Override Room flip() {
    return new Room11(locked);
  }

  @Override boolean canEnterFrom(Direction direction) {
    return direction == Direction.LEFT;
  }
}