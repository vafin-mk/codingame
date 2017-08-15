package puzzles.veryhard.marslander3;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final Surface surface;
  SolutionCandidate previousBest;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    this.surface = new Surface(scanner);
  }

  void solve() {
    while (true) {
      Lander lander = new Lander(scanner);
      if (surface.maxY != 0 && lander.position.y > surface.maxY + Const.TARGET_OFFSET_HEIGHT) {
        System.err.println("TARGET HEIGHT REACHED");
        surface.maxY = 0;
      }
      GeneticSearch geneticSearch = new GeneticSearch(lander, surface, previousBest);
      SolutionCandidate candidate = geneticSearch.findCandidate();
      if (candidate.landerCondition == LanderCondition.LAND_SUCCESS) {
        for (MoveChange change : candidate.moves) {
          System.out.println(move(change, lander));
          lander = lander.nextTurn(change);
        }
        System.out.println("0 4");
        return;
      }
      System.out.println(move(candidate.moves.get(0), lander));
    }
  }

  Move move(MoveChange change, Lander lander) {
    return new Move(lander.rotation + change.angle, lander.power + change.thrust);
  }
}

class Const {
  static final Random RND = new Random(1141);
  static final double GRAVITY = 3.711;
  static final int WIDTH = 7000;
  static final int HEIGHT = 3000;
  static final int MAX_V_SPEED = 40;
  static final int MAX_H_SPEED = 20;
  static final int TARGET_OFFSET_HEIGHT = 300;

  static final int SOLUTION_DEPTH = 50;
  static final int POPULATION_SIZE = 50;
  static final int MAX_GENERATIONS = 1000;
  static final long MAX_TIME = 95_000_000;//ns
  static final float ELITISM_RATIO = 0.1F;
  static final float CROSSOVER_RATIO = 0.8F;
  static final float CROSSOVER_PROB = 0.8F;
  static final float MUTATE_PROB = 0.1F;

  static final int LAND_SUCCESS_SCORE = 1_000_000;
  static final int CRASH_WHILE_LANDING_SCORE = -60_000;
  static final int CRASHED_SCORE = -100_000;
  static final int ON_FLY_SCORE = -20_000;
  static final int ELEVATE_COEFFICIENT = 100;
  static final int ON_FLY_DIST_COEFFICIENT = 10;
}

class GeneticSearch {
  final Lander startLander;
  final Surface surface;
  final long startTime;
  final SolutionCandidate previousBest;

  GeneticSearch(Lander lander, Surface surface, SolutionCandidate previousBest) {
    this.startLander = lander;
    this.surface = surface;
    this.startTime = System.nanoTime();
    this.previousBest = previousBest;
  }

  SolutionCandidate findCandidate() {
    List<SolutionCandidate> generation = initialGeneration(previousBest);
    evalCandidates(generation);
    Collections.sort(generation);
    int generationsCount = 0;
    SolutionCandidate bestCandidate = generation.get(0);
    while (!timeOut() && generationsCount < Const.MAX_GENERATIONS) {
      if (bestCandidate.landerCondition == LanderCondition.LAND_SUCCESS) {
        System.err.println("FOUND PATH:" + bestCandidate);
        return bestCandidate;
      }
      generation = nextGeneration(generation);
      evalCandidates(generation);
      Collections.sort(generation);
      bestCandidate = generation.get(0);
      generationsCount++;
    }

    System.err.println("---------------  " + generationsCount + "   --------------");
    System.err.println(bestCandidate);
    return bestCandidate;
  }

  List<SolutionCandidate> nextGeneration(List<SolutionCandidate> previousGeneration) {
    List<SolutionCandidate> newGeneration = new ArrayList<>(Const.POPULATION_SIZE);
    for (int i = 0; i < (int) (Const.POPULATION_SIZE * Const.ELITISM_RATIO); i++) {
      List<MoveChange> changes = new ArrayList<>(previousGeneration.get(i).moves);
      newGeneration.add(new SolutionCandidate(changes));
    }

    for (int i = 0; i < (int) (Const.POPULATION_SIZE * Const.CROSSOVER_RATIO) / 2; i++) {
      SolutionCandidate first = pickRandomCandidate(previousGeneration);
      SolutionCandidate second = pickRandomCandidate(previousGeneration);

      List<MoveChange> firstMoves = new ArrayList<>(Const.SOLUTION_DEPTH);
      List<MoveChange> secondMoves = new ArrayList<>(Const.SOLUTION_DEPTH);

      for (int index = 0; index < Const.SOLUTION_DEPTH; index++) {
        MoveChange firstChange = first.moves.get(index);
        if (Const.RND.nextFloat() < Const.MUTATE_PROB) {
          firstChange = MoveChange.random(Const.RND);
        }
        MoveChange secondChange = second.moves.get(index);
        if (Const.RND.nextFloat() < Const.MUTATE_PROB) {
          secondChange = MoveChange.random(Const.RND);
        }

        if (Const.RND.nextFloat() < Const.CROSSOVER_PROB) {
          firstMoves.add(secondChange);
          secondMoves.add(firstChange);
        } else {
          firstMoves.add(firstChange);
          secondMoves.add(secondChange);
        }
      }

      newGeneration.add(new SolutionCandidate(firstMoves));
      newGeneration.add(new SolutionCandidate(secondMoves));
    }

    for (int i = newGeneration.size(); i < Const.POPULATION_SIZE; i++) {
      newGeneration.add(randomSolution());
    }

    return newGeneration;
  }

