import IsaSimulator.ByteCodeTranslator;
import IsaSimulator.CodeAnalyzator;
import IsaSimulator.CodeReader;
import IsaSimulator.Interpreter;
import ValueStoreElement.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            CodeReader reader = new CodeReader(args[0]);
            reader.readCode();
            CodeAnalyzator analyzator = new CodeAnalyzator(reader.getCodeLines());
            analyzator.analyzeCode();

            ByteCodeTranslator byteCodeTranslator = new ByteCodeTranslator(reader.getCodeLines());
            byteCodeTranslator.translateToMaschineCode();

            Interpreter.maschineCodeInterpret();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


