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
    private List<Direction> possibleDirections; //correspond to room w/ same index in possibleRooms
    private List<Integer> possibleRooms;        //correspond to direction w/ same index
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
        possibleDirections = new ArrayList<>();
        possibleRooms = new ArrayList<>();
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

    public void setPossibleDirections(List<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    public void setPossibleRooms(List<Integer> possibleRooms) {
        this.possibleRooms = possibleRooms;
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
     * Determines the index of a given direction in this room's list of
     * possible directions.
     *
     * @param direction the given direction to search for.
     *
     * @return the index of the given direction in the list of possible directions.
     */
    public int findIndexOfDirection(Direction direction) {
        return possibleDirections.indexOf(direction);
    }

    /**
     * Determines the room number given its index in the list of
     * possible rooms to move to for this room.
     *
     * @param roomNumberIndex the index of the room number to retrieve.
     *
     * @return the room number corresponding to the given index.
     */
    public int findPossibleRoomNumber(int roomNumberIndex) {
        return possibleRooms.get(roomNumberIndex);
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

        for (Direction direction: possibleDirections) {
            stringDirections.add(direction.toString());
        }

        return stringDirections;
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
        } else if (possibleDirections.size() < 1 || possibleRooms.size() < 1) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String roomDetails = "You are currently in: " + roomName + "\n" + roomDescription + ". \n" +
                "From here, you can go: ";

        for (int index = 0; index < possibleDirections.size(); index++) {
            String currentDirection = possibleDirections.get(index).name();

            if (index == possibleDirections.size() - 1) {
                roomDetails += "or " + currentDirection + ". \n";
            }
            else {
                roomDetails += currentDirection + ", ";
            }
        }

        roomDetails += "Items visible: ";

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
