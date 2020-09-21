import student.adventure.GameEngine;
import static student.adventure.PlayerInteractionHandler.executePlayerCommand;

import java.io.IOException;

/**
 * Main class that runs the game Kidnapped! on console.
 */
public class ConsoleMain {
    public static void main(String[] args) throws IOException {
        GameEngine newGame = new GameEngine("src/test/resources/fullValidGame.json", "> ", 0);

        System.out.print(newGame.getCurrentGameState().getMessage());

        while (!newGame.isGameEnded()) {
            System.out.print(executePlayerCommand(newGame, System.in).toString());
        }
    }
}