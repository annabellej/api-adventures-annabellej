package student.adventure;

import java.io.IOException;
import java.util.*;

import student.server.AdventureState;
import student.server.GameStatus;
import student.server.Command;

import static student.adventure.MapDataReader.deserializeFile;

/**
 * GameEngine runs the adventure game Kidnapped! by taking in user input
 * and responding with feedback to progress in the game.
 *
 * Custom game feature: print history of player's visited rooms.
 *
 * @author  Annabelle Ju
 * @version 9/19/2020
 */
public class GameEngine {
    private int gameID;
    private GameStatus currentGameState;
    Map<String, List<String>> commandOptions;
    private String inputPrompter;

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
     *
     * Code for generating random game ID from 0 to max int value derived from:
     * https://stackoverflow.com/questions/31635157/generating-a-random-int/31635240
     */
    public GameEngine(String fileName, String inputPrompter) {
        gamePlayer = new Player();
        gameEnded = false;
        orderedVisitedRooms = new ArrayList<>();
        this.inputPrompter = inputPrompter;

        try {
            gameMap = deserializeFile(fileName);
            roomNumbersToIndices = gameMap.mapRoomNumbersToIndex();
            currentRoom = gameMap.retrieveRoomAt(0);

            //Create map of command options
            commandOptions = new HashMap<>();
            commandOptions.put("examine", new ArrayList<>(Arrays.asList("room")));
            commandOptions.put("quit", new ArrayList<>(Arrays.asList("game")));
            commandOptions.put("go", new ArrayList<>(Arrays.asList(
                    "east", "west", "north", "south")));
            commandOptions.put("take", new ArrayList<>(Arrays.asList(
                    "chair", "rope", "key", "microscope")));
            commandOptions.put("drop", new ArrayList<>(Arrays.asList(
                    "chair", "rope", "key", "microscope")));

            gameID = new Random().nextInt(100);
            currentGameState = new GameStatus(false, gameID, writeGameIntro() + writePlayerPrompter(),
                    currentRoom.getRoomImageURL(), "", new AdventureState(), commandOptions);
        }
        catch (IOException e) {
            currentGameState = new GameStatus(true, 0, "", "", "",
                                                    new AdventureState(), null);
        }
    }

    public Player getGamePlayer() {
        return gamePlayer;
    }

    public int getGameID() {
        return gameID;
    }

    public GameStatus getCurrentGameState() {
        return currentGameState;
    }

    /**
     * Takes a step in the game given a command from the player.
     *
     * @param playerCommand the command inputted by the player.
     *
     * @return the status of this game after the player's action.
     */
    public GameStatus takeGameStep(Command playerCommand) {
        String responseMessage = "";

        responseMessage += performAction(playerCommand.getCommandName(),
                                         playerCommand.getCommandValue());
        if (gameEnded) {
            responseMessage += writeGameOutro();
        }
        else {
            responseMessage += writePlayerPrompter();
        }

        GameStatus updatedStatus = new GameStatus(false, gameID, responseMessage, currentRoom.getRoomImageURL(),
                                                "", new AdventureState(), commandOptions);
        currentGameState = updatedStatus;

        return updatedStatus;
    }

    /**
     * Writes a welcome message for the player.
     * Includes the game's backstory, rules, etc.
     *
     * @return the String welcome message.
     */
    private String writeGameIntro() {
        return "Welcome to Kidnapped!" + "\n" +
                "You have been mysteriously abducted by someone--" +
                "is it the government? The russian mob? Who knows. " + "\n" +
                "Either way, you have awoken in a strange room " +
                "and must now find your way out of this compound. " + "\n" +
                "Luckily, the compound has convenient windows for ceilings, " +
                "allowing you to navigate by the sun by moving " +
                "north, south, east, or west through the compound's rooms.";
    }

    /**
     * Writes the prompt for the player's next move: provides them with current room
     * details and prompt for an action.
     *
     * @return the String player prompt.
     */
    private String writePlayerPrompter() {
        return "\n" + currentRoom.toString() + "\n" +
               "What action would you like to take?" + "\n" + inputPrompter;
    }

