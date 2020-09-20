package student.server;

import student.adventure.GameEngine;
import static student.adventure.PlayerInteractionHandler.executePlayerCommand;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.SortedMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Adventure game service that store and runs a number of Kidnapped! games.
 *
 * @author  Annabelle Ju
 * @version 9/19/2020
 */
public class KidnappedService implements AdventureService {
    private List<GameEngine> gamesRunning;
    private SortedMap<String, Integer> leaderboard;
    private String gameMapFile;

    private final static String DATABASE_URL = "jdbc:sqlite:src/main/resources/adventure.db";
    private final Connection dbConnection;

    /**
     * Default constructor for KidnappedService.
     * Initiates an empty list of Kidnapped! games and empty leaderboard.
     * Connects to the leaderboard database.
     */
    public KidnappedService() throws SQLException {
        gameMapFile = "src/test/resources/fullValidGame.json";
        dbConnection = DriverManager.getConnection(DATABASE_URL);
        reset();
    }

    @Override
    public void reset() {
        gamesRunning = new ArrayList<>();
        leaderboard = new TreeMap<>();
    }

    @Override
    public int newGame() throws AdventureException {
        try {
            GameEngine newKidnappedGame = new GameEngine(gameMapFile, "");
            gamesRunning.add(newKidnappedGame);
            leaderboard.put(newKidnappedGame.getGamePlayer().getPlayerName(),
                            newKidnappedGame.getGamePlayer().getPlayerScore());

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
        leaderboard.put(gameEngine.getGamePlayer().getPlayerName(),
                        gameEngine.getGamePlayer().getPlayerScore());
    }

    @Override
    public SortedMap<String, Integer> fetchLeaderboard() {
        return leaderboard;
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
}
