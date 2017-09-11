package common.model;

public class Point2I {
  public final int x, y;

  public Point2I(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point2I up() {
    return new Point2I(x, y - 1);
  }

  public Point2I down() {
    return new Point2I(x, y + 1);
  }

  public Point2I right() {
    return new Point2I(x + 1, y);
  }

  public Point2I left() {
    return new Point2I(x - 1, y);
  }

  public int manhattanDist(Point2I other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }

  public double distSquared(Point2I other) {
    double xOffset = other.x - x;
    double yOffset = other.y - y;
    return xOffset * xOffset + yOffset * yOffset;
  }

  public double dist(Point2I other) {
    return Math.sqrt(distSquared(other));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point2I)) return false;

    Point2I point2I = (Point2I) o;

    return x == point2I.x && y == point2I.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }

  @Override
  public String toString() {
    return x + " " + y;
  }
}
