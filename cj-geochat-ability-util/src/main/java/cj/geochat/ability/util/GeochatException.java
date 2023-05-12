package cj.geochat.ability.util;

public class GeochatException extends Exception {
    public GeochatException(String code, String message) {
        super(String.format("%s %s", code, message));
    }

    public GeochatException(String code, Throwable e) {
        super(String.format("%s %s", code, e.getMessage()), e);
    }

}
