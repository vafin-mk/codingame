package multiplayer.ghostcell.model.commands;

public class MessageCommand implements InnerCommand{
  public final String message;
  public MessageCommand(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "MSG " + message;
  }
}
