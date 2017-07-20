package puzzles.hard.supercomputer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    Scanner scanner = new Scanner(System.in);
    int N = scanner.nextInt();
    List<Calculation> calculations = new ArrayList<>(N);
    for (int i = 0; i < N; i++) {
      int J = scanner.nextInt();
      int D = scanner.nextInt();
      calculations.add(new Calculation(J, D));
    }

    calculations.sort(Comparator.comparingInt(c -> c.end));

    int answer = 0;
    int lastDay = 0;
    for (Calculation calculation : calculations) {
      if (calculation.start > lastDay) {
        lastDay = calculation.end;
        answer++;
      }
    }

    System.out.println(answer);
  }
}

class Calculation {
  final int start;
  final int duration;
  final int end;

  Calculation(int start, int duration) {
    this.start = start;
    this.duration = duration;
    this.end = start + duration - 1;
  }
}