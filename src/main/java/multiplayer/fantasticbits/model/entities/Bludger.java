package multiplayer.fantasticbits.model.entities;

import common.model.Vector;

public class Bludger {
  public final int id;
  public final Vector position;
  public final Vector velocity;
  public final int targetId;

  public Bludger(int id, Vector position, Vector velocity, int targetId) {
    this.id = id;
    this.position = position;
    this.velocity = velocity;
    this.targetId = targetId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Bludger wizard = (Bludger) o;

    return id == wizard.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("Bludger[%s; %s; %s; %s]", id, position, velocity, targetId);
  }
}
