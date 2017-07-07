package puzzles.easy.mimetype;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt(); // Number of elements which make up the association table.
    int Q = in.nextInt(); // Number Q of file names to be analyzed.
    Map<String, String> mimes = new HashMap<>(N);
    for (int i = 0; i < N; i++) {
      String EXT = in.next(); // file extension
      String MT = in.next(); // MIME type.
      mimes.put(EXT.toLowerCase(), MT);
      in.nextLine();
    }
    for (int i = 0; i < Q; i++) {
      String FNAME = in.nextLine(); // One file name per line.
      if (!FNAME.contains(".")) {
        System.out.println("UNKNOWN");
        continue;
      }
      String fileExt = FNAME.substring(FNAME.lastIndexOf(".") + 1).toLowerCase();
      System.out.println(mimes.getOrDefault(fileExt, "UNKNOWN"));
    }
  }
}
