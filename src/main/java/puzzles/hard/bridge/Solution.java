package puzzles.hard.bridge;

import java.util.*;

public class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final int initialMotos;
  final int targetMotos;
  final Road road;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    initialMotos = scanner.nextInt();
    targetMotos = scanner.nextInt();
    String L0 = scanner.next();
    road = new Road(L0.length());
    road.addLane(0, L0);
    String L1 = scanner.next();
    road.addLane(1, L1);
    String L2 = scanner.next();
    road.addLane(2, L2);
    String L3 = scanner.next();
    road.addLane(3, L3);
  }

  void solve() {
    int initialSpeed = scanner.nextInt();
    List<Point> motos = new ArrayList<>();

    for (int i = 0; i < initialMotos; i++) {
      int X = scanner.nextInt();
      int Y = scanner.nextInt();
      int A = scanner.nextInt();
      if (A == 1) {
        motos.add(new Point(X, Y));
      }
    }

    State startState = new State();
    startState.speed = initialSpeed;
    startState.motos = motos;
    startState.commands = new ArrayList<>();
    startState.road = road;

    Deque<State> states = new ArrayDeque<>();
    states.add(startState);
    List<Command> winCommands;

    while (true) {
      State curr = states.poll();
      if (lost(curr)) continue;
      if (win(curr)) {
        winCommands = curr.commands;
        break;
      }
      for (Command command : availableCommands(curr)) {
        states.addFirst(curr.applyCommand(command));
      }
    }

    for (Command winCommand : winCommands) {
      System.out.println(winCommand);
    }
    for (int i = 0; i < 100; i++) {
      System.out.println(Command.SPEED);
    }
  }

  List<Command> availableCommands(State state) {
    List<Command> commands = new ArrayList<>();
    if (state.speed == 0) {
      commands.add(Command.SPEED);
      return commands;
    }

    commands.add(Command.SLOW);

    boolean topOccupied = false;
    boolean botOccupied = false;
    for (Point moto : state.motos) {
      if (moto.y == 0) topOccupied = true;
      if (moto.y == 3) botOccupied = true;
    }
    if (!topOccupied) commands.add(Command.UP);
    if (!botOccupied) commands.add(Command.DOWN);

    commands.add(Command.JUMP);
    commands.add(Command.WAIT);
    commands.add(Command.SPEED);
    return commands;
  }

  boolean lost(State state) {
    return state.motos.size() < targetMotos || state.commands.size() > 40;
  }

  boolean win(State state) {
    for (Point moto : state.motos) {
      for (int x = moto.x; x < road.length; x++) {
        if (road.road.get(new Point(x, moto.y))) return false;
      }
    }
    return true;
  }
}

class State {
  Road road;
  int speed;
  List<Point> motos;
  List<Command> commands;

  State applyCommand(Command command) {
    State newState = new State();
    newState.road = road;
    newState.motos = new ArrayList<>();
    newState.commands = new ArrayList<>(commands);
    newState.commands.add(command);
    newState.speed = speed;

    switch (command) {
      case SPEED:
        newState.speed = speed + 1;
        moto: for (Point moto : motos) {
          int fromX = moto.x;
          int toX = Math.min(fromX + newState.speed, road.length - 1);
          for (int x = fromX; x <= toX; x++) {
            if (road.road.get(new Point(x, moto.y))) continue moto;
          }
          newState.motos.add(new Point(toX, moto.y));
        }
        break;
      case SLOW:
        newState.speed = speed - 1;
        moto: for (Point moto : motos) {
          int fromX = moto.x;
          int toX = Math.min(fromX + newState.speed, road.length - 1);
          for (int x = fromX; x <= toX; x++) {
            if (road.road.get(new Point(x, moto.y))) continue moto;
          }
          newState.motos.add(new Point(toX, moto.y));
        }
        break;
      case JUMP:
        for (Point moto : motos) {
          int toX = Math.min(moto.x + newState.speed, road.length - 1);
          if (road.road.get(new Point(toX, moto.y))) continue;
          newState.motos.add(new Point(toX, moto.y));
        }
        break;
      case WAIT:
        if (speed <= 0) throw new IllegalStateException();
        moto: for (Point moto : motos) {
          int fromX = moto.x;
          int toX = Math.min(fromX + newState.speed, road.length - 1);
          for (int x = fromX; x <= toX; x++) {
            if (road.road.get(new Point(x, moto.y))) continue moto;
          }
          newState.motos.add(new Point(toX, moto.y));
        }
        break;
      case UP:
        moto: for (Point moto : motos) {
          if (moto.y == 0) throw new IllegalStateException();
          int fromX = moto.x;
          int toX = Math.min(fromX + newState.speed, road.length - 1);
          for (int x = fromX + 1; x < toX; x++) {
            if (road.road.get(new Point(x, moto.y))) continue moto;
            if (road.road.get(new Point(x, moto.y - 1))) continue moto;
          }
          if (road.road.get(new Point(toX, moto.y - 1))) continue;
          newState.motos.add(new Point(toX, moto.y - 1));
        }
        break;
      case DOWN:
        moto: for (Point moto : motos) {
          if (moto.y == 3) throw new IllegalStateException();
          int fromX = moto.x;
          int toX = Math.min(fromX + newState.speed, road.length - 1);
          for (int x = fromX + 1; x < toX; x++) {
            if (road.road.get(new Point(x, moto.y))) continue moto;
            if (road.road.get(new Point(x, moto.y + 1))) continue moto;
          }
          if (road.road.get(new Point(toX, moto.y + 1))) continue;
          newState.motos.add(new Point(toX, moto.y + 1));
        }
        break;
    }
    return newState;
  }

  @Override
  public String toString() {
    return String.format("speed=%s, alive motos=%s, depth=%s", speed, motos.size(), commands.size());
  }
}

class Road {
  final int length;
  final Map<Point, Boolean> road;
  Road(int length) {
    this.length = length;
    road = new HashMap<>(length * 4);
  }

  void addLane(int laneNumber, String lane) {
    char[] chars = lane.toCharArray();
    for (int x = 0; x < chars.length; x++) {
      char ch = chars[x];
      if (ch == '0') {
        road.put(new Point(x, laneNumber), true);
      } else {
        road.put(new Point(x, laneNumber), false);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < length; x++) {
        boolean hole = road.get(new Point(x, y));
        builder.append(hole ? "0" : ".");
      }
      builder.append("\n");
    }
    return builder.toString();
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
}

enum Command {
  SPEED, SLOW, JUMP, WAIT, UP, DOWN
}