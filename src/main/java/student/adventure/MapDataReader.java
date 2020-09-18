package student.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Object handling reading in data from any JSON file to a game map.
 *
 * @author  Annabelle Ju
 * @version 9/14/2020
 */
public class MapDataReader {
    private String fileName;

    public MapDataReader() {
        fileName = null;
    }

    /**
     * Constructor for objects of class JSONDataReader.
     * Given the Object for which to deserialize, initiates this data reader accordingly.
     *
     * @param fileName the Object for which to deserialize a JSON file.
     */
    public MapDataReader(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Given the name of a JSON file, deserializes the file if it's valid.
     *
     * @return the Object containing the deserialized data.
     */
    public GameMap deserializeFile() throws IOException {
        //error check: given file name is invalid
        if (fileName == null || fileName.length() == 0) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        BufferedReader dataFile = new BufferedReader(new FileReader(fileName));
        dataFile.mark(100);

        //error check: data file is empty
        if (dataFile.readLine() == null) {
            throw new NullPointerException("Data file is empty.");
        }
        dataFile.reset();

        GameMap deserializedMap = new ObjectMapper().readValue(dataFile, GameMap.class);

        //error check: deserialized game map has invalid structure
        if (!deserializedMap.hasEndRoom()) {
            throw new InvalidObjectException("Game map does not have an end room, or too many end rooms.");
        }
        else if (!deserializedMap.hasUniqueRoomNumbers()) {
            throw new InvalidObjectException("Two or more rooms have same room number.");
        }

        //error check: deserialized game map has invalid values
        if (!deserializedMap.hasValidRooms()) {
            throw new InvalidObjectException("One or more rooms have invalid attribute values.");
        }

        return deserializedMap;
    }
}
