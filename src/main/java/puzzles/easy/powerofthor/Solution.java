package puzzles.easy.powerofthor;

import java.util.Scanner;

public class Solution {

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int lightX = in.nextInt(); // the X position of the light of power
    int lightY = in.nextInt(); // the Y position of the light of power
    int initialTX = in.nextInt(); // Thor's starting X position
    int initialTY = in.nextInt(); // Thor's starting Y position

    int xOffset = initialTX - lightX;
    int yOffset = initialTY - lightY;
    StringBuilder angle = new StringBuilder();

    // game loop
    while (true) {
      int remainingTurns = in.nextInt(); // The remaining amount of turns Thor can move. Do not remove this line.

      angle.setLength(0);
      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      if (yOffset > 0) {
        angle.append("N");
        yOffset--;
      } else if (yOffset < 0) {
        angle.append("S");
        yOffset++;
      }

      if (xOffset > 0) {
        angle.append("W");
        xOffset--;
      } else if (xOffset < 0) {
        angle.append("E");
        xOffset++;
      }



      // A single line providing the move to be made: N NE E SE S SW W or NW
      System.out.println(angle.toString());
    }
  }
}
