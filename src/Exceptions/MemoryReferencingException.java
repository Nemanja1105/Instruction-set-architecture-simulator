package Exceptions;

public class MemoryReferencingException extends Exception {
    public MemoryReferencingException() {
        super("Greska prilikom referenciranja memorije!!");
    }

    public MemoryReferencingException(String message) {
        super(message);
    }
}
