package puzzles.medium.conway;

import java.util.Objects;
import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int R = in.nextInt();
    int L = in.nextInt();

    System.out.println(line(String.valueOf(R), 1, L));
  }

  private static String line(String curr, int line, int target) {
    if (line >= target) {
      return curr;
    }

    StringBuilder builder = new StringBuilder();
    String[] chars = curr.split(" ");
    String ch = chars[0];
    int count = 0;
    for (String chr : chars) {
      if (Objects.equals(chr, ch)) {
        count++;
        continue;
      }
      builder.append(count).append(" ").append(ch).append(" ");
      ch = chr;
      count = 1;
    }
    builder.append(count).append(" ").append(ch);

    return line(builder.toString(), line+1, target);
  }
}
