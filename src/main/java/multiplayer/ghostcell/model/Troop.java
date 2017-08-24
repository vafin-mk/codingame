package multiplayer.ghostcell.model;

public class Troop {
  public final int id;
  public final int owner;
  public final int source;
  public final int target;
  public final int count;
  public final int turnsToArrive;

  public Troop(int id, int owner, int source, int target, int count, int turnsToArrive) {
    this.id = id;
    this.owner = owner;
    this.source = source;
    this.target = target;
    this.count = count;
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
    builder.append("from ").append(source).append(" to ").append(target);
    builder.append(";cyborgs=").append(count).append(";turnsToArrive=").append(turnsToArrive);
    return builder.toString();
  }
}
