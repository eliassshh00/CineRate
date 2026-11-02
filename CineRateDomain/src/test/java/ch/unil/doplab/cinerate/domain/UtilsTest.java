package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testHashPasswordSuccess() {
        printMethodName();
        var hash = Utils.hashPassword("popcorn");
        assertNotEquals("popcorn", hash);
        assertTrue(Utils.checkPassword("popcorn", hash));
        assertFalse(Utils.checkPassword("nachos", hash));
    }

    @Test
    void testBuildExceptionSuccess() {
        printMethodName();
        var description = new ExceptionDescription(IllegalStateException.class.getName(), "alice_m has already reviewed Inception");
        var exception = Utils.buildException(description);
        assertInstanceOf(IllegalStateException.class, exception);
        assertEquals("alice_m has already reviewed Inception", exception.getMessage());
    }

    @Test
    void testFormatMovieRatingSuccess() {
        printMethodName();
        assertEquals("not rated yet", Utils.formatMovieRating(Movie.noRating));
        assertTrue(Utils.formatMovieRating(8.7).endsWith(" / 10"));
        assertTrue(Utils.formatMovieRating(Movie.maxRating).endsWith(" / 10"));
        System.out.println(Utils.formatMovieRating(8.7));
    }

    @Test
    void testFormatMovieRatingSuccess_FrenchLocale() {
        printMethodName();
        var defaultLocale = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.FRANCE);
            assertEquals("7.5 / 10", Utils.formatMovieRating(7.5));
        } finally {
            java.util.Locale.setDefault(defaultLocale);
        }
    }
}
