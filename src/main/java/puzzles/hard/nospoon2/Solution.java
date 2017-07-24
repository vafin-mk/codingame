package puzzles.hard.nospoon2;

import java.util.*;
import java.util.stream.Collectors;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final int width;
  final int height;
  final List<Vertex> vertices = new ArrayList<>();
  final List<Edge> edges = new ArrayList<>();
  final int maxConnects;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    width = scanner.nextInt();
    height = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    int connects = 0;
    for (int y = 0; y < height; y++) {
      String line = scanner.nextLine();
      for (int x = 0; x < width; x++) {
        char ch = line.charAt(x);
        if (ch != '.') {
          int required = Character.getNumericValue(ch);
          connects += required;
          vertices.add(new Vertex(x, y, required));
        }
      }
    }
    maxConnects = connects / 2;
  }

  void solve() {
    SolutionCandidate winner = null;

    SolutionCandidate candidate = new SolutionCandidate();
    candidate.maxConnects = maxConnects;
    candidate.width = width;
    candidate.height = height;
    candidate.edges = new ArrayList<>(edges);
    candidate.vertices = new ArrayList<>(vertices);
    candidate.fillNeighs();
    candidate.forcedConnections();
    candidate.applyReconnectionTechniques();
    candidate.centerPoint = new Point(width / 2, height / 2);

    Deque<SolutionCandidate> candidates = new LinkedList<>();
    candidates.add(candidate);
    SolutionCandidate current = null;

    while (!candidates.isEmpty()) {
      current = candidates.poll();
      if (current.usedConnects == current.maxConnects) {
        boolean win = current.allVerticesConnected() && !current.haveCrossedLines();
        if (win) {
          winner = current;
          break;
        } else {
          continue;
        }
      }
      for (Edge edge : current.availableConnections()) {
        candidates.addFirst(current.applyConnection(edge));
      }
    }
    if (winner == null) {
      System.err.println("NO SOLUTION");
      for (Edge edge : current.edges) {
        System.out.println(edge);
      }
      for (Vertex vertex : current.vertices) {
        System.err.println(vertex + "||" + vertex.unconnected() + "||" + current.availableConnects(vertex));
      }
      return;
    }
    for (Edge edge : winner.edges) {
      System.out.println(edge);
    }
  }
}

class SolutionCandidate {
  List<Vertex> vertices;
  List<Edge> edges;
  Point centerPoint;
  int maxConnects;
  int usedConnects;
  int width;
  int height;

  SolutionCandidate applyConnection(Edge edge) {
    SolutionCandidate candidate = new SolutionCandidate();
    candidate.width = width;
    candidate.height = height;
    candidate.maxConnects = maxConnects;
    candidate.vertices = new ArrayList<>(vertices.size());
    for (Vertex vertex : vertices) {
      Vertex copy = new Vertex(vertex.x, vertex.y, vertex.maxConnects);
      copy.currentConnects = vertex.currentConnects;
      candidate.vertices.add(copy);
    }
    candidate.edges = new ArrayList<>(edges);
    candidate.usedConnects = usedConnects;
    candidate.centerPoint = centerPoint;
    candidate.fillNeighs();

    int power = edge.power;
    candidate.edges.add(edge);
    Vertex left = null;
    Vertex right = null;
    for (Vertex vertex : candidate.vertices) {
      if (vertex.equals(edge.left)) {
        left = vertex;
      } else if (vertex.equals(edge.right)) {
        right = vertex;
      }
    }
    left.currentConnects += power;
    right.currentConnects += power;
    candidate.usedConnects += power;

    candidate.applyReconnectionTechniques();
    return candidate;
  }

  boolean allVerticesConnected() {
    Set<Vertex> visited = new HashSet<>();
    Queue<Vertex> unvisited = new LinkedList<>();
    unvisited.add(vertices.get(0));
    while (!unvisited.isEmpty()) {
      Vertex current = unvisited.poll();
      if (visited.contains(current)) continue;
      visited.add(current);
      unvisited.addAll(connectedVertices(current));
    }
    return visited.size() == vertices.size();
  }

