package puzzles.medium.networkcabling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Solution {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    long N = in.nextInt();
    long minX = Integer.MAX_VALUE;
    long maxX = Integer.MIN_VALUE;
    List<Long> heights = new ArrayList<>();

    for (long i = 0; i < N; i++) {
      long x = in.nextInt();
      long y = in.nextInt();
      System.err.print(String.format("(%s %s) ", x, y));

      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      heights.add(y);
    }

    Collections.sort(heights);
    long medianY;
    if (heights.size() % 2 == 0)
      medianY = (heights.get(heights.size()/2) + heights.get(heights.size()/2 - 1))/2;
    else
      medianY = heights.get(heights.size()/2);

    long res = maxX - minX;
    for (Long height : heights) {
      long delta = height > medianY ? height - medianY : medianY - height;
      res += delta;
    }

    System.out.println("" + res);
  }
}
