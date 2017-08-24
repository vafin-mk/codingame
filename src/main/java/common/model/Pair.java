package common.model;

public class Pair<FIRST,SECOND> {
  public final FIRST first;
  public final SECOND second;

  public Pair(FIRST first, SECOND second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Pair<?, ?> pair = (Pair<?, ?>) o;

    return first.equals(pair.first) && second.equals(pair.second);
  }

  @Override
  public int hashCode() {
    int result = first.hashCode();
    result = 31 * result + second.hashCode();
    return result;
  }
}
