package multiplayer.rift.ai;

import common.ai.AbstractAI;
import common.model.Command;
import multiplayer.rift.model.*;

import java.util.*;

public class AI extends AbstractAI {

  private List<MoveCommand> moveCommands() {
    List<MoveCommand> commands = new ArrayList<>();
    List<Zone> zonesWithMyUnits = zonesWithMyUnits();
    for (Zone zone : zonesWithMyUnits) {
      if (zone.onFight()) continue;
//      int attackers = zone.maxPossibleAttackers(myId);
      int myUnits = zone.unitsCount(myId);
//      int unitsCanUse = attackers == 0 ? myUnits : myUnits - attackers;
//      if (unitsCanUse <= 0) continue;
      for (Zone neigh : valuableNeighs(zone)) {
//        if (unitsCanUse <= 0) break;
        if (myUnits <= 0) break;
        int neighAttackers = neigh.maxPossibleAttackers(myId);
//        if (neighAttackers >= unitsCanUse) continue;
//        if (neighAttackers >= myUnits) continue;
        if (neighAttackers > myUnits) continue;
//        commands.add(new MoveCommand(neighAttackers + 1, zone.id, neigh.id));
        commands.add(new MoveCommand(neighAttackers == 0 ? 1 : neighAttackers , zone.id, neigh.id));
//        unitsCanUse -= (neighAttackers + 1);
//        myUnits -= (neighAttackers + 1);
        myUnits -= neighAttackers;
      }

//      if (unitsCanUse <= 0) continue;
      if (myUnits <= 0) continue;
      Zone closestToBorder = findNeighClosestToBorder(zone);
//      commands.add(new MoveCommand(unitsCanUse, zone.id, closestToBorder.id));
      commands.add(new MoveCommand(myUnits, zone.id, closestToBorder.id));
    }
    return commands;
  }

  private Zone findNeighClosestToBorder(Zone from) {
    int minDistance = 10000;
    Zone closest = null;
    for (Zone zone : from.getNeighs()) {
      if (closest == null) {
        closest = zone;
        minDistance = distanceToClosestBorder(zone);
        continue;
      }
      int dist = distanceToClosestBorder(zone);
      if (dist < minDistance) {
        minDistance = dist;
        closest = zone;
      }
    }

    return closest;
  }

  private int distanceToClosestBorder(Zone from) {
    int dist = 0;
    Set<Zone> visited = new HashSet<>();
    Queue<Zone> unvisited = new LinkedList<>();
    unvisited.add(from);
    while (!unvisited.isEmpty()) {
      List<Zone> allNeighs = new ArrayList<>();
      while (!unvisited.isEmpty()) {
        Zone curr = unvisited.poll();
        if (curr.isBorder(myId)) return dist;
        if (visited.contains(curr)) continue;
        visited.add(curr);
        allNeighs.addAll(curr.getNeighs());
      }
      dist++;
      unvisited.addAll(allNeighs);
      allNeighs.clear();
    }

    return dist;
  }

  private List<Zone> valuableNeighs(Zone zone) {
    List<Zone> valuableNeutrals = new ArrayList<>();
    List<Zone> valuableEnemies = new ArrayList<>();

    for (Zone neigh : zone.getNeighs()) {
      if (neigh.getOwner() == Zone.NEUTRAL) {
        valuableNeutrals.add(neigh);
      }
      if (neigh.getOwner() != myId) {
        valuableEnemies.add(neigh);
      }
    }

    valuableNeutrals.sort(INCOME_COMPARATOR);
    valuableEnemies.sort(INCOME_COMPARATOR);

    List<Zone> result = new ArrayList<>();
    result.addAll(valuableNeutrals);
    result.addAll(valuableEnemies);
    return result;
  }

  private List<BuyCommand> buyCommands() {
    List<BuyCommand> commands = new ArrayList<>();
    int unitsCanBuy = myPlatinum / Const.BUY_COST;
    if (round == 0) {
      return initialBuyCommands(unitsCanBuy);
    }
    if (unitsCanBuy <= 0) return commands;

    List<Zone> neutralZones = neutrals();
    for (Zone neutralZone : neutralZones) {
      commands.add(new BuyCommand(1, neutralZone.id));
      unitsCanBuy--;
      if (unitsCanBuy <= 0) return commands;
    }

    Map<Zone, Integer> zonesInDanger = zonesInDanger();
    for (Map.Entry<Zone, Integer> entry : zonesInDanger.entrySet()) {
      if (unitsCanBuy <= 0) return commands;
      if (entry.getValue() <= unitsCanBuy) {
        commands.add(new BuyCommand(entry.getValue(), entry.getKey().id));
        unitsCanBuy -= entry.getValue();
      }
    }

    if (unitsCanBuy > 0) {
      List<Zone> myZones = myZones();
      myZones.removeIf(z -> !z.isBorder(myId));
      if (myZones.isEmpty()) return commands;
      myZones.sort(INCOME_COMPARATOR);
      commands.add(new BuyCommand(unitsCanBuy, myZones.get(0).id));
    }
    return commands;
  }

