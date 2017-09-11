package multiplayer.wondevwoman.model.commands;

import common.model.Command;
import multiplayer.wondevwoman.model.Direction;

public class PushBuild extends Command {

  public final int index;
  public final Direction targetDirection;
  public final Direction pushDirection;

  public PushBuild(int index, Direction targetDirection, Direction pushDirection) {
    this.index = index;
    this.targetDirection = targetDirection;
    this.pushDirection = pushDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PushBuild moveBuild = (PushBuild) o;

    if (index != moveBuild.index) return false;
    if (targetDirection != moveBuild.targetDirection) return false;
    return pushDirection == moveBuild.pushDirection;

  }

  @Override
  public int hashCode() {
    int result = index;
    result = 31 * result + targetDirection.hashCode();
    result = 31 * result + pushDirection.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("PUSH&BUILD %s %s %s", index, targetDirection, pushDirection);
  }

  @Override
  public void execute() {
    System.out.println(this);
  }
}
