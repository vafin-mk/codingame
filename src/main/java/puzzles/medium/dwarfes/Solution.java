package puzzles.medium.dwarfes;

import java.util.*;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Map<Integer, Node> nodes = new HashMap<>();
    int n = in.nextInt(); // the number of relationships of influence
    for (int i = 0; i < n; i++) {
      int x = in.nextInt(); // a relationship of influence between two people (x influences y)
      int y = in.nextInt();
      nodes.putIfAbsent(x, new Node(x));
      nodes.putIfAbsent(y, new Node(y));
      nodes.get(x).childs.add(nodes.get(y));
      nodes.get(y).parents.add(nodes.get(x));
    }

    int longest = 0;
    for (Node node : nodes.values()) {
      if (!node.isRoot()) {
        continue;
      }
      int lg = longestBranch(node);
      if (lg > longest) {
        longest = lg;
      }
    }

    // The number of people involved in the longest succession of influences
    System.out.println("" + longest);
  }

  private static int longestBranch(Node node) {
    int max = 0;
    for(Node child : node.childs){
      max = Math.max(longestBranch(child),max);
    }
    return 1+max;
  }

  private static class Node{
    int id;
    Node(int id) {this.id=id;}
    List<Node> parents = new ArrayList<>(), childs = new ArrayList<>();
    boolean isRoot() {return parents.isEmpty();}
    boolean isLeaf() {return childs.isEmpty();}

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Node node = (Node) o;

      return id == node.id;

    }

    @Override
    public int hashCode() {
      return id;
    }
  }
}
