package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class User {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String role;
    private LocalDate registrationDate;
    private Set<UUID> watchlist;
    private Set<UUID> watchedMovies;
    private Map<UUID, Review> reviews;

    public User() {
        this(null, null, null, null, null);
    }

    public User(String firstName, String lastName, String email, String username, String password) {
        this(null, firstName, lastName, email, username, password);
    }

    public User(UUID uuid, String firstName, String lastName, String email, String username, String password) {
        this(uuid, firstName, lastName, email, username, password, Utils.memberRole);
    }

    public User(UUID uuid, String firstName, String lastName, String email, String username, String password, String role) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.registrationDate = LocalDate.now();
        this.watchlist = new LinkedHashSet<>();
        this.watchedMovies = new LinkedHashSet<>();
        this.reviews = new TreeMap<>();
    }

    public void replaceWith(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        this.uuid = user.uuid;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.username = user.username;
        this.password = user.password;
        this.role = user.role;
        this.registrationDate = user.registrationDate;
        this.watchlist = user.watchlist;
        this.watchedMovies = user.watchedMovies;
        this.reviews = user.reviews;
    }

    public void mergeWith(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.uuid != null) {
            this.uuid = user.uuid;
        }
        if (user.firstName != null) {
            this.firstName = user.firstName;
        }
        if (user.lastName != null) {
            this.lastName = user.lastName;
        }
        if (user.email != null) {
            this.email = user.email;
        }
        if (user.username != null) {
            this.username = user.username;
        }
        if (user.password != null) {
            this.password = user.password;
        }
        if (!user.watchlist.isEmpty()) {
            this.watchlist.addAll(user.watchlist);
        }
        if (!user.watchedMovies.isEmpty()) {
            this.watchedMovies.addAll(user.watchedMovies);
        }
        if (!user.reviews.isEmpty()) {
            this.reviews.putAll(user.reviews);
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String plainPassword) {
        if (plainPassword == null || password == null) {
            return false;
        }
        return Utils.checkPassword(plainPassword, password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return Utils.adminRole.equals(role);
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<UUID> getWatchlist() {
        return new ArrayList<>(watchlist);
    }

    public void setWatchlist(List<UUID> watchlist) {
        this.watchlist = new LinkedHashSet<>(watchlist);
    }

    public List<UUID> getWatchedMovies() {
        return new ArrayList<>(watchedMovies);
    }

    public void setWatchedMovies(List<UUID> watchedMovies) {
        this.watchedMovies = new LinkedHashSet<>(watchedMovies);
    }

    public void addToWatchlist(UUID movieID) {
        if (movieID == null) {
            throw new IllegalArgumentException("Movie ID must not be null");
        }
        if (watchlist.contains(movieID)) {
            throw new IllegalStateException("Movie " + movieID + " is already in the watchlist of " + username);
        }
        watchlist.add(movieID);
    }

    public void removeFromWatchlist(UUID movieID) {
        if (!watchlist.contains(movieID)) {
            throw new IllegalStateException("Movie " + movieID + " is not in the watchlist of " + username);
        }
        watchlist.remove(movieID);
        watchedMovies.remove(movieID);
    }

    public void markAsWatched(UUID movieID) {
        if (!watchlist.contains(movieID)) {
            throw new IllegalStateException("Movie " + movieID + " is not in the watchlist of " + username);
        }
        watchedMovies.add(movieID);
    }

    public void markAsUnwatched(UUID movieID) {
        watchedMovies.remove(movieID);
    }

    public boolean isInWatchlist(UUID movieID) {
        return watchlist.contains(movieID);
    }

    public boolean hasWatched(UUID movieID) {
        return watchedMovies.contains(movieID);
    }

    public Review getReview(UUID movieID) {
        return reviews.get(movieID);
    }

    public boolean hasReviewed(UUID movieID) {
        return movieID != null && reviews.containsKey(movieID);
    }

    public boolean putReview(Review review) {
        return reviews.putIfAbsent(review.getMovieID(), review) == null;
    }

    public boolean removeReview(Review review) {
        return reviews.remove(review.getMovieID()) != null;
    }

    public List<Review> getReviews() {
        return reviews.values().stream()
                .sorted(Comparator.comparing(Review::getDate))
                .toList();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews.stream().collect(Collectors.toMap(Review::getMovieID, review -> review, (a, b) -> a, TreeMap::new));
    }

    public String describe() {
        return "id=" + this.uuid + ", firstName='" + this.firstName + "', lastName='" + this.lastName + "'" +
                ", username='" + this.username + "', email='" + this.email + "', role='" + this.role + "'" +
                ", registrationDate=" + this.registrationDate + ", watchlist=" + this.watchlist +
                ", watchedMovies=" + this.watchedMovies + ", reviews=" + this.reviews.size();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid) && Objects.equals(getFirstName(), user.getFirstName()) && Objects.equals(getLastName(), user.getLastName()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getRole(), user.getRole()) && Objects.equals(getWatchlist(), user.getWatchlist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, getFirstName(), getLastName(), getUsername(), getEmail(), getRole(), getWatchlist());
    }
}
