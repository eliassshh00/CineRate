package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void testFromValueSuccess() {
        printMethodName();
        for (Rating rating : Rating.values()) {
            assertEquals(rating, Rating.fromValue(rating.getValue()));
        }
        assertEquals(Rating.FOUR_STARS, Rating.fromValue(4));
        assertEquals("★★★★", Rating.FOUR_STARS.getLabel());
        assertEquals("No rating", Rating.NO_RATING.getLabel());
    }

    @Test
    void testFromValueFailure_OutOfRange() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(6));
        assertTrue(exception.getMessage().contains("Invalid rating value: 6"));
        System.out.println(exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(-1));
        assertTrue(exception.getMessage().contains("Invalid rating value: -1"));
    }
}
