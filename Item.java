/**
 * Item class.
 * This is part of the "Center of Madniverse" adventure game.
 * Item is immutable except for the found field, indicating whether the user has picked it up.
 * It has fields for description and bitwise flag for various characteristics such as stationary.
 * Item objects are supposed to be part of Room objects.
 * 
 *
 */

public class Item
{
	// Type constants
	public static final int THROWABLE = 1;
	public static final int STATIONARY = 2;
	public static final int TARGET = 4;
	public static final int WEARABLE = 8;
	public static final int KEY = 16;
	public static final int MEDICINE = 32;
	
	private String title;
	private int type;
	private boolean found;
	private String description;
	
	public Item(String itemTitle, int itemType, String itemDescription)
	{
		title = itemTitle;
		type = itemType;
		found = false;
		description = itemDescription;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean isFound()
	{
		return found;
	}
	
	public void get()
	{
		if (found) {
			System.out.println("You already have this item!");
		}
		if ((type & Item.STATIONARY) > 0) {
			System.out.println("You cannot pick up this item.");
		}
		else {
			found = true;
			System.out.println("You got " + getTitle());
		}
	}
	
	public void remove()
	{
		if (!found) {
			System.out.println("You never had this in the first place!");
		}
		else {
			System.out.println("You put back " + getTitle());
		}
		found = false;
	}
	
	public String getDescription()
	{
		return description;
	}
}

