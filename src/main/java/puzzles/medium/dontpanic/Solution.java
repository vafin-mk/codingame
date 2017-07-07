package puzzles.medium.dontpanic;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int nbFloors = in.nextInt(); // number of floors
    int width = in.nextInt(); // width of the area
    int nbRounds = in.nextInt(); // maximum number of rounds
    int exitFloor = in.nextInt(); // floor on which the exit is found
    int exitPos = in.nextInt(); // position of the exit on its floor
    int nbTotalClones = in.nextInt(); // number of generated clones
    int nbAdditionalElevators = in.nextInt(); // ignore (always zero)
    int nbElevators = in.nextInt(); // number of elevators
//    System.err.println("" + nbElevators);

    Point[] floorPos = new Point[nbElevators];
    for (int i = 0; i < nbElevators; i++) {
      int elevatorFloor = in.nextInt(); // floor on which this elevator is found
      int elevatorPos = in.nextInt(); // position of the elevator on its floor
      floorPos[i] = new Point(elevatorFloor, elevatorPos);
    }
    System.err.println(Arrays.toString(floorPos));

    // game loop
    while (true) {
      int cloneFloor = in.nextInt(); // floor of the leading clone
      int clonePos = in.nextInt(); // position of the leading clone on its floor
      String direction = in.next(); // direction of the leading clone: LEFT or RIGHT
      int [] target = new int[1];
      target[0] = 0;
      if (cloneFloor != exitFloor) {
        Arrays.stream(floorPos)
          .filter(x -> x.x == cloneFloor)
          .reduce((x, y) -> Math.abs(clonePos - x.y) < Math.abs(clonePos - y.x) ? x : y)
          .ifPresent(p -> target[0] = p.y);
      } else {
        target[0] = exitPos;
      }
      System.err.println("floor:" + cloneFloor + "|pos:" + clonePos + "|target=" + target[0]);
      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      if ((target[0] < clonePos && "RIGHT".equals(direction)) || (target[0] > clonePos && "LEFT".equals(direction))) {
        System.out.println("BLOCK"); // action: WAIT or BLOCK
      } else {
        System.out.println("WAIT"); // action: WAIT or BLOCK
      }
    }
  }
}
