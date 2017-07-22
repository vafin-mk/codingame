package puzzles.hard.genomesequencing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Random random = new Random(1141);
  static final int POPULATION_SIZE = 25000;
  static final int LOOK_THRESHOLD = 1500;
  static final double CUT_THRESHOLD = 0.4;
  static final double MUTATE_THRESHOLD = 0.5;
  final Scanner scanner;
  final List<String> subSequences;
  final int minLength;
  final int maxLength;
  Solver(Scanner scanner) {
    this.scanner = scanner;
    int N = scanner.nextInt();
    subSequences = new ArrayList<>(N);
    int min = 0;
    int max = 0;
    for (int i = 0; i < N; i++) {
      String subseq = scanner.next();
      subSequences.add(subseq);
      max += subseq.length();
      if (subseq.length() > min) {
        min = subseq.length();
      }
    }
    minLength = min;
    maxLength = max;
  }

  void solve() {
    long start = System.nanoTime();
    String[] population = init(POPULATION_SIZE);
    while (true) {
      long passed = System.nanoTime() - start;
      if (passed / 1000000 > LOOK_THRESHOLD) break;
      population = evolve(population);
    }

    System.out.println(fittest(population).length());
  }

  String[] evolve(String[] from) {
    String[] population = new String[from.length];
    String start = fittest(from);
    population[0] = start;
    for (int i = 1; i < from.length; i++) {
      population[i] = mutate(start);
    }
    return population;
  }

  int fitness(String candidate) {
    int fitness = 0;
    for (String subSequence : subSequences) {
      if (candidate.contains(subSequence)) {
        fitness += 10;
      }
    }
    fitness += maxLength - candidate.length();
    return fitness;
  }

  String fittest(String[] population) {
    String fittest = null;
    int bestFittnes = Integer.MIN_VALUE;
    for (String candidate : population) {
      int fittness = fitness(candidate);
      if (fittness > bestFittnes) {
        bestFittnes = fittness;
        fittest = candidate;
      }
    }
    return fittest;
  }

  String[] init(int size) {
    String[] population = new String[size];
    String start = subSequences.stream().reduce(String::concat).orElse("");
    population[0] = start;
    for (int i = 1; i < size; i++) {
      population[i] = mutate(start);
    }
    return population;
  }

  String mutate(String other) {
    double rnd = random.nextDouble();
    if (other.length() > minLength && rnd > CUT_THRESHOLD) {
      int bound = other.length() - minLength - 1;
      int len = bound <= 0 ? 1 : random.nextInt(bound) + 1;
      int sub = random.nextInt(other.length() - len);
      other = other.substring(0, sub).concat(other.substring(sub + len, other.length()));
    }
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < other.length(); i++) {
      if (rnd < MUTATE_THRESHOLD) {
        res.append(other.charAt(i));
      } else {
        int rn = random.nextInt(4);
        switch (rn) {
          case 0:
            res.append("A");
            break;
          case 1:
            res.append("C");
            break;
          case 2:
            res.append("T");
            break;
          case 3:
            res.append("G");
            break;
        }
      }
    }

    return res.toString();
  }
}