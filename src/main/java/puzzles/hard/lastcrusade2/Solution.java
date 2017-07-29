package puzzles.hard.lastcrusade2;

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
  final Map<Point, Room> rooms;
  final Point exit;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    rooms = new HashMap<>(width, height);
    for (int y = 0; y < height; y++) {
      String LINE = scanner.nextLine();
      String[] types = LINE.split(" ");
      for (int x = 0; x < width; x++) {
        int type = Integer.valueOf(types[x]);
        if (type == 0) continue;
        boolean locked = type < 0;
        rooms.put(new Point(x, y), new Room(Math.abs(type), locked));
      }
    }
    int exitX = scanner.nextInt();
    exit = new Point(exitX, height - 1);
  }

  void solve() {
    Queue<Action> actions = new LinkedList<>();
    int previousRocksCount = 0;
    while (true) {
      int currentX = scanner.nextInt();
      int currentY = scanner.nextInt();
      Position currentPosition = Position.valueOf(scanner.next());
      Unit indy = new Unit(new Point(currentX, currentY), currentPosition);

      int rocksCount = scanner.nextInt();
      Set<Unit> rocks = new HashSet<>(rocksCount);
      for (int i = 0; i < rocksCount; i++) {
        int rockX = scanner.nextInt();
        int rockY = scanner.nextInt();
        Position rockEnterPosition = Position.valueOf(scanner.next());
        rocks.add(new Unit(new Point(rockX, rockY), rockEnterPosition));
      }

      if (actions.isEmpty() || previousRocksCount != rocksCount) {
        actions = prepareActions(findActionsToReachExit(indy, rocks));
      }

      previousRocksCount = rocksCount;

      Action nextAction = actions.poll();
      if (nextAction instanceof RotateAction) {
        RotateAction rotate = (RotateAction) nextAction;
        Room oldRoom = rooms.get(rotate.point);
        rooms.put(rotate.point, oldRoom.rotate(rotate.position));
      }
      System.out.println(nextAction);
    }
  }

  Queue<Action> prepareActions(List<Action> actions) {
    List<Flip> flips = new ArrayList<>();
    for (Action action : actions) {
      if (action instanceof Flip) {
        flips.add((Flip) action);
      }
    }
    List<Action> transform = new ArrayList<>();
    for (Action action : actions) {
      if (action instanceof WaitAction && !flips.isEmpty()) {
        Flip flip = flips.get(0);
        transform.add(new RotateAction(flip.point, Position.RIGHT));
        flips.remove(0);
        continue;
      } else if (action instanceof Flip) {
        transform.add(new RotateAction(((Flip)action).point, Position.RIGHT));
        continue;
      }
      transform.add(action);
    }

    Queue<Action> result = new LinkedList<>();
    for (Action action : transform) {
      if (!(action instanceof WaitAction)) {
        result.add(action);
      }
    }
    for (int i = 0; i < 30; i++) {
      result.add(new WaitAction());
    }

    return result;
  }

  List<Action> findActionsToReachExit(Unit indy, Set<Unit> rocks) {
    State startState = new State();
    startState.indy = indy;
    startState.rocks = rocks;
    startState.rooms = rooms;
    startState.actionsToReachState = new ArrayList<>();

    Deque<State> states = new LinkedList<>();
    states.add(startState);

    while (!states.isEmpty()) {
      State current = states.poll();
      if (lost(current)) {
        continue;
      }
      if (current.indy.coord.equals(exit)) {
        return current.actionsToReachState;
      }
      for (Action action : availableActions(current)) {
        states.addFirst(current.applyAction(action));
      }
    }

    System.err.println("Could't reach exit:(");
    return new ArrayList<>();
  }

  boolean lost(State state) {
    if (state.indy == null) return true;
    for (Unit rock : state.rocks) {
      if (rock == null) continue;
      if (rock.coord.equals(state.indy.coord) && rock.position.equals(state.indy.position)) {
        return true;
      }
    }
    return false;
  }

  List<Action> availableActions(State state) {
    List<Action> actions = new ArrayList<>();

    Unit currentIndy = state.indy;
    if (currentIndy == null) {
      return actions;
    }
    Room currentRoom = state.rooms.get(currentIndy.coord);
    if (currentRoom == null) {
      return actions;
    }
    Unit nextIndy = currentRoom.nextUnit(currentIndy);
    if (nextIndy == null) {
      return actions;
    }
    Room nextRoom = state.rooms.get(nextIndy.coord);
    if (nextRoom == null) {
      return actions;
    }

    if (nextRoom.positionAcceptable(nextIndy.position)) {
      actions.add(new WaitAction());
    }

    if (!nextRoom.locked) {
      actions.add(new RotateAction(nextIndy.coord, Position.RIGHT));
      if (nextRoom.type > 5) {
        actions.add(new RotateAction(nextIndy.coord, Position.LEFT));
        actions.add(new Flip(nextIndy.coord));
      }
    }

    actions.addAll(stopRocksActions(state.rocks));

    return actions;
  }

  List<Action> stopRocksActions(Set<Unit> rocks) {
    List<Action> actions = new ArrayList<>();
    for (Unit rock : rocks) {
      if (rock == null) continue;
      Unit nextRock = rooms.get(rock.coord).nextUnit(rock);
      if (nextRock == null) continue;

      Room nextRockRoom = rooms.get(nextRock.coord);
      if (nextRockRoom == null || nextRockRoom.locked) continue;

      if (nextRockRoom.positionAcceptable(nextRock.position)) {
        actions.add(new RotateAction(nextRock.coord, Position.RIGHT));
      }

      while (true) {
        nextRock = rooms.get(nextRock.coord).nextUnit(nextRock);
        if (nextRock == null) break;

        nextRockRoom = rooms.get(nextRock.coord);
        if (nextRockRoom == null) break;

        if (!nextRockRoom.locked && nextRockRoom.positionAcceptable(nextRock.position)) {
          actions.add(new RotateAction(nextRock.coord, Position.RIGHT));
        }
      }
    }
    Collections.reverse(actions);
    return actions;
  }
}

