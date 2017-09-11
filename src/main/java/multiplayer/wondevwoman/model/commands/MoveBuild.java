package multiplayer.wondevwoman.model.commands;

import common.model.Command;
import multiplayer.wondevwoman.model.Direction;

public class MoveBuild extends Command{

  public final int index;
  public final Direction moveDirection;
  public final Direction buildDirection;

  public MoveBuild(int index, Direction moveDirection, Direction buildDirection) {
    this.index = index;
    this.moveDirection = moveDirection;
    this.buildDirection = buildDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MoveBuild moveBuild = (MoveBuild) o;

    if (index != moveBuild.index) return false;
    if (moveDirection != moveBuild.moveDirection) return false;
    return buildDirection == moveBuild.buildDirection;

  }

  @Override
  public int hashCode() {
    int result = index;
    result = 31 * result + moveDirection.hashCode();
    result = 31 * result + buildDirection.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("MOVE&BUILD %s %s %s", index, moveDirection, buildDirection);
  }

  @Override
  public void execute() {
    System.out.println(this);
  }
}
