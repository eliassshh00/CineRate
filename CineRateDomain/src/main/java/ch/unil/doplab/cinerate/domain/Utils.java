package ch.unil.doplab.cinerate.domain;

import java.lang.reflect.Constructor;
import org.mindrot.jbcrypt.BCrypt;

public class Utils {
    public static final String memberRole = "member";
    public static final String adminRole = "admin";

    public static void printMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        var methodName = stackTraceElements[2].getMethodName();
        var className = stackTraceElements[2].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        var method = className + "." + methodName;
        printSeparator('-', method.length());
        System.out.println(method);
        printSeparator('-', method.length());
    }

    public static void printSeparator(char c, int n) {
        for (int i = 0; i < n; i++) {
            System.out.print(c);
        }
        System.out.println();
    }

    public static Exception buildException(ExceptionDescription description) {
        try {
            Class<?> exceptionClass = Class.forName(description.getType());
            Constructor<?> constructor = exceptionClass.getConstructor(String.class);
            Exception exception = (Exception) constructor.newInstance(description.getMessage());
            return exception;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatMovieRating(double movieRating) {
        return movieRating <= 0 ? "not rated yet" : String.format("%.1f / %d", movieRating, Movie.maxRating);
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
