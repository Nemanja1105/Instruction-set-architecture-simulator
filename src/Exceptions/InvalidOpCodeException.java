package Exceptions;

public class InvalidOpCodeException extends Exception {
    public InvalidOpCodeException() {
        super("Za dati byte code ne postoji instrukcija");
    }

    public InvalidOpCodeException(String message) {
        super(message);
    }
}
