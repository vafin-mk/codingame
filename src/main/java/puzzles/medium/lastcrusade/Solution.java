package puzzles.medium.lastcrusade;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    if (in.hasNextLine()) {
      in.nextLine();
    }
    Grid grid = new Grid(width, height);
    for (int i = 0; i < height; i++) {
      grid.applyRoom(i, in.nextLine());
    }
    int exitX = in.nextInt();

    // game loop
    while (true) {
      int currentX = in.nextInt();
      int currentY = in.nextInt();
      Position entered = Position.valueOf(in.next());
      Point next = grid.nextPoint(new Point(currentX, currentY), entered);
      System.out.println(next.x + " " + next.y);
    }
  }
}

class Grid {

  final int width;
  final int height;

  Map<Point, Integer> rooms;

  Grid(int width, int height) {
    this.width = width;
    this.height = height;
    rooms = new HashMap<>(width * height);
  }

  void applyRoom(int height, String roomString) {
    String[] rooms = roomString.split(" ");
    for (int x = 0; x < rooms.length; x++) {
      this.rooms.put(new Point(x, height), Integer.valueOf(rooms[x]));
    }
  }

  Point nextPoint(Point from, Position enterPosition) {
    int roomType = rooms.get(from);
    switch (roomType) {
      case 1:
        return from.down();
      case 2:
        if (enterPosition == Position.LEFT) {
          return from.right();
        } else if (enterPosition == Position.RIGHT) {
          return from.left();
        }
      case 3:
        if (enterPosition == Position.TOP) {
          return from.down();
        }
      case 4:
        if (enterPosition == Position.TOP) {
          return from.left();
        } else if (enterPosition == Position.RIGHT) {
          return from.down();
        }
      case 5:
        if (enterPosition == Position.TOP) {
          return from.right();
        } else if (enterPosition == Position.LEFT) {
          return from.down();
        }
      case 6:
        if (enterPosition == Position.LEFT) {
          return from.right();
        } else if (enterPosition == Position.RIGHT) {
          return from.left();
        }
      case 7:
        if (enterPosition == Position.TOP || enterPosition == Position.RIGHT) {
          return from.down();
        }
      case 8:
        if (enterPosition == Position.LEFT || enterPosition == Position.RIGHT) {
          return from.down();
        }
      case 9:
        if (enterPosition == Position.TOP || enterPosition == Position.LEFT) {
          return from.down();
        }
      case 10:
        if (enterPosition == Position.TOP) {
          return from.left();
        }
      case 11:
        if (enterPosition == Position.TOP) {
          return from.right();
        }
      case 12:
        if (enterPosition == Position.RIGHT) {
          return from.down();
        }
      case 13:
        if (enterPosition == Position.LEFT) {
          return from.down();
        }
    }
    System.err.println("from = " + from + "; roomType = " + roomType + "; enterPosition = " + enterPosition);
    throw new IllegalStateException();
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
    return x+"|"+y;
  }
}
