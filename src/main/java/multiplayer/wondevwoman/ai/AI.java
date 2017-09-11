package multiplayer.wondevwoman.ai;

import common.ai.AbstractAI;
import common.model.Command;
import common.model.Point2I;
import multiplayer.wondevwoman.model.Direction;
import multiplayer.wondevwoman.model.commands.MoveBuild;
import multiplayer.wondevwoman.model.commands.PushBuild;
import multiplayer.wondevwoman.model.commands.Surrender;

import java.util.*;

public class AI extends AbstractAI {

  @Override
  protected Command think() {
    if (availableCommands.isEmpty()) return new Surrender();
    return availableCommands.stream().sorted((c1, c2) -> {
      Point2I target1 = moveTarget(c1);
      Point2I target2 = moveTarget(c2);
      return Integer.compare(grid.get(target2), grid.get(target1));
    }).findFirst().get();
  }

  @Override
  protected void init() {
    size = scanner.nextInt();
    grid = new HashMap<>(size);
    unitsPerPlayer = scanner.nextInt();
  }

  @Override
  protected void readInput() {
    grid.clear();
    for (int y = 0; y < size; y++) {
      String row = scanner.next();
      for (int x = 0; x < row.length(); x++) {
        char ch = row.charAt(x);
        if (Character.isDigit(ch)) {
          int value = Character.getNumericValue(ch);
          if (value >= 0 && value <= 3) {
            grid.put(new Point2I(x, y), value);
          }
        }
      }
    }

    myUnits.clear();
    for (int i = 0; i < unitsPerPlayer; i++) {
      int unitX = scanner.nextInt();
      int unitY = scanner.nextInt();
      myUnits.add(new Point2I(unitX, unitY));
    }

    rivalUnits.clear();
    for (int i = 0; i < unitsPerPlayer; i++) {
      int otherX = scanner.nextInt();
      int otherY = scanner.nextInt();
      rivalUnits.add(new Point2I(otherX, otherY));
    }

    buildAvailableCommands();
    int legalActions = scanner.nextInt();
    if (availableCommands.size() != legalActions) {
      System.err.println(availableCommands);
      throw new IllegalStateException(availableCommands.size() + " != " + legalActions);
    }
    for (int i = 0; i < legalActions; i++) {
      String atype = scanner.next();
      int index = scanner.nextInt();
      String dir1 = scanner.next();
      String dir2 = scanner.next();
      Command command = null;
      switch (atype) {
        case "MOVE&BUILD":
          command = new MoveBuild(index, Direction.valueOf(dir1), Direction.valueOf(dir2));
          break;
        case "PUSH&BUILD":
          command = new PushBuild(index, Direction.valueOf(dir1), Direction.valueOf(dir2));
          break;
        default:
          throw new IllegalStateException();
      }

      if (!availableCommands.contains(command)) {
        throw new IllegalStateException(command + " not exist");
      }
    }

  }

  @Override
  protected void sendOutput(Command command) {
    command.execute();
  }

  private void buildAvailableCommands() {
    availableCommands.clear();
    for (int index = 0; index < myUnits.size(); index++) {
      Point2I unit = myUnits.get(index);
      Set<Point2I> neighs = moveNeighbours(unit);
      for (Point2I neigh : neighs) {
        for (Point2I buildTarget : buildNeighbours(neigh, unit)) {
          availableCommands.add(new MoveBuild(index, findDirection(unit, neigh), findDirection(neigh, buildTarget)));
        }
      }

      for (Point2I neigh : neighbours(unit)) {
        if (rivalUnits.contains(neigh)) {
          for (Point2I pushTarget : moveNeighbours(neigh)) {
            Direction targetDirection = findDirection(unit, neigh);
            Direction pushDirection = findDirection(neigh, pushTarget);
            if (!pushDirectionValid(targetDirection, pushDirection)) continue;
            availableCommands.add(new PushBuild(index, targetDirection, pushDirection));
          }
        }
      }
    }
  }

  private Set<Point2I> moveNeighbours(Point2I point) {
    Set<Point2I> result = new HashSet<>();
    for (Point2I neigh : neighbours(point)) {
      if (grid.get(neigh) > grid.get(point) + 1) continue;
      if (isOccupied(neigh)) continue;
      result.add(neigh);
    }
    return result;
  }

  private Set<Point2I> buildNeighbours(Point2I point, Point2I builder) {
    Set<Point2I> result = new HashSet<>();
    for (Point2I neigh : neighbours(point)) {
      if (isOccupied(neigh) && !neigh.equals(builder)) continue;
      result.add(neigh);
    }
    return result;
  }

  private Set<Point2I> neighbours(Point2I point) {
    Set<Point2I> result = new HashSet<>();
    for (Direction direction : Direction.values()) {
      Point2I neigh = findPoint(point, direction);
      if (grid.containsKey(neigh)) {
        result.add(neigh);
      }
    }
    return result;
  }

  private Point2I moveTarget(Command command) {
    if (command instanceof MoveBuild) {
      MoveBuild mb = (MoveBuild) command;
      Point2I unit = myUnits.get(mb.index);
      return findPoint(unit, mb.moveDirection);
    } else {
      PushBuild pb = (PushBuild) command;
      Point2I unit = myUnits.get(pb.index);
      return unit;
    }
  }

  private boolean pushDirectionValid(Direction targetDirection, Direction pushDirection) {
    String target = targetDirection.name();
    String push = pushDirection.name();
    if (target.length() == 2) {
      return push.equals(target) || push.equals(target.substring(0, 1)) || push.equals(target.substring(1, 2));
    } else {
      return push.contains(target);
    }
  }

  private boolean isOccupied(Point2I point) {
    return myUnits.contains(point) || rivalUnits.contains(point);
  }

  private Point2I findPoint(Point2I from, Direction direction) {
    switch (direction) {
      case N:
        return new Point2I(from.x, from.y - 1);
      case S:
        return new Point2I(from.x, from.y + 1);
      case W:
        return new Point2I(from.x - 1, from.y);
      case E:
        return new Point2I(from.x + 1, from.y);
      case NW:
        return new Point2I(from.x - 1, from.y - 1);
      case SW:
        return new Point2I(from.x - 1, from.y + 1);
      case NE:
        return new Point2I(from.x + 1, from.y - 1);
      case SE:
        return new Point2I(from.x + 1, from.y + 1);
    }
    throw new IllegalStateException();
  }

  private Direction findDirection(Point2I from, Point2I point) {
    if (point.x < from.x && point.y < from.y) {
      return Direction.NW;
    } else if (point.x < from.x && point.y > from.y) {
      return Direction.SW;
    } else if (point.x > from.x && point.y < from.y) {
      return Direction.NE;
    } else if (point.x > from.x && point.y > from.y) {
      return Direction.SE;
    } else if (point.y < from.y) {
      return Direction.N;
    } else if (point.y > from.y) {
      return Direction.S;
    } else if (point.x < from.x) {
      return Direction.W;
    } else if (point.x > from.x) {
      return Direction.E;
    } else if (equals(point)) {
      throw new IllegalStateException("same point");
    } else {
      throw new IllegalStateException();
    }
  }

  public AI(Scanner scanner) {
    super(scanner);
  }

  private int size;
  private int unitsPerPlayer;
  private Map<Point2I, Integer> grid;
  private List<Point2I> myUnits = new ArrayList<>();
  private List<Point2I> rivalUnits = new ArrayList<>();
  private Set<Command> availableCommands = new HashSet<>();
}