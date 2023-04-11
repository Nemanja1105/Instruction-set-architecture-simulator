package Operations;

public class SpecialOperation extends Operation {
    private Runnable action;

    public SpecialOperation(String name, byte byteCode, Runnable action) {
        super(name, byteCode);
        this.action = action;
    }

    public Runnable getAction() {
        return this.action;
    }
}
