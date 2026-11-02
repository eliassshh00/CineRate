package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class DirectorTest {

    private Director nolan;
    private Director villeneuve;
    private UUID inceptionID;
    private UUID duneID;

    @BeforeEach
    void setUp() {
        nolan = new Director(UUID.randomUUID(), "Christopher", "Nolan", LocalDate.of(1970, 7, 30), "British");
        villeneuve = new Director(UUID.randomUUID(), "Denis", "Villeneuve", LocalDate.of(1967, 10, 3), "Canadian");
        inceptionID = UUID.randomUUID();
        duneID = UUID.randomUUID();
    }

    @Test
    void testAddMovieSuccess() {
        printMethodName();
        nolan.addMovie(inceptionID);
        villeneuve.addMovie(duneID);
        assertTrue(nolan.hasDirected(inceptionID));
        assertFalse(nolan.hasDirected(duneID));
        assertEquals(List.of(duneID), villeneuve.getMovies());
    }

    @Test
    void testAddMovieFailure_NullMovie() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> nolan.addMovie(null));
        String expectedMessage = "Movie ID must not be null";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testReplaceSuccess() {
        printMethodName();
        villeneuve.addMovie(duneID);
        var uuid = nolan.getUUID();
        nolan.replaceWith(villeneuve);
        assertEquals(uuid, nolan.getUUID());
        assertNotEquals(villeneuve, nolan);
        assertEquals("Denis Villeneuve", nolan.getFullName());
        assertEquals(List.of(duneID), nolan.getMovies());
    }
}