  private List<BuyCommand> initialBuyCommands(int unitsCanBuy) {
    List<BuyCommand> commands = new ArrayList<>();

    List<Zone> neutralZones = neutrals();
    for (Zone neutralZone : neutralZones) {
      int used = 0;
      int maxUsed = Math.max(neutralZone.getNeighs().size() / 2, 1);

      List<Zone> neighs = new ArrayList<>(neutralZone.getNeighs());
      neighs.sort(INCOME_COMPARATOR);
      for (Zone neigh : neighs) {
        commands.add(new BuyCommand(1, neigh.id));
        unitsCanBuy--;
        if (unitsCanBuy <= 0) return commands;
        used++;
        if (used >= maxUsed) break;
      }

      if (unitsCanBuy <= 0) return commands;
    }

    return commands;
  }

  private List<Zone> neutrals() {
    List<Zone> result = new ArrayList<>(zones.values());
    result.removeIf(z -> z.getOwner() != Zone.NEUTRAL);
    result.sort(INCOME_COMPARATOR);
    return result;
  }

  private Map<Zone, Integer> zonesInDanger() {
    //zone -- max attackers
    Map<Zone, Integer> result = new LinkedHashMap<>();
    List<Zone> myZones = myZones();
    myZones.sort((z1, z2) -> Integer.compare(z2.income, z1.income));
    for (Zone zone : myZones()) {
      int attackers = zone.maxPossibleAttackers(myId);
      int myUnits = zone.unitsCount(myId);
      if (attackers > myUnits) {
        result.put(zone, attackers - myUnits);
      }
    }
    return result;
  }

  private List<Zone> myZones() {
    List<Zone> result = new ArrayList<>();
    for (Zone zone : zones.values()) {
      if (zone.getOwner() == myId) result.add(zone);
    }

    return result;
  }

  private List<Zone> zonesWithMyUnits() {
    List<Zone> result = new ArrayList<>();
    for (Zone zone : zones.values()) {
      if (zone.haveMyUnits(myId)) result.add(zone);
    }

    return result;
  }

  @Override
  protected Command think() {
    return new RiftCommand(moveCommands(), buyCommands());
  }

  @Override
  protected void init() {
    playersCount = scanner.nextInt();
    myId = scanner.nextInt();
    zonesCount = scanner.nextInt();
    int linkCount = scanner.nextInt();
    for (int i = 0; i < zonesCount; i++) {
      int zoneId = scanner.nextInt();
      int platinumSource = scanner.nextInt();
      zones.put(zoneId, new Zone(zoneId, platinumSource));
    }
    for (int i = 0; i < linkCount; i++) {
      int zone1 = scanner.nextInt();
      int zone2 = scanner.nextInt();
      zones.get(zone1).addNeigh(zones.get(zone2));
      zones.get(zone2).addNeigh(zones.get(zone1));
    }

    buildContinents();
    for (Continent continent : continents.values()) {
      System.err.println("Continent[" + continent.id + "] --> " + continent.zones.size() + " tiles");
    }
  }

  @Override
  protected void readInput() {
    myPlatinum = scanner.nextInt();
    for (int i = 0; i < zonesCount; i++) {
      int zId = scanner.nextInt();
      int ownerId = scanner.nextInt();
      int podsP0 = scanner.nextInt();
      int podsP1 = scanner.nextInt();
      int podsP2 = scanner.nextInt();
      int podsP3 = scanner.nextInt();

      Zone zone = zones.get(zId);
      if (zone == null) continue;
      zone.setOwner(ownerId);
      zone.setUnits(podsP0, podsP1, podsP2, podsP3);
    }
    clearRedundantContinents();
  }

  @Override
  protected void sendOutput(Command command) {
    command.execute();
  }

  public AI(Scanner scanner) {
    super(scanner);
  }

  private void buildContinents() {
    Set<Zone> visited = new HashSet<>();
    for (Continent continent : continents.values()) {
      visited.addAll(continent.zones.values());
    }

    for (Zone zone : zones.values()) {
      if (visited.contains(zone)) continue;
      Continent continent = new Continent();
      Queue<Zone> unvisited = new LinkedList<>();
      unvisited.add(zone);

      while (!unvisited.isEmpty()) {
        Zone curr = unvisited.poll();
        if (visited.contains(curr)) continue;
        visited.add(curr);
        continent.zones.put(curr.id, curr);
        unvisited.addAll(curr.getNeighs());
      }
      continents.put(continent.id, continent);
      buildContinents();
      return;
    }
  }

  private void clearRedundantContinents() {
    List<Integer> toRemove = new ArrayList<>();
    for (Continent continent : continents.values()) {
      if (continent.fullyCaptured() || !continent.canAttack(myId)) {
        System.err.println("Continent[" + continent.id + "] fully captured or not available to attack");
        for (Integer zoneId : continent.zones.keySet()) {
          zones.remove(zoneId);
        }
        toRemove.add(continent.id);
      }
    }

    for (Integer index : toRemove) {
      continents.remove(index);
    }
    for (Continent continent : continents.values()) {
      System.err.println("Continent[" + continent.id + "] --> " + continent.zones.size() + " tiles");
    }
  }

  private final Map<Integer, Zone> zones = new HashMap<>();
  private final Map<Integer, Continent> continents = new HashMap<>();

  private int playersCount;
  private int myId;
  private int zonesCount;
  private int myPlatinum;

  private final Comparator<Zone> INCOME_COMPARATOR = (z1, z2) -> Integer.compare(z2.income, z1.income);
}
