package multiplayer.hypersonic.model;

import common.model.Point2I;

public class Hero {
  public final Point2I position;
  public final int owner;
  public final int bombsLeft;
  public final int bombRange;

  public Hero(Point2I position, int owner, int bombsLeft, int bombRange) {
    this.position = position;
    this.owner = owner;
    this.bombsLeft = bombsLeft;
    this.bombRange = bombRange;
  }
}
