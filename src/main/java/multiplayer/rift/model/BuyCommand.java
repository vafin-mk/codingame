package multiplayer.rift.model;

public class BuyCommand {
  public final int count;
  public final int zoneId;

  public BuyCommand(int count, int zoneId) {
    this.count = count;
    this.zoneId = zoneId;
  }

  @Override
  public String toString() {
    return String.format("%s %s", count, zoneId);
  }
}
