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
        assertEquals(11, Rating.values().length);
        assertEquals(Rating.ONE, Rating.fromValue(1));
        assertEquals(Rating.SEVEN, Rating.fromValue(7));
        assertEquals(Rating.TEN, Rating.fromValue(10));
        assertEquals("★☆☆☆☆☆☆☆☆☆", Rating.ONE.getLabel());
        assertEquals("★★★★★★★☆☆☆", Rating.SEVEN.getLabel());
        assertEquals("★★★★★★★★★★", Rating.TEN.getLabel());
        assertEquals("No rating", Rating.NO_RATING.getLabel());
    }

    @Test
    void testFromValueFailure_OutOfRange() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(11));
        assertTrue(exception.getMessage().contains("Invalid rating value: 11"));
        System.out.println(exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(-1));
        assertTrue(exception.getMessage().contains("Invalid rating value: -1"));
    }
}
