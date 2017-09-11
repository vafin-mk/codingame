package multiplayer.wondevwoman.model.commands;

import common.model.Command;

public class Surrender extends Command {

  final String message;

  public Surrender(String message) {
    this.message = message;
  }

  public Surrender() {
    this("GG WP!");
  }

  @Override
  public String toString() {
    return String.format("ACCEPT-DEFEAT %s", message);
  }

  @Override
  public void execute() {
    System.out.println(this);
  }
}
