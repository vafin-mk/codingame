package puzzles.medium.teads;

import java.util.*;
import java.util.stream.Collectors;

public class Solution {
  static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Map<Integer, Node> nodes = new HashMap<>();
    int n = in.nextInt(); // the number of adjacency relations
    for (int i = 0; i < n; i++) {
      int xi = in.nextInt(); // the ID of a person which is adjacent to yi
      int yi = in.nextInt(); // the ID of a person which is adjacent to xi
      nodes.putIfAbsent(xi, new Node(xi));
      nodes.putIfAbsent(yi, new Node(yi));
      nodes.get(xi).adj.add(yi);
      nodes.get(yi).adj.add(xi);
    }
    int steps = 0;
    while (nodes.size() > 1) {
      removeLeafs(nodes);
      steps++;
    }

    // The minimal amount of steps required to completely propagate the advertisement
    System.out.println("" + steps);
  }

  private static void removeLeafs(Map<Integer, Node> nodes) {
    Set<Integer> removed = nodes.entrySet().stream().filter(entry -> entry.getValue().isLeaf()).map(Map.Entry::getKey).collect(Collectors.toSet());
    removed.forEach(nodes::remove);
    nodes.entrySet().stream().forEach(entry -> entry.getValue().adj.removeIf(removed::contains));
  }

  private static class Node{
    int vertex;Set<Integer> adj = new HashSet<>();
    Node(int vertex) {this.vertex = vertex;}
    boolean isLeaf() {return adj.size() < 2;}

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Node node = (Node) o;

      return vertex == node.vertex;

    }

    @Override
    public int hashCode() {
      return vertex;
    }
  }
}
