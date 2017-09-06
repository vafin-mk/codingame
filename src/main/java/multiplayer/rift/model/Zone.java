package multiplayer.rift.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Zone {
  public static final int NEUTRAL = -1;
  public final int id;
  public final int income;

  //id -- units
  final Map<Integer, Integer> unitsOnZone = new HashMap<>(4);
  private final Set<Zone> neighs = new HashSet<>();
  private int ownerId;


  public Zone(int id, int income) {
    this.id = id;
    this.income = income;
    unitsOnZone.put(0, 0);
    unitsOnZone.put(1, 0);
    unitsOnZone.put(2, 0);
    unitsOnZone.put(3, 0);
    ownerId = NEUTRAL;
  }

  public int getOwner() {
    return ownerId;
  }

  public void setOwner(int ownerId) {
    this.ownerId = ownerId;
  }

  public void setUnits(int units0, int units1, int units2, int units3) {
    unitsOnZone.put(0, units0);
    unitsOnZone.put(1, units1);
    unitsOnZone.put(2, units2);
    unitsOnZone.put(3, units3);
  }

  public boolean haveMyUnits(int myId) {
    return unitsCount(myId) > 0;
  }

  public int unitsCount(int playerId) {
    return unitsOnZone.get(playerId);
  }

  public Set<Zone> getNeighs() {
    return new HashSet<>(neighs);
  }

  public void addNeigh(Zone neigh) {
    this.neighs.add(neigh);
  }

  public boolean isBorder(int myId) {
    for (Zone neigh : this.neighs) {
      if (neigh.getOwner() != myId) return true;
    }
    return false;
  }

  public int maxPossibleAttackers(int myId) {
    Map<Integer, Integer> possibleAttackers = new HashMap<>(unitsOnZone);
    for (Zone neigh : neighs) {
      possibleAttackers.put(0, possibleAttackers.get(0) + neigh.unitsOnZone.get(0));
      possibleAttackers.put(1, possibleAttackers.get(1) + neigh.unitsOnZone.get(1));
      possibleAttackers.put(2, possibleAttackers.get(2) + neigh.unitsOnZone.get(2));
      possibleAttackers.put(3, possibleAttackers.get(3) + neigh.unitsOnZone.get(3));
    }

    possibleAttackers.put(myId, 0);
    int max = 0;
    for (Integer units : possibleAttackers.values()) {
      if (units > max) {
        max = units;
      }
    }
    return max;
  }

  public boolean onFight() {
    boolean haveOneSide = false;

    for (Integer units : unitsOnZone.values()) {
      if (units > 0) {
        if (!haveOneSide) {
          haveOneSide = true;
          continue;
        }
        //have second side
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Zone zone = (Zone) o;

    return id == zone.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
