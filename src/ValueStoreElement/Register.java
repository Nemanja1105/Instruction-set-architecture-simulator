package ValueStoreElement;

public class Register implements IStoreValue {
    private String name;
    private byte byteCode;
    private long value;

    public Register(String name, byte byteCode) {
        this.name = name;
        this.byteCode = byteCode;
        this.value = 0;
    }

   /* public Register(String name,long value)
    {
        this.name=name;
        this.value=value;
    }*/

    public String getName() {
        return this.name;
    }

    public byte getByteCode() {
        return this.byteCode;
    }

    @Override
    public long getValue() {
        return this.value;
    }

    @Override
    public void setValue(long value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return this.name + ":" + this.value;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
