package common.ai;

import java.util.Scanner;

public abstract class AbstractAI {

  protected abstract void init();
  protected abstract void readInput();
  protected abstract void think();
  protected abstract void sendOutput();

  public final void start() {
    init();
    while(true) {
      readInput();
      think();
      sendOutput();
      round++;
    }
  }

  public AbstractAI(Scanner scanner) {
    this.scanner = scanner;
  }

  private final Scanner scanner;
  protected int round;
}
