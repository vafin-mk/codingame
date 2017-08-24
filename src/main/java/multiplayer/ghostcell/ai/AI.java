package multiplayer.ghostcell.ai;

import common.ai.AbstractAI;
import common.model.Command;
import common.model.Pair;
import multiplayer.ghostcell.model.*;
import multiplayer.ghostcell.model.commands.*;

import java.util.*;

public class AI extends AbstractAI {

  @Override
  protected Command think() {
    List<InnerCommand> commands = new ArrayList<>();
    commands.add(new WaitCommand());
    String message = "";

    if (!message.isEmpty()) {
      commands.add(new MessageCommand(message));
    }
    return new Decision(commands);
  }

  @Override
  protected void init() {
    factoryCount = scanner.nextInt();
    linksCount = scanner.nextInt();
    distances = new HashMap<>(linksCount);
    for (int i = 0; i < linksCount; i++) {
      int factory1 = scanner.nextInt();
      int factory2 = scanner.nextInt();
      int distance = scanner.nextInt();
      distances.put(new Pair<>(factory1, factory2), distance);
      distances.put(new Pair<>(factory2, factory1), distance);
    }
  }

  @Override
  protected void readInput() {
    factories.clear();
    troops.clear();
    bombs.clear();
    int entityCount = scanner.nextInt();
    for (int i = 0; i < entityCount; i++) {
      int entityId = scanner.nextInt();
      String entityType = scanner.next();
      int arg1 = scanner.nextInt();
      int arg2 = scanner.nextInt();
      int arg3 = scanner.nextInt();
      int arg4 = scanner.nextInt();
      int arg5 = scanner.nextInt();

      switch (entityType) {
        case Const.ENTITY_FACTORY:
          factories.add(new Factory(entityId, arg1, arg2, arg3, arg4));
          break;
        case Const.ENTITY_TROOP:
          troops.add(new Troop(entityId, arg1, arg2, arg3, arg4, arg5));
          break;
        case Const.ENTITY_BOMB:
          bombs.add(new Bomb(entityId, arg1, arg2, arg3, arg4));
          break;
        default:
          throw new IllegalStateException(entityType);
      }
    }

    splitFactories();

//    printDebug();
  }

  private void splitFactories() {
    allies.clear();
    rivals.clear();
    neutrals.clear();
    allyNeutrals.clear();
    rivalNeutrals.clear();
    for (Factory factory : factories) {
      switch (factory.owner) {
        case Const.OWNER_ALLY:
          allies.add(factory);
          break;
        case Const.OWNER_NEUTRAL:
          //todo remove on bronze
          if (factory.production == 0) {
            continue;
          }
          neutrals.add(factory);
          break;
        case Const.OWNER_RIVAL:
          rivals.add(factory);
          break;
      }
    }

    if (allies.isEmpty() || rivals.isEmpty()) {
      //game over
      return;
    }

    for (Factory neutral : neutrals) {
      int allyDist = closestAllyDistance(neutral);
      int rivalDist = closestRivalDistance(neutral);

      if (allyDist < rivalDist) {
        allyNeutrals.add(neutral);
      } else if (allyDist > rivalDist) {
        rivalNeutrals.add(neutral);
      } else {
        allyNeutrals.add(neutral);
        rivalNeutrals.add(neutral);
      }
    }
  }

  int closestAllyDistance(Factory target) {
    int closestDist = Const.MAX_DISTANCE;
    for (Factory ally : allies) {
      int distance = distances.get(new Pair<>(ally.id, target.id));
      if (distance < closestDist) {
        closestDist = distance;
      }
    }
    return closestDist;
  }

  int closestRivalDistance(Factory target) {
    int closestDist = Const.MAX_DISTANCE;
    for (Factory rival : rivals) {
      int distance = distances.get(new Pair<>(rival.id, target.id));
      if (distance < closestDist) {
        closestDist = distance;
      }
    }
    return closestDist;
  }

  Factory closestAlly(Factory target) {
    Factory closest = allies.get(0);
    int closestDist = Const.MAX_DISTANCE;
    for (Factory ally : allies) {
      int distance = distances.get(new Pair<>(ally.id, target.id));
      if (distance < closestDist) {
        closestDist = distance;
        closest = ally;
      }
    }
    return closest;
  }

  Factory closestRival(Factory target) {
    Factory closest = rivals.get(0);
    int closestDist = Const.MAX_DISTANCE;
    for (Factory rival : rivals) {
      int distance = distances.get(new Pair<>(rival.id, target.id));
      if (distance < closestDist) {
        closestDist = distance;
        closest = rival;
      }
    }
    return closest;
  }

  Factory byId(int id) {
    for (Factory factory : factories) {
      if (factory.id == id) return factory;
    }
    throw new IllegalStateException("" + id);
  }

  @Override
  protected void sendOutput(Command command) {
    command.execute();
  }

  private void printDebug() {
    System.err.println("------   ROUND " + round + "   ------------");
    for (Factory ally : allies) {
      System.err.println(ally);
    }
  }

  private int factoryCount;
  private int linksCount;
  private int bombsUsed = 0;

  private List<Factory> factories = new ArrayList<>();
  private List<Troop> troops = new ArrayList<>();
  private List<Bomb> bombs = new ArrayList<>();
  private Map<Pair<Integer, Integer>, Integer> distances;

  private List<Factory> neutrals = new ArrayList<>();
  private List<Factory> allyNeutrals = new ArrayList<>();
  private List<Factory> rivalNeutrals = new ArrayList<>();
  private List<Factory> rivals = new ArrayList<>();
  private List<Factory> allies = new ArrayList<>();

  public AI(Scanner scanner) {
    super(scanner);
  }
}