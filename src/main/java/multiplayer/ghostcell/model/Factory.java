package multiplayer.ghostcell.model;

public class Factory {
  public final int id;
  public final int owner;
  public final int count;
  public final int production;
  public final int recoverInTurns;

  public Factory(int id, int owner, int count, int production, int recoverInTurns) {
    this.id = id;
    this.owner = owner;
    this.count = count;
    this.production = production;
    this.recoverInTurns = recoverInTurns;
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
    builder.append("cyborgs=").append(count).append(";production=").append(production);
    builder.append(";recoverIn=").append(recoverInTurns);
    return builder.toString();
  }
}
