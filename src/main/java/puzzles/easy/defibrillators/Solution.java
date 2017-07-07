package puzzles.easy.defibrillators;

import java.util.Scanner;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String LON = in.next().replace(",", ".");
    String LAT = in.next().replace(",", ".");
    int N = in.nextInt();
    in.nextLine();

    double myLon = Double.parseDouble(LON);
    double myLat = Double.parseDouble(LAT);
    String closest = null;
    double closestDist = 1000000;
    double lon,lat,dist;
    String[] splitter;

    for (int i = 0; i < N; i++) {
      String DEFIB = in.nextLine().replace(",", ".");
      splitter = DEFIB.split(";");
      lon = Double.parseDouble(splitter[splitter.length - 2]);
      lat = Double.parseDouble(splitter[splitter.length - 1]);
      dist = Math.sqrt(Math.pow((lon - myLon) * Math.cos((lat + myLat) / 2), 2) + Math.pow(lat - myLat, 2)) * 6371;
      if (dist < closestDist) {
        closestDist = dist;
        closest = splitter[1];
      }
    }

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println(closest);
  }
}
