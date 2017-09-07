package multiplayer.fantasticbits.model.entities;

import common.model.Command;

public class Petrificus extends Command {

  public final int target;

  public Petrificus(int target) {
    this.target = target;
  }

  @Override
  public void execute() {
    System.out.println("PETRIFICUS " + target);
  }
}
