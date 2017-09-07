package multiplayer.fantasticbits.model.commands;

import common.model.Command;

public class FBCommand extends Command {

  public final Command firstWizardCommand;
  public final Command secondWizardCommand;

  public FBCommand(Command firstWizardCommand, Command secondWizardCommand) {
    this.firstWizardCommand = firstWizardCommand;
    this.secondWizardCommand = secondWizardCommand;
  }

  @Override
  public void execute() {
    firstWizardCommand.execute();
    secondWizardCommand.execute();
  }
}
