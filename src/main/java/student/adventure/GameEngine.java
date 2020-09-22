package student.adventure;

import java.io.IOException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

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
 * @version 9/21/2020
 */
public class GameEngine {
    private int gameID;
    private GameStatus currentGameState;
    private Map<String, List<String>> commandOptions;
    private String inputPrompter;

    private GameMap gameMap;
    private Room currentRoom;
    private Player gamePlayer;
    private boolean gameEnded;

    private Map<Integer, Integer> roomNumbersToIndices; //link room number to index in room list
    private List<Integer> orderedVisitedRooms;   //list of the indexes of player's visited rooms

    private final String REQUIRED_ESCAPE_ITEM = "key";

    /**
     * Constructor for objects of class GameEngine.
     * Assumes the game prints messages to the console (System.out).
     * Initiates this game map from a given file.
     * Player always starts with no items in room number 1.
     *
     * @param fileName      the name of the file to generate this game's GameMap.
     * @param inputPrompter prompt to be printed for a player to enter commands.
     * @param gameID        the id to identify this particular GameEngine.
     */
    public GameEngine(String fileName, String inputPrompter, int gameID) {
        gamePlayer = new Player();
        gameEnded = false;
        orderedVisitedRooms = new ArrayList<>();
        this.inputPrompter = inputPrompter;
        this.gameID = gameID;

        try {
            gameMap = deserializeFile(fileName);
            roomNumbersToIndices = gameMap.mapRoomNumbersToIndex();
            currentRoom = gameMap.retrieveRoomAt(0);

            commandOptions = new HashMap<>();
            commandOptions.put("examine", new ArrayList<>(Arrays.asList("room")));
            commandOptions.put("quit", new ArrayList<>(Arrays.asList("game")));
            commandOptions.put("go", new ArrayList<>());
            commandOptions.put("take", new ArrayList<>());
            commandOptions.put("drop", new ArrayList<>());

            fillCommandOptions();

            currentGameState = new GameStatus(false, gameID, writeGameIntro() + writePlayerPrompter(),
                    currentRoom.getRoomImageURL(), "", new AdventureState(), commandOptions);
        } catch (IOException e) {
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

    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Determine the total number of rooms visited so far.
     *
     * @return the number of rooms visited by the player so far.
     */
    public int findNumberVisitedRooms() {
        return orderedVisitedRooms.size();
    }

    /**
     * Finds the name of the current room.
     *
     * @return the String name of the current room.
     */
    public String fetchCurrentRoom() {
        return currentRoom.getRoomName();
    }

    /**
     * Finds the number of command value options for a given command.
     *
     * @param commandName the name of the command to search for.
     *
     * @return the number of possible values for this command.
     */
    public int fetchNumberOfCommandOptions(String commandName) {
        return commandOptions.get(commandName).size();
    }

    /**
     * Takes a step in the game given a command from the player.
     *
     * @param playerCommand the command inputted by the player.
     *
     * @return the status of this game after the player's action.
     */
    public GameStatus takeGameStep(Command playerCommand) {
        gamePlayer.setPlayerName(playerCommand.getPlayerName());

        String responseMessage = "";

        responseMessage += performPlayerAction(playerCommand.getCommandName(),
                                         playerCommand.getCommandValue());
        if (gameEnded) {
            responseMessage += writeGameOutro();
        }
        else {
            responseMessage += writePlayerPrompter();
        }

        fillCommandOptions();
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
     * Determines the possible player commands for the current room/game state
     * and puts those options into the list of command options.
     */
    private void fillCommandOptions() {
        commandOptions.put("go", currentRoom.fetchPossibleDirections());
        commandOptions.put("take", currentRoom.fetchItemsVisible());
        commandOptions.put("drop", gamePlayer.fetchPlayerInventory());
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
    private String performPlayerAction(String commandName, String commandValue) {
        switch (commandName) {
            case "quit": case "exit":
                return quitGame();
            case "examine":
                return "\n" + "Examining this room..." + "\n";
            case "move": case "go":
                return handleGoCommand(commandValue);
            case "grab": case "take":
                return handleTakeCommand(commandValue);
            case "drop": case "leave": case "put":
                return handleDropCommand(commandValue);
            default:
                return "\n" + "I don't understand " + commandName + ". Try again: \n";
        }
    }

    /**
     * Ends game play for this game without a win.
     * Player's score is adjusted accordingly.
     *
     * @return String message telling player the game is ending.
     */
    private String quitGame(){
        gameEnded = true;
        gamePlayer.setPlayerScore(Integer.MAX_VALUE);
        return "\n" + "Quitting game..." + "\n";
    }

    /**
     * Handles a player command to go in a certain String direction.
     *
     * @param directionName the String direction the player wants to go.
     *
     * @return String message informing player of their movement or asking to try again.
     */
    private String handleGoCommand(String directionName) {
        try {
            String gameResponse = changeRoomsTo(Direction.valueOf(directionName));

            if (currentRoom.isEndRoom()) {
                gameResponse += respondToReachingEndRoom();
            }

            return gameResponse;
        } catch (NullPointerException e) {
            return "\n" + "Please include a direction to move in. Try again:";
        }
    }

    /**
     * Handles a player command to take an item from the current room.
     *
     * @param itemName the name of the item to take.
     *
     * @return String message informing player of their action or asking to try again.
     */
    private String handleTakeCommand(String itemName) {
        try {
            return takeItem(itemName);
        } catch (NullPointerException e) {
            return "\n" + "Please include an item to take. Try again: ";
        }
    }

    /**
     * Handles a player command to drop an item from their inventory.
     *
     * @param itemName the name of the item to drop.
     *
     * @return String message informing player of their action or asking to try again.
     */
    private String handleDropCommand(String itemName) {
        try {
            return dropItem(itemName);
        } catch (NullPointerException e) {
            return "\n" + "Please include an item to drop. Try again:";
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
    private String changeRoomsTo(Direction direction) {
        int newRoomNumber = currentRoom.findRoomNumberInDirection(direction);

        //If room number is < 0, movement in given direction is impossible
        if (newRoomNumber < 0) {
            return "\n" + "I can't go " + direction.name() + ". Try again: \n";
        }

        int newRoomIndex = roomNumbersToIndices.get(newRoomNumber);
        currentRoom = gameMap.retrieveRoomAt(newRoomIndex);

        orderedVisitedRooms.add(gameMap.indexOfRoom(currentRoom));
        gamePlayer.addToScore();

        return "\n" + "You have moved to: " + currentRoom.getRoomName() + "." + "\n";
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
        if (itemName == null) {
            throw new NullPointerException("There is no such item!");
        }

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
        if (itemName == null) {
            throw new NullPointerException("There is no such item!");
        }

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

    /**
     * Provides appropriate game response to player finding the end room of the map.
     * Whether player has won depends on whether they have the key in the end room.
     * Player only wins if they also have the key at this point.
     *
     * @return String message informing player of results of reaching end room.
     */
    private String respondToReachingEndRoom() {
        if (gamePlayer.inventoryContains(REQUIRED_ESCAPE_ITEM)) {
            gameEnded = true;
            return "\n" + "Congrats! You escaped." + "\n";
        }
        else {
            return "\n" + "You seem to be missing a " + REQUIRED_ESCAPE_ITEM + "\n";
        }
    }
}
