package puzzles.medium.skynet

input = new Scanner(System.in);

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

N = input.nextInt() // the total number of nodes in the level, including the gateways
List<Node> nodes = new ArrayList<>(N)
N.times {id -> nodes.add(new Node(id: id))}
L = input.nextInt() // the number of links
E = input.nextInt() // the number of exit gateways
for (i = 0; i < L; ++i) {
  N1 = input.nextInt() // N1 and N2 defines a link between these nodes
  N2 = input.nextInt()
  nodes.get(N1).adjNodes.add(nodes.get(N2))
  nodes.get(N2).adjNodes.add(nodes.get(N1))
}
for (i = 0; i < E; ++i) {
  EI = input.nextInt() // the index of a gateway node
  nodes.get(EI).exit = true;
}

nodes.each { node ->
  findStepsToExit(node)
  nodes.each {it -> it.visited = false}
}

// game loop
while (true) {
  SI = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn
  Node SINode = nodes.get(SI)
  Node closestToExit = null;
  int min = 10000;
  for (Node nd : SINode.adjNodes) {
    if (nd.minStepsToExit != -1 && nd.minStepsToExit < min) {
      min = nd.minStepsToExit;
      closestToExit = nd;
    }
  }


  // Write an action using println
  // To debug: System.err << "Debug messages...\n"


  // Example: 0 1 are the indices of the nodes you wish to sever the link between
  println "$SINode.id $closestToExit.id"
  SINode.adjNodes.remove(closestToExit)
  closestToExit.adjNodes.remove(SINode)
}

void findStepsToExit(Node node) {
  if (node.exit) {
    node.minStepsToExit = 0;
    return;
  }
  List<Node> nodes = new ArrayList<>();
  node.visited = true;
  nodes.add(node);
  List<Node> nds = new ArrayList<>();
  int step = 1;
  while (!nodes.isEmpty()) {
    nds.clear()
    for (Node node1 : nodes) {
      for (Node node2 : node1.adjNodes) {

        if (node2.exit) {
          node.minStepsToExit = step;
          return
        }
        if (!node2.visited) {
          node2.visited = true;
          nds.add(node2)
        }
      }
    }
    nodes.clear()
    nodes.addAll(nds)
  }
  node.minStepsToExit = -1
}

class Node {
  int id;
  List<Node> adjNodes = new ArrayList<>();
  boolean visited;
  boolean exit;
  int minStepsToExit = -1;
}