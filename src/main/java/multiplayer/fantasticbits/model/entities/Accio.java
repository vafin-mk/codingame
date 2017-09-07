package multiplayer.fantasticbits.model.entities;

import common.model.Command;

public class Accio extends Command {

  public final int target;

  public Accio(int target) {
    this.target = target;
  }

  @Override
  public void execute() {
    System.out.println("ACCIO " + target);
  }
}
