package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    private Movie inception;
    private Movie interstellar;
    private Director nolan;
    private Actor leonardo;
    private Actor matthew;

    @BeforeEach
    void setUp() {
        inception = new Movie(UUID.randomUUID(),
                "Inception",
                2010,
                "A thief who steals corporate secrets through dream-sharing technology is given the task of planting an idea.",
                148);
        inception.setTmdbId(27205);
        inception.addGenre(Genre.Action);
        inception.addGenre(Genre.ScienceFiction);
        inception.addKeyword("dream");

        interstellar = new Movie(UUID.randomUUID(),
                "Interstellar",
                2014,
                "A team of explorers travel through a wormhole in space to ensure humanity's survival.",
                169);
        interstellar.setTmdbId(157336);

        nolan = new Director(UUID.randomUUID(), "Christopher", "Nolan", LocalDate.of(1970, 7, 30), "British");
        leonardo = new Actor(UUID.randomUUID(), "Leonardo", "DiCaprio", LocalDate.of(1974, 11, 11), "American");
        matthew = new Actor(UUID.randomUUID(), "Matthew", "McConaughey", LocalDate.of(1969, 11, 4), "American");
    }

    @Test
    void testRatingSuccess() {
        printMethodName();
        System.out.println("inception is not rated yet (" + inception.getRatingAverage() + ")");
        assertEquals(Movie.noRating, inception.getRatingAverage());
        inception.rate(Movie.maxRating);
        assertEquals(Movie.maxRating, inception.getRatingAverage());
        System.out.println("inception is rated " + inception.getRatingAverage());

        var previousRating = inception.getRatingAverage();
        inception.rate(Movie.minRating);
        assertEquals((Movie.minRating + Movie.maxRating) / 2.0, inception.getRatingAverage());
        assertEquals(2, inception.getRatingCount());
        System.out.println("inception is rated " + inception.getRatingAverage());

        inception.unrate(Movie.minRating);
        assertEquals(previousRating, inception.getRatingAverage());
        assertEquals(1, inception.getRatingCount());

        inception.unrate(Movie.maxRating);
        assertEquals(Movie.noRating, inception.getRatingAverage());
        assertEquals(0, inception.getRatingCount());
    }

    @Test
    void testRatingFailure_OutOfRange() {
        printMethodName();
        String expectedMessage = "Rating must be between " + Movie.minRating + " and " + Movie.maxRating;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> inception.rate(Movie.maxRating + 1));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(Movie.noRating, inception.getRatingAverage());

        exception = assertThrows(IllegalArgumentException.class, () -> inception.rate(Movie.minRating - 1));
        assertTrue(exception.getMessage().contains(expectedMessage));

        exception = assertThrows(IllegalArgumentException.class, () -> inception.unrate(Movie.maxRating + 1));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(Movie.noRating, inception.getRatingAverage());
        System.out.println(exception.getMessage());
    }

    @Test
    void testReplaceSuccess() {
        printMethodName();
        interstellar.addGenre(Genre.Drama);
        interstellar.addKeyword("space");
        interstellar.setDirector(nolan);
        inception.replaceWith(interstellar);
        assertEquals(interstellar, inception);
        assertEquals("Interstellar", inception.getTitle());
        assertEquals(List.of(Genre.Drama), inception.getGenres());
        assertFalse(inception.hasKeyword("dream"));
    }

    @Test
    void testMergeSuccess() {
        printMethodName();
        var uuid = inception.getUUID();
        var patch = new Movie();
        patch.setPosterUrl("https://image.tmdb.org/t/p/w500/inception.jpg");
        patch.addGenre(Genre.Thriller);
        patch.addKeyword("Heist");
        inception.mergeWith(patch);
        assertEquals(uuid, inception.getUUID());
        assertEquals("Inception", inception.getTitle());
        assertEquals(2010, inception.getReleaseYear());
        assertEquals(148, inception.getDuration());
        assertEquals(27205, inception.getTmdbId());
        assertEquals("https://image.tmdb.org/t/p/w500/inception.jpg", inception.getPosterUrl());
        assertEquals(List.of(Genre.Action, Genre.ScienceFiction, Genre.Thriller), inception.getGenres());
        assertEquals(List.of("dream", "heist"), inception.getKeywords());
        System.out.println(inception);
    }

    @Test
    void testAddActorSuccess() {
        printMethodName();
        inception.addActor(leonardo);
        interstellar.addActor(matthew);
        assertTrue(inception.hasActor(leonardo.getUUID()));
        assertFalse(inception.hasActor(matthew.getUUID()));
        assertTrue(leonardo.playsIn(inception.getUUID()));
        assertTrue(matthew.playsIn(interstellar.getUUID()));

        inception.removeActor(leonardo);
        assertFalse(inception.hasActor(leonardo.getUUID()));
        assertFalse(leonardo.playsIn(inception.getUUID()));
    }

    @Test
    void testAddActorFailure_AlreadyInCast() {
        printMethodName();
        inception.addActor(leonardo);
        Exception exception = assertThrows(IllegalStateException.class, () -> inception.addActor(leonardo));
        String expectedMessage = leonardo.getFullName() + " is already in the cast of " + inception.getTitle();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(1, inception.getCast().size());
        System.out.println(actualMessage);
    }

    @Test
    void testRemoveActorFailure_NotInCast() {
        printMethodName();
        Exception exception = assertThrows(IllegalStateException.class, () -> inception.removeActor(matthew));
        String expectedMessage = matthew.getFullName() + " is not in the cast of " + inception.getTitle();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testSetDirectorSuccess() {
        printMethodName();
        inception.setDirector(nolan);
        interstellar.setDirector(nolan);
        assertEquals(nolan.getUUID(), inception.getDirectorID());
        assertEquals("Christopher Nolan", inception.getDirectorName());
        assertTrue(inception.isDirectedBy(nolan.getUUID()));
        assertEquals(List.of(inception.getUUID(), interstellar.getUUID()), nolan.getMovies());
    }

    @Test
    void testSetDirectorFailure_MissingID() {
        printMethodName();
        var unknown = new Director("Unknown", "Director");
        Exception exception = assertThrows(IllegalStateException.class, () -> inception.setDirector(unknown));
        String expectedMessage = "Movie and director must have an ID";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertNull(inception.getDirectorID());
        System.out.println(actualMessage);

        exception = assertThrows(IllegalArgumentException.class, () -> inception.setDirector(null));
        assertTrue(exception.getMessage().contains("Director must not be null"));
    }

    @Test
    void testGenresAndKeywordsSuccess() {
        printMethodName();
        assertTrue(inception.hasGenre(Genre.ScienceFiction));
        assertFalse(inception.hasGenre(Genre.Comedy));
        inception.addKeyword("  Subconscious ");
        assertTrue(inception.hasKeyword("subconscious"));
        assertTrue(inception.hasKeyword("DREAM"));
        inception.removeKeyword("dream");
        assertFalse(inception.hasKeyword("dream"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> inception.addKeyword(" "));
        assertTrue(exception.getMessage().contains("Keyword must not be empty"));
        System.out.println(exception.getMessage());
    }
}
