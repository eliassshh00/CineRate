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
        alice.replaceWith(jonas);
        assertEquals(jonas, alice);
        assertEquals(List.of(inceptionID), alice.getWatchlist());
        assertTrue(alice.isAdmin());
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
}
