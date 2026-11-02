package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private User alice;
    private User jonas;
    private Movie inception;
    private Movie interstellar;

    @BeforeEach
    void setUp() {
        alice = new User(UUID.randomUUID(),
                "Alice",
                "Martin",
                "alice@cinerate.ch",
                "alice_m",
                "1234");

        jonas = new User(UUID.randomUUID(),
                "Jonas",
                "Keller",
                "jonas@cinerate.ch",
                "jonas84",
                "1234");

        inception = new Movie(UUID.randomUUID(), "Inception", 2010);
        interstellar = new Movie(UUID.randomUUID(), "Interstellar", 2014);
    }

    @Test
    void testPostSuccess() {
        printMethodName();
        var review = new Review(UUID.randomUUID(), Rating.TEN, "Mind-blowing, I want to watch it again.");
        review.post(alice, inception);
        assertTrue(review.isPosted());
        assertNotNull(review.getDate());
        assertEquals(alice.getUUID(), review.getUserID());
        assertEquals(inception.getUUID(), review.getMovieID());
        assertEquals("alice_m", review.getUsername());
        assertEquals("Inception", review.getMovieTitle());
        assertSame(review, alice.getReview(inception.getUUID()));
        assertSame(review, inception.getReview(alice.getUUID()));
        assertEquals(10.0, inception.getRatingAverage());
        assertEquals(1, inception.getRatingCount());
        System.out.println(review);
    }

    @Test
    void testPostSuccess_AverageRating() {
        printMethodName();
        new Review(Rating.TEN, "Masterpiece").post(alice, inception);
        new Review(Rating.THREE, "Too confusing for me").post(jonas, inception);
        new Review(Rating.EIGHT, null).post(alice, interstellar);
        assertEquals(6.5, inception.getRatingAverage());
        assertEquals(2, inception.getReviews().size());
        assertEquals(2, alice.getReviews().size());
        assertEquals(8.0, interstellar.getRatingAverage());
        System.out.println(inception.getTitle() + " is rated " + inception.getFormattedRating());
    }

    @Test
    void testPostFailure_NullUserOrMovie() {
        printMethodName();
        var review = new Review(Rating.SIX, "Not bad");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> review.post(null, inception));
        String expectedMessage = "User or movie cannot be null";
        assertTrue(exception.getMessage().contains(expectedMessage));
        System.out.println(exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> review.post(alice, null));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertFalse(review.isPosted());
    }

    @Test
    void testPostFailure_AlreadyReviewed() {
        printMethodName();
        new Review(Rating.TEN, "Masterpiece").post(alice, inception);
        var secondReview = new Review(Rating.ONE, "Changed my mind");
        Exception exception = assertThrows(IllegalStateException.class, () -> secondReview.post(alice, inception));
        String expectedMessage = alice.getUsername() + " has already reviewed " + inception.getTitle();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(10.0, inception.getRatingAverage());
        assertEquals(1, inception.getReviews().size());
        System.out.println(actualMessage);
    }

    @Test
    void testPostFailure_InvalidRating() {
        printMethodName();
        String expectedMessage = "Rating must be between 1 and 10";
        var unrated = new Review(Rating.NO_RATING, "No stars?");
        Exception exception = assertThrows(IllegalStateException.class, () -> unrated.post(alice, inception));
        assertTrue(exception.getMessage().contains(expectedMessage));
        System.out.println(exception.getMessage());

        var nullRating = new Review(null, "No rating at all");
        exception = assertThrows(IllegalStateException.class, () -> nullRating.post(alice, inception));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertFalse(alice.hasReviewed(inception.getUUID()));
        assertEquals(0, inception.getRatingCount());
    }

    @Test
    void testPostFailure_CommentTooLong() {
        printMethodName();
        var review = new Review(Rating.EIGHT, "a".repeat(Review.maxCommentLength + 1));
        Exception exception = assertThrows(IllegalStateException.class, () -> review.post(alice, inception));
        String expectedMessage = "Comment must not be longer than " + Review.maxCommentLength + " characters";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertFalse(inception.hasReviewFrom(alice.getUUID()));
        System.out.println(actualMessage);
    }

    @Test
    void testPostFailure_MissingID() {
        printMethodName();
        var anonymous = new User("John", "Doe", "john@doe.com", "john", "1234");
        var review = new Review(Rating.SIX, "Okay");
        Exception exception = assertThrows(IllegalStateException.class, () -> review.post(anonymous, inception));
        String expectedMessage = "User and movie must have an ID";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        System.out.println(actualMessage);
    }

    @Test
    void testEditSuccess() {
        printMethodName();
        var review = new Review(Rating.FOUR, "Too long");
        review.post(alice, inception);
        new Review(Rating.EIGHT, "Great").post(jonas, inception);
        assertEquals(6.0, inception.getRatingAverage());
        review.edit(alice, inception, Rating.TEN, "Second viewing: brilliant");
        assertEquals(Rating.TEN, review.getRating());
        assertEquals("Second viewing: brilliant", review.getComment());
        assertEquals(9.0, inception.getRatingAverage());
        assertEquals(2, inception.getRatingCount());
    }

    @Test
    void testEditFailure_WrongUser() {
        printMethodName();
        var review = new Review(Rating.FOUR, "Too long");
        review.post(alice, inception);
        Exception exception = assertThrows(IllegalStateException.class, () -> review.edit(jonas, inception, Rating.ONE, "Hacked"));
        String expectedMessage = "Review is not written by " + jonas.getUsername();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(Rating.FOUR, review.getRating());
        assertEquals(4.0, inception.getRatingAverage());
        System.out.println(actualMessage);
    }

    @Test
    void testDeleteSuccess() {
        printMethodName();
        var review = new Review(Rating.TEN, "Masterpiece");
        review.post(alice, inception);
        new Review(Rating.SIX, "Good").post(jonas, inception);
        review.delete(alice, inception);
        assertNull(alice.getReview(inception.getUUID()));
        assertNull(inception.getReview(alice.getUUID()));
        assertEquals(6.0, inception.getRatingAverage());
        assertEquals(1, inception.getRatingCount());
        assertTrue(alice.getReviews().isEmpty());
    }

    @Test
    void testDeleteFailure_WrongMovie() {
        printMethodName();
        var review = new Review(Rating.TEN, "Masterpiece");
        review.post(alice, inception);
        Exception exception = assertThrows(IllegalStateException.class, () -> review.delete(alice, interstellar));
        String expectedMessage = "Review is not about " + interstellar.getTitle();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertNotNull(alice.getReview(inception.getUUID()));
        System.out.println(actualMessage);
    }

    @Test
    void testDeleteFailure_AlreadyDeleted() {
        printMethodName();
        var review = new Review(Rating.TEN, "Masterpiece");
        review.post(alice, inception);
        review.delete(alice, inception);
        Exception exception = assertThrows(IllegalStateException.class, () -> review.delete(alice, inception));
        String expectedMessage = "was not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(Movie.noRating, inception.getRatingAverage());
        System.out.println(actualMessage);
    }

    @Test
    void testEditSuccess_DetachedCopy() {
        printMethodName();
        new Review(Rating.TWO, "Meh").post(alice, inception);
        new Review(Rating.EIGHT, "Great").post(jonas, inception);
        var copy = new Review(Rating.TEN, "copy from JSON");
        copy.setUserID(alice.getUUID());
        copy.setMovieID(inception.getUUID());
        copy.edit(alice, inception, Rating.SIX, "Better the second time");
        assertEquals(7.0, inception.getRatingAverage(), 1e-9);
        assertEquals(2, inception.getRatingCount());
        assertEquals(Rating.SIX, inception.getReview(alice.getUUID()).getRating());
        assertEquals(Rating.SIX, alice.getReview(inception.getUUID()).getRating());
        assertEquals("Better the second time", inception.getReview(alice.getUUID()).getComment());
        assertEquals(Rating.SIX, copy.getRating());
    }

    @Test
    void testDeleteSuccess_DetachedCopy() {
        printMethodName();
        new Review(Rating.TWO, "Meh").post(alice, inception);
        new Review(Rating.EIGHT, "Great").post(jonas, inception);
        var copy = new Review(Rating.TEN, "copy with a wrong rating");
        copy.setUserID(alice.getUUID());
        copy.setMovieID(inception.getUUID());
        copy.delete(alice, inception);
        assertEquals(8.0, inception.getRatingAverage(), 1e-9);
        assertEquals(1, inception.getRatingCount());
        assertFalse(alice.hasReviewed(inception.getUUID()));
        assertFalse(inception.hasReviewFrom(alice.getUUID()));
    }

    @Test
    void testEditAndDeleteSuccess_MultiUserAverage() {
        printMethodName();
        var bob = new User(UUID.randomUUID(), "Bob", "Dylan", "bob@cinerate.ch", "bob", "1234");
        var aliceReview = new Review(Rating.TEN, "Top");
        aliceReview.post(alice, inception);
        new Review(Rating.FOUR, "So so").post(jonas, inception);
        new Review(Rating.SEVEN, "Good").post(bob, inception);
        assertEquals(7.0, inception.getRatingAverage(), 1e-9);
        aliceReview.edit(alice, inception, Rating.ONE, "Changed my mind");
        assertEquals(4.0, inception.getRatingAverage(), 1e-9);
        aliceReview.delete(alice, inception);
        assertEquals(5.5, inception.getRatingAverage(), 1e-9);
        assertEquals(2, inception.getRatingCount());
    }

    @Test
    void testJsonSuccess_RoundTrip() {
        printMethodName();
        var review = new Review(UUID.randomUUID(), Rating.NINE, "Great");
        review.post(alice, inception);
        var jsonb = jakarta.json.bind.JsonbBuilder.create();
        var back = jsonb.fromJson(jsonb.toJson(review), Review.class);
        assertEquals(review, back);
        assertEquals(alice.getUUID(), back.getUserID());
        assertEquals("Inception", back.getMovieTitle());
    }
}
