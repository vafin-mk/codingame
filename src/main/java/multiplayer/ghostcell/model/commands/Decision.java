package multiplayer.ghostcell.model.commands;

import common.model.Command;

import java.util.List;

public class Decision extends Command {
  private final List<InnerCommand> innerCommands;
  public Decision(List<InnerCommand> innerCommands) {
    this.innerCommands = innerCommands;
  }
  @Override
  public void execute() {
    StringBuilder builder = new StringBuilder();
    for (InnerCommand cmd : innerCommands) {
      builder.append(cmd).append(";");
    }
    builder.setLength(builder.length() - 1);
    System.out.println(builder.toString());
  }
}
