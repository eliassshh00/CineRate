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
        assertEquals(1, Movie.minRating);
        assertEquals(10, Movie.maxRating);
        inception.rate(10);
        assertEquals(10.0, inception.getRatingAverage());
        System.out.println("inception is rated " + inception.getRatingAverage());

        var previousRating = inception.getRatingAverage();
        inception.rate(Movie.minRating);
        assertEquals(5.5, inception.getRatingAverage());
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
        String expectedMessage = "Rating must be between 1 and 10";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> inception.rate(11));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(Movie.noRating, inception.getRatingAverage());

        exception = assertThrows(IllegalArgumentException.class, () -> inception.rate(0));
        assertTrue(exception.getMessage().contains(expectedMessage));

        exception = assertThrows(IllegalArgumentException.class, () -> inception.unrate(11));
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
        var uuid = inception.getUUID();
        inception.replaceWith(interstellar);
        assertEquals(uuid, inception.getUUID());
        assertNotEquals(interstellar, inception);
        assertEquals(nolan.getUUID(), inception.getDirectorID());
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

    @Test
    void testReplaceSuccess_KeepsServerData() {
        printMethodName();
        var alice = new User(UUID.randomUUID(), "Alice", "Martin", "alice@cinerate.ch", "alice_m", "1234");
        new Review(Rating.EIGHT, "Great").post(alice, inception);
        var put = new Movie(UUID.randomUUID(), "Inception (edited)", 2010);
        inception.replaceWith(put);
        assertEquals("Inception (edited)", inception.getTitle());
        assertNotEquals(put.getUUID(), inception.getUUID());
        assertEquals(1, inception.getReviews().size());
        assertEquals(1, inception.getRatingCount());
        assertEquals(8.0, inception.getRatingAverage());
    }

    @Test
    void testReplaceSuccess_NoAliasing() {
        printMethodName();
        var source = new Movie();
        inception.replaceWith(source);
        source.addKeyword("leak");
        source.addGenre(Genre.Comedy);
        source.setCast(List.of(UUID.randomUUID()));
        assertTrue(inception.getKeywords().isEmpty());
        assertTrue(inception.getGenres().isEmpty());
        assertTrue(inception.getCast().isEmpty());
    }

    @Test
    void testMergeSuccess_IgnoresRatingAndReviews() {
        printMethodName();
        var patch = new Movie();
        patch.setRatingAverage(10);
        patch.setRatingCount(99);
        inception.mergeWith(patch);
        assertEquals(0, inception.getRatingCount());
        assertEquals(Movie.noRating, inception.getRatingAverage());
    }

    @Test
    void testChangeDirectorSuccess() {
        printMethodName();
        var villeneuve = new Director(UUID.randomUUID(), "Denis", "Villeneuve", null, null);
        inception.setDirector(nolan);
        inception.setDirector(villeneuve, nolan);
        assertTrue(inception.isDirectedBy(villeneuve.getUUID()));
        assertTrue(villeneuve.hasDirected(inception.getUUID()));
        assertFalse(nolan.hasDirected(inception.getUUID()));
        inception.removeDirector(villeneuve);
        assertNull(inception.getDirectorID());
        assertNull(inception.getDirectorName());
        assertFalse(villeneuve.hasDirected(inception.getUUID()));
    }

    @Test
    void testChangeDirectorFailure_WrongOldDirector() {
        printMethodName();
        var villeneuve = new Director(UUID.randomUUID(), "Denis", "Villeneuve", null, null);
        inception.setDirector(nolan);
        Exception exception = assertThrows(IllegalStateException.class, () -> inception.setDirector(nolan, villeneuve));
        assertTrue(exception.getMessage().contains("Denis Villeneuve is not the director of Inception"));
        assertTrue(inception.isDirectedBy(nolan.getUUID()));
        exception = assertThrows(IllegalStateException.class, () -> inception.removeDirector(villeneuve));
        System.out.println(exception.getMessage());
    }

    @Test
    void testNullSettersSuccess() {
        printMethodName();
        var movie = new Movie();
        movie.setGenres(null);
        movie.setCast(null);
        movie.setKeywords(null);
        movie.setReviews(null);
        assertTrue(movie.getGenres().isEmpty());
        assertTrue(movie.getCast().isEmpty());
        assertTrue(movie.getKeywords().isEmpty());
        assertTrue(movie.getReviews().isEmpty());
    }

    @Test
    void testSetReviewsFailure_MissingUserID() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> inception.setReviews(List.of(new Review(Rating.ONE, "x"))));
        assertTrue(exception.getMessage().contains("must have a user ID"));
        System.out.println(exception.getMessage());
    }

    @Test
    void testGetReviewsSuccess_NullDate() {
        printMethodName();
        var unposted = new Review(Rating.ONE, "no date");
        unposted.setUserID(UUID.randomUUID());
        var posted = new Review(Rating.TWO, "dated");
        posted.setUserID(UUID.randomUUID());
        posted.setDate(java.time.LocalDateTime.now());
        inception.setReviews(List.of(unposted, posted));
        assertEquals(List.of(posted, unposted), inception.getReviews());
    }

    @Test
    void testUnrateFailure_NoRating() {
        printMethodName();
        Exception exception = assertThrows(IllegalStateException.class, () -> inception.unrate(5));
        assertTrue(exception.getMessage().contains("has no rating to remove"));
        System.out.println(exception.getMessage());
    }

    @Test
    void testKeywordsSuccess_TurkishLocale() {
        printMethodName();
        var defaultLocale = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr"));
            inception.addKeyword("HEIST");
            assertTrue(inception.getKeywords().contains("heist"));
        } finally {
            java.util.Locale.setDefault(defaultLocale);
        }
    }

    @Test
    void testEqualsSuccess_SameUUID() {
        printMethodName();
        var copy = new Movie(inception.getUUID(), "Other title", 1999);
        assertEquals(inception, copy);
        assertEquals(inception.hashCode(), copy.hashCode());
        assertNotEquals(inception, interstellar);
    }

    @Test
    void testJsonSuccess_DirectorNotAProperty() {
        printMethodName();
        inception.setDirector(nolan);
        var jsonb = jakarta.json.bind.JsonbBuilder.create();
        var json = jsonb.toJson(inception);
        var back = jsonb.fromJson(json, Movie.class);
        assertEquals(nolan.getUUID(), back.getDirectorID());
        assertEquals(inception.getTitle(), back.getTitle());
    }
}
