package student.adventure;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InvalidObjectException;

import static org.junit.Assert.assertEquals;
import static student.adventure.MapDataReader.deserializeFile;

public class DataReaderTest {
    //Invalid file tests:
    @Test (expected = IllegalArgumentException.class)
    public void testNullFileName() throws IOException {
        deserializeFile(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyFileName() throws IOException {
        deserializeFile("");
    }

    @Test (expected = NullPointerException.class)
    public void testEmptyDataFile() throws IOException {
        deserializeFile("src/test/resources/emptyFile.json");
    }

    //Invalid data value tests:
    @Test (expected = InvalidObjectException.class)
    public void testNoEndRooms() throws IOException {
        deserializeFile("src/test/resources/noEndRooms.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testTwoEndRooms() throws IOException {
        deserializeFile("src/test/resources/twoEndRooms.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testTwoRoomsSameNumber() throws IOException {
        deserializeFile("src/test/resources/notUniqueRooms.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithEmptyRoomName() throws IOException {
        deserializeFile("src/test/resources/emptyRoomName.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithEmptyRoomDescription() throws IOException {
        deserializeFile("src/test/resources/emptyRoomDescription.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithZeroRoomNumber() throws IOException {
        deserializeFile("src/test/resources/zeroRoomNumber.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNegativeRoomNumber() throws IOException {
        deserializeFile("src/test/resources/negRoomNumber.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNoPossibleDirections() throws IOException {
        deserializeFile("src/test/resources/noDirections.json");
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNoPossibleRooms() throws IOException {
        deserializeFile("src/test/resources/noRooms.json");
    }

    //Valid json file test:
    @Test
    public void testValidDataFile() throws IOException {
        assertEquals(9, deserializeFile("src/test/resources/fullValidGame.json").findMapSize());
    }
}