package multiplayer.rift.model;

public class MoveCommand {
  public final int count;
  public final int fromId;
  public final int toId;

  public MoveCommand(int count, int fromId, int toId) {
    this.count = count;
    this.fromId = fromId;
    this.toId = toId;
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", count, fromId, toId);
  }
}
