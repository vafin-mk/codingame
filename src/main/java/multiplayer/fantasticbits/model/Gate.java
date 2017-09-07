package multiplayer.fantasticbits.model;

import common.model.Vector;

public class Gate {

  public final Vector topPole;
  public final Vector bottomPole;
  public final Vector topGoal;
  public final Vector bottomGoal;
  public final Vector centerGoal;

  public Gate(boolean leftGate) {
    double x = leftGate ? 0 : Const.WIDTH - 1;

    centerGoal = new Vector(x, Const.GATE_CENTER_Y);
    topPole = new Vector(x, Const.GATE_CENTER_Y - Const.GATE_LENGTH / 2);
    bottomPole = new Vector(x, Const.GATE_CENTER_Y + Const.GATE_LENGTH / 2);
    topGoal = topPole.add(new Vector(x, Const.POLE_RADIUS / 2));
    bottomGoal = bottomPole.add(new Vector(x, -Const.POLE_RADIUS / 2));
  }
}
