package dev.eloo.mechanics.exceptions;

public class MechanicsFileNotFoundException extends RuntimeException {
    public MechanicsFileNotFoundException(String s, Throwable cause){
        super(s,cause);
    }
}
