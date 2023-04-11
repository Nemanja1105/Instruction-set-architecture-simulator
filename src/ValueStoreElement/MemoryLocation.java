package ValueStoreElement;

public class MemoryLocation implements IStoreValue {
    private long adress;
    private byte value;

    public MemoryLocation(long adress, byte value) {
        this.adress = adress;
        this.value = value;
    }

    public long getAdress() {
        return this.adress;
    }

    @Override
    public long getValue() {
        return this.value;
    }

    @Override
    public void setValue(long value) {
        this.value = (byte) value;
    }

    @Override
    public String toString() {
        return this.adress + ":" + this.value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = hash * 7 + (int) this.adress;
        return hash;
    }

}
