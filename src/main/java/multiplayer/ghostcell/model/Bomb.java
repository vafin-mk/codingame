package multiplayer.ghostcell.model;

public class Bomb {
  public final int id;
  public final int owner;
  public final int source;
  public final int target;
  public final int turnsToArrive;

  public Bomb(int id, int owner, int source, int target, int turnsToArrive) {
    this.id = id;
    this.owner = owner;
    this.source = source;
    this.target = target;
    this.turnsToArrive = turnsToArrive;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Id=").append(id).append(";");
    builder.append("Owner=");
    switch (owner) {
      case Const.OWNER_ALLY:
        builder.append("ally;");
        break;
      case Const.OWNER_NEUTRAL:
        builder.append("neutral;");
        break;
      case Const.OWNER_RIVAL:
        builder.append("rival;");
        break;
    }
    builder.append("source=").append(source).append(";target=").append(target == -1 ? "UNKNOWN" : target)
      .append(";turnsToArrive=").append(turnsToArrive == -1 ? "UNKNOWN" : turnsToArrive);
    return builder.toString();
  }
}
