package IsaSimulator;

import Operations.*;
import ValueStoreElement.*;
import Exceptions.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Interpreter {

    public static enum Flags {
        REG(generateByteCode()), CONST(generateByteCode()), ADRESS(generateByteCode()), INDIRECT(generateByteCode()),
        LABEL(generateByteCode()), INSTRUCTION(generateByteCode());
        public byte byteCode;

        Flags(byte byteCode) {
            this.byteCode = byteCode;
        }
    }

    public static HashSet<String> keywords = new HashSet<>();
    public static HashMap<String, Byte> keywordToByteCodeOperation = new HashMap<>();//generator bajt kod

    public static HashMap<Byte, String> byteCodeToKeyword = new HashMap<>();
    public static HashMap<Byte, Operation> byteCodeToOperation = new HashMap<>();//interpreter
    public static HashMap<Byte, Register> byteCodeToRegister = new HashMap<>();//interpter

    public static HashMap<Byte, Flags> byteToFlag = new HashMap<>();


    //interpret data
    public static HashMap<Long, MemoryLocation> adressSpace = new HashMap<>();
    public static final long ADRESS_SPACE_START = 0x0;
    public static Register programCounter;
    public static boolean equalsResult = false;
    public static boolean greaterThenResult = false;
    // public static HashMap<String,Byte> operationToByteCode=new HashMap<>();

    public static final String breakPoint = "BREAKPOINT";
    private static boolean debugMode = false;


    static {
        setRegisters();
        setOperations();
        setCodeDecodeFlags();
        keywordToByteCodeOperation.entrySet().stream().forEach((p) -> {
            byteCodeToKeyword.put(p.getValue(), p.getKey());
        });
    }

    private static byte byteCodeId = 1;

    private static byte generateByteCode() {
        return byteCodeId++;
    }

    public static void resetInterpreter()
    {
        programCounter.setValue(ADRESS_SPACE_START);
        adressSpace.clear();
        equalsResult=false;
        greaterThenResult=false;
    }


    private static void addNewRegister(Register r) {
        byteCodeToRegister.put(r.getByteCode(), r);
    }

    private static void addNewOperation(Operation op) {
        byteCodeToOperation.put(op.getByteCode(), op);
    }

    public static long getRegisterValue(String register)
    {
        return byteCodeToRegister.get(keywordToByteCodeOperation.get(register)).getValue();
    }

    private static void setOperations() {
        addNewOperation(new BinaryOperation("ADD", generateByteCode(), OperationUtil::add));
        addNewOperation(new BinaryOperation("SUB", generateByteCode(), OperationUtil::sub));
        addNewOperation(new BinaryOperationWithDefaultArgument("MUL", generateByteCode(), OperationUtil::mul, byteCodeToRegister.get(keywordToByteCodeOperation.get("RAX"))));
        addNewOperation(new BinaryOperationWithDefaultArgument("DIV", generateByteCode(), OperationUtil::div, byteCodeToRegister.get(keywordToByteCodeOperation.get("RAX"))));
        addNewOperation(new BinaryOperation("AND", generateByteCode(), OperationUtil::and));
        addNewOperation(new BinaryOperation("OR", generateByteCode(), OperationUtil::or));
        addNewOperation(new BinaryOperation("XOR", generateByteCode(), OperationUtil::xor));
        addNewOperation(new BinaryOperation("MOV", generateByteCode(), OperationUtil::mov));
        addNewOperation(new BinaryOperation("CMP", generateByteCode(), OperationUtil::cmp));
        addNewOperation(new UnaryOperation("NOT", generateByteCode(), OperationUtil::not));
        addNewOperation(new UnaryOperation("PRINT", generateByteCode(), OperationUtil::print));
        addNewOperation(new UnaryOperation("INPUT", generateByteCode(), OperationUtil::input));
        addNewOperation(new JmpOperation("JMP", generateByteCode(), OperationUtil::jmp));
        addNewOperation(new JmpOperation("JE", generateByteCode(), OperationUtil::je));
        addNewOperation(new JmpOperation("JNE", generateByteCode(), OperationUtil::jne));
        addNewOperation(new JmpOperation("JGE", generateByteCode(), OperationUtil::jge));
        addNewOperation(new JmpOperation("JL", generateByteCode(), OperationUtil::jl));
        addNewOperation(new SpecialOperation(breakPoint, generateByteCode(), Interpreter::debug));
        byteCodeToOperation.values().stream().forEach((o) -> {
            keywordToByteCodeOperation.put(o.getName(), o.getByteCode());
            keywords.add(o.getName());
        });
    }

    private static void setRegisters() {
        addNewRegister(new Register("RAX", generateByteCode()));
        addNewRegister(new Register("RCX", generateByteCode()));
        addNewRegister(new Register("RDX", generateByteCode()));
        addNewRegister(new Register("RBX", generateByteCode()));
        programCounter = new Register("RIP", generateByteCode());
        addNewRegister(programCounter);
        byteCodeToRegister.values().stream().forEach((p) -> {
            keywordToByteCodeOperation.put(p.getName(), p.getByteCode());
            keywords.add(p.getName());
        });
    }

    private static void setCodeDecodeFlags() {
        Arrays.stream(Flags.values()).forEach((p) -> {
            byteToFlag.put(p.byteCode, p);
        });
        /*Arrays.stream(Flags.values()).forEach((p) -> {
            keywordToByteCodeOperation.put(p.toString(), generateByteCode());
        });*/

    }

    private static void incProgramCounter() {
        programCounter.setValue(programCounter.getValue() + 1);
    }

    public static void maschineCodeInterpret() throws InvalidOpCodeException, InvalidOperandTypeException, MemoryReferencingException {
        programCounter.setValue(ADRESS_SPACE_START);
        while (adressSpace.get(programCounter.getValue()).getValue() != 0) {
            //fetch
            MemoryLocation instructionLocation = adressSpace.get(programCounter.getValue());
            incProgramCounter();
            //decode
            Operation operation = byteCodeToOperation.get((byte) instructionLocation.getValue());
            if (operation == null)
                throw new InvalidOpCodeException();

            if (operation instanceof SpecialOperation) {
                SpecialOperation specialOperation = (SpecialOperation) operation;
                specialOperation.getAction().run();
                continue;
            } else if (operation instanceof BinaryOperation) {
                BinaryOperation binaryOperation = (BinaryOperation) operation;
                var leftOperand = fetchOperands();
                var rightOperand = fetchOperands();
                try {
                    binaryOperation.getAction().accept((IStoreValue) leftOperand, rightOperand);
                } catch (ClassCastException e) {
                    throw new InvalidOperandTypeException();
                }
            } else if (operation instanceof UnaryOperation) {
                UnaryOperation unaryOperation = (UnaryOperation) operation;
                var operand = fetchOperands();
                try {
                    unaryOperation.getAction().accept((IStoreValue) operand);
                } catch (ClassCastException e) {
                    throw new InvalidOperandTypeException();
                }
            }
            if (debugMode)
                debug();
        }

    }

    public static IHaveValue fetchOperands() throws MemoryReferencingException {
        try {
            MemoryLocation operandType = adressSpace.get(programCounter.getValue());
            incProgramCounter();

            // String type = byteCodeToKeyword.get((byte) operandType.getValue());
            Flags type = byteToFlag.get((byte) operandType.getValue());
            if (Flags.REG.equals(type)) {
                var regByteCode = adressSpace.get(programCounter.getValue());
                incProgramCounter();
                return byteCodeToRegister.get((byte) regByteCode.getValue());
            } else if (Flags.CONST.equals(type)) {
                long result = getLongFromMemory();
                return new Constant(result);
            } else if (Flags.ADRESS.equals(type)) {
                long address = getLongFromMemory();
                if (!adressSpace.containsKey(address))
                    adressSpace.put(address, new MemoryLocation(address, (byte) 0));
                return adressSpace.get(address);
            } else if (Flags.INDIRECT.equals(type)) {
                var byteCodeOfRegister = adressSpace.get(programCounter.getValue());
                incProgramCounter();
                var reg = byteCodeToRegister.get((byte) byteCodeOfRegister.getValue());
                if (!adressSpace.containsKey(reg.getValue()))
                    adressSpace.put(reg.getValue(), new MemoryLocation(reg.getValue(), (byte) 0));
                return adressSpace.get(reg.getValue());
            } else if (Flags.LABEL.equals(type))//labela
            {
                long address = getLongFromMemory();
                return adressSpace.get(address);
            } else
                throw new MemoryReferencingException();

        } catch (Exception e) {
            throw new MemoryReferencingException();
        }
    }

    private static long getLongFromMemory() {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) adressSpace.get(programCounter.getValue()).getValue();
            incProgramCounter();
        }
        return getLongFromBytes(bytes);
    }

    private static long getLongFromBytes(byte[] arr) {
        return ByteBuffer.wrap(arr).getLong();
    }

    private static long getLongValueFromString(String value) {
        if (value.startsWith(ByteCodeTranslator.HEX_NUMBER_START)) {
            value = value.substring(2);
            return Long.parseLong(value, 16);
        }
        return Long.parseLong(value);
    }

    private static void debug() {
        System.out.println("Stanje registara:");
        byteCodeToRegister.values().forEach(System.out::println);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine().toUpperCase();
            if (line.startsWith("&")) {
                line = line.substring(1);
                try {
                    long adress = getLongValueFromString(line);
                    if (!adressSpace.containsKey(adress))
                        System.out.println(adress + ":" + 0);
                    else
                        System.out.println(adressSpace.get(adress));
                } catch (NumberFormatException e) {
                    System.out.println("Nepravilan format adrese");
                }
            } else if ("CONTINUE".equals(line)) {
                debugMode = false;
                break;
            } else if ("NEXT".equals(line)) {
                debugMode = true;
                break;
            }
        }
    }


}
