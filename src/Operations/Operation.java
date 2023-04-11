package Operations;

public class Operation {
    private String name;
    private byte byteCode;


    public Operation(String name, byte byteCode) {
        this.name = name;
        this.byteCode = byteCode;
    }

    public String getName() {
        return this.name;
    }

    public byte getByteCode() {
        return this.byteCode;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Operation))
            return false;
        Operation temp = (Operation) other;
        return temp.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }


}
