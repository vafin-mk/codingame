package multiplayer.fantasticbits.model.entities;

import common.model.Vector;

public class Wizard {
  public final int id;
  public final Vector position;
  public final Vector velocity;
  public final boolean grabbedSnaffle;

  public Wizard(int id, Vector position, Vector velocity, boolean grabbedSnaffle) {
    this.id = id;
    this.position = position;
    this.velocity = velocity;
    this.grabbedSnaffle = grabbedSnaffle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Wizard wizard = (Wizard) o;

    return id == wizard.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("Wizard[%s; %s; %s; %s]", id, position, velocity, grabbedSnaffle);
  }
}
