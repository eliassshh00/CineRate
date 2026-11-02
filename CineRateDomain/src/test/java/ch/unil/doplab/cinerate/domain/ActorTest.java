package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    private Actor leonardo;
    private UUID inceptionID;
    private UUID titanicID;

    @BeforeEach
    void setUp() {
        leonardo = new Actor(UUID.randomUUID(), "Leonardo", "DiCaprio", LocalDate.of(1974, 11, 11), "American");
        inceptionID = UUID.randomUUID();
        titanicID = UUID.randomUUID();
    }

    @Test
    void testAddMovieSuccess() {
        printMethodName();
        leonardo.addMovie(titanicID);
        leonardo.addMovie(inceptionID);
        leonardo.addMovie(titanicID);
        assertEquals(List.of(titanicID, inceptionID), leonardo.getMovies());
        assertTrue(leonardo.playsIn(inceptionID));
        leonardo.removeMovie(inceptionID);
        assertFalse(leonardo.playsIn(inceptionID));
        System.out.println(leonardo);
    }

    @Test
    void testAddMovieFailure_NullMovie() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> leonardo.addMovie(null));
        String expectedMessage = "Movie ID must not be null";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(leonardo.getMovies().isEmpty());
        System.out.println(actualMessage);
    }

    @Test
    void testMergeSuccess() {
        printMethodName();
        leonardo.addMovie(titanicID);
        var patch = new Actor();
        patch.setNationality("American-Italian");
        patch.addMovie(inceptionID);
        leonardo.mergeWith(patch);
        assertEquals("Leonardo DiCaprio", leonardo.getFullName());
        assertEquals("American-Italian", leonardo.getNationality());
        assertEquals(List.of(titanicID, inceptionID), leonardo.getMovies());
    }

    @Test
    void testEqualsFailure_ActorIsNotDirector() {
        printMethodName();
        var uuid = UUID.randomUUID();
        assertNotEquals(new Actor(uuid, "Clint", "Eastwood", null, null), new Director(uuid, "Clint", "Eastwood", null, null));
        assertNotEquals(new Actor("Clint", "Eastwood"), new Director("Clint", "Eastwood"));
        assertEquals(new Actor(uuid, "Clint", "Eastwood", null, null), new Actor(uuid, "C.", "Eastwood", null, null));
    }

    @Test
    void testReplaceSuccess_KeepsUUIDAndNoAliasing() {
        printMethodName();
        var actor = new Actor(UUID.randomUUID(), "Tom", "Hardy", null, null);
        var source = new Actor(UUID.randomUUID(), "Cillian", "Murphy", null, null);
        var uuid = actor.getUUID();
        actor.replaceWith(source);
        source.addMovie(UUID.randomUUID());
        assertEquals(uuid, actor.getUUID());
        assertTrue(actor.getMovies().isEmpty());
        actor.setMovies(null);
        assertTrue(actor.getMovies().isEmpty());
    }
}
