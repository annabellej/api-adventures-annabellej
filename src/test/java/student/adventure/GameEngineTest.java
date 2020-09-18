package student.adventure;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class GameEngineTest {
    GameEngine gamePlayTester;
    InputStream inputStream;
    ByteArrayOutputStream outputStream;
    String playerInput;

    @Before
    public void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        gamePlayTester = new GameEngine("src/test/resources/fullValidGame.json", outputStream);
    }

    //Tests for correct initial game settings
    @Test
    public void testEmptyInitialInventory() {
        assertEquals(0, gamePlayTester.getPlayerInventory().size());
    }

    @Test
    public void testGameNotEnded() {
        assertFalse(gamePlayTester.isGameEnded());
    }

    @Test
    public void testInitialRoomNumberIsOne() {
        assertEquals(1, gamePlayTester.getCurrentRoom().getRoomNumber());
    }

    //Tests for moving around:
    @Test
    public void testValidGoCommand() {
        playerInput = "go east" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You have moved to: Hallway"));
        assertEquals("Hallway", gamePlayTester.getCurrentRoom().getRoomName());
    }

    @Test
    public void testInvalidGoCommand() {
        playerInput = "go north" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("I can't go north."));
    }

    //Test quitting the game:
    @Test
    public void testQuittingGame() {
        playerInput = "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Thanks for playing!"));
    }

    @Test
    public void testExitingGame() {
        playerInput = "exit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Thanks for playing!"));
    }

    //Tests for interacting with items:
    @Test
    public void testTakingValidItem() {
        playerInput = "take chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You have picked up: chair."));
        assertTrue(gamePlayTester.getPlayerInventory().contains("chair"));
    }

    @Test
    public void testTakingItemAlreadyInInventory() {
        playerInput = "take chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.addToInventory("chair");
        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You already have chair!"));
    }

    @Test
    public void testTakingItemNotInRoom() {
        playerInput = "take key" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("There is no key in the room."));
    }

    @Test
    public void testDroppingValidItem() {
        playerInput = "drop potato" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.addToInventory("potato");
        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You've dropped: potato."));
        assertEquals(-1, gamePlayTester.getPlayerInventory().indexOf("potato"));
        assertTrue(gamePlayTester.getCurrentRoom().getItemsVisible().contains("potato"));
    }

    @Test
    public void testDroppingItemAlreadyInRoom() {
        playerInput = "drop chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.addToInventory("chair");
        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("The item chair is already in this room!"));
        assertEquals(-1, gamePlayTester.getPlayerInventory().indexOf("chair"));
    }

    @Test
    public void testDroppingItemNotInInventory() {
        playerInput = "drop chair" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You don't have chair!"));
    }

    //Test examining room:
    @Test
    public void testExamineCommand() {
        playerInput = "examine" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Examining this room..."));
    }

    //Test reaching end room:
    @Test
    public void testWinningGame() {
        //Player must have reached the end room AND have the key
        playerInput = "go south" + "\n" + "take key" + "\n" + "go north" + "\n" +
                      "go east" + "\n" + "go east" + "\n" + "go south" + "\n" +
                      "go south" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Congrats!"));
        assertTrue(gamePlayTester.isGameEnded());
    }

    @Test
    public void testEndRoomMissingKey() {
        playerInput = "go east" + "\n" + "go east" + "\n" + "go south" + "\n" + "go south" + "\n"
                    + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You seem to be missing a key..."));
    }

    //Test player command syntax issues: (casing/white space, unknown command, other command names)
    @Test
    public void testWhiteSpaceInCommand() {
        playerInput = "go                      east" + "\n" + "         quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You have moved to: Hallway"));
    }

    @Test
    public void testCasingIssuesInCommand() {
        playerInput = "gO eaSt" + "\n" + "QUIT" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You have moved to: Hallway"));
    }

    @Test
    public void testUnknownCommand() {
        playerInput = "jump" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("I don't understand jump."));
    }

    @Test
    public void testAlternativeCommandName() {
        playerInput = "move south" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("You have moved to: Storage Closet"));
    }

    @Test
    public void testNoGivenDirection() {
        playerInput = "go" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include a direction to move in."));
    }

    @Test
    public void testNoGivenItemToTake() {
        playerInput = "take" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include an item to take."));
    }

    @Test
    public void testNoGivenItemToDrop() {
        playerInput = "drop" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertThat(gameOutput, CoreMatchers.containsString("Please include an item to drop."));
    }

    //Custom feature test
    @Test
    public void testVisitedRoomsHistory() {
        playerInput = "go south" + "\n" + "go north" + "\n" + "quit" + "\n";
        inputStream = new ByteArrayInputStream(playerInput.getBytes());

        gamePlayTester.runGame(inputStream);

        String gameOutput = outputStream.toString();
        assertTrue(gamePlayTester.getOrderedVisitedRooms().get(0) == 3);
        assertTrue(gamePlayTester.getOrderedVisitedRooms().get(1) == 0);
    }
}
