package puzzles.hard.bender2;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final int roomsCount;
  final Map<Integer, Room> rooms = new HashMap<>();
  Solver(Scanner scanner) {
    this.scanner = scanner;
    roomsCount = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    for (int i = 0; i < roomsCount; i++) {
      String text = scanner.nextLine();
      Room room = new Room(text);
      rooms.put(room.id, room);
    }
  }


  void solve() {
    Room start = rooms.get(0);
    start.moneyFromStart = start.money;

    Queue<Room> queue = new PriorityQueue<>();
    queue.add(start);
    while (!queue.isEmpty()) {
      Room current = queue.poll();
      if (current.leftRoom != Room.EXIT) {
        Room target = rooms.get(current.leftRoom);
        int weight = target.money;
        int moneyThroughCurrent = current.moneyFromStart + weight;
        if (moneyThroughCurrent > target.moneyFromStart) {
          queue.remove(target);
          target.moneyFromStart = moneyThroughCurrent;
          target.previous = current;
          queue.add(target);
        }
      }
      if (current.rightRoom != Room.EXIT) {
        Room target = rooms.get(current.rightRoom);
        int weight = target.money;
        int moneyThroughCurrent = current.moneyFromStart + weight;
        if (moneyThroughCurrent > target.moneyFromStart) {
          queue.remove(target);
          target.moneyFromStart = moneyThroughCurrent;
          target.previous = current;
          queue.add(target);
        }
      }
    }

    int max = 0;
    for (Room room : rooms.values()) {
      if (!room.haveExit()) continue;
      if (room.moneyFromStart > max) {
        max = room.moneyFromStart;
      }
    }

    System.out.println(max);
  }
}

class Room implements Comparable<Room>{
  static final int EXIT = -1;
  final int id;
  final int money;
  final int leftRoom;
  final int rightRoom;
  int moneyFromStart = Integer.MIN_VALUE;
  Room previous;

  Room(String text) {
    String[] split = text.split(" ");
    id = Integer.valueOf(split[0]);
    money = Integer.valueOf(split[1]);
    if (split[2].equals("E")) {
      leftRoom = EXIT;
    } else {
      leftRoom = Integer.valueOf(split[2]);
    }
    if (split[3].equals("E")) {
      rightRoom = EXIT;
    } else {
      rightRoom = Integer.valueOf(split[3]);
    }
  }

  boolean haveExit() {
    return leftRoom == EXIT || rightRoom == EXIT;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Room room = (Room) o;

    return id == room.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public int compareTo(Room o) {
    return Integer.compare(o.moneyFromStart, moneyFromStart);
  }

  @Override
  public String toString() {
    return String.format("id=%s;money=%s;left=%s;right=%s", id, money, leftRoom, rightRoom);
  }
}