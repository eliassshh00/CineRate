package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private Person anne;

    @BeforeEach
    void setUp() {
        anne = new Person(UUID.randomUUID(), "Anne", "Hathaway", LocalDate.of(1982, 11, 12), "American");
    }

    @Test
    void testReplaceSuccess() {
        printMethodName();
        var jessica = new Person(UUID.randomUUID(), "Jessica", "Chastain", LocalDate.of(1977, 3, 24), "American");
        jessica.setTmdbId(83002);
        anne.replaceWith(jessica);
        assertEquals(jessica, anne);
        assertEquals("Jessica Chastain", anne.getFullName());
    }

    @Test
    void testMergeSuccess() {
        printMethodName();
        var uuid = anne.getUUID();
        var patch = new Person();
        patch.setNationality("American-British");
        patch.setTmdbId(1813);
        anne.mergeWith(patch);
        assertEquals(uuid, anne.getUUID());
        assertEquals("Anne", anne.getFirstName());
        assertEquals("Hathaway", anne.getLastName());
        assertEquals(LocalDate.of(1982, 11, 12), anne.getBirthDate());
        assertEquals("American-British", anne.getNationality());
        assertEquals(1813, anne.getTmdbId());
        System.out.println(anne);
    }

    @Test
    void testMergeFailure_NullPerson() {
        printMethodName();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> anne.mergeWith(null));
        String expectedMessage = "Person must not be null";
        assertTrue(exception.getMessage().contains(expectedMessage));
        exception = assertThrows(IllegalArgumentException.class, () -> anne.replaceWith(null));
        assertTrue(exception.getMessage().contains(expectedMessage));
        System.out.println(exception.getMessage());
    }
}
