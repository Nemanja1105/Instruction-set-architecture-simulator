package IsaSimulator;

import Exceptions.LexicalException;
import Exceptions.SemanticException;
import Exceptions.SyntaxException;
import Operations.*;

import java.util.HashSet;
import java.util.List;

import static IsaSimulator.ByteCodeTranslator.HEX_NUMBER_START;

public class CodeAnalyzator {
    private List<String> codeLines;

    public CodeAnalyzator(List<String> codeLines) {
        this.codeLines = codeLines;
    }

    private HashSet<String> labels = new HashSet<>();

    private void labelsFinder() throws Exception {
        int lineNum = 1;
        for (var line : codeLines) {
            String ins = line.trim().toUpperCase();
            int indexOfFreeSpace = ins.indexOf(' ');
            if (indexOfFreeSpace == -1 && !Interpreter.keywords.contains(ins))//labele
            {
                if (ins.endsWith(":"))
                    labels.add(ins.substring(0, ins.length() - 1));
            }

        }
    }


    public void analyzeCode() throws Exception {
        this.labelsFinder();
        int lineNum = 1;
        for (var line : codeLines) {
            String ins = line.trim().toUpperCase();
            int indexOfFreeSpace = ins.indexOf(' ');
            if (indexOfFreeSpace == -1 && ins.equals(Interpreter.breakPoint)) {
                lineNum++;//jedinicna instrukcija
                continue;
            } else if (indexOfFreeSpace == -1 && ins.equals("")) {
                lineNum++;
                continue;
            } else if (indexOfFreeSpace == -1 && !Interpreter.keywords.contains(ins))//labele
            {
                if (!ins.endsWith(":"))
                    throw new Exceptions.SyntaxException("Labela mora da zavrsava sa \":\", linija:" + lineNum);
            } else //imamo operande
            {
                if (indexOfFreeSpace == -1)
                    throw new Exceptions.SyntaxException("Pogresan broj operanada, linija:" + lineNum);
                var insDec = ins.substring(0, indexOfFreeSpace);
                if (!Interpreter.keywords.contains(insDec) || isRegister(insDec))
                    throw new Exceptions.LexicalException("Nepodrzana instrukcija, linija:" + lineNum);
                ins = ins.substring(indexOfFreeSpace + 1);
                var operands = ins.split(",");
                Operation operation = Interpreter.byteCodeToOperation.get(Interpreter.keywordToByteCodeOperation.get(insDec));
                if (operation instanceof UnaryOperation && operands.length != 1)
                    throw new Exceptions.SyntaxException("Pogresan broj operanada, linija:" + lineNum);
                else if (operation instanceof BinaryOperation && operands.length != 2 && !(operation instanceof BinaryOperationWithDefaultArgument))
                    throw new Exceptions.SyntaxException("Pogresan broj operanada, linija:" + lineNum);

                if (!(operation instanceof BinaryOperationWithDefaultArgument) && isNumber(operands[0].trim()))
                    throw new Exceptions.SemanticException("Prvi parametar ne moze da bude broj, linija:" + lineNum);
                else {

                    for (var operand : operands) {
                        operand = operand.trim();
                        if (operation instanceof JmpOperation && !labels.contains(operand))
                            throw new SemanticException("Nepostojeca labela, linija:" + lineNum);
                        else if ((operand.startsWith("[") && !operand.endsWith("]")) || (!operand.startsWith("[") && operand.endsWith("]")))
                            throw new LexicalException("Pogresna specifikacija operanda, linija" + lineNum);

                        else if (operand.startsWith("[") && operand.endsWith("]")) {
                            var opt = operand.substring(1, operand.length() - 1);
                            if (!isRegister(opt) && !isNumber(opt))
                                throw new SyntaxException("Sintaksna greska, linija:" + lineNum);
                        } else if (!isRegister(operand) && !isNumber(operand) && !labels.contains(operand))
                            throw new LexicalException("Greska sa tipom operanda, linija:" + lineNum);
                    }
                }
            }

            lineNum++;
        }
    }


    public static boolean isRegister(String operand) {
        return Interpreter.byteCodeToRegister.get(Interpreter.keywordToByteCodeOperation.get(operand)) != null;
    }

    public static boolean isNumber(String value) {
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
}
