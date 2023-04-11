package Operations;

import ValueStoreElement.*;

import java.util.function.BiConsumer;

public class BinaryOperationWithDefaultArgument extends BinaryOperation {
    private Register defaultOperand;

    public BinaryOperationWithDefaultArgument(String name, byte byteCode, BiConsumer<IStoreValue, IHaveValue> action, Register defaultOperand) {
        super(name, byteCode, action);
        this.defaultOperand = defaultOperand;
    }

    public Register getDefaultOperand() {
        return this.defaultOperand;
    }
}