  boolean belongToBigGroup(Vertex vertex) {
    Set<Vertex> visited = new HashSet<>();
    Queue<Vertex> unvisited = new LinkedList<>();
    unvisited.add(vertex);
    while (!unvisited.isEmpty()) {
      Vertex current = unvisited.poll();
      if (visited.contains(current)) continue;
      visited.add(current);
      unvisited.addAll(connectedVertices(current));
    }
    return visited.size() >= vertices.size() / 3;
  }

  boolean haveCrossedLines() {
    for (Edge first : edges) {
      for (Edge second : edges) {
        if (first.equals(second)) continue;
        Set<Point> check = new HashSet<>(first.line);
        check.retainAll(second.line);
        if (check.size() > 0) {
//          System.err.println("INTERSECTION BETWEEN " + first + " AND " + second);
          return true;
        }
      }
    }
    return false;
  }

  boolean crossedByHorizontalLine(Point point) {
    for (Edge edge : edges) {
      boolean horizontal = edge.left.y == edge.right.y;
      if (!horizontal) continue;
      if (edge.line.contains(point)) return true;
    }
    return false;
  }

  boolean crossedByVerticalLine(Point point) {
    for (Edge edge : edges) {
      boolean vertical = edge.left.x == edge.right.x;
      if (!vertical) continue;
      if (edge.line.contains(point)) return true;
    }
    return false;
  }

  List<Vertex> connectedVertices(Vertex from) {
    List<Vertex> vertices = new ArrayList<>();
    for (Edge edge : edges) {
      if (edge.left.equals(from)) vertices.add(edge.right);
      else if (edge.right.equals(from)) vertices.add(edge.left);
    }
    return vertices;
  }

  List<Edge> availableConnections() {
    List<Edge> result = new ArrayList<>();
    if (usedConnects < maxConnects / 2) {
      result.addAll(availableConnectionsForSparseGraph());
    } else {
      result.addAll(availableConnectionsForDenseGraph());
    }

    Collections.reverse(result);
    if (result.size() > 3) {
      result = result.subList(result.size() - 3, result.size());
    }
    return result;
  }

  List<Edge> availableConnectionsForSparseGraph() {
    List<Edge> result = new ArrayList<>();
    vertices.sort(Comparator.comparingInt(Vertex::unconnected));
    for (Vertex vertex : vertices) {
      if (vertex.unconnected() == 0) continue;
      for (Vertex neigh : vertex.availableNeighs.stream()
        .sorted(Comparator.comparingInt(v -> v.dist(vertex)))
        .collect(Collectors.toList())) {
        if (!canConnect(vertex, neigh)) continue;
        int power = connectionPower(vertex, neigh);
        Edge edge = new Edge(vertex, neigh, power);
        if (power == 2) {
          result.add(new Edge(vertex, neigh, 1));
        }
        result.add(edge);
      }
    }
    if (result.isEmpty()) return availableConnectionsForDenseGraph();
    return result;
  }

  List<Edge> availableConnectionsForDenseGraph() {
    List<Edge> result = new ArrayList<>();
    List<Vertex> unconnectedVertices = new ArrayList<>();
    for (Vertex vertex : vertices) {
      if (vertex.unconnected() > 0) unconnectedVertices.add(vertex);
    }
    List<Vertex> belongToBigGroup = new ArrayList<>();
    List<Vertex> notBelongToBigGroup = new ArrayList<>();
    for (Vertex unconnectedVertex : unconnectedVertices) {
      if (belongToBigGroup(unconnectedVertex)) {
        belongToBigGroup.add(unconnectedVertex);
      } else {
        notBelongToBigGroup.add(unconnectedVertex);
      }
    }
    if (belongToBigGroup.isEmpty() || notBelongToBigGroup.isEmpty()) {
      return availableConnectionsForSparseGraph();
    }
    belongToBigGroup.sort(Comparator.comparingInt(Vertex::unconnected));
    for (Vertex vertex : belongToBigGroup) {
      for (Vertex neigh : vertex.availableNeighs.stream()
        .filter(notBelongToBigGroup::contains)
        .sorted(Comparator.comparingInt(v -> v.dist(vertex)))
        .collect(Collectors.toList())) {
        if (!canConnect(vertex, neigh)) continue;
        int power = connectionPower(vertex, neigh);
        if (power == 2) {
          result.add(new Edge(vertex, neigh, 1));
        }
        Edge edge = new Edge(vertex, neigh, power);
        result.add(edge);
      }
    }
    if (result.isEmpty()) return availableConnectionsForSparseGraph();
    return result;
  }

