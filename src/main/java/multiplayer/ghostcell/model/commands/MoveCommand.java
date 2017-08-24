package multiplayer.ghostcell.model.commands;

public class MoveCommand implements InnerCommand {
  public final int source;
  public final int target;
  public final int count;

  public MoveCommand(int source, int target, int count) {
    this.source = source;
    this.target = target;
    this.count = count;
  }

  @Override
  public String toString() {
    return String.format("MOVE %s %s %s", source, target, count);
  }
}
