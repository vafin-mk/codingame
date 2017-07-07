package puzzles.medium.phonenumbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Node root = new Node(-1);
    int N = in.nextInt();
    for (int i = 0; i < N; i++) {
      String telephone = in.next();
      System.err.println(telephone);
      addNumber(root, telephone);
    }

    System.out.println("" + (countChilds(root) - 1));
  }

  private static void addNumber(Node root, String number) {
    char[] num = number.toCharArray();
    Node curr = root;
    for (char n : num) {
      Node node = new Node(n);
      if (curr.subNodes.contains(node)) {
        curr = curr.subNodes.get(curr.subNodes.indexOf(node));
        continue;
      }
      curr.subNodes.add(node);
      curr = node;
    }
  }

  private static int countChilds(Node node) {
    int size = 1;
    for (Node subNode : node.subNodes) {
      size += countChilds(subNode);
    }
    return size;
  }

  private static class Node{
    int vertex; List<Node> subNodes = new ArrayList<>();
    Node(int vertex) {this.vertex = vertex;}

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
