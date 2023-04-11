package ValueStoreElement;

public class Constant implements IHaveValue {
    private long value;

    public Constant(long value) {
        this.value = value;
    }

    @Override
    public long getValue() {
        return this.value;
    }
}