class State {
  Unit indy;
  Set<Unit> rocks;
  Map<Point, Room> rooms;
  List<Action> actionsToReachState;

  State applyAction(Action action) {
    State newState = new State();
    newState.actionsToReachState = new ArrayList<>(actionsToReachState);
    newState.actionsToReachState.add(action);
    newState.rooms = new HashMap<>(rooms);

    if (action instanceof RotateAction) {
      RotateAction rotate = (RotateAction) action;
      Room oldRoom = newState.rooms.get(rotate.point);
      newState.rooms.put(rotate.point, oldRoom.rotate(rotate.position));
    } else if (action instanceof Flip) {
      Flip flip = (Flip) action;
      Room oldRoom = newState.rooms.get(flip.point);
      newState.rooms.put(flip.point, oldRoom.rotate(Position.RIGHT).rotate(Position.RIGHT));
    }

    newState.rocks = new HashSet<>();
    for (Unit rock : rocks) {
      if (rock == null) continue;
      newState.rocks.add(newState.rooms.get(rock.coord).nextUnit(rock));
    }
    if (indy != null && newState.rooms.get(indy.coord) != null) {
      newState.indy = newState.rooms.get(indy.coord).nextUnit(indy);
    }
    return newState;
  }
}

class Unit {
  final Point coord;
  final Position position;
  Unit(Point coord, Position position) {
    this.coord = coord;
    this.position = position;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Unit rock = (Unit) o;
    return coord.equals(rock.coord);
  }

  @Override
  public int hashCode() {
    return coord.hashCode();
  }

  @Override
  public String toString() {
    return coord + " " + position;
  }
}

class Room {
  final int type;
  final boolean locked;

  Room(int type, boolean locked) {
    this.type = type;
    this.locked = locked;
  }

  Room(int type) {
    this.type = type;
    this.locked = false;
  }

