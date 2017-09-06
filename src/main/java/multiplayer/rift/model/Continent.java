package multiplayer.rift.model;

import java.util.HashMap;
import java.util.Map;

public class Continent {
  static int newId;
  public final int id;
  public final Map<Integer, Zone> zones = new HashMap<>();

  public Continent() {
    this.id = newId++;
  }

  public boolean haveZone(Zone zone) {
    return zones.values().contains(zone);
  }

  public boolean fullyCaptured() {
    int owner = Zone.NEUTRAL;
    for (Zone zone : zones.values()) {
      if (zone.getOwner() == Zone.NEUTRAL) {
        return false;
      }
      if (owner == Zone.NEUTRAL) {
        owner = zone.getOwner();
        continue;
      }

      if (zone.getOwner() != owner) {
        return false;
      }
    }

    return true;
  }

  public boolean canAttack(int myId) {
    for (Zone zone : zones.values()) {
      if (zone.getOwner() == Zone.NEUTRAL || zone.getOwner() == myId) {
        return true;
      }
    }
    return false;
  }
}
