package multiplayer.ghostcell.model.commands;

public class BombCommand implements InnerCommand {
  public final int source;
  public final int target;

  public BombCommand(int source, int target) {
    this.source = source;
    this.target = target;
  }

  @Override
  public String toString() {
    return String.format("BOMB %s %s", source, target);
  }
}
