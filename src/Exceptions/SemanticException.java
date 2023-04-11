package Exceptions;

public class SemanticException extends Exception {
    public SemanticException() {
        super("Semanticka greska u kodu");
    }

    public SemanticException(String message) {
        super(message);
    }
}
