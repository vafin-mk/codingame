package multiplayer.fantasticbits.model.entities;

import common.model.Command;

public class Flipendo extends Command {

  public final int target;

  public Flipendo(int target) {
    this.target = target;
  }

  @Override
  public void execute() {
    System.out.println("FLIPENDO " + target);
  }
}
