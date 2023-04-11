package Exceptions;

public class SyntaxException extends Exception {
    public SyntaxException() {
        super("Sintaksna greska u kodu");
    }

    public SyntaxException(String message) {
        super(message);
    }
}