    /**
     * Writes the game outro for the player, including the list of visited rooms
     * in order of when the player visited them.
     *
     * @return the String outro message.
     */
    private String writeGameOutro() {
        String gameOutro = "\n" + "Thanks for playing! " +
                           "Here's a quick history of your room traversal: \n";

        for (int roomIndex: orderedVisitedRooms) {
            gameOutro += gameMap.retrieveRoomAt(roomIndex).getRoomName() + "\n";
        }

        return gameOutro;
    }

    /**
     * Given an inputted command by the player, determine the action the player wants to take
     * and perform that action, updating the game parameters accordingly.
     *
     * @param commandName  the command inputted by the player.
     * @param commandValue the argument of the command.
     *
     * @return the String game response to the given player action.
     */
    private String performAction(String commandName, String commandValue) {
        switch (commandName) {
            case "quit": case "exit": {
                gameEnded = true;
                return "\n" + "Quitting game..." + "\n";
            }
            case "examine": {
                return "\n" + "Examining this room..." + "\n";
            }
            case "move": case "go": {
                try {
                    return changeRooms(Direction.valueOf(commandValue));
                }
                catch (NoSuchElementException e) {
                    return "\n" + "Please include a direction to move in. Try again:";
                }
            }
            case "grab": case "take": {
                try {
                    return takeItem(commandValue);
                }
                catch (NoSuchElementException e) {
                    return "\n" + "Please include an item to take. Try again: ";
                }
            }
            case "drop": case "leave": case "put": {
                try {
                    return dropItem(commandValue);
                }
                catch (NoSuchElementException e) {
                    return "\n" + "Please include an item to drop. Try again:";
                }
            }
            default: {
                return "\n" + "I don't understand " + commandName + ". Try again: \n";
            }
        }
    }

    /**
     * Given a direction, move the player to the room in that direction.
     * Update the game parameters accordingly.
     *
     * @param direction the direction to move in.
     *
     * @return the String game response to this change room command.
     */
    private String changeRooms(Direction direction) {
        int directionIndex = currentRoom.findIndexOfDirection(direction);

        if (directionIndex == -1) {
            return "\n" + "I can't go " + direction.name() + ". Try again: \n";
        }

        int newRoomNumber = currentRoom.findPossibleRoomNumber(directionIndex);
        int newRoomIndex = roomNumbersToIndices.get(newRoomNumber);

        currentRoom = gameMap.retrieveRoomAt(newRoomIndex);

        String gameResponse = "\n" + "You have moved to: " + currentRoom.getRoomName() + "." + "\n";

        orderedVisitedRooms.add(gameMap.indexOfRoom(currentRoom));
        gamePlayer.addToScore();

        if (currentRoom.isEndRoom()) {
            if (gamePlayer.inventoryContains("key")) {
                gameEnded = true;
                gameResponse += "\n" + "Congrats! You escaped." + "\n";
            }
            else {
                gameResponse += "\n" + "You seem to be missing a key..." + "\n";
            }
        }

        return gameResponse;
    }

    /**
     * Given an item the player wants to take, remove the item from the room and
     * place into the player's inventory.
     *
     * @param itemName the item the player wants to take.
     *
     * @return the String game response to this take item command.
     */
    private String takeItem(String itemName) {
        if (!currentRoom.containsItem(itemName)) {
            return "\n" + "There is no " + itemName + " in the room.";
        }

        currentRoom.removeItemFromRoom(itemName);

        String gameResponse = "\n" + "You have picked up: " + itemName + "." + "\n";

        if (!gamePlayer.inventoryContains(itemName)) {
            gamePlayer.addToInventory(itemName);
        }
        else {
            gameResponse += "\n" + "You already have " + itemName + "!" + "\n";
        }

        return gameResponse;
    }

    /**
     * Given an item the player wants to drop, remove the item from their inventory and
     * place it into the room.
     *
     * @param itemName the item the player wants to drop.
     *
     * @return the String game response to this drop item command.
     */
    private String dropItem(String itemName) {
        if (!gamePlayer.inventoryContains(itemName)) {
            return "\n" + "You don't have " + itemName + "!";
        }

        gamePlayer.removeFromInventory(itemName);

        if (currentRoom.containsItem(itemName)) {
            return "\n" + "The item " + itemName + " is already in this room!";
        }

        currentRoom.addItemToRoom(itemName);

        return "\n" + "You've dropped: " + itemName + "." + "\n";
    }
}