  boolean positionAcceptable(Position position) {
    switch (type) {
      case 1: return true;
      case 2: return position == Position.LEFT || position == Position.RIGHT;
      case 3: return position == Position.TOP;
      case 4: return position == Position.TOP || position == Position.RIGHT;
      case 5: return position == Position.TOP || position == Position.LEFT;
      case 6: return position == Position.LEFT || position == Position.RIGHT;
      case 7: return position == Position.TOP || position == Position.RIGHT;
      case 8: return position == Position.LEFT || position == Position.RIGHT;
      case 9: return position == Position.TOP || position == Position.LEFT;
      case 10: return position == Position.TOP;
      case 11: return position == Position.TOP;
      case 12: return position == Position.RIGHT;
      case 13: return position == Position.LEFT;
    }
    return false;
  }

  Unit nextUnit(Unit from) {
    switch (type) {
      case 1: return new Unit(from.coord.down(), Position.TOP);
      case 2:
        if (from.position == Position.LEFT) {
          return new Unit(from.coord.right(), from.position);
        } else if (from.position == Position.RIGHT) {
          return new Unit(from.coord.left(), from.position);
        }
        break;
      case 3:
        if (from.position == Position.TOP) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 4:
        if (from.position == Position.TOP) {
          return new Unit(from.coord.left(), Position.RIGHT);
        } else if (from.position == Position.RIGHT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 5:
        if (from.position == Position.TOP) {
          return new Unit(from.coord.right(), Position.LEFT);
        } else if (from.position == Position.LEFT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 6:
        if (from.position == Position.LEFT) {
          return new Unit(from.coord.right(), from.position);
        } else if (from.position == Position.RIGHT) {
          return new Unit(from.coord.left(), from.position);
        }
        break;
      case 7:
        if (from.position == Position.TOP || from.position == Position.RIGHT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 8:
        if (from.position == Position.LEFT || from.position == Position.RIGHT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 9:
        if (from.position == Position.TOP || from.position == Position.LEFT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 10:
        if (from.position == Position.TOP) {
          return new Unit(from.coord.left(), Position.RIGHT);
        }
        break;
      case 11:
        if (from.position == Position.TOP) {
          return new Unit(from.coord.right(), Position.LEFT);
        }
        break;
      case 12:
        if (from.position == Position.RIGHT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
      case 13:
        if (from.position == Position.LEFT) {
          return new Unit(from.coord.down(), Position.TOP);
        }
        break;
    }
    //System.err.println("from = " + from + "; roomType = " + type);
    return null;
  }

  Room rotate(Position position) {
    if (locked) throw new IllegalStateException();
    if (position == Position.TOP) throw new IllegalStateException();
    switch (type) {
      case 1: return new Room(1);
      case 2: return new Room(3);
      case 3: return new Room(2);
      case 4: return new Room(5);
      case 5: return new Room(4);
      case 6: return new Room(position == Position.LEFT ? 9 : 7);
      case 7: return new Room(position == Position.LEFT ? 6 : 8);
      case 8: return new Room(position == Position.LEFT ? 7 : 9);
      case 9: return new Room(position == Position.LEFT ? 8 : 6);
      case 10: return new Room(position == Position.LEFT ? 13 : 11);
      case 11: return new Room(position == Position.LEFT ? 10 : 12);
      case 12: return new Room(position == Position.LEFT ? 11 : 13);
      case 13: return new Room(position == Position.LEFT ? 12 : 10);
    }
    //System.err.println("roomType = " + type + "; rotatePosition = " + position);
    throw new IllegalStateException();
  }

  @Override
  public String toString() {
    return type + "|" + locked;
  }
}

interface Action {}
class RotateAction implements Action {
  final Point point;
  final Position position;
  RotateAction(Point point, Position position) {
    this.point = point;
    this.position = position;
  }

  @Override
  public String toString() {
    return point + " " + position;
  }
}
class Flip implements Action {
  final Point point;
  Flip(Point point) {
    this.point = point;
  }
}
class WaitAction implements Action{
  @Override
  public String toString() {
    return "WAIT";
  }
}

enum Position{
  TOP, RIGHT, LEFT
}

class Point{
  final int x;
  final int y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point right() {
    return new Point(x + 1, y);
  }

  public Point left() {
    return new Point(x - 1, y);
  }

  public Point down() {
    return new Point(x, y + 1);
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