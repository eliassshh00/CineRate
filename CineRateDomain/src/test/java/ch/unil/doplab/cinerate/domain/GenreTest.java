package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void testFromLabelSuccess() {
        printMethodName();
        assertEquals(Genre.ScienceFiction, Genre.fromLabel("Science Fiction"));
        assertEquals(Genre.ScienceFiction, Genre.fromLabel(" science fiction "));
        assertEquals(Genre.Drama, Genre.fromLabel("Drama"));
        for (Genre genre : Genre.values()) {
            assertEquals(genre, Genre.fromLabel(genre.getLabel()));
        }
    }

    @Test
    void testFromLabelFailure_UnknownLabel() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Genre.fromLabel("Telenovela"));
        assertTrue(exception.getMessage().contains("Unknown genre: Telenovela"));
        System.out.println(exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> Genre.fromLabel(null));
        assertTrue(exception.getMessage().contains("Genre label must not be null"));
    }

    @Test
    void testFromTmdbIdSuccess() {
        printMethodName();
        assertEquals(Genre.ScienceFiction, Genre.fromTmdbId(878));
        assertEquals(Genre.Action, Genre.fromTmdbId(28));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Genre.fromTmdbId(-1));
        assertTrue(exception.getMessage().contains("Unknown TMDB genre id"));
        System.out.println(exception.getMessage());
    }
}
