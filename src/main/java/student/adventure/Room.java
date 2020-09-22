package student.adventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a room in the game map.
 * A room can either be a normal room or the end room of the game.
 *
 * @author  Annabelle Ju
 * @version 9/21/2020
 */
public class Room {
    private String roomName;
    private String roomDescription;
    private int roomNumber;
    private boolean isEndRoom;
    private List<String> itemsVisible;
    private List<PlayerMovement> possibleMovements;
    private String roomImageURL;

    /**
     * Default constructor for objects of class Room.
     * Initiates Room attributes to default values.
     */
    public Room() {
        roomName = "";
        roomDescription = "";
        roomNumber = 0;
        isEndRoom = false;
        itemsVisible = new ArrayList<>();
        possibleMovements = new ArrayList<>();
        roomImageURL = "";
    }

    //Getters:
    public String getRoomName() {
        return roomName;
    }

    public String getRoomDescription () {
        return roomDescription;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomImageURL() {
        return roomImageURL;
    }

    public boolean isEndRoom() {
        return isEndRoom;
    }

    //Setters:
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setIsEndRoom(boolean endRoom) {
        this.isEndRoom = endRoom;
    }

    public void setItemsVisible(List<String> itemsVisible) {
        this.itemsVisible = itemsVisible;
    }

    public void setPossibleMovements(List<PlayerMovement> possibleMovements) {
        this.possibleMovements = possibleMovements;
    }

    public void setRoomImageURL(String roomImageURL) {
        this.roomImageURL = roomImageURL;
    }

    /**
     * Adds an item to the visible items in this room.
     *
     * @param itemName name of the item to add to this room.
     */
    public void addItemToRoom(String itemName) {
        itemsVisible.add(itemName);
    }

    /**
     * Removes an item from this room.
     *
     * @param itemName the name of the item to remove.
     */
    public void removeItemFromRoom(String itemName) {
        itemsVisible.remove(itemName);
    }

    /**
     * Determines whether this room contains a given item.
     *
     * @param itemName the name of the item to check.
     *
     * @return true  if this room contains this item, else
     *         false if this room doesn't contain this item.
     */
    public boolean containsItem(String itemName) {
        return itemsVisible.contains(itemName);
    }

    /**
     * Finds the string direction values of the possible ways a player can move from
     * this room.
     *
     * @return a list of the possible directions as Strings.
     */
    public List<String> fetchPossibleDirections() {
        List<String> stringDirections = new ArrayList<>();

        for (PlayerMovement movement: possibleMovements) {
            Direction direction = movement.getMovementDirection();
            stringDirections.add(direction.toString());
        }

        return stringDirections;
    }

    /**
     * Finds all the possible numbers of rooms a player can move to from this room.
     *
     * @return a list of all the possible room numbers.
     */
    public List<Integer> fetchPossibleRoomNumbers() {
        List<Integer> possibleRoomNumbers = new ArrayList<>();

        for (PlayerMovement movement: possibleMovements) {
            possibleRoomNumbers.add(movement.getMovedRoomNumber());
        }

        return possibleRoomNumbers;
    }

    /**
     * Makes a copied list of items visible in this room.
     *
     * @return a list of all items in the room.
     */
    public List<String> fetchItemsVisible() {
        List<String> copiedList = Arrays.asList(new String[itemsVisible.size()]);
        Collections.copy(copiedList, itemsVisible);

        return copiedList;
    }

    /**
     * Retrieves the room number of the room moved to after traveling in given direction.
     *
     * @param direction the direction to travel in.
     *
     * @return the room number of the room traveled to.
     */
    public int findRoomNumberInDirection(Direction direction) {
        for (PlayerMovement movement: possibleMovements) {
            if (movement.getMovementDirection().equals(direction)) {
                return movement.getMovedRoomNumber();
            }
        }

        return -1;
    }

    /**
     * Determines if this room has valid properties:
     * Room name and description should not be empty, room number should be positive,
     * this room should have at least one possible direction/room to move to.
     *
     * @return true  if this room has valid properties, else
     *         false if this room has at least one invalid property.
     */
    public boolean isValidRoom() {
        if (roomName.isEmpty() || roomDescription.isEmpty()) {
            return false;
        } else if (roomNumber <= 0) {
            return false;
        } else if (possibleMovements.size() < 1) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String roomDetails = "You are currently in: " + roomName + "\n" + roomDescription + ". \n" +
                "From here, you can go: ";

        //loop to concatenate possible directions the player can move from here
        for (int index = 0; index < possibleMovements.size(); index++) {
            String currentDirection = possibleMovements.get(index).getMovementDirection().toString();

            if (index == possibleMovements.size() - 1) {
                roomDetails += "or " + currentDirection + ". \n";
            }
            else {
                roomDetails += currentDirection + ", ";
            }
        }

        roomDetails += "Items visible: ";

        //loop to concatenate all items visible to player in this room
        for (int index = 0; index < itemsVisible.size(); index++) {
            roomDetails += itemsVisible.get(index);

            if (index < itemsVisible.size() - 1) {
                roomDetails += ", ";
            }
            else {
                roomDetails += ". \n";
            }
        }

        return roomDetails;
    }
}
