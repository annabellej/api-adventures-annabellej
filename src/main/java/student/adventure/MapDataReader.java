package student.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Object handling reading in data from any JSON file to a GameMap.
 *
 * @author  Annabelle Ju
 * @version 9/21/2020
 */
public class MapDataReader {
    /**
     * Given the name of a JSON file, deserializes the file if it's valid.
     *
     * @throws IllegalArgumentException if given file name is invalid
     * @throws IOException              if file doesn't exist
     * @throws NullPointerException     if file is empty
     * @throws InvalidObjectException   if GameMap given by file is invalid.
     *
     * @return the Object containing the deserialized data.
     */
    public static GameMap deserializeFile(String fileName) throws IOException {
        //error check: given file name is invalid
        if (fileName == null || fileName.length() == 0) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        BufferedReader dataFile = new BufferedReader(new FileReader(fileName));

        //add bookmark at start of file to return to after error check:
        dataFile.mark(100);

        //error check: data file is empty
        if (dataFile.readLine() == null) {
            throw new NullPointerException("Data file is empty.");
        }

        //reset pointer of file reader to the bookmark at start of file:
        dataFile.reset();

        GameMap deserializedMap = new ObjectMapper().readValue(dataFile, GameMap.class);
        checkMapStructureValidity(deserializedMap);

        return deserializedMap;
    }

    /**
     * Helper function to check if a given game map has valid structure and values.
     *
     * @param gameMap the game map to be checked.
     *
     * @throws InvalidObjectException if the given map is not valid.
     */
    private static void checkMapStructureValidity(GameMap gameMap) throws InvalidObjectException {
        //error check: deserialized game map has invalid structure
        if (!gameMap.hasEndRoom()) {
            throw new InvalidObjectException("Game map does not have an end room, or too many end rooms.");
        }
        else if (!gameMap.hasUniqueRoomNumbers()) {
            throw new InvalidObjectException("Two or more rooms have same room number.");
        }

        //error check: deserialized game map has invalid values
        if (!gameMap.hasValidRooms()) {
            throw new InvalidObjectException("One or more rooms have invalid attribute values.");
        }
    }
}
