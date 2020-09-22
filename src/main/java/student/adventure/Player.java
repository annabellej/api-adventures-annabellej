package student.adventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player of the game Kidnapped!
 *
 * @author  Annabelle Ju
 * @version 9/21/2020
 */
public class Player {
    private String playerName;
    private List<String> playerInventory;
    private int playerScore; //player's score = how many rooms they have visited

    /**
     * Default constructor for objects of class Player.
     * Initializes a new player with no name and no items in inventory.
     */
    public Player() {
        playerName = "";
        playerInventory = new ArrayList<>();
        playerScore = 0;
    }

    /**
     * Constructor for objects of class Player.
     * Initializes a new player with a given name and no items in inventory.
     *
     * @param playerName the name of this player.
     */
    public Player(String playerName) {
        this.playerName = playerName;
        playerInventory = new ArrayList<>();
        playerScore = 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    /**
     * Makes a copied list of items in the player's inventory.
     *
     * @return a list of items in this player's inventory.
     */
    public List<String> fetchPlayerInventory() {
        List<String> copiedList = Arrays.asList(new String[playerInventory.size()]);
        Collections.copy(copiedList, playerInventory);

        return copiedList;
    }

    /**
     * Adds one to the player's score, every time they change a room.
     */
    public void addToScore() {
        playerScore++;
    }

    /**
     * Determines the size of the player's inventory, aka how many items
     * the player currently has.
     *
     * @return the number of items in the player's inventory.
     */
    public int findSizeOfInventory() {
        return playerInventory.size();
    }

    /**
     * Determines if the player's inventory contains a given item.
     *
     * @param itemName the name of the item to search for.
     *
     * @return true  if the player's inventory contains this item, else
     *         false if the inventory does not contain the item.
     */
    public boolean inventoryContains(String itemName) {
        return playerInventory.contains(itemName);
    }

    /**
     * Adds an item to this player's inventory.
     *
     * @param itemName the item to be added.
     */
    public void addToInventory(String itemName) {
        playerInventory.add(itemName);
    }

    /**
     * Removes an item from this player's inventory.
     *
     * @param itemName the item to be removed.
     */
    public void removeFromInventory(String itemName) {
        playerInventory.remove(itemName);
    }
}