  SolutionCandidate pickRandomCandidate(List<SolutionCandidate> candidates) {
    return candidates.get(Const.RND.nextInt(candidates.size()));
  }

  void evalCandidates(List<SolutionCandidate> candidates) {
    for (SolutionCandidate candidate : candidates) {
      Lander lander = startLander;
      LanderCondition condition = LanderCondition.ON_FLY;
      for (MoveChange change : candidate.moves) {
        condition = checkCondition(lander);
        if (condition != LanderCondition.ON_FLY) {
          break;
        }
        lander = lander.nextTurn(change);
      }
      candidate.score = evalLander(lander, condition);
      candidate.landerCondition = condition;
      candidate.finishLander = lander;
    }
  }

  LanderCondition checkCondition(Lander lander) {
    if (outsideMap(lander)) return LanderCondition.CRASHED;
    if (lander.fuel <= 0) return LanderCondition.CRASHED;
    Line landerLine = new Line(lander.previousPosition, lander.position);
    if (landerLine.intersect(surface.landLine)) {
      //landing
      if (crashSpeed(lander) || lander.rotation != 0) {
        return LanderCondition.CRASH_WHILE_LANDING;
      } else {
        return LanderCondition.LAND_SUCCESS;
      }
    }

    for (Line line : surface.lines) {
      if (landerLine.intersect(line)) {
        return LanderCondition.CRASHED;
      }
    }

    return LanderCondition.ON_FLY;
  }

  boolean outsideMap(Lander lander) {
    return lander.position.x < 0 || lander.position.x >= Const.WIDTH
      || lander.position.y < 0 || lander.position.y > Const.HEIGHT;
  }

  boolean crashSpeed(Lander lander) {
    return Math.abs(Math.round(lander.velocity.x)) >= Const.MAX_H_SPEED
      || Math.abs(Math.round(lander.velocity.y)) >= Const.MAX_V_SPEED;
  }

  double evalLander(Lander lander, LanderCondition condition) {
    if (condition == LanderCondition.LAND_SUCCESS) {
      return Const.LAND_SUCCESS_SCORE;
    }

    double hSpeedScore = Math.round(Math.abs(lander.velocity.x));
    if (hSpeedScore < Const.MAX_H_SPEED) {
      hSpeedScore = 0;
    }

    double vSpeedScore = Math.round(Math.abs(lander.velocity.y));
    if (vSpeedScore < Const.MAX_V_SPEED) {
      vSpeedScore = 0;
    }

    double rotationScore = Math.abs(lander.rotation);

    if (condition == LanderCondition.CRASH_WHILE_LANDING) {
      return Const.CRASH_WHILE_LANDING_SCORE - hSpeedScore - vSpeedScore - rotationScore;
    }

    double distScore = Math.abs(lander.position.x - surface.landLine.from.x) + Math.abs(lander.position.x - surface.landLine.to.x);

    double heightScore = Const.ELEVATE_COEFFICIENT * (lander.position.y - surface.maxY);
    if (surface.maxY == 0) {
      heightScore = -Math.abs(lander.position.y - surface.landY);
    }

    if (condition == LanderCondition.CRASHED) {
      return Const.CRASHED_SCORE - hSpeedScore - vSpeedScore - distScore + heightScore;
    }
    return Const.ON_FLY_SCORE - hSpeedScore - vSpeedScore - Const.ON_FLY_DIST_COEFFICIENT * distScore + heightScore;
  }

  boolean timeOut() {
    return System.nanoTime() - startTime > Const.MAX_TIME;
  }

  List<SolutionCandidate> initialGeneration(SolutionCandidate previousBest) {
    List<SolutionCandidate> candidates = new ArrayList<>();
    if (previousBest != null) {
      List<MoveChange> moves = new ArrayList<>(previousBest.moves);
      moves.remove(0);
      candidates.add(new SolutionCandidate(moves));
    }
    for (int i = candidates.size(); i < Const.POPULATION_SIZE; i++) {
      candidates.add(randomSolution());
    }
    return candidates;
  }

  SolutionCandidate randomSolution() {
    List<MoveChange> moveChanges = new ArrayList<>();
    for (int i = 0; i < Const.SOLUTION_DEPTH; i++) {
      moveChanges.add(MoveChange.random(Const.RND));
    }
    return new SolutionCandidate(moveChanges);
  }
}

class SolutionCandidate implements Comparable<SolutionCandidate> {
  final List<MoveChange> moves;
  double score;
  LanderCondition landerCondition = LanderCondition.ON_FLY;
  Lander finishLander;

