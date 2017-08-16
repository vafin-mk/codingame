 	The Goal

Outlive your opponents.

 	Rules

The game is played on a grid 13 units wide and 11 units high. The coordinate X=0, Y=0 is the top left cell.

The game is played with up to 4 players. Each player starts out on a corner of the map.

All actions players take are computed simultaneously.

Each player starts out on a corner of the map.

Boxes are scattered across the grid, and can be destroyed by bombs.

A box may have an item inside. Destroying a box will make the item inside appear .

The map works as follows:

Each cell of the grid is either a floor, a box or a wall. Floor cells are indicated by a dot ( .), boxes by a zero ( 0) and walls by a cross ( X).

Floor cells may be occupied by any number of players.

A random amount of boxes between 30 and 65 inclusive will be placed symmetrically across the grid.

The players work as follows:

Every turn, a player may move horizontally or vertically to an adjacent floor cell. If a bomb is already occupying that cell, the player won't be able to move there.

Players can occupy the same cell as a bomb only when the bomb appears on the same turn as when the player enters the cell.

Using the MOVE command followed by grid coordinates will make the player attempt to move one cell closer to those coordinates. The player will automatically compute the shortest path within the grid to get to the target point. If the given coordinates are impossible to get to, the player will instead target the valid cell closest to the given coordinates.

Using the BOMB command followed by map coordinates will make the player attempt to place a bomb on the currently occupied cell, then move one cell closer to the given coordinates.

Players may stay on the cell on which they place a bomb.

At the start of the game, players can only have 1 bomb in the grid at one time.

At the start of the game, players can only place bombs with a range of 3.

Bombs work as follows:

Bombs have an 8 round timer. On each subsequent round, the timer will decrease by 1. On the round where the timers hits 0, the bomb explodes.

Explosions have a range, meaning they span horizontally and vertically up to range squares in each direction unless they encounter a wall, box, item or other bomb.

Explosions will cause the boxes they hit to be destroyed.

Explosions will cause the items they hit to be destroyed.

Explosions will cause the players they hit to be eliminated.

Explosions will cause the bombs they hit to also explode.

Items work as follow:

Items appear once all the explosions have been applied.

A player may collect an item by moving onto the same cell as an item. Several players make collect the same item if they arrive simultaneously.

There are 2 different items, each represented by an integer:

1 extra range: the player's bombs will explode over an extra cell in every direction. Does not apply to bombs already in play.

2 extra bomb: the player can have an additional bomb in play.

The game ends when at most one player is left. The surviving player wins.

After 200 rounds, or if the game state was identical for the last 20 turns, or 20 turns after the destruction of the last box, the game is stopped as if all remaining players are eliminated.

Players who are eliminated on the same turn are ranked by the number of boxes they managed to destroy during the game.

The game state of every round is given to you as a list of entities, each with a entityType, owner, position, param1 and param2.

The entityType will be:

For players: 0.

For bombs: 1.

For items: 2.

The owner will be:

For players: id of the player ( 0 or 1).

For bombs: id of the bomb's owner.

For items: ignored number (= 0).

The param1 will be:

For players: number of bombs the player can still place.

For bombs: number of rounds left until the bomb explodes.

For items: the integer representing the item.

The param2 will be:

For players: current explosion range of the player's bombs.

For bombs: current explosion range of the bomb.

For items: ignored number (= 0).
 
Victory Conditions

Your are the last player standing.

You and other opponents are eliminated simultaneously and you are the one who blew up the most boxes.
 
Lose Conditions

You are hit by a bomb.

You provide incorrect instructions.

 	Expert Rules

Details on how a round is computed:

At the start of the round, all bombs have their countdown decreased by 1.

Any bomb countdown that reaches 0 will cause the bomb to explode immediately, before players move.

Any bomb caught in an explosion is treated as if it had exploded at the very same moment.

Explosions do not go through obstructions such as boxes, items or other bombs, but are included on the cells the obstruction occupies.

A single obstruction may block the explosions of several bombs that explode on the same turn.

Once the explosions have been computed, any box hit scores a point to the owner of each explosion. This means that the destruction of 1 box can count for 2 different players.

Players then perform their actions simultaneously.

New items appear, destroyed items and boxes are removed.

Any bombs placed by a player appear at the end of the round.

 	Note

The program must first read initialization input and then, within an infinite loop, read the contextual data from the standard input and provide to the standard output the desired instructions.

 	Game Input

Initialization input

Line 1: 3 integers, width, height and myId. width and height represent the size of the grid and will always be 13 and 11 respectively, and are given for purely for convenience. myId is your player's id.

Input for one game turn

First height lines: a string row representing each row of the grid. Each character can be:

. an empty cell.

0 an empty box.

A positive integer: a box containing the item represented by the integer.

X a wall.

Next line: an integer entities for the amount of entities on the grid.

Next entities lines : 6 space separated integers entityType , owner , x , y , param1 & param2 . Represents a player, bomb or item.

Output for one game turn

One of the following:

MOVE followed by two integers x and y.

BOMB followed by two integers x and y.

You may append text to your instructions, it will be displayed in the viewer. e.g. MOVE 1 1 hypersonic.

Constraints

Response time per turn ≤ 100 ms

Response time for the first turn ≤ 1000 ms