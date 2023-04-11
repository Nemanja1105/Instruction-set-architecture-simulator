package Operations;

import Operations.Operation;
import ValueStoreElement.IStoreValue;

import java.util.function.Consumer;

public class UnaryOperation extends Operation
{
    private Consumer<IStoreValue> action;

    public UnaryOperation(String name, byte byteCode, Consumer<IStoreValue> action)
    {
        super(name,byteCode);
        this.action=action;
    }

    public Consumer<IStoreValue> getAction(){return this.action;}
}
