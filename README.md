### About the project
A multi-player game in LAN using client-server architecture on java Socket in the class of N-tier Architecture by Jie and Sivan. 

### Some highlights
In the game, each two players are associated with a checkerboard and all the rules of the game are applied to the checkerboard.


The first difficulty was to understand how to build the server to manage the players and how the players can interact on the checkerboard. To resolve this issue, we have created a Game class and an internal threaded class Player. A new instance of the class Game will be generated whenever two players are online and available.


The second problem was how to update the numbers and ips of online players (session management) in real time. The fact that all players are not assigned to the same game instance adds to the complexity of session management. We have designed two methods called updateplayerList and addPlayerAndStart to manage players easily and efficiently.

