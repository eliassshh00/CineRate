package ch.unil.doplab.cinerate.domain;

public enum Genre {
    Action("Action", 28),
    Adventure("Adventure", 12),
    Animation("Animation", 16),
    Comedy("Comedy", 35),
    Crime("Crime", 80),
    Documentary("Documentary", 99),
    Drama("Drama", 18),
    Family("Family", 10751),
    Fantasy("Fantasy", 14),
    History("History", 36),
    Horror("Horror", 27),
    Music("Music", 10402),
    Mystery("Mystery", 9648),
    Romance("Romance", 10749),
    ScienceFiction("Science Fiction", 878),
    TVMovie("TV Movie", 10770),
    Thriller("Thriller", 53),
    War("War", 10752),
    Western("Western", 37);

    private String label;
    private int tmdbId;

    Genre(String label, int tmdbId) {
        this.label = label;
        this.tmdbId = tmdbId;
    }

    public String getLabel() {
        return label;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public static Genre fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Genre label must not be null");
        }
        for (Genre genre : values()) {
            if (genre.label.equalsIgnoreCase(label.trim())) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + label);
    }

    public static Genre fromTmdbId(int tmdbId) {
        for (Genre genre : values()) {
            if (genre.tmdbId == tmdbId) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown TMDB genre id: " + tmdbId);
    }
}
