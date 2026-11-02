package ch.unil.doplab.cinerate.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@ApplicationScoped
public class ApplicationState {

    public static final int minPasswordLength = 6;

    private Map<UUID, User> users;
    private Map<String, UUID> usernames;

    @PostConstruct
    public void init() {
        users = new TreeMap<>();
        usernames = new TreeMap<>();
        populateApplicationState();
    }

    public User addUser(User user) {
        // the ID of a new user is always created by the service
        return addUser(UUID.randomUUID(), user);
    }

    public User addUser(UUID id, User user) {
        checkUser(user);
        var username = user.getUsername();
        if (usernames.get(username) != null) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        var password = user.getPassword();
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("User's password cannot be empty");
        }
        checkPassword(password);
        user.setUUID(id);
        user.setPassword(Utils.hashPassword(password));
        // role, registration date, reviews and watchlist belong to the server
        user.setRole(Utils.memberRole);
        user.setRegistrationDate(LocalDate.now());
        user.setReviews(null);
        user.setWatchlist(null);
        usernames.put(username, id);
        users.put(id, user);
        return user;
    }

    public User getUser(UUID id) {
        return users.get(id);
    }

    public Map<UUID, User> getAllUsers() {
        return users;
    }

    public boolean setUser(UUID uuid, User user) {
        var theUser = users.get(uuid);
        if (theUser == null) {
            return false;
        }
        checkUser(user);
        var username = user.getUsername();
        if (!theUser.getUsername().equals(username) &&
                usernames.get(username) != null &&
                !usernames.get(username).equals(uuid)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        if (user.getPassword() != null) {
            checkPassword(user.getPassword());
            user.setPassword(Utils.hashPassword(user.getPassword()));
        }
        // the watchlist is changed with its own requests: it is kept
        user.setWatchlist(theUser.getWatchlist());
        user.setWatchedMovies(theUser.getWatchedMovies());
        usernames.remove(theUser.getUsername());
        usernames.put(username, uuid);
        theUser.replaceWith(user);
        return true;
    }

    public boolean removeUser(UUID id) {
        var user = users.get(id);
        if (user == null) {
            return false;
        }
        // person 4: call the cleanup of the user's reviews here (removeReviewsOf)
        usernames.remove(user.getUsername());
        users.remove(id);
        return true;
    }

    private void checkUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        var username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("User's username cannot be empty");
        }
        var email = user.getEmail();
        if (email == null || !email.matches("[^@\\s]+@[^@\\s]+")) {
            throw new IllegalArgumentException("User's email must contain an @, like name@example.com");
        }
    }

    private void checkPassword(String password) {
        if (password.length() < minPasswordLength) {
            throw new IllegalArgumentException("User's password must have at least " + minPasswordLength + " characters");
        }
    }

    private void populateApplicationState() {
        addUser(new User("Alice", "Martin", "alice@cinerate.ch", "alice", "alice123"));
        addUser(new User("Bob", "Dupont", "bob@cinerate.ch", "bob", "bob12345"));
        var admin = addUser(new User("Admin", "CineRate", "admin@cinerate.ch", "admin", "admin123"));
        admin.setRole(Utils.adminRole);
    }
}
