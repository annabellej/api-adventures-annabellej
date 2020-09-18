package student.adventure;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents one of the four cardinal directions the player can move in:
 * North, South, East, or West.
 *
 * @author  Annabelle Ju
 * @version 9/14/2020
 */
public enum Direction {
    @JsonProperty("North")
    north,
    @JsonProperty("South")
    south,
    @JsonProperty("East")
    east,
    @JsonProperty("West")
    west
}