  void applyReconnectionTechniques() {
    vertices.sort(Comparator.comparingInt(Vertex::unconnected).reversed());
    boolean haveChanges = false;
    for (Vertex vertex : vertices) {
      int unconnected = vertex.unconnected();
      if (unconnected == 0) continue;
      if (unconnected == availableConnects(vertex)) {
        haveChanges = haveChanges || fullConnectionWithNeighbours(vertex);
      }
      int neighsSize = vertex.availableNeighs.size() - blockedNeighsCount(vertex);
      switch (neighsSize) {
        case 1:
          haveChanges = haveChanges || oneNeighConnects(vertex, unconnected);
          break;
        case 2:
          haveChanges = haveChanges || twoNeighConnects(vertex, unconnected);
          break;
        case 3:
          haveChanges = haveChanges || threeNeighConnects(vertex, unconnected);
          break;
        case 4:
          haveChanges = haveChanges || fourNeighConnects(vertex, unconnected);
          break;
      }
      haveChanges = haveChanges || fillNeighs();
    }
    haveChanges = haveChanges || fillNeighs();
    if (haveChanges) {
      applyReconnectionTechniques();
      return;
    }

    if (forcedConnections()) {
      applyReconnectionTechniques();
    }
  }

  boolean forcedConnections() {
    boolean haveChanges = false;
    vertices.sort((v1, v2) -> Integer.compare(v2.maxConnects, v1.maxConnects));
    for (Vertex vertex : vertices) {
      int deadEnds = deadEndNeighsCount(vertex);
      int neighs = vertex.availableNeighs.size();
      if (neighs == 4) {
        if (vertex.maxConnects == 8
          || (vertex.maxConnects == 7 && deadEnds == 1)
          || (vertex.maxConnects == 6 && deadEnds == 2)
          || (vertex.maxConnects == 5 && deadEnds == 3)) {
          haveChanges = haveChanges || fullConnectionWithNeighbours(vertex);
        } else if (vertex.maxConnects == 7) {
          haveChanges = haveChanges || singleConnectionWithNeighbours(vertex);
        }
      } else if (neighs == 3) {
        if (vertex.maxConnects == 6
          || (vertex.maxConnects == 5 && deadEnds == 1)
          || (vertex.maxConnects == 4 && deadEnds == 2)) {
          haveChanges = haveChanges || fullConnectionWithNeighbours(vertex);
        } else if (vertex.maxConnects == 5) {
          haveChanges = haveChanges || singleConnectionWithNeighbours(vertex);
        }
      } else if (neighs == 2) {
        if (vertex.maxConnects == 4
          || (vertex.maxConnects == 3 && deadEnds == 1)) {
          haveChanges = haveChanges || fullConnectionWithNeighbours(vertex);
        } else if (vertex.maxConnects == 3) {
          haveChanges = haveChanges || singleConnectionWithNeighbours(vertex);
        }
      } else {
        haveChanges = haveChanges || fullConnectionWithNeighbours(vertex);
      }
//      haveChanges = haveChanges || fillNeighs();
    }
    haveChanges = haveChanges || fillNeighs();
    haveChanges = haveChanges || isolationTechniques();
    haveChanges = haveChanges || fillNeighs();

    return haveChanges && forcedConnections();
  }

  boolean oneNeighConnects(Vertex vertex, int connects) {
    return fullConnectionWithNeighbours(vertex);
  }

  boolean twoNeighConnects(Vertex vertex, int connects) {
    boolean changed = false;
    if (connects == 4
        || (connects == 3 && deadEndNeighsCount(vertex) > 0)) {
      changed = changed || fullConnectionWithNeighbours(vertex);
    }
    if (connects == 3) {
      changed = changed || singleConnectionWithNeighbours(vertex);
    }

    return changed;
  }

  boolean threeNeighConnects(Vertex vertex, int connects) {
    boolean changed = false;
    int deadEnds = deadEndNeighsCount(vertex);
    if (connects == 6
        || (connects == 5 && deadEnds > 0)
        || (connects == 4 && deadEnds > 1)) {
      changed = changed || fullConnectionWithNeighbours(vertex);
    } else if (connects == 5) {
      changed = changed || singleConnectionWithNeighbours(vertex);
    }

    return changed;
  }


