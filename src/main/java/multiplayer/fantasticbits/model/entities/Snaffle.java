package multiplayer.fantasticbits.model.entities;

import common.model.Vector;

public class Snaffle {
  public final int id;
  public final Vector position;
  public final Vector velocity;
  public final boolean captured;

  public Snaffle(int id, Vector position, Vector velocity, boolean captured) {
    this.id = id;
    this.position = position;
    this.velocity = velocity;
    this.captured = captured;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Snaffle wizard = (Snaffle) o;

    return id == wizard.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("Snaffle[%s; %s; %s; %s]", id, position, velocity, captured);
  }
}
