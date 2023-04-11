package Operations;

import ValueStoreElement.IHaveValue;
import ValueStoreElement.IStoreValue;

import java.util.function.BiConsumer;

public class BinaryOperation extends Operation {
    private BiConsumer<IStoreValue, IHaveValue> action;

    public BinaryOperation(String name, byte byteCode, BiConsumer<IStoreValue, IHaveValue> action) {
        super(name, byteCode);
        this.action = action;
    }

    public BiConsumer<IStoreValue, IHaveValue> getAction() {
        return this.action;
    }
}