  boolean fourNeighConnects(Vertex vertex, int connects) {
    boolean changed = false;
    int deadEnds = deadEndNeighsCount(vertex);
    if (connects == 8
      || (connects == 7 && deadEnds > 0)
      || (connects == 6 && deadEnds > 1)
      || (connects == 5 && deadEnds > 2)) {
      changed = changed || fullConnectionWithNeighbours(vertex);
    }
    if (connects == 7) {
      changed = changed || singleConnectionWithNeighbours(vertex);
    }

    return changed;
  }
  
  boolean isolationTechniques() {
    boolean changed = false;
    for (Vertex vertex : vertices) {
      int deadEnds = deadEndNeighsCount(vertex);
      int connects = vertex.maxConnects;
      int neighs = vertex.availableNeighs.size();

      if (neighs == 2 && connects == 1 && deadEnds == 1) {
        changed = changed || singleConnectionWithNeighbours(vertex, true, false);
      }

      if (neighs == 3 && connects == 2 && deadEnds == 2) {
        changed = changed || singleConnectionWithNeighbours(vertex, true, false);
      }

      int twoConnectsNeighsCount = twoConnectsNeighsCount(vertex);
      if (neighs == 3 && connects == 3 && deadEnds == 1 && twoConnectsNeighsCount == 1) {
        changed = changed || singleConnectionWithNeighbours(vertex, true, true);
      }

      if (neighs == 2 && connects == 2 && twoConnectsNeighsCount == 1) {
        changed = changed || singleConnectionWithNeighbours(vertex, false, true);
      }
    }
    return changed;
  }

  boolean fullConnectionWithNeighbours(Vertex vertex) {
    boolean changed = false;
    for (Vertex neigh : vertex.availableNeighs) {
      if (!canConnect(vertex, neigh)) continue;
      int power = connectionPower(vertex, neigh);

      edges.add(new Edge(vertex, neigh, power));
      vertex.currentConnects += power;
      neigh.currentConnects += power;
      usedConnects += power;
      changed = true;

    }
    return changed;
  }

  boolean singleConnectionWithNeighbours(Vertex vertex) {
    return singleConnectionWithNeighbours(vertex, false, false);
  }

  boolean singleConnectionWithNeighbours(Vertex vertex, boolean passDeadEnds, boolean passTwoMaxConnections) {
    boolean changed = false;
    for (Vertex neigh : vertex.availableNeighs) {
      if (!canConnect(vertex, neigh)) continue;
      if (usedConnections(vertex, neigh) == 1) continue;
      if (passDeadEnds && neigh.maxConnects == 1) continue;
      if (passTwoMaxConnections && neigh.maxConnects == 2) continue;

      Edge edge = new Edge(vertex, neigh, 1);
      edges.add(edge);
      vertex.currentConnects++;
      neigh.currentConnects++;
      usedConnects++;
      changed = true;

    }
    return changed;
  }

  boolean canConnect(Vertex vertex, Vertex neigh) {
    if (vertex.unconnected() == 0 || neigh.unconnected() == 0) return false;
    int used = usedConnections(vertex, neigh);
    if (used == 2) return false;
    return true;
  }

  int usedConnections(Vertex vertex, Vertex neigh) {
    int used = 0;
    Edge check = new Edge(vertex, neigh, 1);
    for (Edge edge : edges) {
      if (edge.equals(check)) {
        used += edge.power;
      }
    }
    return used;
  }

  int connectionPower(Vertex from, Vertex to) {
    int used = usedConnections(from, to);
    return min (2 - used, from.maxConnects, from.unconnected(), to.maxConnects, to.unconnected());
  }

  int min(int...values) {
    int min = 1000000;
    for (int value : values) {
      if (value < min) {
        min = value;
      }
    }
    return min;
  }

  int deadEndNeighsCount(Vertex vertex) {
    int count = 0;
    for (Vertex neigh : vertex.availableNeighs) {
      if (neigh.maxConnects == 1) {
        count++;
      }
    }
    return count;
  }

