package ch.unil.doplab.cinerate.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Movie {

    public static int minRating = Rating.ONE_STAR.getValue();
    public static int maxRating = Rating.FIVE_STARS.getValue();
    public static int noRating = Rating.NO_RATING.getValue();

    private UUID uuid;
    private Integer tmdbId;
    private String title;
    private int releaseYear;
    private String synopsis;
    private int duration;
    private String posterUrl;
    private Set<Genre> genres;
    private UUID directorID;
    private String directorName;
    private List<UUID> cast;
    private Set<String> keywords;
    private double ratingAverage;
    private long ratingCount;
    private Map<UUID, Review> reviews;

    public Movie() {
        this(null, null, 0, null, 0, null);
    }

    public Movie(String title, int releaseYear) {
        this(null, title, releaseYear, null, 0, null);
    }

    public Movie(String title, int releaseYear, String synopsis, int duration) {
        this(null, title, releaseYear, synopsis, duration, null);
    }

    public Movie(UUID uuid, String title, int releaseYear) {
        this(uuid, title, releaseYear, null, 0, null);
    }

    public Movie(UUID uuid, String title, int releaseYear, String synopsis, int duration) {
        this(uuid, title, releaseYear, synopsis, duration, null);
    }

    public Movie(UUID uuid, String title, int releaseYear, String synopsis, int duration, String posterUrl) {
        this.uuid = uuid;
        this.tmdbId = null;
        this.title = title;
        this.releaseYear = releaseYear;
        this.synopsis = synopsis;
        this.duration = duration;
        this.posterUrl = posterUrl;
        this.genres = EnumSet.noneOf(Genre.class);
        this.directorID = null;
        this.directorName = null;
        this.cast = new ArrayList<>();
        this.keywords = new TreeSet<>();
        this.ratingAverage = noRating;
        this.ratingCount = 0;
        this.reviews = new TreeMap<>();
    }

    public void replaceWith(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null");
        }
        this.uuid = movie.uuid;
        this.tmdbId = movie.tmdbId;
        this.title = movie.title;
        this.releaseYear = movie.releaseYear;
        this.synopsis = movie.synopsis;
        this.duration = movie.duration;
        this.posterUrl = movie.posterUrl;
        this.genres = movie.genres;
        this.directorID = movie.directorID;
        this.directorName = movie.directorName;
        this.cast = movie.cast;
        this.keywords = movie.keywords;
        this.ratingAverage = movie.ratingAverage;
        this.ratingCount = movie.ratingCount;
        this.reviews = movie.reviews;
    }

    public void mergeWith(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null");
        }
        if (movie.uuid != null) {
            this.uuid = movie.uuid;
        }
        if (movie.tmdbId != null) {
            this.tmdbId = movie.tmdbId;
        }
        if (movie.title != null) {
            this.title = movie.title;
        }
        if (movie.releaseYear != 0) {
            this.releaseYear = movie.releaseYear;
        }
        if (movie.synopsis != null) {
            this.synopsis = movie.synopsis;
        }
        if (movie.duration != 0) {
            this.duration = movie.duration;
        }
        if (movie.posterUrl != null) {
            this.posterUrl = movie.posterUrl;
        }
        if (!movie.genres.isEmpty()) {
            this.genres.addAll(movie.genres);
        }
        if (movie.directorID != null) {
            this.directorID = movie.directorID;
        }
        if (movie.directorName != null) {
            this.directorName = movie.directorName;
        }
        for (UUID actorID : movie.cast) {
            if (!this.cast.contains(actorID)) {
                this.cast.add(actorID);
            }
        }
        if (!movie.keywords.isEmpty()) {
            this.keywords.addAll(movie.keywords);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<Genre> getGenres() {
        return new ArrayList<>(genres);
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres.isEmpty() ? EnumSet.noneOf(Genre.class) : EnumSet.copyOf(genres);
    }

    public void addGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre must not be null");
        }
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
    }

    public boolean hasGenre(Genre genre) {
        return genres.contains(genre);
    }

    public UUID getDirectorID() {
        return directorID;
    }

    public void setDirectorID(UUID directorID) {
        this.directorID = directorID;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public void setDirector(Director director) {
        if (director == null) {
            throw new IllegalArgumentException("Director must not be null");
        }
        if (director.getUUID() == null || this.uuid == null) {
            throw new IllegalStateException("Movie and director must have an ID");
        }
        this.directorID = director.getUUID();
        this.directorName = director.getFullName();
        director.addMovie(this.uuid);
    }

    public boolean isDirectedBy(UUID directorID) {
        return directorID != null && directorID.equals(this.directorID);
    }

    public List<UUID> getCast() {
        return new ArrayList<>(cast);
    }

    public void setCast(List<UUID> cast) {
        this.cast = new ArrayList<>(new LinkedHashSet<>(cast));
    }

    public void addActor(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("Actor must not be null");
        }
        if (actor.getUUID() == null || this.uuid == null) {
            throw new IllegalStateException("Movie and actor must have an ID");
        }
        if (cast.contains(actor.getUUID())) {
            throw new IllegalStateException(actor.getFullName() + " is already in the cast of " + title);
        }
        cast.add(actor.getUUID());
        actor.addMovie(this.uuid);
    }

    public void removeActor(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("Actor must not be null");
        }
        if (!cast.contains(actor.getUUID())) {
            throw new IllegalStateException(actor.getFullName() + " is not in the cast of " + title);
        }
        cast.remove(actor.getUUID());
        actor.removeMovie(this.uuid);
    }

    public boolean hasActor(UUID actorID) {
        return cast.contains(actorID);
    }

    public List<String> getKeywords() {
        return new ArrayList<>(keywords);
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = new TreeSet<>();
        keywords.forEach(this::addKeyword);
    }

    public void addKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Keyword must not be empty");
        }
        keywords.add(keyword.trim().toLowerCase());
    }

    public void removeKeyword(String keyword) {
        if (keyword != null) {
            keywords.remove(keyword.trim().toLowerCase());
        }
    }

    public boolean hasKeyword(String keyword) {
        return keyword != null && keywords.contains(keyword.trim().toLowerCase());
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getFormattedRating() {
        return Utils.formatMovieRating(ratingAverage);
    }

    public void rate(int rating) {
        if (rating < minRating || rating > maxRating) {
            throw new IllegalArgumentException("Rating must be between " + minRating + " and " + maxRating);
        }
        ratingAverage = (ratingAverage * ratingCount + rating) / ++ratingCount;
    }

    public void unrate(int rating) {
        if (rating < minRating || rating > maxRating) {
            throw new IllegalArgumentException("Rating must be between " + minRating + " and " + maxRating);
        }
        if (ratingCount > 1) {
            ratingAverage = (ratingAverage * ratingCount - rating) / --ratingCount;
        } else {
            ratingAverage = noRating;
            ratingCount = 0;
        }
    }

    public Review getReview(UUID userID) {
        return reviews.get(userID);
    }

    public boolean hasReviewFrom(UUID userID) {
        return userID != null && reviews.containsKey(userID);
    }

    public boolean putReview(Review review) {
        return reviews.putIfAbsent(review.getUserID(), review) == null;
    }

    public boolean removeReview(Review review) {
        return reviews.remove(review.getUserID()) != null;
    }

    public List<Review> getReviews() {
        return reviews.values().stream()
                .sorted(Comparator.comparing(Review::getDate))
                .toList();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews.stream().collect(Collectors.toMap(Review::getUserID, review -> review, (a, b) -> a, TreeMap::new));
    }

    public String describe() {
        return "id=" + uuid + ", tmdbId=" + tmdbId + ", title='" + title + "', releaseYear=" + releaseYear +
                ", duration=" + duration + " min, genres=" + genres + ", director='" + directorName + "'" +
                ", cast=" + cast + ", keywords=" + keywords + ", rating=" + (ratingCount > 0 ? ratingAverage : "none") +
                " (" + ratingCount + " reviews)";
    }

    @Override
    public String toString() {
        return "Movie{" + describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return getReleaseYear() == movie.getReleaseYear() && getDuration() == movie.getDuration() && Objects.equals(uuid, movie.uuid) && Objects.equals(getTmdbId(), movie.getTmdbId()) && Objects.equals(getTitle(), movie.getTitle()) && Objects.equals(getSynopsis(), movie.getSynopsis()) && Objects.equals(getPosterUrl(), movie.getPosterUrl()) && Objects.equals(getGenres(), movie.getGenres()) && Objects.equals(getDirectorID(), movie.getDirectorID()) && Objects.equals(getCast(), movie.getCast()) && Objects.equals(getKeywords(), movie.getKeywords());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, getTmdbId(), getTitle(), getReleaseYear(), getSynopsis(), getDuration(), getPosterUrl(), getGenres(), getDirectorID(), getCast(), getKeywords());
    }
}
