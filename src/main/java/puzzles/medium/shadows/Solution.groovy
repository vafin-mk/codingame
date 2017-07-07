package puzzles.medium.shadows

input = new Scanner(System.in);

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

W = input.nextInt() // width of the building.
H = input.nextInt() // height of the building.

N = input.nextInt() // maximum number of turns before game over.
posX = input.nextInt()
posY = input.nextInt()

//System.err << "W:$W;H:$H;N:$N;posX:$posX;posY:$posY"

String bombDir;
int minX = 0,maxX = W,minY = 0,maxY = H, newPosX, newPosY

// game loop
while (true) {
  bombDir = input.next() // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
//  System.err << "bomdDir:$bombDir"
  //  windows.each {win -> System.err << "($win.x|$win.y) "}

  if (bombDir.contains('U')) {
    maxY = posY - 1;
  }

  if (bombDir.contains('D')) {
    minY = posY + 1;
  }

  if (bombDir.contains('R')) {
    minX = posX + 1;
  }

  if (bombDir.contains('L')) {
    maxX = posX - 1;
  }

  newPosX = (int)((minX + maxX) / 2)
  newPosY = (int)((minY + maxY) / 2)

  // Write an action using println
  // To debug: System.err << "Debug messages...\n
  // fix rounding
  if (newPosX == posX) {
    if (bombDir.contains("L")) newPosX--
    else if (bombDir.contains("R")) newPosX++
  }
  if (newPosY == posY) {
    if (bombDir.contains("U")) newPosY--
    else if(bombDir.contains("D")) newPosY++
  }



  posX = newPosX; posY = newPosY
  // the location of the next window Batman should jump to.
  println "$posX $posY"
}