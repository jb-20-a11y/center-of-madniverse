import java.util.ArrayList;
import java.util.Iterator;

/**
 *  This class is the main class of the "Center of Madniverse" application. 
 *  "Center of Madniverse" is a text-based adventure game.  Users 
 *  can walk around some scenery (the chamber created by a quantum computer), collect items, throw items, ingest items, and do various challenges.
 *  The object of the game is to destroy the quantum computer by which the player finds himself trapped.
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms and items, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * Modified
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private ArrayList<Room> roomStack;
    private Room mainRoom;
    private Room lockedRoom;
    private Room teleport;
    private Room challengeRoom;
        
    /**
     * Create the game and initialise its internal map.
     * Precondition: None
     * Postcondition: Game will be read to play after constructor finishes
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
        roomStack = new ArrayList<Room>();
    }

    /**
     * Create all the rooms and link their exits together.
     * Precondition: None
     * Postcondition: Rooms and items are initialized
     */
    private void createRooms()
    {
        Room centerOfMadness, natureSimulationRoom, eatery, entertainmentRoom, medicineRoom, restroom, chemLab, mathGameRoom, particleCollider, teleport;
      
        // create the rooms
        centerOfMadness = new Room("in the Center of Madness, the main compartment of your own quantum computer's hostage chamber");
        natureSimulationRoom = new Room("in a nature simulation room that looks like a forest and has organisms");
        eatery = new Room("in an eatery (food you can't eat right now!!!)");
        entertainmentRoom = new Room("in an entertainment room");
        medicineRoom = new Room("in a medicine room");
        restroom = new Room("in a restroom for you to relieve yourself");
        chemLab = new Room("in a chemistry lab with some (perhaps) useful things", false);
        mathGameRoom = new Room("in a room with a computer posing math challenges (what could possibly be unveiled if you solve the challenges?)");
        particleCollider = new Room("in a particle collider that is (luckily) not running right now", false);
        teleport = new Room();
        
        Item bluePill, purplePill, coolingEngine, potassium, sodium, bomb, hat, key, toilet, pizza, salad, birdLitter;
        
        // Create the items
        bluePill = new Item("bluepill", Item.MEDICINE, "a blue pill");
        purplePill = new Item("purplepill", Item.MEDICINE, "a purple pill");
        coolingEngine = new Item("engine", Item.STATIONARY | Item.TARGET, "the cooling engine of the CPU");
        potassium = new Item("potassium", Item.THROWABLE, "1 g potassium");
        sodium = new Item("sodium", Item.THROWABLE, "1 g sodium");
        bomb = new Item("bomb", Item.THROWABLE, "an atomic bomb");
        hat = new Item("hat", Item.WEARABLE, "a hat");
        key = new Item("key", Item.KEY, "a key you may or may not need");
        toilet = new Item("toilet", Item.STATIONARY, "a toilet containing some of your previous material");
        pizza = new Item("pizza", 0, "a pizza");
        salad = new Item("salad", 0, "a salad");
        birdLitter = new Item("litter", 0, "bird litter");
        
        
        // initialise room exits and item locations
        centerOfMadness.setExit("north", mathGameRoom);
        centerOfMadness.setExit("west", eatery);
        centerOfMadness.setExit("south",  entertainmentRoom);
        centerOfMadness.setExit("east",  teleport);
        centerOfMadness.addItem(bluePill);
        centerOfMadness.addItem(coolingEngine);

        mathGameRoom.setExit("south",  centerOfMadness);
        mathGameRoom.setExit("west",  medicineRoom);
        mathGameRoom.setExit("east",  chemLab);
        mathGameRoom.addItem(key);
        
        eatery.setExit("east",  centerOfMadness);
        eatery.setExit("west",  natureSimulationRoom);
        eatery.setExit("north",  medicineRoom);
        eatery.addItem(pizza);
        eatery.addItem(salad);
        
        entertainmentRoom.setExit("north",  centerOfMadness);
        entertainmentRoom.setExit("south",  restroom);
        entertainmentRoom.addItem(hat);
        
        chemLab.setExit("west", mathGameRoom);
        chemLab.setExit("east",  particleCollider);
        chemLab.setExit("south",  teleport);
        chemLab.addItem(potassium);
        chemLab.addItem(sodium);
        chemLab.addItem(bomb);
        
        medicineRoom.setExit("east",  mathGameRoom);
        medicineRoom.setExit("south",  eatery);
        medicineRoom.addItem(purplePill);
        
        natureSimulationRoom.setExit("east",  eatery);
        natureSimulationRoom.addItem(birdLitter);
        
        restroom.setExit("north",  entertainmentRoom);
        restroom.addItem(toilet);
        
        particleCollider.setExit("west",  chemLab);
        
        currentRoom = centerOfMadness;  // start game in centerOfMadness
        mainRoom = centerOfMadness; // Contains the target to be blown up
        lockedRoom = chemLab; // Key needed to unlock enter room
        this.teleport = teleport;
        challengeRoom = mathGameRoom;
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     * Precondition: None
     * Postcondition: Player receives message as console output
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the Center of the Madniverse!");
        System.out.println("It is 2080. You are Dr. Warren. You have been trapped by your own quantum computer, and it is your job to escape by destroying it.");
        System.out.println("You need to find the correct item to throw at the cooling engine to win the game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     * Precondition: None
     * Postcondition: If command is understood, executes appropriate action; otherwise prints error message
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("back")) {
        	goBack(command);
        }
        else if (commandWord.equals("search")) {
        	searchRoom(command);
        }
        else if (commandWord.equals("get")) {
        	getItem(command);
        }
        else if (commandWord.equals("remove")) {
        	removeItem(command);
        }
        else if (commandWord.equals("throw")) {
        	wantToQuit = throwItem(command);
        }
        else if (commandWord.equals("take")) {
        	wantToQuit = takeItem(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Precondition: None
     * Postcondition: Prints out some help information.
     */
    private void printHelp() 
    {
        System.out.println("Use the command words to perform various actions.");
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Precondition: None
     * Postcondition: Enter room with the exit going in the specified direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     * Special cases: math challenge room will start math challenges unless key is already found; entering chemistry lab requires the key; teleport picks a random room
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no exit in that direction!");
        }
        else if (nextRoom == teleport) {
        	currentRoom = Room.getRandomRoomReference();
        	System.out.println("You have been teleported.");
        	roomStack.clear();
        	System.out.println(currentRoom.getLongDescription());
        }
        else {
        	if (nextRoom == lockedRoom) {
        		if (Room.getAllFoundItemsWithCharacteristic(Item.KEY).size() == 0) {
        			System.out.println("You need a key to enter this room.");
        			return;
        		}
        	}
        	roomStack.add(currentRoom);
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
        if (currentRoom == challengeRoom && Room.getAllFoundItemsWithCharacteristic(Item.KEY).size() == 0) {
        	startChallenges();
        }
    }
    
    /**
     * Precondition: roomStack.size() > 0 and roomStack consists of non-null elements
     * Postcondition: Returns the user to the previous room if possible ("back" command); prints error message otherwise
     */
    private void goBack(Command command)
    {
    	if (command.hasSecondWord()) {
    		System.out.println("You can only go back to the previous room.");
    	}
    	else {
    		if (roomStack.isEmpty()) {
    			System.out.println("You have to go somewhere first!");
    		}
    		else {
    			int previousRoomIndex = roomStack.size() - 1;
    			currentRoom = roomStack.get(previousRoomIndex);
    			roomStack.remove(previousRoomIndex);
    			System.out.println(currentRoom.getLongDescription());
    			if (currentRoom == challengeRoom && Room.getAllFoundItemsWithCharacteristic(Item.KEY).size() == 0) {
    				startChallenges();
    			}
    		}
    	}
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    
    /**
     * Precondition: currentRoom != null
     * Postcondition: Lists the items in the current room. ("search" command)
     */
    private void searchRoom(Command command)
    {
    	if (command.hasSecondWord()) {
    		System.out.println("You can only search the current room.");
    	}
    	else {
    		ArrayList<String> descriptions = currentRoom.listItemDescriptions();
    		if (descriptions.size() > 0) {
    			Iterator<String> iterator = descriptions.iterator();
    			System.out.println("You found the following:");
    			while (iterator.hasNext()) {
    				System.out.println(iterator.next());
    			}
    		}
    		else {
    			System.out.println("No items for you to take here!");
    		}
    	}
    }
    
    /**
     * Attempts to collect the item with the specified keyword from the current room ("get" command)
     * Precondition: currentRoom != null
     * Postcondition: found field of corresponding Item in currentRoom is set to true if it exists; error message printed if not
     */
    private void getItem(Command command)
    {
    	if (!command.hasSecondWord()) {
    		System.out.println("Get what?");
    		return;
    	}
    	currentRoom.pickUpItem(command.getSecondWord());
    }
    
    /**
     * Attempts to put the item specified by the user back into the room to which it belongs. ("remove" command)
     * Precondition: currentRoom != null
     * Postcondition: Item belonging to currentRoom found field set to false if Item belongs to that room; error message printed otherwise
     */
    private void removeItem(Command command)
    {
    	if (!command.hasSecondWord()) {
    		System.out.println("Remove what?");
    		return;
    	}
    	currentRoom.removeItem(command.getSecondWord());
    }
    
    /**
     * Helper method that responds to the "throw" command. May win or lose the game (or keep it going) depending on if the user throws the correct item at the correct object.
     * Precondition: currentRoom != null
     * Postcondition: Appropriate response to throw command is determined and executed (throwing could win, lose, or keep the game going)
     */
    private boolean throwItem(Command command)
    {
    	if (!command.hasSecondWord()) {
    		System.out.println("Throw what?");
    		return false;
    	}
    	if (!currentRoom.itemIncluded("engine")) {
    		System.out.println("You are not throwing into the right place. Try a different room (hint: search all the rooms to find your target); .");
    		return false;
    	}
    	ArrayList<String> foundThrowables = Room.getAllFoundItemsWithCharacteristic(Item.THROWABLE);
    	Iterator<String> iterator = foundThrowables.iterator();
    	String itemToThrow = "";
    	while (iterator.hasNext()) {
    		String itemTitle = iterator.next();
    		if (command.getSecondWord().equals(itemTitle)) {
    			itemToThrow = itemTitle;
    		}
    	}
    	if (itemToThrow.equals("")) {
    		System.out.println("You don't have any throwable item in your inventory matching the item keyword identifier.");
    		return false;
    	}
    	if (itemToThrow.equals("potassium")) {
    		System.out.println("You did it! You blew up your own creation! How did you know the most reactive alkaline metal present in this chamber? You won.");
    		return true;
    	}
    	else if (itemToThrow.equals("sodium")) {
    		System.out.println("You started a fire, but it wasn't enough to disrupt the system. Instead, an alarm of intrusion was triggered, and you have been locked in a cage. You will slowly but surely die here, so no need to continue playing. You lost.");
    		return true;
    	}
    	else if (itemToThrow.equals("bomb")) {
    		System.out.println("You fell for the trap! You blew up your contraption, but now you got blown up as well. You lost.");
    		return true;
    	}
    	return false;
    }
    
    /**
     * Helper method that responds to "take" command for user to ingest medicine (dies from this).
     * Precondition: currentRoom != null
     * Postcondition: Taking any medicine loses the game; game will exit
     */
    private boolean takeItem(Command command)
    {
    	if (!command.hasSecondWord()) {
    		System.out.println("Take what?");
    		return false;
    	}
    	ArrayList<String> foundMedicines = Room.getAllFoundItemsWithCharacteristic(Item.MEDICINE);
    	Iterator<String> iterator = foundMedicines.iterator();
    	String itemToTake = "";
    	while (iterator.hasNext()) {
    		String itemTitle = iterator.next();
    		if (command.getSecondWord().equals(itemTitle)) {
    			itemToTake = itemTitle;
    		}
    	}
    	if (itemToTake.equals("")) {
    		System.out.println("You don't have any medicine item in your inventory matching the item keyword identifier.");
    		return false;
    	}
    	if (itemToTake.equals("bluepill")) {
    		System.out.println("Seriously?  You would rather live in a fake reality? Now your senses are tethered to an imaginary world created by your computer, and there is no turning back. Therefore, there is no point in continuing, because you won't reach the objective. Why would you take unsuspecting medicine? You lost.");
    		return true;
    	}
    	else if (itemToTake.equals("purplepill")) {
    		System.out.println("You just took a poison pill. You lost.");
    		return true;
    	}
    	return false;
    }
    
    /**
     * Helper method to prompt the user and return whether the answer to the challenge was correct.
     * Precondition: parser != null
     * Postcondition: Returns whether int solution is correct by the user with the answer argument
     */
    private boolean testAnswer(int answer)
    {
    	String line = parser.getLine();
    	try {
    		int response = Integer.parseInt(line);
    		if (response == answer) {
    			System.out.println("Perfect!");
    			return true;
    		}
    		else {
    			System.out.println("Incorrect. You will get another form of the same problem to try to redeem yourself, so make it count this time.");
    			return false;
    		}
    	}
    	catch (NumberFormatException exception) {
    		System.out.println("All answers are integers. That is not what you entered. Try this form of the problem instead.");
    		return false;
    	}
    }
    
    /**
     * Helper method to allow the user to complete math challenges. This poses the problems.
     * Another form of the problem will be given until the user answers it correctly.
     * Precondition: parser != null
     * Postcondition: Math problems will be generated; each problem will regenerate another form until user gets it correct; user is free when all challenges are solved
     */
    private void startChallenges()
    {
    	boolean correct;
    	System.out.println("You are trapped in the entrance until you correctly solve ALL of the challenges.");
    	do {
    		int angle = (int) (Math.random() * (89 - 46 + 1) + 46);
    		int answer = 90 - angle;
    		System.out.println("You are in a snowball fight with Mr. Haskins that he picked with you. He thinks he is so brilliant and well-prepared and is licking his chops, but there is one thing you have thought of that will definitely catch him off-guard. You throw a snowball at him at a high angle of " + angle + " degrees. While he is looking up to dodge that snowball, after waiting a certain amount of time, you throw another snowball at a low angle. As a result, both snowballs drill him at the same time, and there is nothing he can do. What low angle should you throw your second snowball, in degrees?");
    		correct = testAnswer(answer);
    	} while (!correct);
    	do {
    		int coefficient = (int) (Math.random() * (16 - 2 + 1) + 2);
    		int exponent = (int) (Math.random() * (5 - 2 + 1) + 2);
    		int answer = coefficient * exponent;
    		System.out.println("If f(x) = " + coefficient + "x^" + exponent + " then what is the value of f'(1)?");
    		correct = testAnswer(answer);
    	} while (!correct);
    	do {
    		int acceleration = 2 * (int) (Math.random() * (5 - 1 + 1) + 1);
    		int initialVelocity = (int) (Math.random() * (8 - 2 + 1) + 2);
    		int time = (int) (Math.random() * (4 - 2 + 1) + 2);
    		int answer = (acceleration/2) * (time * time) + initialVelocity * time;
    		System.out.println("Your car is moving at " + initialVelocity + " m/s, and you then accelerate it at " + acceleration + " m/s^2. How far in m will it have traveled after " + time + " s?");
    		correct = testAnswer(answer);
    	} while (!correct);
    	do {
    		int initialVelocity = 10 * (int) (Math.random() * (5 - 1 + 1) + 1);
    		int time = initialVelocity / 10;
    		int answer = -5 * (time * time) + initialVelocity * time;
    		System.out.println("A ball is thrown upward at " + initialVelocity + " m/s. What is its greatest height in m that it reaches neglecting air resistance? The acceleration due to gravity should be taken to be rounded to the nearest integer.");
    		correct = testAnswer(answer);
    	} while (!correct);
    	do {
    		int coefficient = (int) (Math.random() * (5 - 2 + 1) + 2);
    		int exponent = (int) (Math.random() * (8 - 3 + 1) + 3);
    		int answer = coefficient * exponent;
    		System.out.println("If f'(x) = " + coefficient + "/x and f(1) = 0, then what is the value of f(e^" + exponent + ")?");
    		correct = testAnswer(answer);
    	} while (!correct);
    	System.out.println("Now you are in this room, and you can take anything available to take that may or may not be in here. Whatever you may need to win this game may also exempt you from doing more challenges when you enter this room again.");
    }
}
