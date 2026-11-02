package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User alice;
    private UUID inceptionID;
    private UUID interstellarID;
    private UUID mementoID;

    @BeforeEach
    void setUp() {
        alice = new User(UUID.randomUUID(),
                "Alice",
                "Martin",
                "alice@cinerate.ch",
                "alice_m",
                "1234");
        inceptionID = UUID.randomUUID();
        interstellarID = UUID.randomUUID();
        mementoID = UUID.randomUUID();
    }

    @Test
    void testReplaceSuccess() {
        printMethodName();
        var jonas = new User(UUID.randomUUID(), "Jonas", "Keller", "jonas@cinerate.ch", "jonas84", "5678", Utils.adminRole);
        jonas.addToWatchlist(inceptionID);
        alice.addToWatchlist(mementoID);
        var uuid = alice.getUUID();
        alice.replaceWith(jonas);
        assertEquals(uuid, alice.getUUID());
        assertEquals("jonas84", alice.getUsername());
        assertEquals(List.of(inceptionID), alice.getWatchlist());
        assertFalse(alice.isAdmin());
        assertEquals("5678", alice.getPassword());
    }

    @Test
    void testMergeSuccess() {
        printMethodName();
        var uuid = alice.getUUID();
        var patch = new User();
        patch.setFirstName("Alicia");
        patch.setEmail("alicia@cinerate.ch");
        patch.addToWatchlist(inceptionID);
        patch.setRole(Utils.adminRole);
        patch.setRegistrationDate(LocalDate.of(2000, 1, 1));
        alice.mergeWith(patch);
        assertEquals(uuid, alice.getUUID());
        assertEquals("Alicia", alice.getFirstName());
        assertEquals("Martin", alice.getLastName());
        assertEquals("alice_m", alice.getUsername());
        assertEquals("alicia@cinerate.ch", alice.getEmail());
        assertTrue(alice.isInWatchlist(inceptionID));
        assertEquals(Utils.memberRole, alice.getRole());
        assertEquals(LocalDate.now(), alice.getRegistrationDate());
        System.out.println(alice);
    }

    @Test
    void testMergeFailure_NullUser() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> alice.mergeWith(null));
        String expectedMessage = "User must not be null";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testPasswordSuccess() {
        printMethodName();
        alice.setPassword(Utils.hashPassword("popcorn"));
        assertNotEquals("popcorn", alice.getPassword());
        assertTrue(alice.checkPassword("popcorn"));
        assertFalse(alice.checkPassword("nachos"));
        assertFalse(alice.checkPassword(null));
    }

    @Test
    void testAddToWatchlistSuccess() {
        printMethodName();
        assertTrue(alice.getWatchlist().isEmpty());
        alice.addToWatchlist(interstellarID);
        alice.addToWatchlist(inceptionID);
        alice.addToWatchlist(mementoID);
        assertEquals(List.of(interstellarID, inceptionID, mementoID), alice.getWatchlist());
        assertTrue(alice.isInWatchlist(inceptionID));
        assertFalse(alice.hasWatched(inceptionID));
    }

    @Test
    void testAddToWatchlistFailure_AlreadyInWatchlist() {
        printMethodName();
        alice.addToWatchlist(inceptionID);
        Exception exception = assertThrows(IllegalStateException.class, () -> alice.addToWatchlist(inceptionID));
        String expectedMessage = "is already in the watchlist of " + alice.getUsername();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(1, alice.getWatchlist().size());
        System.out.println(actualMessage);

        exception = assertThrows(IllegalArgumentException.class, () -> alice.addToWatchlist(null));
        expectedMessage = "Movie ID must not be null";
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testRemoveFromWatchlistSuccess() {
        printMethodName();
        alice.addToWatchlist(inceptionID);
        alice.addToWatchlist(interstellarID);
        alice.markAsWatched(inceptionID);
        alice.removeFromWatchlist(inceptionID);
        assertFalse(alice.isInWatchlist(inceptionID));
        assertFalse(alice.hasWatched(inceptionID));
        assertEquals(List.of(interstellarID), alice.getWatchlist());
    }

    @Test
    void testRemoveFromWatchlistFailure_NotInWatchlist() {
        printMethodName();
        Exception exception = assertThrows(IllegalStateException.class, () -> alice.removeFromWatchlist(inceptionID));
        String expectedMessage = "is not in the watchlist of " + alice.getUsername();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testMarkAsWatchedSuccess() {
        printMethodName();
        alice.addToWatchlist(inceptionID);
        alice.addToWatchlist(interstellarID);
        alice.markAsWatched(interstellarID);
        assertTrue(alice.hasWatched(interstellarID));
        assertFalse(alice.hasWatched(inceptionID));
        assertEquals(List.of(interstellarID), alice.getWatchedMovies());
        alice.markAsUnwatched(interstellarID);
        assertFalse(alice.hasWatched(interstellarID));
        assertTrue(alice.isInWatchlist(interstellarID));
    }

    @Test
    void testMarkAsWatchedFailure_NotInWatchlist() {
        printMethodName();
        Exception exception = assertThrows(IllegalStateException.class, () -> alice.markAsWatched(mementoID));
        String expectedMessage = "is not in the watchlist of " + alice.getUsername();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertFalse(alice.hasWatched(mementoID));
        System.out.println(actualMessage);
    }

    @Test
    void testReplaceSuccess_KeepsServerData() {
        printMethodName();
        var movie = new Movie(UUID.randomUUID(), "Inception", 2010);
        new Review(Rating.NINE, "Great").post(alice, movie);
        var registered = alice.getRegistrationDate();
        var put = new User(UUID.randomUUID(), "Al", "Ice", "al@cinerate.ch", "al", null, Utils.adminRole);
        put.setRegistrationDate(LocalDate.of(2000, 1, 1));
        alice.replaceWith(put);
        assertEquals("1234", alice.getPassword());
        assertEquals(Utils.memberRole, alice.getRole());
        assertEquals(registered, alice.getRegistrationDate());
        assertTrue(alice.hasReviewed(movie.getUUID()));
    }

    @Test
    void testReplaceSuccess_NoAliasing() {
        printMethodName();
        var source = new User();
        alice.replaceWith(source);
        source.addToWatchlist(inceptionID);
        source.markAsWatched(inceptionID);
        assertTrue(alice.getWatchlist().isEmpty());
        assertTrue(alice.getWatchedMovies().isEmpty());
    }

    @Test
    void testMergeSuccess_IgnoresReviews() {
        printMethodName();
        var patch = new User();
        var review = new Review(Rating.ONE, "fake");
        review.setMovieID(inceptionID);
        patch.setReviews(List.of(review));
        alice.mergeWith(patch);
        assertFalse(alice.hasReviewed(inceptionID));
    }

    @Test
    void testSetWatchlistSuccess_PrunesWatched() {
        printMethodName();
        alice.addToWatchlist(inceptionID);
        alice.addToWatchlist(mementoID);
        alice.markAsWatched(inceptionID);
        alice.markAsWatched(mementoID);
        alice.setWatchlist(List.of(mementoID));
        assertEquals(List.of(mementoID), alice.getWatchedMovies());
        alice.setWatchlist(null);
        assertTrue(alice.getWatchlist().isEmpty());
        assertTrue(alice.getWatchedMovies().isEmpty());
    }

    @Test
    void testWatchlistFailure_NullMovie() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> alice.removeFromWatchlist(null));
        assertTrue(exception.getMessage().contains("Movie ID must not be null"));
        assertThrows(IllegalArgumentException.class, () -> alice.markAsWatched(null));
        assertThrows(IllegalArgumentException.class, () -> alice.markAsUnwatched(null));
        System.out.println(exception.getMessage());
    }

    @Test
    void testNullSettersSuccess() {
        printMethodName();
        alice.setWatchedMovies(null);
        alice.setReviews(null);
        assertTrue(alice.getWatchedMovies().isEmpty());
        assertTrue(alice.getReviews().isEmpty());
    }

    @Test
    void testJsonSuccess_PasswordNotSerialized() {
        printMethodName();
        alice.setPassword(Utils.hashPassword("popcorn"));
        var jsonb = jakarta.json.bind.JsonbBuilder.create();
        var json = jsonb.toJson(alice);
        System.out.println(json);
        assertFalse(json.contains("password"));
        assertFalse(json.contains(alice.getPassword()));
        var input = jsonb.fromJson("{\"username\":\"bob\",\"password\":\"secret\"}", User.class);
        assertEquals("secret", input.getPassword());
    }

    @Test
    void testEqualsSuccess_SameUUID() {
        printMethodName();
        var copy = new User(alice.getUUID(), "Other", "Name", "x@y.ch", "other", null);
        assertEquals(alice, copy);
        assertEquals(alice.hashCode(), copy.hashCode());
    }
}