  int twoConnectsNeighsCount(Vertex vertex) {
    int count = 0;
    for (Vertex neigh : vertex.availableNeighs) {
      if (neigh.maxConnects == 2) {
        count++;
      }
    }
    return count;
  }

  int blockedNeighsCount(Vertex vertex) {
    int count = 0;
    for (Vertex neigh : vertex.availableNeighs) {
      if (neigh.unconnected() == 0){
        count++;
      }
    }
    return count;
  }

  int availableConnects(Vertex vertex) {
    int available = 0;
    for (Vertex neigh : vertex.availableNeighs) {
      available += Math.min(2, neigh.unconnected());
    }
    return available;
  }

  boolean fillNeighs() {
    boolean neighsChanged = false;
    Set<Vertex> neighs = new HashSet<>();
    for (Vertex vertex : vertices) {
      neighs.clear();

      Vertex left = null;
      int x = vertex.x - 1;
      while (x >= 0) {
        if (crossedByVerticalLine(new Point(x, vertex.y))) break;
        Vertex check = new Vertex(x, vertex.y, -10000);
        if (vertices.contains(check)) {
          left = vertices.get(vertices.indexOf(check));
          break;
        }
        x--;
      }

      Vertex right = null;
      x = vertex.x + 1;
      while (x < width) {
        if (crossedByVerticalLine(new Point(x, vertex.y))) break;
        Vertex check = new Vertex(x, vertex.y, -10000);
        if (vertices.contains(check)) {
          right = vertices.get(vertices.indexOf(check));
          break;
        }
        x++;
      }

      Vertex top = null;
      int y = vertex.y - 1;
      while (y >= 0) {
        if (crossedByHorizontalLine(new Point(vertex.x, y))) break;
        Vertex check = new Vertex(vertex.x, y, -10000);
        if (vertices.contains(check)) {
          top = vertices.get(vertices.indexOf(check));
          break;
        }
        y--;
      }

      Vertex bot = null;
      y = vertex.y + 1;
      while (y < height) {
        if (crossedByHorizontalLine(new Point(vertex.x, y))) break;
        Vertex check = new Vertex(vertex.x, y, -10000);
        if (vertices.contains(check)) {
          bot = vertices.get(vertices.indexOf(check));
          break;
        }
        y++;
      }

      if (left != null) neighs.add(left);
      if (top != null) neighs.add(top);
      if (right != null) neighs.add(right);
      if (bot != null) neighs.add(bot);
      if (!vertex.availableNeighs.equals(neighs)) {
        vertex.availableNeighs.clear();
        vertex.availableNeighs.addAll(neighs);
        neighsChanged = true;
      }
    }
    return neighsChanged;
  }
}

class Edge {
  final Vertex left, right;
  final int power;
  final Set<Point> line;
  Edge(Vertex left, Vertex right, int power) {
    this.left = left;
    this.right = right;
    this.power = power;
    this.line = new HashSet<>();
    fillLine();
  }

  void fillLine() {
    boolean horizontal = left.y == right.y;
    int minX = Math.min(left.x, right.x);
    int maxX = Math.max(left.x, right.x);
    int minY = Math.min(left.y, right.y);
    int maxY = Math.max(left.y, right.y);
    if (horizontal) {
      for (int x = minX + 1; x < maxX; x++) {
        line.add(new Point(x, left.y));
      }
    } else {//vertical
      for (int y = minY + 1; y < maxY; y++) {
        line.add(new Point(left.x, y));
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Edge edge = (Edge) o;

    return (left.equals(edge.left) && right.equals(edge.right))
      || (left.equals(edge.right) && right.equals(edge.left));
  }

  @Override
  public int hashCode() {
    return left.hashCode() + right.hashCode();
  }

  @Override
  public String toString() {
    return left + " " + right + " " + power;
  }
}

class Vertex extends Point {
  final int maxConnects;
  int currentConnects;
  final Set<Vertex> availableNeighs = new HashSet<>();

  Vertex(int x, int y, int maxConnects) {
    super(x, y);
    this.maxConnects = maxConnects;
  }

  int unconnected() {
    return maxConnects - currentConnects;
  }
}

class Point {
  final int x, y;
  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int dist(Point other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Point point = (Point) o;

    return x == point.x && y == point.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }

  @Override
  public String toString() {
    return x + " " + y;
  }
}