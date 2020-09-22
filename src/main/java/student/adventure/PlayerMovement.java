package student.adventure;

/**
 * Represents a player's movement: moving in a certain Direction to move to a certain
 * Room.
 *
 * @author  Annabelle Ju
 * @version 9/21/2020
 */
public class PlayerMovement {
    private Direction movementDirection;
    private int movedRoomNumber;

    /**
     * Default constructor for a blank player movement.
     * Initialize empty movement direction and room moved to
     * to be starting room.
     */
    public PlayerMovement() {
        movementDirection = null;
        movedRoomNumber = 1;
    }

    public PlayerMovement(Direction movementDirection, int movedRoomNumber) {
        this.movementDirection = movementDirection;
        this.movedRoomNumber = movedRoomNumber;
    }

    public Direction getMovementDirection() {
        return movementDirection;
    }

    public int getMovedRoomNumber() {
        return movedRoomNumber;
    }

    public void setMovementDirection(Direction movementDirection) {
        this.movementDirection = movementDirection;
    }

    public void setMovedRoomNumber(int movedRoomNumber) {
        this.movedRoomNumber = movedRoomNumber;
    }
}
