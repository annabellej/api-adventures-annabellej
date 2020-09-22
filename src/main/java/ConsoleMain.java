import student.adventure.GameEngine;
import static student.adventure.PlayerInteractionHandler.executePlayerCommand;

/**
 * Main class that runs the game Kidnapped! on console.
 */
public class ConsoleMain {
    /**
     * Runs an adventure game on the console.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        GameEngine newGame = new GameEngine("src/test/resources/fullValidGame.json", "> ", 0);

        System.out.print(newGame.getCurrentGameState().getMessage());

        while (!newGame.isGameEnded()) {
            System.out.print(executePlayerCommand(newGame, System.in).toString());
        }
    }
}