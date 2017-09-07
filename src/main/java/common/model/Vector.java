package common.model;

public class Vector {

  public final double x;
  public final double y;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vector add(Vector other) {
    return new Vector(x + other.x, y + other.y);
  }

  public Vector multiply(int multiplier) {
    return new Vector(x * multiplier, y * multiplier);
  }

  public static Vector zero() {
    return new Vector(0, 0);
  }

  public static Vector single() {
    return new Vector(1, 1);
  }

  public double distSquared(Vector other) {
    double xOffset = other.x - x;
    double yOffset = other.y - y;
    return xOffset * xOffset + yOffset * yOffset;
  }

  public double dist(Vector other) {
    return Math.sqrt(distSquared(other));
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
