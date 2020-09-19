package student.adventure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import static student.adventure.MapDataReader.deserializeFile;

/**
 * GameEngine runs the adventure game Kidnapped! by taking in user input
 * and responding with feedback to progress in the game.
 *
 * Custom game feature: print history of player's visited rooms.
 *
 * @author  Annabelle Ju
 * @version 9/18/2020
 */
public class GameEngine {
    private PrintStream gameOutputStream;
    private GameMap gameMap;
    private Room currentRoom;
    private Player gamePlayer;

    private boolean gameEnded;

    private Map<Integer, Integer> roomNumbersToIndices; //link room number to index in room list
    private List<Integer> orderedVisitedRooms;   //list of the indexes of player's visited rooms


    /**
     * Constructor for objects of class GameEngine.
     * Assumes the game prints messages to the console (System.out).
     * Initiates this game map from a given file.
     * Player always starts with no items in room number 1.
     */
    public GameEngine(String fileName) throws IOException {
        gameOutputStream = System.out;

        gamePlayer = new Player();

        gameMap = deserializeFile(fileName);
        roomNumbersToIndices = gameMap.mapRoomNumbersToIndex();
        currentRoom = gameMap.retrieveRoomAt(0);

        gameEnded = false;
        orderedVisitedRooms = new ArrayList<>();
    }

