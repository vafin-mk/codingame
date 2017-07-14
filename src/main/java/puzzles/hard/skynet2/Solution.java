package puzzles.hard.skynet2;

import java.util.*;
import java.util.stream.Collectors;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final List<Node> nodes;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    int nodesCount = scanner.nextInt();
    nodes = new ArrayList<>(nodesCount);
    for (int id = 0; id < nodesCount; id++) {
      nodes.add(new Node(id));
    }

    int linksCount = scanner.nextInt();
    int exitsCount = scanner.nextInt();

    for (int i = 0; i < linksCount; i++) {
      int N1 = scanner.nextInt();
      int N2 = scanner.nextInt();
      nodes.get(N1).adj.add(nodes.get(N2));
      nodes.get(N2).adj.add(nodes.get(N1));
    }
    for (int i = 0; i < exitsCount; i++) {
      int EI = scanner.nextInt();
      nodes.get(EI).exit = true;
    }
  }

  void solve() {
    while (true) {
      int SI = scanner.nextInt();
      recalculateStepsToSkynet(SI);

      Link link = killLink();
      if (link == null) {
        link = dangerousMultilink(nodes.get(SI));
      }
      if (link == null) {
        link = closestLink();
      }

      System.out.println(link);
      nodes.get(link.from).adj.remove(nodes.get(link.to));
      nodes.get(link.to).adj.remove(nodes.get(link.from));
    }
  }

  void recalculateStepsToSkynet(int skynetNode) {
    nodes.forEach(node -> node.stepsToSkynet = Node.UNREACHEABLE);
    Set<Node> visited = new HashSet<>();
    Queue<Node> unvisited = new ArrayDeque<>();
    unvisited.add(nodes.get(skynetNode));
    int steps = 0;
    while(!unvisited.isEmpty()) {
      Set<Node> neighsOnNextStep = new HashSet<>();
      while (!unvisited.isEmpty()) {
        Node curr = unvisited.poll();
        if (visited.contains(curr)) continue;
        visited.add(curr);
        curr.stepsToSkynet = steps;
        neighsOnNextStep.addAll(curr.adj);
      }
      unvisited.addAll(neighsOnNextStep);
      steps++;
    }
  }

  Link killLink() {
    List<Node> exitNodes = nodes.stream().filter(node -> node.exit).collect(Collectors.toList());
    for (Node exitNode : exitNodes) {
      for (Node node : exitNode.adj) {
        if (node.stepsToSkynet == 0) {
          return new Link(node.id, exitNode.id);
        }
      }
    }
    return null;
  }

  Link dangerousMultilink(Node skynetNode) {
    Set<Node> adjs = new HashSet<>();
    List<Node> exitNodes = nodes.stream().filter(node -> node.exit).collect(Collectors.toList());
    exitNodes.forEach(exitNode -> adjs.addAll(exitNode.adj));
    adjs.removeIf(node -> node.adj.stream().filter(nd -> nd.exit).count() < 2);
    if (adjs.isEmpty()) return null;
    Set<Node> startNodes = skynetNode.adj;
    for (Node adj : adjs) {
      for (Node startNode : startNodes) {
        if (oneTurnPath(startNode, adj)) {
          int from = adj.id;
          int to = -1;
          int minSteps = 10000;
          for (Node node : adj.adj) {
            if (!node.exit) continue;
            if (node.stepsToSkynet < minSteps) {
              minSteps = node.stepsToSkynet;
              to = node.id;
            }
          }
          return new Link(from, to);
        }
      }
    }
    return null;
  }

  Link closestLink() {
    int minSteps = 1000;
    int cutFrom = -1;
    int cutAdj = -1;

    Set<Node> adjs = new HashSet<>();
    List<Node> exitNodes = nodes.stream().filter(node -> node.exit).collect(Collectors.toList());
    exitNodes.forEach(exitNode -> adjs.addAll(exitNode.adj));

    for (Node adj : adjs) {
      if (adj.stepsToSkynet < minSteps) {
        minSteps = adj.stepsToSkynet;
        cutAdj = adj.id;
      } else if (adj.stepsToSkynet == minSteps) {
        long cutExits = nodes.get(cutAdj).adj.stream().filter(node -> node.exit).count();
        long currExits = nodes.get(adj.id).adj.stream().filter(node -> node.exit).count();
        if (currExits > cutExits) {
          minSteps = adj.stepsToSkynet;
          cutAdj = adj.id;
        }
      }
    }

    for (Node node : nodes.get(cutAdj).adj) {
      if (node.exit) {
        cutFrom = node.id;
        break;
      }
    }

    return new Link(cutFrom, cutAdj);
  }

  private boolean oneTurnPath(Node from, Node to) {
    Set<Node> visited = new HashSet<>();
    Queue<Node> unvisited = new ArrayDeque<>();
    unvisited.add(nodes.get(from.id));
    while(!unvisited.isEmpty()) {
      Node curr = unvisited.poll();
      if (curr.equals(to)) return true;
      if (visited.contains(curr)) continue;
      if (curr.adj.stream().filter(nd -> nd.exit).count() == 0) continue;
      visited.add(curr);
      unvisited.addAll(curr.adj);
    }
    return false;
  }
}

class Node {
  static final int UNREACHEABLE = -1;
  final int id;
  final Set<Node> adj = new HashSet<>();
  boolean exit;
  int stepsToSkynet = UNREACHEABLE;
  Node(int id) {
    this.id = id;
  }

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

class Link {
  final int from;
  final int to;
  Link(int from, int to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public String toString() {
    return from + " " + to;
  }
}