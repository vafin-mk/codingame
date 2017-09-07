package multiplayer.fantasticbits.model.commands;

import common.model.Command;
import common.model.Vector;

public class MoveCommand extends Command {

  public final Vector target;
  public final int thrust;

  public MoveCommand(Vector target, int thrust) {
    this.target = target;
    this.thrust = thrust;
  }

  @Override
  public void execute() {
    System.out.println(String.format("MOVE %s %s %s", (int) target.x, (int) target.y, thrust));
  }
}
