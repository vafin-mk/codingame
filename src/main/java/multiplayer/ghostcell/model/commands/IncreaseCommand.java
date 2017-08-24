package multiplayer.ghostcell.model.commands;

public class IncreaseCommand implements InnerCommand {
  public final int id;
  public IncreaseCommand(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "INC " + id;
  }
}
