package multiplayer.hypersonic.model;

import common.model.Command;
import common.model.Point2I;

public class MoveCommand extends Command {

  public final Point2I point;
  public MoveCommand(Point2I point) {
    this.point = point;
  }

  @Override
  public void execute() {
    System.out.println("MOVE " + point + (message != null ? " " + message : ""));
  }

  @Override
  public String toString() {
    return "MOVE[" + point + "]";
  }
}