    /**
     * Constructor for objects of class GameEngine.
     * Game prints messages to a given PrintStream.
     * Initiates this game map from a given file.
     * Player always starts with no items in room number 1.
     */
    public GameEngine(String fileName, OutputStream gameOutputStream) throws IOException {
        this.gameOutputStream = new PrintStream(gameOutputStream);

        gamePlayer = new Player();

        gameMap = deserializeFile(fileName);
        roomNumbersToIndices = gameMap.mapRoomNumbersToIndex();
        currentRoom = gameMap.retrieveRoomAt(0);

        gameEnded = false;
        orderedVisitedRooms = new ArrayList<>();
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Player getGamePlayer() {
        return gamePlayer;
    }

    /**
     * Retrieves the room number of a given visited room.
     *
     * @param roomIndex the index of the room to retrieve.
     *
     * @return the room number of a given visited room.
     */
    public int findVisitedRoomNumber(int roomIndex) {
        return orderedVisitedRooms.get(roomIndex);
    }

    /**
     * Begins and runs an adventure game, assuming the input stream used by the player
     * is the console.
     */
    public void runGame() {
        runGame(System.in);
    }

    /**
     * Begins and runs an adventure game by prompting player for moves through a given input
     * stream, updating game parameters accordingly, and responding to the player's
     * actions.
     *
     * Game ends when the end room has been found by the player and the player found the key
     * or if the player quits the game.
     */
    public void runGame(InputStream inputStream) {
        printGameIntro();

        Scanner scanner = new Scanner(inputStream);

        while (!gameEnded)
        {
            gameOutputStream.println("\n" + currentRoom.toString());
            gameOutputStream.print("What action would you like to take?" + "\n" + "> ");

            performAction(scanner.nextLine());
        }

        printGameOutro();
    }

    /**
     * Prints a welcome message to the game for the player.
     * Includes the game's backstory, rules, etc.
     */
    private void printGameIntro() {
        gameOutputStream.println("Welcome to Kidnapped!");
        gameOutputStream.println("You have been mysteriously abducted by someone--" +
                "is it the government? The russian mob? Who knows. " + "\n" +
                "Either way, you have awoken in a strange room " +
                "and must now find your way out of this compound. " + "\n" +
                "Luckily, the compound has convenient windows for ceilings, " +
                "allowing you to navigate by the sun by moving " +
                "north, south, east, or west through the compound's rooms.");
    }

    /**
     * Prints the game outro for the player, including the list of visited rooms
     * in order of when the player visited them.
     */
    private void printGameOutro() {
        gameOutputStream.println("\n" + "Thanks for playing!");
        gameOutputStream.println("Here's a quick history of your room traversal: \n");

        for (int roomIndex: orderedVisitedRooms) {
            gameOutputStream.println(gameMap.retrieveRoomAt(roomIndex).getRoomName());
        }
    }

    /**
     * Given an inputted command by the player, determine the action the player wants to take
     * and perform that action, updating the game parameters accordingly.
     *
     * @param playerCommand the command inputted by the player.
     */
    private void performAction(String playerCommand) {
        StringTokenizer tokenizer = new StringTokenizer(playerCommand);

        String playerAction = tokenizer.nextToken().toLowerCase();

        switch (playerAction) {
            case "quit": case "exit": {
                gameEnded = true;
                break;
            }
            case "examine": {
                gameOutputStream.println("\n" + "Examining this room..." + "\n");
                break;
            }
            case "move": case "go": {
                try {
                    String playerDirection = tokenizer.nextToken().toLowerCase();
                    changeRooms(Direction.valueOf(playerDirection));
                }
                catch (NoSuchElementException e) {
                    gameOutputStream.println("\n" + "Please include a direction to move in. Try again:");
                }
                break;
            }
            case "snatch": case "grab": case "take": {
                try {
                    String itemToTake = tokenizer.nextToken().toLowerCase();
                    takeItem(itemToTake);
                }
                catch (NoSuchElementException e) {
                    gameOutputStream.println("\n" + "Please include an item to take. Try again: ");
                }
                break;
            }
            case "put": case "leave": case "drop": {
                try {
                    String itemToDrop = tokenizer.nextToken().toLowerCase();
                    dropItem(itemToDrop);
                }
                catch (NoSuchElementException e) {
                    gameOutputStream.println("\n" + "Please include an item to drop. Try again:");
                }
                break;
            }
            default: {
                gameOutputStream.println("\n" + "I don't understand " + playerCommand + ". Try again: \n");
            }
        }
    }

    /**
     * Given a direction, move the player to the room in that direction.
     * Update the game parameters accordingly.
     *
     * @param direction the direction to move in.
     */
    private void changeRooms(Direction direction) {
        int directionIndex = currentRoom.findIndexOfDirection(direction);

        if (directionIndex == -1) {
            gameOutputStream.println("\n" + "I can't go " + direction.name() + ". Try again: \n");
            return;
        }

        int newRoomNumber = currentRoom.findPossibleRoomNumber(directionIndex);
        int newRoomIndex = roomNumbersToIndices.get(newRoomNumber);

        currentRoom = gameMap.retrieveRoomAt(newRoomIndex);

        gameOutputStream.println("\n" + "You have moved to: " + currentRoom.getRoomName() + "." + "\n");
        orderedVisitedRooms.add(gameMap.indexOfRoom(currentRoom));

        if (currentRoom.isEndRoom()) {
            if (gamePlayer.inventoryContains("key")) {
                gameEnded = true;
                gameOutputStream.println("\n" + "Congrats! You escaped." + "\n");
            }
            else {
                gameOutputStream.println("\n" + "You seem to be missing a key..." + "\n");
            }
        }
    }

    /**
     * Given an item the player wants to take, remove the item from the room and
     * place into the player's inventory.
     *
     * @param itemName the item the player wants to take.
     */
    private void takeItem(String itemName) {
        if (!currentRoom.containsItem(itemName)) {
            gameOutputStream.println("\n" + "There is no " + itemName + " in the room.");
            return;
        }

        currentRoom.removeItemFromRoom(itemName);

        gameOutputStream.println("\n" + "You have picked up: " + itemName + "." + "\n");

        if (!gamePlayer.inventoryContains(itemName)) {
            gamePlayer.addToInventory(itemName);
        }
        else {
            gameOutputStream.println("\n" + "You already have " + itemName + "!" + "\n");
        }
    }

    /**
     * Given an item the player wants to drop, remove the item from their inventory and
     * place it into the room.
     *
     * @param itemName the item the player wants to drop.
     */
    private void dropItem(String itemName) {
        if (!gamePlayer.inventoryContains(itemName)) {
            gameOutputStream.println("\n" + "You don't have " + itemName + "!");
            return;
        }

        gamePlayer.removeFromInventory(itemName);

        if (currentRoom.containsItem(itemName)) {
            gameOutputStream.println("\n" + "The item " + itemName + " is already in this room!");
            return;
        }

        gameOutputStream.println("\n" + "You've dropped: " + itemName + "." + "\n");

        currentRoom.addItemToRoom(itemName);
    }
}
