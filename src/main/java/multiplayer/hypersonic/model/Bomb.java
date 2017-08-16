package multiplayer.hypersonic.model;

import common.model.Point2I;

public class Bomb {
  public final Point2I position;
  public final int owner;
  public int roundsLeft;
  public final int explosionRange;

  public Bomb(Point2I position, int owner, int roundsLeft, int explosionRange) {
    this.position = position;
    this.owner = owner;
    this.roundsLeft = roundsLeft;
    this.explosionRange = explosionRange;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Bomb)) return false;

    Bomb bomb = (Bomb) o;

    return position.equals(bomb.position);
  }

  @Override
  public int hashCode() {
    return position.hashCode();
  }
}
