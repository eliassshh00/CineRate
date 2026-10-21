package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.*;

public class Actor extends Person {

    private Set<UUID> movies;

    public Actor() {
        super();
        movies = new LinkedHashSet<>();
    }

    public Actor(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Actor(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Actor(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
        super(uuid, firstName, lastName, birthDate, nationality);
        movies = new LinkedHashSet<>();
    }

    public String describe() {
        return super.describe() + ", movies=" + movies;
    }

    public void addMovie(UUID movieID) {
        if (movieID == null) {
            throw new IllegalArgumentException("Movie ID must not be null");
        }
        movies.add(movieID);
    }

    public void removeMovie(UUID movieID) {
        movies.remove(movieID);
    }

    public boolean playsIn(UUID movieID) {
        return movies.contains(movieID);
    }

    public List<UUID> getMovies() {
        return new ArrayList<>(movies);
    }

    public void setMovies(List<UUID> movies) {
        this.movies = new LinkedHashSet<>(movies);
    }

    public void replaceWith(Actor actor) {
        super.replaceWith(actor);
        this.movies = actor.movies;
    }

    public void mergeWith(Actor actor) {
        super.mergeWith(actor);
        if (!actor.movies.isEmpty()) {
            this.movies.addAll(actor.movies);
        }
    }
}
