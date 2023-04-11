package Exceptions;

public class LexicalException extends Exception {
    public LexicalException() {
        super("Leksicka greska u datom isa programu");
    }

    public LexicalException(String message) {
        super(message);
    }
}
