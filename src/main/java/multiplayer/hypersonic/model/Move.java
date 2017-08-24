package multiplayer.hypersonic.model;

import common.model.Command;
import common.model.Point2I;

public class Move extends Command {

  public final Point2I point;
  public final MoveType moveType;
  public Move(Point2I point, MoveType moveType) {
    this.point = point;
    this.moveType = moveType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Move)) return false;

    Move move = (Move) o;

    return point.equals(move.point) && moveType == move.moveType;
  }

  @Override
  public int hashCode() {
    int result = point.hashCode();
    result = 31 * result + moveType.hashCode();
    return result;
  }

  @Override
  public void execute() {
    System.out.println(moveType + " " + point + (message != null ? " " + message : ""));
  }

  @Override
  public String toString() {
    return moveType + "[" + point + "]";
  }

  public enum MoveType {
    MOVE, BOMB
  }
}