  SolutionCandidate(List<MoveChange> moves) {
    this.moves = moves;
  }

  @Override
  public int compareTo(SolutionCandidate o) {
    return Double.compare(o.score, score);
  }

  @Override
  public String toString() {
    return String.format("Score = %s; LanderCondition = %s; Lander = %s", score, landerCondition, finishLander);
  }
}

class MoveChange {
  final int angle;
  final int thrust;

  MoveChange(int angle, int thrust) {
    angle = Math.max(-15, Math.min(15, angle));
    thrust = Math.max(-1, Math.min(1, thrust));

    this.angle = angle;
    this.thrust = thrust;
  }

  @Override
  public String toString() {
    return angle + " " + thrust;
  }

  static MoveChange random(Random random) {
    return new MoveChange(random.nextInt(31) - 15, random.nextInt(3) - 1);
  }
}

class Move {
  final int targetRotation;
  final int targetPower;

  Move(int targetRotation, int targetPower) {
    targetRotation = Math.max(-90, Math.min(90, targetRotation));
    targetPower = Math.max(0, Math.min(4, targetPower));

    this.targetRotation = targetRotation;
    this.targetPower = targetPower;
  }

  @Override
  public String toString() {
    return targetRotation + " " + targetPower;
  }
}

enum LanderCondition {
  ON_FLY, LAND_SUCCESS, CRASH_WHILE_LANDING, CRASHED;
}

class Lander {
  Vector previousPosition;
  Vector position;
  Vector velocity;
  int fuel;
  int rotation;
  int power;

  Lander() {
  }

  Lander(Scanner scanner) {
    int X = scanner.nextInt();
    int Y = scanner.nextInt();
    position = new Vector(X, Y);
    previousPosition = position;
    int hSpeed = scanner.nextInt();
    int vSpeed = scanner.nextInt();
    velocity = new Vector(hSpeed, vSpeed);
    fuel = scanner.nextInt();
    rotation = scanner.nextInt();
    power = scanner.nextInt();
  }

  Lander nextTurn(MoveChange change) {
    Lander next = new Lander();
    next.previousPosition = position;

    next.power = power + change.thrust;
    next.power = Math.max(0, Math.min(4, next.power));

    next.rotation = rotation + change.angle;
    next.rotation = Math.max(-90, Math.min(90, next.rotation));

    next.fuel = fuel - next.power;

    double accelerationX = -next.power * Math.sin(Math.toRadians(next.rotation));
    double accelerationY = next.power * Math.cos(Math.toRadians(next.rotation)) - Const.GRAVITY;

    next.velocity = new Vector(velocity.x + accelerationX, velocity.y + accelerationY);

    next.position = new Vector(
      position.x + next.velocity.x - 0.5 * accelerationX,
      position.y + next.velocity.y - 0.5 * accelerationY
    );
    return next;
  }

  @Override
  public String toString() {
    return String.format("X=%sm, Y=%sm, HSpeed=%sm/s VSpeed=%sm/s\n" +
      "Fuel=%sl, Angle=%s°, Power=%s", position.x, position.y, velocity.x, velocity.y, fuel, rotation, power);
  }
}

class Surface {
  //we need to make higher than maxY in case we are in wrong side of cave
  int maxY = 0;
  int landY;
  Line landLine;
  final List<Line> lines;

  Surface(Scanner scanner) {
    int surfaceN = scanner.nextInt();
    List<Vector> vectors = new ArrayList<>(surfaceN);
    for (int i = 0; i < surfaceN; i++) {
      int landX = scanner.nextInt();
      int landY = scanner.nextInt();
      if (landY > maxY) {
        maxY = landY;
      }
      vectors.add(new Vector(landX, landY));
    }

    lines = new ArrayList<>(surfaceN - 1);
    for (int i = 0; i < vectors.size() - 1; i++) {
      Vector left = vectors.get(i);
      Vector right = vectors.get(i + 1);
      if (left.y == right.y) {
        landLine = new Line(left, right);
        landY = (int) left.y;
      } else {
        lines.add(new Line(left, right));
      }
    }
  }
}

class Line {
  final Vector from, to;

  Line(Vector from, Vector to) {
    this.from = from;
    this.to = to;
  }

  //https://stackoverflow.com/questions/16314069/calculation-of-intersections-between-line-segments
  boolean intersect(Line other) {
    double denom = (other.to.y - other.from.y) * (to.x - from.x) - (other.to.x - other.from.x) * (to.y - from.y);
    if (denom == 0.0) {
      return false;
    }
    double ua = ((other.to.x - other.from.x) * (from.y - other.from.y) - (other.to.y - other.from.y) * (from.x - other.from.x)) / denom;
    double ub = ((to.x - from.x) * (from.y - other.from.y) - (to.y - from.y) * (from.x - other.from.x)) / denom;
    if (ua >= 0.0 && ua <= 1.0 && ub >= 0.0 && ub <= 1.0) {
      return true;
    }
    return false;
  }
}

class Vector {
  final double x, y;

  Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Vector vector = (Vector) o;

    return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return x + " " + y;
  }
}