package multiplayer.fantasticbits.ai;

import common.ai.AbstractAI;
import common.model.Command;
import common.model.Vector;
import multiplayer.fantasticbits.model.Const;
import multiplayer.fantasticbits.model.commands.FBCommand;
import multiplayer.fantasticbits.model.commands.MoveCommand;
import multiplayer.fantasticbits.model.commands.ThrowCommand;
import multiplayer.fantasticbits.model.entities.Bludger;
import multiplayer.fantasticbits.model.Gate;
import multiplayer.fantasticbits.model.entities.Flipendo;
import multiplayer.fantasticbits.model.entities.Snaffle;
import multiplayer.fantasticbits.model.entities.Wizard;

import java.util.*;

public class AI extends AbstractAI {

  private Command wizardCommand(Wizard wizard) {
    if (wizard.grabbedSnaffle) {
      return new ThrowCommand(enemyGate.centerGoal, Const.MAX_THROW_THRUST);
    }
    List<Snaffle> snaffles = byDistance(wizard.position);
    Snaffle target = snaffles.get(0);
    if (mana > Const.FLIPENDO_COST && wizard.position.dist(target.position) < 1500) {
      double topSlope = (enemyGate.topGoal.y - wizard.position.y) / (enemyGate.topGoal.x - wizard.position.x);
      double bottomSlope = (enemyGate.bottomGoal.y - wizard.position.y) / (enemyGate.bottomGoal.x - wizard.position.x);
      double targetSlope = (target.position.y - wizard.position.y) / (target.position.x - wizard.position.x);
      if (targetSlope >= topSlope && targetSlope <= bottomSlope) {
        mana -= Const.FLIPENDO_COST;
        return new Flipendo(target.id);
      }
    }
    return new MoveCommand(predictPosition(target.position, target.velocity, 1), Const.MAX_MOVE_THRUST);
  }

  private List<Snaffle> byDistance(Vector from) {
    List<Snaffle> result = new ArrayList<>(snaffles);
    result.sort(Comparator.comparingDouble(sn -> from.distSquared(sn.position)));
    return result;
  }

  private Vector predictPosition(Vector currentPosition, Vector velocity, int turns) {
    return currentPosition.add(velocity.multiply(turns));
  }

  @Override
  protected Command think() {
    Command first = wizardCommand(wizards.get(0));
    Command second = wizardCommand(wizards.get(1));

    return new FBCommand(first, second);
  }

  @Override
  protected void init() {
    myId = scanner.nextInt();
    if (myId == 0) {
      myGate = new Gate(true);
      enemyGate = new Gate(false);
    } else {
      myGate = new Gate(false);
      enemyGate = new Gate(true);
    }
  }

  @Override
  protected void readInput() {
    score = scanner.nextInt();
    mana = scanner.nextInt();
    rivalScore = scanner.nextInt();
    rivalMana = scanner.nextInt();

    wizards.clear();
    rivals.clear();
    snaffles.clear();
    bludgers.clear();

    int entities = scanner.nextInt();
    for (int i = 0; i < entities; i++) {
      int entityId = scanner.nextInt();
      String entityType = scanner.next();
      int x = scanner.nextInt();
      int y = scanner.nextInt();
      Vector position = new Vector(x, y);
      int vx = scanner.nextInt();
      int vy = scanner.nextInt();
      Vector velocity = new Vector(vx, vy);
      int state = scanner.nextInt();

      switch (entityType) {
        case "WIZARD":
          wizards.add(new Wizard(entityId, position, velocity, state == 1));
          break;
        case "OPPONENT_WIZARD":
          rivals.add(new Wizard(entityId, position, velocity, state == 1));
          break;
        case "SNAFFLE":
          snaffles.add(new Snaffle(entityId, position, velocity, state == 1));
          break;
        case "BLUDGER":
          bludgers.add(new Bludger(entityId, position, velocity, state));
          break;
        default:
          throw new IllegalStateException(entityType);
      }
    }

    if (round == 0) {
      targets.put(wizards.get(0).id, -1);
      targets.put(wizards.get(1).id, -1);
    }
  }

  @Override
  protected void sendOutput(Command command) {
    command.execute();
  }

  public AI(Scanner scanner) {
    super(scanner);
  }

  private final List<Wizard> wizards = new ArrayList<>();
  private final List<Wizard> rivals = new ArrayList<>();
  private final List<Snaffle> snaffles = new ArrayList<>();
  private final List<Bludger> bludgers = new ArrayList<>();

  private int myId;
  private Gate myGate;
  private Gate enemyGate;

  private int mana;
  private int rivalMana;
  private int score;
  private int rivalScore;

  private Map<Integer, Integer> targets = new HashMap<>();
}
