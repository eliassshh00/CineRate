package ch.unil.doplab.cinerate.domain;

public enum Rating {
    NO_RATING(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    private int value;

    Rating(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Rating fromValue(int value) {
        return switch (value) {
            case 0 -> NO_RATING;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            case 6 -> SIX;
            case 7 -> SEVEN;
            case 8 -> EIGHT;
            case 9 -> NINE;
            case 10 -> TEN;
            default -> throw new IllegalArgumentException("Invalid rating value: " + value);
        };
    }

    public String getLabel() {
        return switch (value) {
            case 0 -> "No rating";
            case 1 -> "★☆☆☆☆☆☆☆☆☆";
            case 2 -> "★★☆☆☆☆☆☆☆☆";
            case 3 -> "★★★☆☆☆☆☆☆☆";
            case 4 -> "★★★★☆☆☆☆☆☆";
            case 5 -> "★★★★★☆☆☆☆☆";
            case 6 -> "★★★★★★☆☆☆☆";
            case 7 -> "★★★★★★★☆☆☆";
            case 8 -> "★★★★★★★★☆☆";
            case 9 -> "★★★★★★★★★☆";
            case 10 -> "★★★★★★★★★★";
            default -> throw new IllegalArgumentException("Invalid rating value: " + value);
        };
    }
}
