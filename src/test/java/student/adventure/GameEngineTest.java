package student.adventure;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import student.server.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static student.adventure.PlayerInteractionHandler.executePlayerCommand;

import static org.junit.Assert.*;

public class GameEngineTest {
    private GameEngine testerEngine;
    InputStream inputStream;
    ByteArrayOutputStream outputStream;
    String playerInput;

    @Before
    public void setUp() {
        testerEngine = new GameEngine("src/test/resources/fullValidGame.json", "", 0);
        outputStream = new ByteArrayOutputStream();
    }

    //Tests for correct initial game settings:
    @Test
    public void testCorrectStartRoom() {
        assertEquals("Holding Room", testerEngine.fetchCurrentRoom());
    }

    @Test
    public void testEmptyInitialInventory() {
        assertEquals(0, testerEngine.getGamePlayer().findSizeOfInventory());
    }

    @Test
    public void testGameNotEnded() {
        assertFalse(testerEngine.isGameEnded());
    }

    @Test
    public void testWelcomeMessageReady() {
        assertTrue(testerEngine.getCurrentGameState().getMessage().contains("Welcome to Kidnapped!"));
    }

    @Test
    public void testNoGameStatusErrors() {
        assertFalse(testerEngine.getCurrentGameState().isError());
    }

    @Test
    public void testCorrectCommandOptions() {
        assertEquals(2, testerEngine.fetchNumberOfCommandOptions("go"));
        assertEquals(2, testerEngine.fetchNumberOfCommandOptions("take"));
        assertEquals(0, testerEngine.fetchNumberOfCommandOptions("drop"));
    }

    //Tests for reaching end room
    @Test
    public void testWinningGame() {
        executePlayerCommand(testerEngine, new Command("go", "south"));
        executePlayerCommand(testerEngine, new Command("take", "key"));
        executePlayerCommand(testerEngine, new Command("go", "north"));
        executePlayerCommand(testerEngine, new Command("go", "east"));
        executePlayerCommand(testerEngine, new Command("go", "east"));
        executePlayerCommand(testerEngine, new Command("go", "south"));
        executePlayerCommand(testerEngine, new Command("go", "south"));

        assertTrue(testerEngine.isGameEnded());
    }

    @Test
    public void testMissingKey() {
        executePlayerCommand(testerEngine, new Command("go", "east"));
        executePlayerCommand(testerEngine, new Command("go", "east"));
        executePlayerCommand(testerEngine, new Command("go", "south"));
        executePlayerCommand(testerEngine, new Command("go", "south"));

        assertFalse(testerEngine.isGameEnded());
    }

    //Tests for server run game
    @Test
    public void testMovingAround() {
        executePlayerCommand(testerEngine, new Command("go", "east"));

        assertEquals("Hallway", testerEngine.fetchCurrentRoom());
    }

    @Test
    public void testTakingItem() {
        executePlayerCommand(testerEngine, new Command("take", "chair"));

        assertTrue(testerEngine.getGamePlayer().inventoryContains("chair"));
    }

    @Test
    public void testDroppingItem() {
        executePlayerCommand(testerEngine, new Command("take", "chair"));
        executePlayerCommand(testerEngine, new Command("drop", "chair"));

        assertEquals(0, testerEngine.getGamePlayer().findSizeOfInventory());
    }

    @Test
    public void testQuittingGame() {
        executePlayerCommand(testerEngine, new Command("quit", "game"));

        assertTrue(testerEngine.isGameEnded());
    }

    //Tests for console run game
    @Test
    public void testValidMovingAround() {
        playerInput = "go east" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        executePlayerCommand(testerEngine, inputStream);

        assertEquals("Hallway", testerEngine.fetchCurrentRoom());
    }

    @Test
    public void testInvalidMovingAround() {
        playerInput = "go north" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);
        String gameOutput = outputStream.toString();

        assertThat(gameOutput, CoreMatchers.containsString("I can't go north."));
    }

    @Test
    public void testValidTakingItem() {
        playerInput = "take chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        executePlayerCommand(testerEngine, inputStream);

        assertTrue(testerEngine.getGamePlayer().inventoryContains("chair"));
    }

    @Test
    public void testInValidTakingItem() {
        playerInput = "take key" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);
        String gameOutput = outputStream.toString();

        assertThat(gameOutput, CoreMatchers.containsString("There is no key in the room."));
        assertFalse(testerEngine.getGamePlayer().inventoryContains("key"));
    }

    @Test
    public void testTakingItemAlreadyInInventory() {
        playerInput = "take chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        testerEngine.getGamePlayer().addToInventory("chair");
        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You already have chair!"));
    }

    @Test
    public void testValidDroppingItem() {
        playerInput = "drop potato" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        testerEngine.getGamePlayer().addToInventory("potato");
        executePlayerCommand(testerEngine, inputStream);

        assertFalse(testerEngine.getGamePlayer().inventoryContains("potato"));
    }

    @Test
    public void testDroppingItemAlreadyInRoom() {
        playerInput = "drop chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        testerEngine.getGamePlayer().addToInventory("chair");
        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("The item chair is already in this room!"));
    }

    @Test
    public void testDroppingItemNotInInventory() {
        playerInput = "drop chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You don't have chair!"));
    }

    @Test
    public void testWhiteSpaceInCommand() {
        playerInput = "go                      east" + "\n" + "         quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        executePlayerCommand(testerEngine, inputStream);

        assertEquals("Hallway", testerEngine.fetchCurrentRoom());
    }

    @Test
    public void testCasingIssuesInCommand() {
        playerInput = "gO eaSt" + "\n" + "QUIT" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        executePlayerCommand(testerEngine, inputStream);

        assertEquals("Hallway", testerEngine.fetchCurrentRoom());
    }

    @Test
    public void testUnknownCommand() {
        playerInput = "jump" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("I don't understand jump."));
    }

    @Test
    public void testNoGivenDirection() {
        playerInput = "go" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include a direction to move in."));
    }

    @Test
    public void testNoGivenItemToTake() {
        playerInput = "take";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include an item to take."));
    }

    @Test
    public void testNoGivenItemToDrop() {
        playerInput = "drop" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include an item to drop."));
    }

    //Custom feature test
    @Test
    public void testVisitedRoomsHistory() {
        playerInput = "go south";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());
        executePlayerCommand(testerEngine, inputStream);
        playerInput = "go north";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());
        executePlayerCommand(testerEngine, inputStream);
        playerInput = "quit";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());
        outputStream = executePlayerCommand(testerEngine, inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Storage Closet" + "\n" + "Holding Room"));
    }

    @Test
    public void testImmediateQuitRoomHistory() {
        playerInput = "quit";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());
        executePlayerCommand(testerEngine, inputStream);

        assertEquals(0, testerEngine.findNumberVisitedRooms());
    }
}
