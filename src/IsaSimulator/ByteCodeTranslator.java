package IsaSimulator;

import Operations.BinaryOperationWithDefaultArgument;
import ValueStoreElement.MemoryLocation;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


//todo izrada tokena i refactor bytecoddeTranslatora
public class ByteCodeTranslator {
    private List<String> codeLines;
    private HashMap<String, Long> labels = new HashMap<>();
    private long adress;

    public static final String HEX_NUMBER_START = "0X";

    public ByteCodeTranslator(List<String> codeLines) {
        this.codeLines = codeLines;
        this.adress = Interpreter.ADRESS_SPACE_START;
    }

    public void translateToMaschineCode() {
        HashMap<String, Long> jumpsWithoutData = new HashMap<>();

        for (var line : this.codeLines) {
            String ins = line.trim().toUpperCase();//uklanjamo praznine
            int indexOfFreeSpace = ins.indexOf(' ');
            if (indexOfFreeSpace == -1 && Interpreter.keywords.contains(ins))//break point
                putInMemory(Interpreter.keywordToByteCodeOperation.get(ins));
            else if (indexOfFreeSpace == -1 && ins.equals(""))
                continue;
            else if (indexOfFreeSpace == -1 && !Interpreter.keywords.contains(ins) && ins.endsWith(":")) {
                labels.put(ins.substring(0, ins.length() - 1), adress);
            } else if (indexOfFreeSpace != -1)// print operand
            {
                var insDec = ins.substring(0, indexOfFreeSpace);
                putInMemory(Interpreter.keywordToByteCodeOperation.get(insDec));

                var operation = Interpreter.byteCodeToOperation.get(Interpreter.keywordToByteCodeOperation.get(insDec));
                if (operation instanceof BinaryOperationWithDefaultArgument) {
                    BinaryOperationWithDefaultArgument tmp = (BinaryOperationWithDefaultArgument) operation;
                    putInMemory(Interpreter.Flags.REG.byteCode);
                    putInMemory(tmp.getDefaultOperand().getByteCode());
                }

                ins = ins.substring(indexOfFreeSpace + 1);
                var operands = ins.split(",");

                for (var operand : operands) {
                    operand = operand.trim();
                    if (isRegister(operand)) {
                        //  putInMemory(Interpreter.keywordToByteCodeOperation.get(Interpreter.Flags.REG.toString()));
                        putInMemory(Interpreter.Flags.REG.byteCode);
                        putInMemory(Interpreter.keywordToByteCodeOperation.get(operand));
                    } else if (operand.startsWith("[") && operand.endsWith("]")) {
                        var value = operand.substring(1, operand.length() - 1);
                        if (isRegister(value))//indirektno adresiranje
                        {
                            //putInMemory(Interpreter.keywordToByteCodeOperation.get(Interpreter.Flags.INDIRECT.toString()));
                            putInMemory(Interpreter.Flags.INDIRECT.byteCode);
                            putInMemory(Interpreter.keywordToByteCodeOperation.get(value));
                        } else {//adresa
                            //  putInMemory(Interpreter.keywordToByteCodeOperation.get(Interpreter.Flags.ADRESS.toString()));
                            putInMemory(Interpreter.Flags.ADRESS.byteCode);
                            long adr = getLongValueFromString(value);
                            putLongValueInMemory(adr);
                        }
                    } else if (isNumber(operand)) {
                        //  putInMemory(Interpreter.keywordToByteCodeOperation.get(Interpreter.Flags.CONST.toString()));
                        putInMemory(Interpreter.Flags.CONST.byteCode);
                        long number = getLongValueFromString(operand);
                        putLongValueInMemory(number);
                    } else //labela
                    {
                        //putInMemory(Interpreter.keywordToByteCodeOperation.get(Interpreter.Flags.LABEL.toString()));
                        putInMemory(Interpreter.Flags.LABEL.byteCode);
                        if (labels.containsKey(operand))
                            putLongValueInMemory(labels.get(operand));
                        else {
                            jumpsWithoutData.put(operand, adress);
                            adress += 8;

                        }
                    }
                }
            }
        }
        long temp = adress;
        for (var lbl : jumpsWithoutData.entrySet()) {
            long jmpAdress = labels.get(lbl.getKey());//adresa gdhe trebamo skociti
            adress = lbl.getValue();
            putLongValueInMemory(jmpAdress);//na adresu gdje je nedostajao skok upisu adresu skoka
        }
        adress = temp;
        putInMemory((byte) 0);
    }

    private boolean isRegister(String value) {
        return Interpreter.byteCodeToRegister.values().stream().anyMatch((r) -> r.getName().equals(value));
    }

    private boolean isNumber(String value) {
        try {
            if (value.startsWith(HEX_NUMBER_START)) {
                value = value.substring(2);
                Long.parseLong(value, 16);
            } else
                Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void putInMemory(Byte value) {
        MemoryLocation location = new MemoryLocation(adress, value);
        Interpreter.adressSpace.put(adress, location);
        adress++;
    }

    private void putLongValueInMemory(long value) {
        var bytes = getBytes(value);
        for (var bt : bytes)
            putInMemory(bt);
    }

    private long getLongValueFromString(String value) {
        if (value.startsWith(HEX_NUMBER_START)) {
            value = value.substring(2);
            return Long.parseLong(value, 16);
        }
        return Long.parseLong(value);
    }

    private byte[] getBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }
}
