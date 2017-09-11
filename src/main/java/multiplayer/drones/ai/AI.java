package multiplayer.drones.ai;

import common.ai.AbstractAI;
import common.model.Command;
import common.model.Point2I;
import multiplayer.drones.model.Const;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AI extends AbstractAI {

  private Point2I droneCommand(Point2I drone) {
    Point2I closestZone = null;
    double distToClosed = Double.MAX_VALUE;
    for (Point2I zone : zones.values()) {
      double dist = drone.distSquared(zone);
      if (dist < distToClosed) {
        distToClosed = dist;
        closestZone = zone;
      }
    }
    return closestZone;
  }

  @Override
  protected Command think() {
    for (int droneId = 0; droneId < dronesCount; droneId++) {
      Point2I drone = drones.get((myId * dronesCount) + droneId);
      System.out.println(droneCommand(drone));
    }
    return null;
  }

  @Override
  protected void init() {
    playersCount = scanner.nextInt();
    myId = scanner.nextInt();
    dronesCount = scanner.nextInt();
    zonesCount = scanner.nextInt();

    zones = new HashMap<>(zonesCount);
    drones = new HashMap<>(playersCount * dronesCount);
    zoneOwner = new HashMap<>(zonesCount);
    for (int zoneId = 0; zoneId < zonesCount; zoneId++) {
      int zoneX = scanner.nextInt();
      int zoneY = scanner.nextInt();
      Point2I zone = new Point2I(zoneX, zoneY);
      zones.put(zoneId, zone);
      zoneOwner.put(zone, Const.OWNER_NEUTRAL);
    }
  }

  @Override
  protected void readInput() {
    for (int zoneId = 0; zoneId < zonesCount; zoneId++) {
      int ownerId = scanner.nextInt();
      zoneOwner.put(zones.get(zoneId), ownerId);
    }
    for (int playerId = 0; playerId < playersCount; playerId++) {
      for (int droneId = 0; droneId < dronesCount; droneId++) {
        int droneX = scanner.nextInt();
        int droneY = scanner.nextInt();
        drones.put((playerId * dronesCount) + droneId, new Point2I(droneX, droneY));
      }
    }

  }

  @Override
  protected void sendOutput(Command command) {
  }

  public AI(Scanner scanner) {
    super(scanner);
  }

  private int playersCount;
  private int myId;
  private int dronesCount;
  private int zonesCount;

  private Map<Integer, Point2I> zones;
  private Map<Point2I, Integer> zoneOwner;
  private Map<Integer, Point2I> drones;
}
