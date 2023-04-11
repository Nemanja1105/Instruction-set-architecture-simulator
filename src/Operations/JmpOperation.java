package Operations;

import java.util.function.Consumer;

import ValueStoreElement.IStoreValue;

public class JmpOperation extends UnaryOperation {
    public JmpOperation(String name, byte byteCode, Consumer<IStoreValue> action) {
        super(name, byteCode, action);
    }
}
