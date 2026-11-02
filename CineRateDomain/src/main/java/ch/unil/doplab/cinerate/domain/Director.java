package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.*;

public class Director extends Person {

    private Set<UUID> movies;

    public Director() {
        super();
        movies = new LinkedHashSet<>();
    }

    public Director(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Director(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Director(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
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

    public boolean hasDirected(UUID movieID) {
        return movies.contains(movieID);
    }

    public List<UUID> getMovies() {
        return new ArrayList<>(movies);
    }

    public void setMovies(List<UUID> movies) {
        this.movies = movies == null ? new LinkedHashSet<>() : new LinkedHashSet<>(movies);
    }

    public void replaceWith(Director director) {
        super.replaceWith(director);
        this.movies = new LinkedHashSet<>(director.movies);
    }

    public void mergeWith(Director director) {
        super.mergeWith(director);
        if (!director.movies.isEmpty()) {
            this.movies.addAll(director.movies);
        }
    }
}
