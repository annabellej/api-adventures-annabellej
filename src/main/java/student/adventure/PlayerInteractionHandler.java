package student.adventure;

import student.server.Command;
import student.server.GameStatus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Handles any given input from a Kidnapped! game player and determines the appropriate
 * game response.
 *
 * @author  Annabelle Ju
 * @version 9/19/2020
 */
public class PlayerInteractionHandler {
    /**
     * Takes in a player command in the form of a Command and determines the status of
     * the game after the action has been performed.
     *
     * @param gameEngine  the game engine to execute the command on.
     * @param command the command given by the player.
     *
     * @return the GameStatus of the game after the player command.
     */
    public static GameStatus executePlayerCommand(GameEngine gameEngine, Command command) {
        return gameEngine.takeGameStep(command);
    }

    /**
     * Takes in a player command from an input stream and puts the game's response into
     * an output stream.
     *
     * @param gameEngine  the game engine to execute the command on.
     * @param inputStream the command given by the player.
     *
     * @return an OutputStream containing the game's response.
     */
    public static OutputStream executePlayerCommand(GameEngine gameEngine, InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        StringTokenizer tokenizer = new StringTokenizer(scanner.nextLine());

        String commandName = tokenizer.nextToken().toLowerCase();
        String commandValue;

        try {
            commandValue = tokenizer.nextToken().toLowerCase();
        }
        catch (NoSuchElementException e) {
            commandValue = null;
        }

        Command playerCommand =  new Command(commandName, commandValue);
        GameStatus gameResponse = gameEngine.takeGameStep(playerCommand);

        PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
        printStream.println(gameResponse.getMessage());

        return printStream;
    }
}
