package puzzles.medium.nospoon

/**
 * Don't let the machines win. You are humanity's last hope...
 **/

class Node{
  int x, y;
}
width = input.nextInt() // the number of cells on the X axis
height = input.nextInt() // the number of cells on the Y axis
input.nextLine()

int[][] map = new int[width][height]
for (i = 0; i < height; ++i) {
  line = input.nextLine() // width characters, each either 0 or .
  for (x = 0; x < line.length(); x++) {
    if ('.' as char == line.charAt(x)) {
      map[x][i] = 0
    } else {
      map[x][i] = 1
    }
  }
}

def nodes = new ArrayList<Node>();

width.times {x ->
  height.times { y ->
    if (map[x][y] == 1) {
      nodes.add(new Node(x: x, y: y));
    }
  }
}

Node closestRight = new Node(x: -1,y: -1)
Node closestBottom = new Node(x: -1,y: -1)

nodes.each {node ->
  closestRight.x = -1;
  closestRight.y = -1;
  closestBottom.x = -1;
  closestBottom.y = -1;
  nodes.each {subNode ->
    if (node.x == subNode.x && node.y == subNode.y) {
      return
    }
    if (node.x == subNode.x) {
      if (((closestBottom.y == -1 || subNode.y < closestBottom.y) && subNode.y > node.y)) {
        closestBottom.x = subNode.x;
        closestBottom.y = subNode.y;
      }
    }
    if (node.y == subNode.y && ((closestRight.x == -1 || subNode.x < closestRight.x) && subNode.x > node.x)) {
      closestRight.x = subNode.x
      closestRight.y = subNode.y
    }
  }
  println(node.x + ' ' + node.y + ' ' + closestRight.x + ' ' + closestRight.y + ' ' + closestBottom.x + ' ' + closestBottom.y)
}