package Operations;

import IsaSimulator.Interpreter;
import ValueStoreElement.IHaveValue;
import ValueStoreElement.IStoreValue;
import ValueStoreElement.MemoryLocation;

import java.util.Scanner;

public class OperationUtil {

   // private static Scanner scanner = new Scanner(System.in);

    public static void add(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() + rightOperand.getValue());
    }

    public static void sub(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() - rightOperand.getValue());
    }

    public static void mul(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() * rightOperand.getValue());
    }

    public static void div(IStoreValue leftOperand, IHaveValue rightOperand) {
        if (rightOperand.getValue() == 0)
            throw new ArithmeticException();
        leftOperand.setValue(leftOperand.getValue() / rightOperand.getValue());
    }

    public static void and(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() & rightOperand.getValue());
    }

    public static void or(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() | rightOperand.getValue());
    }

    public static void xor(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(leftOperand.getValue() ^ rightOperand.getValue());
    }

    public static void not(IStoreValue operand) {
        operand.setValue(~operand.getValue());
    }

    public static void mov(IStoreValue leftOperand, IHaveValue rightOperand) {
        leftOperand.setValue(rightOperand.getValue());
    }

    public static void print(IHaveValue operand) {
        System.out.println(operand.getValue());
    }

    public static void input(IStoreValue operand) {
        Scanner scanner = new Scanner(System.in);
        long result = scanner.nextLong();
        operand.setValue(result);
    }

    public static void jmp(IStoreValue operand) {
        if (operand instanceof MemoryLocation) {
            MemoryLocation ml = (MemoryLocation) operand;
            Interpreter.programCounter.setValue(ml.getAdress());
        }

    }

    public static void cmp(IStoreValue leftOperand, IHaveValue rightOperand) {
        Interpreter.equalsResult = leftOperand.getValue() == rightOperand.getValue();
        Interpreter.greaterThenResult = leftOperand.getValue() > rightOperand.getValue();
    }

    public static void je(IStoreValue operand) {
        if (Interpreter.equalsResult)
            jmp(operand);
    }

    public static void jne(IStoreValue operand) {
        if (!Interpreter.equalsResult)
            jmp(operand);
    }

    public static void jge(IStoreValue operand) {
        if (Interpreter.equalsResult || Interpreter.greaterThenResult)
            jmp(operand);
    }

    public static void jl(IStoreValue operand) {
        if (!Interpreter.equalsResult && !Interpreter.greaterThenResult)
            jmp(operand);
    }
}
