# Center of Madniverse Java Text Game
This is a command-line adventure game utilizing object-oriented programming concepts. Rooms and items are abstracted into their own respective classes and are utilized by the Game class, which is instantiated by the Main file.
The object of the game is for the player to escape the computer of the player's own creation, in which the player has been trapped, by retrieving the correct item to throw at the cooling engine.
## Interacting with the Game
Commands are used in the form: `[command] [parameter]`. Commands include:
* Navigate in a specified cardinal direction to another room (go)
* Navigate to the previous room (back) - uses a stack data structure
* Retrieve an item from the current room and place in inventory (get)
* Consume the specified medicinal item in the inventory (take)
* Throw the specified item in the inventory (throw)
* quit

One of the rooms is a teleporter that will teleport to a random room, and there is a math challenge room that requires solving all the challenges to escape it.