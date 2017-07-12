package puzzles.medium.marslander2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  private static final double GRAVITY = 3.711;
  private static final int NEAR_LAND_LENGTH = 100;
  public static final int MAX_H_SPEED = 20;
  public static final int MAX_V_SPEED = 40;
  final Scanner scanner;
  final Surface surface;
  final MarsLander lander;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    surface = new Surface(scanner);
    lander = new MarsLander();
  }

  void solve() {
    while (true) {
      lander.update(scanner);
      System.out.println(bestMove());
    }
  }

  private boolean flatZone() {
    return lander.position.x > surface.leftX && lander.position.x < surface.rightX;
  }

  private Move bestMove() {
    if (flatZone()) {
      return flatZoneMove();
    }
    return approachFlatMove();
  }

  private Move flatZoneMove() {
    if (lander.position.y - surface.Y < NEAR_LAND_LENGTH) {
      return new Move(0, 3);
    }
    if (!lander.crashFreeSpeed()) {
      return new Move(slowdownRotation(), 4);
    }
    return new Move(0, 2);
  }

  private Move approachFlatMove() {
    boolean wrongDirection =
      (lander.position.x < surface.leftX && lander.velocity.x < 0)
      || (lander.position.x > surface.rightX && lander.velocity.x > 0);
    boolean tooFast = Math.abs(lander.velocity.x) > MAX_H_SPEED * 4;
    boolean tooSlow = Math.abs(lander.velocity.x) < MAX_H_SPEED * 2;
    if (tooFast || wrongDirection) return new Move(slowdownRotation(), 4);
    if (tooSlow) return new Move(speedUpRotation(), 4);
    return new Move(0, lander.velocity.y > 0 ? 3 : 4);
  }

  private int slowdownRotation() {
    double speed = lander.velocity.length();
    return (int) Math.toDegrees(Math.asin(lander.velocity.x / speed));
  }

  private int speedUpRotation() {
    int rotate = (int) Math.toDegrees(Math.acos(GRAVITY / 4.0));
    if (lander.position.x < surface.leftX) {
      rotate *= -1;
    }
    return rotate;
  }
}

class Move {
  final int rotate, power;
  Move(int rotate, int power) {
    if (rotate > 90) rotate = 90;
    else if (rotate < -90) rotate = -90;
    this.rotate = rotate;
    if (power < 0) power = 0;
    else if (power > 4) power = 4;
    this.power = power;
  }

  @Override
  public String toString() {
    return rotate + " " + power;
  }
}

class MarsLander {
  Vector position, velocity;
  int fuel, rotate, power;

  void update(Scanner scanner) {
    position = new Vector(scanner.nextInt(), scanner.nextInt());
    velocity = new Vector(scanner.nextInt(), scanner.nextInt());
    fuel = scanner.nextInt();
    rotate = scanner.nextInt();
    power = scanner.nextInt();
  }

  boolean crashFreeSpeed() {
    return Math.abs(velocity.x) < Solver.MAX_H_SPEED - 5 && Math.abs(velocity.y) < Solver.MAX_V_SPEED - 5;
  }
}

class Surface {

  List<Vector> points;
  int leftX, rightX, Y;

  Surface(Scanner scanner) {
    int surfaceN = scanner.nextInt();
    points = new ArrayList<>(surfaceN);
    for (int i = 0; i < surfaceN; i++) {
      int landX = scanner.nextInt();
      int landY = scanner.nextInt();
      points.add(new Vector(landX, landY));
    }

    for (int i = 0; i < points.size() - 1; i++) {
      Vector left = points.get(i);
      Vector right = points.get(i + 1);
      if (left.y == right.y) {
        Y = (int) left.y;
        leftX = (int) left.x;
        rightX = (int) right.x;
      }
    }
  }
}

class Vector {
  final double x, y;
  Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  double length() {
    return Math.sqrt(x * x + y * y);
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
    return x + "|" + y;
  }
}