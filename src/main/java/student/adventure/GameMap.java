package student.adventure;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents the map of a game with all of the Rooms.
 *
 * @author  Annabelle Ju
 * @version 9/21/2020
 */
public class GameMap {
    private List<Room> allRooms;

    /**
     * Default constructor for objects of class GameMap.
     * Initiates a new empty list of all Rooms for the map.
     */
    public GameMap() {
        allRooms = new ArrayList<>();
    }

    public void setAllRooms(List<Room> allRooms) {
        this.allRooms = allRooms;
    }

    /**
     * Calculates the size of this map; the total number of rooms.
     *
     * @return the size of this game map.
     */
    public int findMapSize() {
        return allRooms.size();
    }

    /**
     * Gets the room at the given index in the list of all rooms.
     *
     * @param roomIndex the index of the room to find.
     *
     * @return the room at the given index.
     */
    public Room retrieveRoomAt(int roomIndex) {
        return allRooms.get(roomIndex);
    }

    /**
     * Finds the index of a given room in the map.
     *
     * @return the index of the given room in the list of all rooms.
     */
    public int indexOfRoom(Room room) {
        return allRooms.indexOf(room);
    }

    /**
     * Builds a map linking each room's number to its index in the list of all rooms.
     * Helps to transition to different rooms during a game.
     *
     * @return a map linking each room's number to its corresponding index in allRooms.
     */
    public Map<Integer, Integer> mapRoomNumbersToIndex() {
        Map<Integer, Integer> roomNumberIndexLinks = new HashMap<>();

        for (int index = 0; index < allRooms.size(); index++) {
            int currentRoomNumber = allRooms.get(index).getRoomNumber();
            roomNumberIndexLinks.put(currentRoomNumber, index);
        }

        return roomNumberIndexLinks;
    }

    /**
     * Determines whether this map has rooms with valid properties.
     * Helps check if this is a valid map for a game.
     *
     * @return true  if this map has valid rooms, else
     *         false if this map doesn't have valid rooms.
     */
    public boolean hasValidRooms() {
        for (Room currentRoom: allRooms) {
            if (!currentRoom.isValidRoom()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether this map has exactly one end room.
     * Helps check if this is a valid map for a game.
     *
     * @return true  if this map has exactly one end room, else
     *         false if this map doesn't have exactly one end room.
     */
    public boolean hasSingleEndRoom() {
        int numEndRooms = 0;

        for (Room currentRoom: allRooms) {
            if (currentRoom.isEndRoom()) {
                numEndRooms++;
            }
        }

        return numEndRooms == 1;
    }

    /**
     * Determines whether this map has rooms with unique numbers;
     * No two rooms should have the same room number.
     * Helps check if this is a valid map for a game.
     *
     * @return true  if this map has unique room numbers, else
     *         false if this map has two or more rooms with the same number.
     */
    public boolean hasUniqueRoomNumbers() {
        HashSet<Integer> roomNumbers = new HashSet<>();

        for (Room currentRoom: allRooms) {
            roomNumbers.add(currentRoom.getRoomNumber());
        }

        return roomNumbers.size() == allRooms.size();
    }
}
