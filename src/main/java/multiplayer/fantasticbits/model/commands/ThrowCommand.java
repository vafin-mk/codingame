package multiplayer.fantasticbits.model.commands;

import common.model.Command;
import common.model.Vector;

public class ThrowCommand extends Command {
  public final Vector target;
  public final int thrust;

  public ThrowCommand(Vector target, int thrust) {
    this.target = target;
    this.thrust = thrust;
  }

  @Override
  public void execute() {
    System.out.println(String.format("THROW %s %s %s", (int) target.x, (int) target.y, thrust));
  }
}
