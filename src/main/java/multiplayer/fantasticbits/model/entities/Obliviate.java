package multiplayer.fantasticbits.model.entities;

import common.model.Command;

public class Obliviate extends Command {

  public final int target;

  public Obliviate(int target) {
    this.target = target;
  }

  @Override
  public void execute() {
    System.out.println("OBLIVIATE " + target);
  }
}
