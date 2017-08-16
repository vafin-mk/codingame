package multiplayer.hypersonic.model;

import common.model.Command;
import common.model.Point2I;

public class BombCommand extends Command {

  public final Point2I point;
  public BombCommand(Point2I point) {
    this.point = point;
  }

  @Override
  public void execute() {
    System.out.println("BOMB " + point + (message != null ? " " + message : ""));
  }

  @Override
  public String toString() {
    return "BOMB[" + point + "]";
  }
}