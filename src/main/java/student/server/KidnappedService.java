package student.server;

import student.adventure.GameEngine;
import static student.adventure.PlayerInteractionHandler.executePlayerCommand;

import java.sql.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Adventure game service that store and runs a number of Kidnapped! games.
 *
 * @author  Annabelle Ju
 * @version 9/20/2020
 */
public class KidnappedService implements AdventureService {
    private List<GameEngine> gamesRunning;
    private String gameMapFile;

    private final static String DATABASE_URL = "jdbc:sqlite:src/main/resources/adventure.db";
    private Connection dbConnection;

    /**
     * Default constructor for KidnappedService.
     * Initiates an empty list of Kidnapped! games and empty leaderboard.
     * Connects to the leaderboard database.
     */
    public KidnappedService() {
        gameMapFile = "src/test/resources/fullValidGame.json";
        gamesRunning = new ArrayList<>();

        try {
            dbConnection = DriverManager.getConnection(DATABASE_URL);
        }
        catch (SQLException e) {
            dbConnection = null;
        }
    }

    @Override
    public void reset() {
        gamesRunning = new ArrayList<>();
    }

    @Override
    public int newGame() throws AdventureException {
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS leaderboard_aju3 (name varchar(50), score int)");

            GameEngine newKidnappedGame = new GameEngine(gameMapFile, "");
            gamesRunning.add(newKidnappedGame);

            return newKidnappedGame.getGameID();
        }
        catch (Exception e) {
            throw new AdventureException("Could not start new game.");
        }
    }

    @Override
    public GameStatus getGame(int id) {
        return fetchGameWithID(id).getCurrentGameState();
    }

    @Override
    public boolean destroyGame(int id) {
        GameEngine engineToRemove;

        try {
            engineToRemove = fetchGameWithID(id);
        }
        catch (NoSuchElementException e) {
            return false;
        }

        gamesRunning.remove(engineToRemove);
        return true;
    }

    @Override
    public void executeCommand(int id, Command command) {
        GameEngine gameEngine = fetchGameWithID(id);

        executePlayerCommand(gameEngine, command);

        //update leaderboard if game ends after this command
        if (gameEngine.isGameEnded()) {
            try {
                Statement statement = dbConnection.createStatement();
                statement.execute("INSERT INTO leaderboard_aju3 VALUES (\'" +
                                      gameEngine.getGamePlayer().getPlayerName() + "\', " +
                                      gameEngine.getGamePlayer().getPlayerScore() + ")");
            }
            catch (SQLException e) {
                return;
            }
        }
    }

    @Override
    public Map<String, Integer> fetchLeaderboard() {
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute("SELECT * FROM leaderboard_aju3");

            return sortLeaderboard(statement.getResultSet());

        }
        catch (SQLException e) {
            return null;
        }
    }

    /**
     * Finds the GameEngine run by this service that has the given id.
     *
     * @param gameID the id of the game to search for.
     *
     * @return the GameEngine with the given id.
     */
    private GameEngine fetchGameWithID(int gameID) {
        for (GameEngine currentEngine: gamesRunning) {
            if (currentEngine.getGameID() == gameID) {
                return currentEngine;
            }
        }

        throw new NoSuchElementException("No game with this ID is currently running.");
    }

    /**
     * Helper method for fetchLeaderboard function; sorts the leaderboard players.
     * Sorting leaderboard by score derived from:
     * https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
     *
     * @param leaderboardItems the leaderboard data items to sort.
     *
     * @return a sorted map of the leaderboard players.
     */
    private Map<String, Integer> sortLeaderboard(ResultSet leaderboardItems) {
        Map<String, Integer> leaderboard = new HashMap<>();

        try {
            while (leaderboardItems.next()) {
                String playerName = leaderboardItems.getString(1);
                int playerScore = leaderboardItems.getInt(2);

                leaderboard.put(playerName, playerScore);
            }
        }
        catch (SQLException e) {
            return null;
        }

        return leaderboard.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
