package student.server;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

public class KidnappedServiceTest {
    private KidnappedService testerService;

    private final static String DATABASE_URL = "jdbc:sqlite:src/main/resources/adventure.db";
    private Connection dbConnection;

    @Before
    public void setUp() throws SQLException {
        dbConnection = DriverManager.getConnection(DATABASE_URL);
        testerService = new KidnappedService();
    }

    @Test
    public void testReset() throws AdventureException {
        testerService.newGame();
        testerService.reset();

        assertEquals(0, testerService.fetchNumberRunningGames());
    }

    @Test
    public void testGameAddition() throws AdventureException{
        testerService.newGame();

        assertEquals(1, testerService.fetchNumberRunningGames());
    }

    @Test
    public void testDatabaseCreated() throws AdventureException, SQLException {
        testerService.newGame();

        Statement statement = dbConnection.createStatement();

        assertTrue(statement.execute("SELECT * FROM leaderboard_aju3"));
    }

    @Test
    public void testFetchCorrectGameStatus() throws AdventureException {
        testerService.newGame();
        testerService.newGame();

        testerService.executeCommand(1, new Command("go", "east"));

        assertTrue(testerService.getGame(1).getMessage().contains("Hallway"));
    }

    @Test
    public void testDestroyGame() throws AdventureException {
        testerService.newGame();
        testerService.destroyGame(0);

        assertEquals(0, testerService.fetchNumberRunningGames());
    }

    @Test
    public void testLeaderboardUpdatedAfterEndGame() throws AdventureException, SQLException {
        testerService.newGame();
        Command quitCommand = new Command("quit", "game");
        quitCommand.setPlayerName("LeaderboardTester");
        testerService.executeCommand(0, quitCommand);

        Statement statement = dbConnection.createStatement();
        ResultSet leaderboardResults;

        statement.execute("SELECT score FROM leaderboard_aju3 WHERE name='LeaderboardTester'");
        leaderboardResults = statement.getResultSet();

        assertEquals(Integer.MAX_VALUE, leaderboardResults.getInt("score"));
    }

    @Test
    public void testFetchLeaderboardInCorrectOrder() {
        LinkedHashMap<String, Integer> leaderboard = testerService.fetchLeaderboard();
        assertEquals("Anonymous=0", leaderboard.entrySet().iterator().next().toString());
    }

    //Tests for initial state of service
    @Test
    public void testCorrectGameMap() {
        assertEquals("src/test/resources/fullValidGame.json", testerService.getGameMapFile());
    }

    @Test
    public void testNoGamesRunning() {
        assertEquals(0, testerService.fetchNumberRunningGames());
    }

    @Test
    public void testCorrectConnectedDatabase() {
        assertEquals(DATABASE_URL, testerService.getDatabaseUrl());
    }
}
