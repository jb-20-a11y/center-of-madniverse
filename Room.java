import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "Center of Madniverse" application.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * Room objects can have items represented as Item objects.
 * 
 * @author  Michael Kölling and David J. Barnes
 * Modified
 */

public class Room 
{
    private String description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    private ArrayList<Item> items;
    private static ArrayList<Room> allRooms = new ArrayList<Room>();
    private static ArrayList<Room> teleportRooms = new ArrayList<Room>();

    /**
     * Default constructor.
     * Teleport room uses default constructor; not added to static field allRooms
     * Precondition: None
     * Postcondition: Room constructed described as a teleport
     */
    public Room()
    {
        this.description = "in a teleport";
        exits = new HashMap<>();
        items = new ArrayList<Item>();
    }
    
    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * Room is added to Room.teleportRooms for use with the teleport and Room.allRooms.
     * Precondition: description != null
     * Postcondition: Room object constructed; reference added to Room.allRooms and Room.teleportRooms
     */
    public Room(String description) 
    {
        this.description = description;
        exits = new HashMap<>();
        items = new ArrayList<Item>();
        allRooms.add(this);
        teleportRooms.add(this);
    }
    
    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * Room is added to Room.teleportRooms for use with the teleport only if teleportable is true and Room.allRooms.
     * Precondition: description != null
     * Postcondition: Room object constructed; only added to Room.teleportRooms if teleportable is true; added to Room.allRooms
     */
    public Room(String description, boolean teleportable) 
    {
        this.description = description;
        exits = new HashMap<>();
        items = new ArrayList<Item>();
        allRooms.add(this);
        if (teleportable) {
        	teleportRooms.add(this);
        }
    }
    
    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
    
    /**
     * Precondition: items != null && item != null
     * Postcondition: Item is added to the current room's ArrayList of Item objects
     */
    public void addItem(Item item)
    {
    	items.add(item);
    }
    
    /**
     * Precondition: items != null
     * Postcondition: Returns an ArrayList of String descriptions of items in the current room
     */
    public ArrayList<String> listItemDescriptions()
    {
    	ArrayList<String> descriptions = new ArrayList<String>();
    	if (items.size() > 0) {
    		Iterator<Item> iterator = items.iterator();
    		while (iterator.hasNext()) {
    			Item currentItem = iterator.next();
    			if (!currentItem.isFound()) {
    				descriptions.add(currentItem.getTitle() + " - " + currentItem.getDescription());
    				}
    		}
    	}
    	return descriptions;
    }
    
    /**
     * Precondition: items != null && keyword != null
     * Postcondition: Item with matching keyword in current room is picked up, found field of that item set to true
     */
    public void pickUpItem(String keyword)
    {
    	Iterator<Item> iterator = items.iterator();
    	while (iterator.hasNext()) {
    		Item currentItem = iterator.next();
    		if (currentItem.getTitle().equals(keyword)) {
    			currentItem.get();
    			return;
    		}
    	}
    	System.out.println("Item not found");;
    }
    
    /**
     * Precondition: items != null && keyword != null
     * Postcondition: Item is put back in original room, found field of that item set to false
     */
    public void removeItem(String keyword)
    {
    	Iterator<Item> iterator = items.iterator();
    	while (iterator.hasNext()) {
    		Item currentItem = iterator.next();
    		if (currentItem.getTitle().equals(keyword)) {
    			currentItem.remove();
    			return;
    		}
    	}
    	System.out.println("Item not in your inventory, or you tried to put an item back in something other than its original location, which is forbidden.");
    }
    
    /**
     * Precondition: items != null
     * Postcondition: Returns ArrayList of String values of item keywords that the user has found that contain the given bitwise characteristic flag in the current room
     */
    public ArrayList<String> getFoundItemsWithCharacteristic(int characteristic)
    {
    	ArrayList<String> titles = new ArrayList<String>();
    	Iterator<Item> iterator = items.iterator();
    	while (iterator.hasNext()) {
    		Item currentItem = iterator.next();
    		if ((currentItem.isFound()) && (currentItem.getType() & characteristic) > 0) {
    			titles.add(currentItem.getTitle());
    		}
    	}
    	return titles;
    }
    
    /**
     * Precondition: Room.allRooms.size() > 0
     * Postcondition: Returns ArrayList of String values of item keywords that the user has found that contain the given bitwise characteristic flag in the rooms in Room.allRooms
     */
    public static ArrayList<String> getAllFoundItemsWithCharacteristic(int characteristic)
    {
    	ArrayList<String> titles = new ArrayList<String>();
    	Iterator<Room> iterator = allRooms.iterator();
    	while (iterator.hasNext()) {
    		Room currentRoom = iterator.next();
    		ArrayList<String> foundItems = currentRoom.getFoundItemsWithCharacteristic(characteristic);
    		Iterator<String> itemsIterator = foundItems.iterator();
    		while (itemsIterator.hasNext()) {
    			titles.add(itemsIterator.next());
    		}
    	}
    	return titles;
    }
    
    /**
     * Precondition: items != null && keyword != null
     * Postcondition: Returns if an item may be found in the room
     */
    public boolean itemIncluded(String keyword)
    {
    	Iterator<Item> iterator = items.iterator();
    	while (iterator.hasNext()) {
    		Item currentItem = iterator.next();
    		if (currentItem.getTitle().equals(keyword)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Precondition: Room.teleportRooms.size() > 0
     * Postcondition: Returns a random room reference from the list of Room references.
     */
    public static Room getRandomRoomReference()
    {
    	int location = (int) (Math.random() * (teleportRooms.size()));
    	return teleportRooms.get(location);
    }
}

