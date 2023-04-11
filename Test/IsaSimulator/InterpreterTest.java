package IsaSimulator;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {


    private void executeCode(String path)
    {
        Interpreter.resetInterpreter();
        try {
            CodeReader reader = new CodeReader(path);
            reader.readCode();

            CodeAnalyzator analyzator=new CodeAnalyzator(reader.getCodeLines());
            analyzator.analyzeCode();

            ByteCodeTranslator byteCodeTranslator = new ByteCodeTranslator(reader.getCodeLines());
            byteCodeTranslator.translateToMaschineCode();

            Interpreter.maschineCodeInterpret();
            //assertEquals(44, Interpreter.getRegisterValue("RAX"));
        }
        catch(Exception e){fail(e.getMessage());}
    }


    @Test
    public void arithmeticOperationsTest()
    {
        this.executeCode("Test\\ArithmeticOperationsTestInput.txt");
        assertEquals(44, Interpreter.getRegisterValue("RAX"));
    }

   @Test
    public void bitwiseOperationsTest()
    {
      this.executeCode("Test\\bitwiseOperationsTestInput.txt");
      assertEquals(6, Interpreter.getRegisterValue("RAX"));

    }

    @Test
    public void movTest()
    {
        this.executeCode("Test\\movTestInput.txt");
        assertEquals(15,Interpreter.getRegisterValue("RAX"));
    }

    @Test
    public void printTest()
    {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        this.executeCode("Test\\printTestInput.txt");
        String result="157\r\n111\r\n";
        assertEquals(result,outputStream.toString());
        System.setOut(System.out);
    }

    @Test
    public void inputTest()
    {
        ByteArrayInputStream inputStream=new ByteArrayInputStream("5".getBytes());
        System.setIn(inputStream);
        this.executeCode("Test\\inputTestInput.txt");

        //System.setIn(System.in);
        assertEquals(5,Interpreter.getRegisterValue("RAX"));
    }

    @Test
    public void indirectAddressingTest()
    {
        this.executeCode("Test\\indirectAddressingTestInput.txt");
        assertEquals(5,Interpreter.getRegisterValue("RBX"));
    }

    @Test
    public void directAddressingTest()
    {
        this.executeCode("Test\\directAddressingTestInput.txt");
        assertEquals(60,Interpreter.getRegisterValue("RAX"));
    }

    @Test
    public void jmpTest()
    {
        this.executeCode("Test\\jmpTestInput.txt");
        assertEquals(5,Interpreter.getRegisterValue("RAX"));
        assertEquals(4,Interpreter.getRegisterValue("RBX"));
    }

    @Test
    public void cmpTest()
    {
        this.executeCode("Test\\cmpTestInput.txt");
        assertFalse(Interpreter.equalsResult);
        assertTrue(Interpreter.greaterThenResult);
    }

    @Test
    public void jumpEqualsNotEqualTest()
    {
        this.executeCode("Test\\jumpEqualNotEqualTestInput.txt");
        assertEquals(5,Interpreter.getRegisterValue("RAX"));
        assertEquals(4,Interpreter.getRegisterValue("RBX"));
    }

    @Test
    public void jumpGreaterLessThanTest()
    {
        this.executeCode("Test\\jumpGreaterLessThanTestInput.txt");
        assertEquals(5,Interpreter.getRegisterValue("RAX"));
        assertEquals(20,Interpreter.getRegisterValue("RBX"));
    }

    @Test
    public void selfModifyingCodeTest()
    {
        this.executeCode("Test\\selfModifyingCodeTestInput.txt");
        assertEquals(-10,Interpreter.getRegisterValue("RAX"));
    }

}