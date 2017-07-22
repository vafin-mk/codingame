package puzzles.hard.tannetwork;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver{
  final Scanner scanner;
  final String startId;
  final String finishId;
  final Map<String, Stop> nodes;
  final Map<String, Vertex> vertices;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    startId = scanner.next();
    finishId = scanner.next();

    int stopCount = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    nodes = new HashMap<>(stopCount);
    vertices = new HashMap<>(stopCount);
    for (int i = 0; i < stopCount; i++) {
      String stopName = scanner.nextLine();
      Stop stop = new Stop(stopName);
      nodes.put(stop.id, stop);
      vertices.put(stop.id, new Vertex(stop.id));
    }

    int routeCount = scanner.nextInt();
    if (scanner.hasNextLine()) {
      scanner.nextLine();
    }
    for (int i = 0; i < routeCount; i++) {
      String route = scanner.nextLine();
      String[] split = route.split(" ");

      String fromId = split[0];
      String toId = split[1];

      Stop from = nodes.get(fromId);
      Stop to = nodes.get(toId);

      Edge edge = new Edge(vertices.get(toId), from.dist(to));
      vertices.get(fromId).routes.add(edge);
    }
  }

  //dijkstra
  void solve() {
    Vertex start = vertices.get(startId);
    start.minDistToSource = 0;
    Vertex finish = vertices.get(finishId);

    Queue<Vertex> queue = new PriorityQueue<>();
    queue.add(start);
    while (!queue.isEmpty()) {
      Vertex current = queue.poll();
      if (current.id.equals(finish.id)) break;
      for (Edge route : current.routes) {
        Vertex target = route.target;
        double weight = route.dist;
        double distanceThroughCurrent = current.minDistToSource + weight;
        if (distanceThroughCurrent < target.minDistToSource) {
          queue.remove(target);
          target.minDistToSource = distanceThroughCurrent;
          target.previous = current;
          queue.add(target);
        }
      }
    }

    List<Vertex> path = new ArrayList<>();
    for (Vertex vertex = finish; vertex != null; vertex = vertex.previous) {
      path.add(vertex);
    }
    Collections.reverse(path);

    if (path.isEmpty() || !path.get(0).id.equals(start.id)) {
      System.out.println("IMPOSSIBLE");
    } else {
      for (Vertex vertex : path) {
        System.out.println(nodes.get(vertex.id).name);
      }
    }
  }
}

class Stop {
  final String id;
  final String name;
  final String description;
  final double lat;
  final double lon;
  final String zoneId;
  final String url;
  final String type;
  final String motherStation;

  Stop(String text) {
    String[] split = text.split(",");
    id = split[0];
    name = split[1].replaceAll("\"","");
    description = split[2];
    lat = Math.toRadians(Double.valueOf(split[3]));
    lon = Math.toRadians(Double.valueOf(split[4]));
    zoneId = split[5];
    url = split[6];
    type = split[7];
    motherStation = split.length > 8 ? split[8] : "";
  }

  double dist(Stop other) {
    double x = (other.lon - lon) * Math.cos((lat + other.lat) / 2);
    double y = other.lat - lat;
    double dist = Math.sqrt(x * x + y * y) * 6371;
    return dist;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Stop stop = (Stop) o;

    return id.equals(stop.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}

class Vertex implements Comparable<Vertex> {
  final String id;
  final Set<Edge> routes;
  double minDistToSource = Double.POSITIVE_INFINITY;
  Vertex previous;

  Vertex(String id) {
    this.id = id;
    routes = new HashSet<>();
  }

  @Override
  public int compareTo(Vertex o) {
    return Double.compare(minDistToSource, o.minDistToSource);
  }

  @Override
  public String toString() {
    return id;
  }
}

class Edge {
  final Vertex target;
  final double dist;
  Edge(Vertex target, double dist) {
    this.target = target;
    this.dist = dist;
  }

  @Override
  public String toString() {
    return target + ":" + dist;
  }
}