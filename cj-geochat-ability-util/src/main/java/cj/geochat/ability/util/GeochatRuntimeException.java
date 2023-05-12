package cj.geochat.ability.util;

public class GeochatRuntimeException extends RuntimeException {
    public GeochatRuntimeException(String code, String message) {
        super(String.format("%s %s", code, message));
    }

    public GeochatRuntimeException(String code, Throwable e) {
        super(String.format("%s %s", code, e.getMessage()), e);
    }
}
