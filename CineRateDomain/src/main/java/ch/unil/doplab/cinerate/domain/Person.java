package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Person {

    private UUID uuid;
    private Integer tmdbId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String nationality;

    public Person() {
        this(null, null, null, null);
    }

    public Person(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Person(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Person(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
        this.uuid = uuid;
        this.tmdbId = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public void replaceWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        this.tmdbId = person.tmdbId;
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.birthDate = person.birthDate;
        this.nationality = person.nationality;
    }

    public void mergeWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        if (person.uuid != null) {
            this.uuid = person.uuid;
        }
        if (person.tmdbId != null) {
            this.tmdbId = person.tmdbId;
        }
        if (person.firstName != null) {
            this.firstName = person.firstName;
        }
        if (person.lastName != null) {
            this.lastName = person.lastName;
        }
        if (person.birthDate != null) {
            this.birthDate = person.birthDate;
        }
        if (person.nationality != null) {
            this.nationality = person.nationality;
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String describe() {
        return "id=" + this.uuid + ", tmdbId=" + this.tmdbId + ", firstName='" + this.firstName + "', lastName='" + this.lastName + "'" +
                ", birthDate=" + this.birthDate + ", nationality='" + this.nationality + "'";
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        if (uuid != null || person.uuid != null) {
            return Objects.equals(uuid, person.uuid);
        }
        return Objects.equals(uuid, person.uuid) && Objects.equals(getTmdbId(), person.getTmdbId()) && Objects.equals(getFirstName(), person.getFirstName()) && Objects.equals(getLastName(), person.getLastName()) && Objects.equals(getBirthDate(), person.getBirthDate()) && Objects.equals(getNationality(), person.getNationality());
    }

    @Override
    public int hashCode() {
        if (uuid != null) {
            return uuid.hashCode();
        }
        return Objects.hash(uuid, getTmdbId(), getFirstName(), getLastName(), getBirthDate(), getNationality());
    }
}
