package common.ai;

import common.model.Command;

import java.util.Scanner;

public abstract class AbstractAI {

  protected abstract void init();
  protected abstract void readInput();
  protected abstract Command think();
  protected abstract void sendOutput(Command command);

  public final void start() {
    init();
    while(true) {
      readInput();
      Command command = think();
      sendOutput(command);
      round++;
    }
  }

  public AbstractAI(Scanner scanner) {
    this.scanner = scanner;
  }

  protected final Scanner scanner;
  protected int round;
}
