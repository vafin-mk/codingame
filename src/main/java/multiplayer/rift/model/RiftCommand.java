package multiplayer.rift.model;

import common.model.Command;

import java.util.List;

public class RiftCommand extends Command {

  private final List<MoveCommand> moveCommands;
  private final List<BuyCommand> buyCommands;

  public RiftCommand(List<MoveCommand> moveCommands, List<BuyCommand> buyCommands) {
    this.moveCommands = moveCommands;
    this.buyCommands = buyCommands;
  }

  @Override
  public void execute() {
    StringBuilder builder = new StringBuilder();
    if (moveCommands.isEmpty()) {
      builder.append("WAIT");
    } else {
      for (MoveCommand moveCommand : moveCommands) {
        builder.append(moveCommand).append(" ");
      }
      builder.setLength(builder.length() - 1);
    }
    System.out.println(builder);

    builder.setLength(0);
    if (buyCommands.isEmpty()) {
      builder.append("WAIT");
    } else {
      for (BuyCommand buyCommand : buyCommands) {
        builder.append(buyCommand).append(" ");
      }
      builder.setLength(builder.length() - 1);
    }
    System.out.println(builder);
  }
}
