package puzzles.hard.bender3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * https://en.wikipedia.org/wiki/Analysis_of_algorithms
 * Assuming the execution time follows power rule, t ≈ k n^a,
 * the coefficient a can be found [8] by taking empirical measurements of run time
 * {{t_{1},t_{2}}}  at some problem-size points {{n_{1},n_{2}}}
 * and calculating {t_{2}/t_{1}=(n_{2}/n_{1})^{a}}
 * so that {a=log(t_{2}/t_{1})\log(n_{2}/n_{1})}.
 * In other words, this measures the slope of the empirical line on the log–log plot of execution time
 * vs. problem size, at some size point. If the order of growth indeed follows
 * the power rule (and so the line on log–log plot is indeed a straight line),
 * the empirical value of a will stay constant at different ranges, and if not,
 * it will change (and the line is a curved line) - but still could serve for comparison of any
 * two given algorithms as to their empirical local orders of growth behaviour
 */
class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final double growthPower;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    int measurements = scanner.nextInt();
    List<Integer> itemsList = new ArrayList<>();
    List<Integer> timeList = new ArrayList<>();
    for (int i = 0; i < measurements; i++) {
      int items = scanner.nextInt();
      int time = scanner.nextInt();
      itemsList.add(items);
      timeList.add(time);
    }
    itemsList.sort(Integer::compare);
    timeList.sort(Integer::compare);
    //removing outliers
    int cut = measurements / 10;
    itemsList = itemsList.subList(cut, itemsList.size() - cut);
    timeList = timeList.subList(cut, timeList.size() - cut);

    double minItems = itemsList.get(0);
    double minTime = timeList.get(0);
    double maxItems = itemsList.get(itemsList.size() - 1);
    double maxTime = timeList.get(timeList.size() - 1);
    double itemsRatio = maxItems / minItems;
    double timeRatio = maxTime / minTime;
    growthPower = Math.log(timeRatio)/Math.log(itemsRatio);
  }

  void solve() {
    System.err.println(growthPower);
    System.out.println(Complexity.byGrowthPower(growthPower));
  }
}

enum Complexity{
  CONSTANT("O(1)")
  ,LOGARITHMIC("O(log n)")
  ,LINEAR("O(n)")
  ,LOGLINEAR("O(n log n)")
  ,QUADRATIC("O(n^2)")
  ,LOGQUADRATIC("O(n^2 log n)")
  ,QUBIC("O(n^3)")
  ,EXPOTENTIAL("O(2^n)")
  ;
  String representation;
  Complexity(String representation) {
    this.representation = representation;
  }

  static Complexity byGrowthPower(double power) {
    if (power < 0.1) return CONSTANT;
    if (power >= 0.1 && power < 0.5) return LOGARITHMIC;
    if (power >= 0.5 && power < 1.1) return LINEAR;
    if (power >= 1.1 && power < 1.5) return LOGLINEAR;
    if (power >= 1.5 && power < 2.1) return QUADRATIC;
    if (power >= 2.1 && power < 2.5) return LOGQUADRATIC;
    if (power >= 2.5 && power < 3.1) return QUBIC;
    return EXPOTENTIAL;
  }

  @Override
  public String toString() {
    return representation;
  }
}