package dev.eloo.mechanics.exceptions;

public abstract class RuntimeMechanicException extends RuntimeException {

    public RuntimeMechanicException() {
        super();
    }

    public RuntimeMechanicException(String message) {
        super(message);
    }

}
