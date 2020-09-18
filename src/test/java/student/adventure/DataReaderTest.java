package student.adventure;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InvalidObjectException;

import static org.junit.Assert.assertEquals;

public class DataReaderTest {
    private MapDataReader dataReaderTester;

    @Before
    public void setUp() {
        dataReaderTester = new MapDataReader();
    }

    //Invalid file tests:
    @Test (expected = IllegalArgumentException.class)
    public void testNullFileName() throws IOException {
        dataReaderTester.deserializeFile();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyFileName() throws IOException {
        dataReaderTester.setFileName("");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = NullPointerException.class)
    public void testEmptyDataFile() throws IOException {
        dataReaderTester.setFileName("src/test/resources/emptyFile.json");
        dataReaderTester.deserializeFile();
    }

    //Invalid data value tests:
    @Test (expected = InvalidObjectException.class)
    public void testNoEndRooms() throws IOException {
        dataReaderTester.setFileName("src/test/resources/noEndRooms.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testTwoEndRooms() throws IOException {
        dataReaderTester.setFileName("src/test/resources/twoEndRooms.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testTwoRoomsSameNumber() throws IOException {
        dataReaderTester.setFileName("src/test/resources/notUniqueRooms.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithEmptyRoomName() throws IOException {
        dataReaderTester.setFileName("src/test/resources/emptyRoomName.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithEmptyRoomDescription() throws IOException {
        dataReaderTester.setFileName("src/test/resources/emptyRoomDescription.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithZeroRoomNumber() throws IOException {
        dataReaderTester.setFileName("src/test/resources/zeroRoomNumber.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNegativeRoomNumber() throws IOException {
        dataReaderTester.setFileName("src/test/resources/negRoomNumber.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNoPossibleDirections() throws IOException {
        dataReaderTester.setFileName("src/test/resources/noDirections.json");
        dataReaderTester.deserializeFile();
    }

    @Test (expected = InvalidObjectException.class)
    public void testExistsRoomWithNoPossibleRooms() throws IOException {
        dataReaderTester.setFileName("src/test/resources/noRooms.json");
        dataReaderTester.deserializeFile();
    }

    //Valid json file test:
    @Test
    public void testValidDataFile() throws IOException {
        dataReaderTester.setFileName("src/test/resources/fullValidGame.json");
        assertEquals(9, dataReaderTester.deserializeFile().findMapSize());
    }
}