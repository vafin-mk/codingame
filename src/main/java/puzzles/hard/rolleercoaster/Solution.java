package puzzles.hard.rolleercoaster;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    Scanner scanner = new Scanner(System.in);
    int places = scanner.nextInt();
    int rides = scanner.nextInt();
    int count = scanner.nextInt();
    int[] groups = new int[count];
    int allPeople = 0;
    for (int i = 0; i < count; i++) {
      int group = scanner.nextInt();
      groups[i] = group;
      allPeople += group;
    }
    int[] earnings = new int[count];
    int[] nextGroupId = new int[count];

    for (int i = 0; i < count; i++) {
      int index = i;
      earnings[i] = 0;
      while (true) {
        int participants = groups[index];
        if (earnings[i] + participants > places) {
          break;
        }
        earnings[i] += participants;
        if (earnings[i] == allPeople) {
          break;
        }
        index++;
        if (index == count) {
          index = 0;
        } else if (index == i) {
          break;
        }
      }
      nextGroupId[i] = index;
    }

    long totalEarnings = 0;
    int index = 0;

    for (int i = 0; i < rides; i++) {
      totalEarnings += earnings[index];
      index = nextGroupId[index];
    }

    System.out.println(totalEarnings);
  }
}